package org.ucb.c5.labplanner;

import org.ucb.c5.constructionfile.ParseExperimentDirectory;
import org.ucb.c5.constructionfile.model.*;
import org.ucb.c5.labplanner.inventory.AddSampleToBox;
import org.ucb.c5.labplanner.inventory.ParseInventory;
import org.ucb.c5.labplanner.inventory.model.Box;
import org.ucb.c5.labplanner.inventory.model.Inventory;
import org.ucb.c5.labplanner.inventory.model.Location;
import org.ucb.c5.labplanner.inventory.model.Sample;
import org.ucb.c5.labplanner.labpacket.LabSheetFactory;
import org.ucb.c5.labplanner.labpacket.model.LabPacket;
import org.ucb.c5.labplanner.labpacket.model.LabSheet;

import java.util.*;

/**
 * High Level Function of LabPlanner -- it inputs an Experiment
 * and outputs a LabPacket.  Serialization of output and Deserialization
 * of inputs is not included.
 *
 * TODO: add authors
 * @author J. Christopher Anderson
 */
public class PlanExperiment {
    private HashMap<Operation, List<Step>> Order_Map;
    private AddSampleToBox addSampleToBox;
    private boolean cleansheetPCR = false;
    private boolean cleansheetDigest = false;

    /**
     * A class which implements more useful functions than Step, including making an easily accessible
     * list of reagents for the step
     */
    public class HelperStep implements Step {
        // the Step from which this HelperStep was generated
        public Step step;
        // list of reagents, which are construct names accessible by searching the inventory for them
        public List<String> reagents; // TODO what about Enzyme and Antibiotic? Are these stored in the inventory?
        public String product;
        public String extraProduct; // construction files have their own product names, which seems unnecessary but can
            // mess with our logic, so we must check for this extra product

        public HelperStep(Step step) {
            this.step = step;
            Operation operation = step.getOperation();
            switch (operation) {
                case acquire:
                    throw new IllegalArgumentException("operation cannot be acquire");
                case pca:
                    reagents.addAll(((PCA) step).getOligoPool());
                    break;
                case pcr:
                    reagents.addAll(((PCR) step).getTemplates());
                    reagents.add(((PCR) step).getOligo1());
                    reagents.add(((PCR) step).getOligo2());
                    cleansheetPCR = true;
                    break;
                case digest:
                    // requires.addAll(((Digestion) step).getEnzymes());
                    reagents.add(((Digestion) step).getSubstrate());
                    cleansheetDigest = true;
                    break;
                case ligate:
                    reagents.addAll(((Ligation) step).getFragments());
                    break;
                case assemble:
                    reagents.addAll(((Assembly) step).getFragments());
                    break;
                case blunting:
                    reagents.add(((Blunting) step).getSubstrate());
                    break;
                case transform:
                    reagents.add(((Transformation) step).getDna());
                    reagents.add(((Transformation) step).getStrain());
                    break;
            }
            product = step.getProduct();
            extraProduct = null;
        }

        @Override
        public Operation getOperation() {
            return step.getOperation();
        }

        @Override
        public String getProduct() {
            return step.getProduct();
        }

        public Step getStep() {
            return step;
        }

        public String getExtraProduct() {
            return extraProduct;
        }

        public List<String> getReagents() {
            return reagents;
        }
    }

    public void initiate()  throws Exception {
        //TODO: write me, or delete comment if not needed
        Order_Map = new HashMap<>();

        addSampleToBox = new AddSampleToBox();
        addSampleToBox.initiate();
    }

    public LabPacket run(Experiment expt, Inventory inventory) throws Exception {
        // TODO: write me
        Order_Map = new HashMap<>(); // ensure repeatability

        // Group together similar Steps into List<Step>
        List<List<HelperStep>> groupedSteps = groupSimilarSteps(expt, inventory);

        // Inject gel and zymo cleanup labsheets for each PCR
        // Inject zymo cleanup labsheets for digests
        // NOTE modify the inventory
        Inventory modifiedInventory = inventory.getCopy();
        List<LabSheet> allSheets = applyChangesToInventoryAndAddCleanSheets(groupedSteps, expt, modifiedInventory);

        //Relay List<Step> and Inventory to create LabSheets via separate Functions
        //Bundle up the LabSheets and modified Boxes in LabPacket, return it
        LabPacket labPacket = bundleLabSheets(allSheets, inventory, modifiedInventory);

        return labPacket;
    }

    //Temporarily turned into public to test
    public List<List<HelperStep>> groupSimilarSteps(Experiment expt, Inventory inventory) throws Exception {
        // TODO PlanExperiment Main Logic Part 1
        List<ConstructionFile> list_of_construction_files;
        list_of_construction_files = expt.getCfs();

        // make a list of list of steps which are the groupings
        List<List<HelperStep>> groupedSteps = new ArrayList<>();

        for (ConstructionFile x : list_of_construction_files) {
            // A construction file has the following values
            //    private final List<Step> steps;
            //    private String pdtName = null;
            //    private final Map<String, Polynucleotide> sequences;

            // for each construction file, add all the steps in that construction file to groupedSteps
            for (Step s : x.getSteps()) {
                groupedSteps.add(new ArrayList<>(Arrays.asList(new HelperStep(s))));
            }
            // construction files have an extra name for the same product, add that to the last step's product
            groupedSteps.get(groupedSteps.size() - 1).get(0).extraProduct = x.getPdtName();
        }

        // do optimization to this list of groupings. Currently we group each step individually, which is not optimal.
        // for each group of steps, see how far they can move to the left (i.e. done first) based on dependencies.
        // 1. If the limit to how for the steps can move results in no additional steps which are combined together,
        //  don't move them.
        // 2. Otherwise, move all those steps to where they would satisfy their dependency requirements + merge with
        //  other steps
        // Repeat this process until no steps move.
        boolean didStepsMove = true;

        while (didStepsMove) {
            didStepsMove = false;
            for (int i = 0; i < groupedSteps.size(); i++) {
                List<HelperStep> currentStepGroup = groupedSteps.get(i);
                int leftmostIndexToMoveTo = i;
                for (int j = i - 1; j >= 0; j--) {
                    Set<String> previous_outputs = new HashSet<>();
                    List<HelperStep> previousStepGroup = groupedSteps.get(j);
                    for (HelperStep previousStep : previousStepGroup) {
                        previous_outputs.add(previousStep.getProduct());
                        if (previousStep.getExtraProduct() != null)
                            previous_outputs.add(previousStep.getExtraProduct());
                    }
                    Set<String> current_inputs = new HashSet<>();
                    for (HelperStep currentStep : currentStepGroup) {
                        current_inputs.addAll(currentStep.getReagents());
                    }
                    previous_outputs.retainAll(current_inputs);
                    if (previous_outputs.size() > 0) {
                        // the current stepgroup requires the previous step group
                        // this means that some dependencies are not met, i.e. you can't merge with this group.
                        // stop searching backwards.
                        break;
                    } else if (previousStepGroup.get(0).getOperation().equals(currentStepGroup.get(0).getOperation())) {
                        // operations are the same between these groups, and no dependencies are broken
                        leftmostIndexToMoveTo = j;
                    } else {
                        // though grouping these together breaks no dependency requirements, there is no point in
                        // grouping them together because they are different operation types
                    }
                }
                if (leftmostIndexToMoveTo == i) {
                    // do nothing, you can't move this group left
                } else {
                    // we found a group to combine with, move it left
                    // move this group to the leftmost index possible (popping from the list),
                    // stop the iteration over groupedSteps, and set didStepsMove to true
                    currentStepGroup = groupedSteps.remove(i);
                    groupedSteps.get(leftmostIndexToMoveTo).addAll(currentStepGroup);
                    didStepsMove = true;
                    break;
                }
            }
        }

        return groupedSteps;
    }

    private List<LabSheet> applyChangesToInventoryAndAddCleanSheets(List<List<HelperStep>> groupedSteps, Experiment expt, Inventory inventory)
            throws Exception {
        // TODO PlanExperiment Main Logic Part 2
        //  - collect a list of sources and destinations for each group of Steps
        //  - Apply these changes to the inventory
        //  - run labSheetFactory on each group, then add the output to the final list of LabSheets
        //  - for each group, also add a cleanup LabSheet to the final list of LabSheets
        //  - if the cleanup modifies the inventory, do so

        ArrayList<LabSheet> gatheredLabSheets = new ArrayList<LabSheet>();

        //Initiate LabSheetFactory
        LabSheetFactory labSheetFactory = new LabSheetFactory();
        labSheetFactory.initiate();

        //Initiate AddSampleToBox
        AddSampleToBox addSampleToBox = new AddSampleToBox();

        Map<String, Set<Location>> getLocations = inventory.getConstructToLocations();
        Map<String, Sample.Concentration> mapToConcentration = new HashMap<>();
        //mapToConcentration.put("pca", Sample.Concentration.uM10);
        mapToConcentration.put("pcr", Sample.Concentration.uM10);
        mapToConcentration.put("digest", Sample.Concentration.zymo);
        mapToConcentration.put("ligate", Sample.Concentration.zymo);
        mapToConcentration.put("assemble", Sample.Concentration.zymo);
        mapToConcentration.put("transform", Sample.Concentration.miniprep);
        mapToConcentration.put("blunting", Sample.Concentration.zymo);

        for (List<Step> groupOfSteps : groupedSteps) {
            ArrayList<Location> sources = new ArrayList<Location>();
            ArrayList<Location> destinations = new ArrayList<Location>();

            //Collect locations for sources and destinations list; sources = samples to retrieve from Box, destinations = samples to add to Box
            for (Step singleStep : groupOfSteps) {
                String sample = singleStep.getProduct();
                Set<Location> locations = getLocations.get(sample);
                ArrayList<Location> modifiedLocations = new ArrayList<>(locations);
                Location selectedLocation = modifiedLocations.get(0);
                Operation operation = singleStep.getOperation();
                //Check if sample already exists in Inventory
                List<String> reagents = HelperStep.getReagents();
                for (String r : reagents) {
                    if (getLocations.containsKey(r)) {
                        sources.add(selectedLocation);
                    } else {
                        throw new java.lang.Error("Reagent is not in Inventory!");


                    }

                }
                //Apply these changes to Inventory?? - 1). AddSampleToBox, 2). Add to maps in Inventory
                destinations.add(selectedLocation);
                //1). AddSampleToBox Modification
                List<Box> listBoxes = inventory.getBoxes();
                String boxName = selectedLocation.getBoxname();
                Box locatedBox = null;
                for (Box box : listBoxes) {
                    if (box.getName().equals(boxName)) {
                        locatedBox = box;
                    }
                }
                //Look for Sample Object with 'sample' name
                //TODO GET CORRECT CLONE
                Sample sampleObject = new Sample(sample, "",  mapToConcentration.get(sample), sample, Sample.Culture.primary, "Clone");
                addSampleToBox.run(sampleObject, locatedBox, selectedLocation.getRow(), selectedLocation.getCol());
                //2). Add to maps in Inventory
                int row = 0;
                int col = 0;
                boolean found = false;
                Location newLocation = null;
                for (Box box : listBoxes) {
                    Sample[][] sampleWell = box.getSamples();
                    for (int i = 0; i < sampleWell.length; i++) { //i = row
                        for (int j = 0; j < sampleWell[0].length; j++) { //j = col
                            if (sampleWell[i][j] == null) {
                                row = i;
                                col = j;
                                newLocation = new Location(box.getName(), row, col, "", "");
                                found = true;
                                break;
                            }
                        }
                        if (found) {
                            break;
                        }
                    }
                    if (found) {
                        break;
                    }
                }

                if (inventory.getConstructToLocations().containsKey(sample)) {
                    Set<Location> obtainLocations = inventory.getConstructToLocations().get(sample);
                    obtainLocations.add(newLocation);
                } else {
                    Set<Location> set = new HashSet<>();
                    set.add(newLocation);
                    inventory.getConstructToLocations().put(sample, set);
                }
                inventory.getLocToConc().put(newLocation, mapToConcentration.get(sample));
                inventory.getLocToCulture().put(newLocation, Sample.Culture.primary);
                //TODO GET CORRECT CLONE
                inventory.getLocToClone().put(newLocation, "Clone");
                


            }


            //Run labSheetFactory on each group of steps and add output to gatheredLabSheets
            LabSheet resultingLabSheet = labSheetFactory.run(groupOfSteps, sources, destinations);
            gatheredLabSheets.add(resultingLabSheet);

            //Add a cleanup LabSheet to the final list of LabSheets; checks if steps have pcr or digest and adds to end.
            if (cleansheetPCR){
                LabSheet cleanUpLabSheet = new LabSheet("Cleanup LabSheet PCR", null, null, null, null, null, null, null, null);
                gatheredLabSheets.add(cleanUpLabSheet);
                cleansheetPCR = false;
            }
            if (cleansheetDigest){
                LabSheet cleanUpLabSheet = new LabSheet("Cleanup LabSheet Digest", null, null, null, null, null, null, null, null);
                gatheredLabSheets.add(cleanUpLabSheet);
                cleansheetDigest = false;
            }

        }







//        /// OLD
//
//        // Obtain steps for PCR, Digest
//        List<Step> pcrSteps = groupedSteps.get(2);
//        List<Step> digestSteps = groupedSteps.get(3);
//
//        //Initiate LabSheetFactory
//        LabSheetFactory labSheetFactory = new LabSheetFactory();
//        labSheetFactory.initiate();
//
//        // Inject gel and zymo cleanup labsheets for each PCR
//        // Inject zymo cleanup labsheets for digests
//        Map<String, Set<Location>> getLocations = inventory.getConstructToLocations();
//        List<ConstructionFile> list_constructs;
//        list_constructs = expt.getCfs();
//        ArrayList<Location> listLocations = new ArrayList<Location>();
//        for (ConstructionFile construct : list_constructs) {
//            String name = construct.getPdtName();
//            if(getLocations.containsKey(name)) {
//                listLocations.add(getLocations.get(name));
//            }
//        }
//
//        // TODO make a list of modified locations. Sources is of type List<Location> which are sources.
//        //  Destinations is type List<Location> which are destinations.
//
//        labSheetFactory.run(pcrSteps, sources, destinations);
//        labSheetFactory.run(digestSteps, sources, destinations);

        return gatheredLabSheets;
    }

    private LabPacket bundleLabSheets(List<LabSheet> allSheets,
                                      Inventory originalInventory,
                                      Inventory modifiedInventory) throws Exception {
        // TODO PlanExperiment Main Logic Part 3

        List<Box> modifiedBoxes = new ArrayList<>();
        // compare inventories for differing boxes
        for (int i = 0; i < originalInventory.getBoxes().size(); i++) {
            if (!originalInventory.getBoxes().get(i).equals(modifiedInventory.getBoxes().get(i))) {
                modifiedBoxes.add(modifiedInventory.getBoxes().get(i));
            }
        }

        return new LabPacket(allSheets, modifiedBoxes);
    }

    public static void main(String[] args) throws Exception {
        //Read in an example experiment
        ParseExperimentDirectory parseExp = new ParseExperimentDirectory();
        parseExp.initiate();
        Experiment exp = parseExp.run("LabPlannerData/experiments/Lyc6");

        //Read in an example inventory
        ParseInventory parseInv = new ParseInventory();
        parseInv.initiate();
        Inventory inv = parseInv.run("LabPlannerData/inventory");

        //Run the planner
        PlanExperiment planner = new PlanExperiment();
        planner.initiate();
        LabPacket result = planner.run(exp, inv);

        System.out.println(result.getSheets().size());
    }
}

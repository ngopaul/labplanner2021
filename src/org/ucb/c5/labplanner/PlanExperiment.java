package org.ucb.c5.labplanner;

import org.ucb.c5.constructionfile.ParseExperimentDirectory;
import org.ucb.c5.constructionfile.model.ConstructionFile;
import org.ucb.c5.constructionfile.model.Experiment;
import org.ucb.c5.constructionfile.model.Operation;
import org.ucb.c5.constructionfile.model.Step;
import org.ucb.c5.labplanner.inventory.AddSampleToBox;
import org.ucb.c5.labplanner.inventory.ParseInventory;
import org.ucb.c5.labplanner.inventory.model.Box;
import org.ucb.c5.labplanner.inventory.model.Inventory;
import org.ucb.c5.labplanner.inventory.model.Location;
import org.ucb.c5.labplanner.inventory.model.Sample;
import org.ucb.c5.labplanner.labpacket.LabSheetFactory;
import org.ucb.c5.labplanner.labpacket.model.LabPacket;
import org.ucb.c5.labplanner.labpacket.model.LabSheet;

import java.lang.reflect.Array;
import java.util.*;

import static org.ucb.c5.constructionfile.model.Operation.*; //Added by Patrick

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
        List<List<Step>> groupedSteps = groupSimilarSteps(expt, inventory);

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

    private List<List<Step>> groupSimilarSteps(Experiment expt, Inventory inventory) throws Exception {
        // TODO PlanExperiment Main Logic Part 1
        List<ConstructionFile> list_constructs;
        List<Step> list_steps;
        list_constructs = expt.getCfs();
        for (ConstructionFile x : list_constructs){
            list_steps = x.getSteps();
            for (Step y : list_steps){
                Operation cur_step_opera = y.getOperation();
                if (Order_Map.containsKey(cur_step_opera)){ //Checks if map already has the key (operation)
                    Order_Map.get(cur_step_opera).add(y); //Gets the list of steps associated with the operation and adds the step to it
                }else{ //Doesn't contain the operation seen yet
                    List<Step> steps_for_operation = new ArrayList<>(); //New list of steps
                    steps_for_operation.add(y); //Adds current step
                    Order_Map.put(cur_step_opera, steps_for_operation); //Adds to the map
                }
            }
        }

        //Priority Rearranging
        Operation[] ordering = new Operation[]{pca, pcr, digest, ligate, assemble, transform, blunting}; //order of step groups
        ArrayList<List<Step>> Prioritized = new ArrayList<>();
        for (int i = 0; i < 8; i++){
            if(Order_Map.containsKey(ordering[i])){
                Prioritized.add(Order_Map.get(ordering[i]));
            }
        }
        /* RESULT: ArrayList of List<Step> which should be in order of the ordering array */
        return Prioritized;
    }

    private List<LabSheet> applyChangesToInventoryAndAddCleanSheets(List<List<Step>> groupedSteps, Experiment expt, Inventory inventory)
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
        mapToConcentration.put("pca", Sample.Concentration.uM10);
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
                List<String> reagents = Step.getReagents();
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

            //Add a cleanup LabSheet to the final list of LabSheets; ask tomorrow in class about this
            LabSheet cleanUpLabSheet = new LabSheet("Cleanup LabSheet", null, null, null, null, null, null, null, null);
            gatheredLabSheets.add(cleanUpLabSheet);
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

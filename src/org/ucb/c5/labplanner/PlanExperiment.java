package org.ucb.c5.labplanner;

import org.ucb.c5.constructionfile.ParseExperimentDirectory;
import org.ucb.c5.constructionfile.model.ConstructionFile;
import org.ucb.c5.constructionfile.model.Experiment;
import org.ucb.c5.constructionfile.model.Operation;
import org.ucb.c5.constructionfile.model.Step;
import org.ucb.c5.labplanner.inventory.ParseInventory;
import org.ucb.c5.labplanner.inventory.model.Inventory;
import org.ucb.c5.labplanner.labpacket.model.LabPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List; //Added by Patrick

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

    public void initiate()  throws Exception {
        //TODO: write me, or delete comment if not needed
        Order_Map = new HashMap<>();


    }

    public LabPacket run(Experiment expt, Inventory inventory) throws Exception {
        // TODO: write me
        Order_Map = new HashMap<>(); // ensure repeatability

        // Group together similar Steps into List<Step>
        List<List<Step>> groupedSteps = groupSimilarSteps(expt, inventory);

        // Inject gel and zymo cleanup labsheets for each PCR
        // Inject zymo cleanup labsheets for digests
        // TODO verify correct function signature
        List<List<Step>> cleanedSteps = cleanGroupedSteps(groupedSteps, expt, inventory);

        //Relay List<Step> and Inventory to create LabSheets via separate Functions
        //Bundle up the LabSheets and modified Boxes in LabPacket, return it
        LabPacket labPacket = createAndBundleLabSheets(cleanedSteps, inventory);

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
        Operation[] ordering = new Operation[]{acquire, pca, pcr, digest, ligate, assemble, transform, blunting}; //order of step groups
        ArrayList<List<Step>> Prioritized = new ArrayList<>();
        for (int i = 0; i < 8; i++){
            if(Order_Map.containsKey(ordering[i])){
                Prioritized.add(Order_Map.get(ordering[i]));
            }
        }
        /* RESULT: ArrayList of List<Step> which should be in order of the ordering array */
        return Prioritized;
    }

    private List<List<Step>> cleanGroupedSteps(List<List<Step>> groupedSteps, Experiment expt, Inventory inventory)
            throws Exception {
        // TODO PlanExperiment Main Logic Part 2
        return null;
    }

    private LabPacket createAndBundleLabSheets(List<List<Step>> cleanedSteps, Inventory inventory) throws Exception {
        // TODO PlanExperiment Main Logic Part 3
        return null;
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

package org.ucb.c5.labplanner;

import org.ucb.c5.constructionfile.ParseExperimentDirectory;
import org.ucb.c5.constructionfile.model.Experiment;
import org.ucb.c5.constructionfile.model.Step;
import org.ucb.c5.labplanner.inventory.ParseInventory;
import org.ucb.c5.labplanner.inventory.model.Inventory;
import org.ucb.c5.labplanner.labpacket.model.LabPacket;

import java.util.List;

/**
 * High Level Function of LabPlanner -- it inputs an Experiment
 * and outputs a LabPacket.  Serialization of output and Deserialization
 * of inputs is not included.
 * 
 * TODO: add authors
 * @author J. Christopher Anderson
 */
public class PlanExperiment {
    
    public void initiate()  throws Exception {
        //TODO: write me, or delete comment if not needed
    }
    
    public LabPacket run(Experiment expt, Inventory inventory) throws Exception {
        // TODO: write me
        
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
    }

    private List<List<Step>> cleanGroupedSteps(List<List<Step>> groupedSteps, Experiment expt, Inventory inventory)
            throws Exception {
        // TODO PlanExperiment Main Logic Part 2
    }

    private LabPacket createAndBundleLabSheets(List<List<Step>> cleanedSteps, Inventory inventory) throws Exception {
        // TODO PlanExperiment Main Logic Part 3
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

package org.ucb.c5.labplanner;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.ucb.c5.labplanner.inventory.model.Sample.Concentration.miniprep;
import static org.ucb.c5.labplanner.inventory.model.Sample.Culture.library;

import org.ucb.c5.constructionfile.model.*;
import org.ucb.c5.labplanner.PlanExperiment;
import org.ucb.c5.labplanner.inventory.model.*;
import org.ucb.c5.labplanner.inventory.*;

import java.util.*;

public class Test_Overall {
    @Test
    public void testGroupStep_simple3() throws Exception {
        PlanExperiment test_groupstep = new PlanExperiment();
        test_groupstep.initiate();

        //Construction file creation 1
        List<Step> steps = new ArrayList();
        String pdtName = "pTarg-amilGFP";
        List<String> templates = new ArrayList();
        templates.add("pTargetF");
        steps.add(new PCR("ca4238", "ca4239", templates, "pcr1", 2023));
        List<Enzyme> enzymes = new ArrayList();
        enzymes.add(Enzyme.SpeI);
        enzymes.add(Enzyme.DpnI);
        steps.add(new Digestion("pcr1", enzymes, 1, "spedig"));
        List<String> digs = new ArrayList();
        digs.add("spedig");
        steps.add(new Ligation(digs, "lig1"));
        steps.add(new Transformation("lig1", "DH10B", Antibiotic.Spec, pdtName));
        ConstructionFile constf = new ConstructionFile(steps, "pdt", (Map)null);

        ArrayList<ConstructionFile> cfs_list = new ArrayList<ConstructionFile>();
        cfs_list.add(constf);

        //Construction file creation 2
        List<Step> steps1 = new ArrayList();
        String pdtName1 = "pTarg2";
        List<String> templates1 = new ArrayList();
        templates1.add("pTarget2");
        steps1.add(new PCR("ca2", "ca3", templates1, "pcr2", 2023));
        steps1.add(new Digestion("pcr2", enzymes, 1, "spedig2"));
        List<String> digs2 = new ArrayList();
        digs2.add("spedig2");
        steps1.add(new Ligation(digs, "lig2"));
        steps1.add(new Transformation("lig2", "DH10B", Antibiotic.Spec, pdtName1));
        ConstructionFile constf1 = new ConstructionFile(steps1, "pdt1", (Map)null);
        cfs_list.add(constf1);

        //Construction file creation 3
        List<Step> steps2 = new ArrayList();
        String pdtName2 = "pTarg3";
        List<String> templates2 = new ArrayList();
        templates2.add("pTarget3");
        steps2.add(new PCR("ca5", "ca6", templates2, "pcr3", 2023));
        steps2.add(new Digestion("pcr3", enzymes, 1, "spedig3"));
        List<String> digs3 = new ArrayList();
        digs3.add("spedig3");
        steps2.add(new Ligation(digs, "lig3"));
        steps2.add(new Transformation("lig3", "DH10B", Antibiotic.Spec, pdtName2));
        ConstructionFile constf2 = new ConstructionFile(steps2, "pdt2", (Map)null);
        cfs_list.add(constf2);
        
        // Collections.shuffle(cfs_list);

        Experiment randomExp = new Experiment("Experiment", cfs_list, null);
        List<List<PlanExperiment.HelperStep>> result = new ArrayList<>();

        try {
            result = test_groupstep.groupSimilarSteps(randomExp, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (List<PlanExperiment.HelperStep> list: result){
            System.out.println("====");
            for(PlanExperiment.HelperStep stepper : list){
                System.out.println(stepper.getOperation() + " | " + stepper.reagents + " > " + stepper.getProduct());
            }
            System.out.println("====");
        }

        /*
        //Inventory Stuff
        AddSampleToBox sample_adder = new AddSampleToBox();
        Sample[][] box_sample_array = new Sample[9][9];
        Box random1 = new Box("Box 1", "description", "refrigerator", box_sample_array);
        for (int i = 0; i <9; i++){ //Adds # of Samples to random1
            StringBuilder sb = new StringBuilder("Sample");
            StringBuilder cb = new StringBuilder("Construct");
            sb.append(i);
            cb.append(i);
            random1 = AddSampleToBox.run(new Sample(sb.toString(), sb.toString(), miniprep, cb.toString(), library, "clone"), random1, i, 0);
        }
        List<Box> box_list = new ArrayList<Box>();
        box_list.add(random1);


        Map<String, Set<Location>> constructToLocations = new HashMap<>();
        Set<Location> loc_set = new Set<>();
        for (int i = 0; i<9; i++){
            StringBuilder bb = new StringBuilder("Box");
            StringBuilder sb = new StringBuilder("Sample");
            bb.append(i);
            sb.append(i);
            loc_set.add(new Location(bb.toString(), 1, 1, sb.toString(), sb.toString()));
        }
        constructToLocations.put("Construct1", loc_set);
        //Location(String boxname, int row, int col, String label, String sidelabel)

        Map<Location, Sample.Concentration> locToConc = new HashMap<>();
        Map<Location, String> locToClone = new HashMap<>();
        Map<Location, Sample.Culture> locToCulture = new HashMap<>();

        Inventory randomInv = new Inventory(box_list, constructToLocations, locToConc,  locToClone,  locToCulture);*/


    }

    @Test
    public void testGroupStep_moredisordered() throws Exception {
        PlanExperiment test_groupstep = new PlanExperiment();
        test_groupstep.initiate();

        //Construction file creation 1
        List<Step> steps = new ArrayList();
        String pdtName = "pTarg-amilGFP";
        List<String> templates = new ArrayList();
        templates.add("pTargetF");
        steps.add(new PCR("ca4238", "ca4239", templates, "pcr1", 2023));
        List<Enzyme> enzymes = new ArrayList();
        enzymes.add(Enzyme.SpeI);
        enzymes.add(Enzyme.DpnI);
        steps.add(new Digestion("pcr_unrelated", enzymes, 1, "spedig"));
        List<String> digs = new ArrayList();
        digs.add("spedig");
        steps.add(new Ligation(digs, "lig1"));
        steps.add(new Transformation("lig1", "DH10B", Antibiotic.Spec, pdtName));
        ConstructionFile constf = new ConstructionFile(steps, "pdt", (Map)null);

        ArrayList<ConstructionFile> cfs_list = new ArrayList<ConstructionFile>();
        cfs_list.add(constf);

        //Construction file creation 2
        List<Step> steps1 = new ArrayList();
        String pdtName1 = "pTarg2";
        List<String> templates1 = new ArrayList();
        templates1.add("pTarget2");
        steps1.add(new PCR("ca2", "ca3", templates1, "pcr2", 2023));
        steps1.add(new Digestion("pcr2", enzymes, 1, "spedig2"));
        List<String> digs2 = new ArrayList();
        digs2.add("spedig2");
        steps1.add(new Ligation(digs, "lig2"));
        steps1.add(new Transformation("lig3", "DH10B", Antibiotic.Spec, pdtName1));
        ConstructionFile constf1 = new ConstructionFile(steps1, "pdt1", (Map)null);
        cfs_list.add(constf1);

        //Construction file creation 3
        List<Step> steps2 = new ArrayList();
        String pdtName2 = "pTarg3";
        List<String> templates2 = new ArrayList();
        templates2.add("pTarget3");
        steps2.add(new PCR("ca5", "ca6", templates2, "pcr3", 2023));
        steps2.add(new Digestion("pcr3", enzymes, 1, "spedig3"));
        List<String> digs3 = new ArrayList();
        digs3.add("spedig2");
        steps2.add(new Ligation(digs, "lig3"));
        steps2.add(new Transformation("lig3", "DH10B", Antibiotic.Spec, pdtName2));
        ConstructionFile constf2 = new ConstructionFile(steps2, "pdt2", (Map)null);
        cfs_list.add(constf2);

        Experiment randomExp = new Experiment("Experiment", cfs_list, null);
        List<List<PlanExperiment.HelperStep>> result = new ArrayList<>();

        try {
            result = test_groupstep.groupSimilarSteps(randomExp, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (List<PlanExperiment.HelperStep> list: result){
            System.out.println("====");
            for(PlanExperiment.HelperStep stepper : list){
                System.out.println(stepper.getOperation() + " | " + stepper.reagents + " > " + stepper.getProduct());
            }
            System.out.println("====");
        }

    }
}


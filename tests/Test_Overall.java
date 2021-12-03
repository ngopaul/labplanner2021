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
    public void Test(){
        PlanExperiment test_overall = new PlanExperiment();
        test_overall.initiate();

        //Construction file creation
        List<Step> steps = new ArrayList();
        String pdtName = "pTarg-amilGFP";
        List<String> templates = new ArrayList();
        templates.add("pTargetF");
        steps.add(new PCR("ca4238", "ca4239", templates, "ipcr", 2023));
        List<Enzyme> enzymes = new ArrayList();
        enzymes.add(Enzyme.SpeI);
        enzymes.add(Enzyme.DpnI);
        steps.add(new Digestion("pcr", enzymes, 1, "spedig"));
        List<String> digs = new ArrayList();
        digs.add("dig");
        steps.add(new Ligation(digs, "lig"));
        steps.add(new Transformation("lig", "DH10B", Antibiotic.Spec, pdtName));
        ConstructionFile constf = new ConstructionFile(steps, "pdt", (Map)null);

        ArrayList<ConstructionFile> cfs_list = new ArrayList<ConstructionFile>();
        cfs_list.add(constf);
        HashMap<String, Polynucleotide> dum_Map = new HashMap<String, Polynucleotide>();
        Experiment randomExp = new Experiment("Experiment", cfs_list, dum_Map);

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

        Inventory randomInv = new Inventory(box_list, constructToLocations, locToConc,  locToClone,  locToCulture);


    }
}


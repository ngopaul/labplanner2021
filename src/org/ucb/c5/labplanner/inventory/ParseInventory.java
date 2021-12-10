package org.ucb.c5.labplanner.inventory;

import org.ucb.c5.labplanner.inventory.model.Box;
import org.ucb.c5.labplanner.inventory.model.Inventory;
import org.ucb.c5.labplanner.inventory.model.Location;
import org.ucb.c5.labplanner.inventory.model.Sample;

import java.io.File;
import java.util.*;

/**
 * Reads in a folder contain TSV files representing Boxes.  It parses
 * Box objects, populates HashMaps of the Samples described, and returns
 * the an instance of Inventory.
 * 
 * @author Milo
 * @author J. Christopher Anderson
 */
public class ParseInventory {

    private final ParseBox parseBox = new ParseBox();

    public ParseInventory() {
    }

    public void initiate()  throws Exception {
        parseBox.initiate();
    }
    
    public Inventory run(String path) throws Exception {
        //get a list of file in the inventory
        File inventory = new File(path);
        File[] boxFiles = inventory.listFiles();
        if (boxFiles.length == 0) {
            throw new Exception("inventory file is empty");
        }
        //set up inventory requirements
        List<Box> boxes = new ArrayList<>();
        HashMap<String, Set<Location>> Loc = new HashMap<>();
        HashMap<Location, Sample.Concentration> Conc = new HashMap<>();
        HashMap<Location, String> Clone = new HashMap<>();
        HashMap<Location, Sample.Culture> Culture = new HashMap<>();
        //iterate through the inventory and populate the above hashmaps
        for (File i: boxFiles) {
            String a =  i.getAbsolutePath();
//            Box curBox = testBox(); //add testBox() instead of ParseBox.run(a) before running tests
            Box curBox = parseBox.run(a);
            boxes.add(curBox);
            int rowLen = curBox.getSamples().length;
            int colLen = curBox.getSamples()[0].length;
            //iterate through each section in the box
            for (int j = 0; j < rowLen; j++) {
                Sample[] Row = curBox.getSamples()[j];
                for (int k = 0; k < colLen; k++) {
                    Sample curSample = Row[k];
                    //ignore empty containers
                    if (curSample == null) {
                        continue;
                    }
                    Location curLoc = new Location(curBox.getName(), j + 1, k + 1, curSample.getLabel(), curSample.getSidelabel());
                    //populate location hash table
                    if (Loc.containsKey(curSample.getConstruct())) {
                        Loc.get(curSample.getConstruct()).add(curLoc);
                    } else {
                        HashSet sampleLocations = new HashSet();
                        sampleLocations.add(curLoc);
                        Loc.put(curSample.getConstruct(), sampleLocations);
                    }
                    //populate other hash tables
                    Conc.put(curLoc, curSample.getConcentration());
                    Clone.put(curLoc, curSample.getClone());
                    Culture.put(curLoc, curSample.getCulture());
                }
            }

        }
        return new Inventory(boxes, Loc, Conc, Clone, Culture);
    }

    //replace parseBox.run(a) with this to test the function without parse box
    private Box testBox() {
        Sample s1 = new Sample("s1", "s1", Sample.Concentration.miniprep, "con1", Sample.Culture.library, "clo1");
        Sample s2 = new Sample("s2", "s2", Sample.Concentration.miniprep, "con2", Sample.Culture.primary, "clo2");
        Sample s3 = new Sample("s3", "s3", Sample.Concentration.miniprep, "con3", Sample.Culture.primary, "clo3");
        Sample s4 = new Sample("s4", "s4", Sample.Concentration.miniprep, "con4", Sample.Culture.primary, "clo4");
        Sample s5 = new Sample("s5", "s5", Sample.Concentration.miniprep, "con5", Sample.Culture.primary, "clo5");
        Sample s6 = new Sample("s6", "s6", Sample.Concentration.dil20x, "con6", Sample.Culture.secondary, "clo6");
        Sample s7 = new Sample("s7", "s7", Sample.Concentration.dil20x, "con7", Sample.Culture.secondary, "clo7");
        Sample s8 = new Sample("s8", "s8", Sample.Concentration.dil20x, "con8", Sample.Culture.secondary, "clo8");
        Sample s9 = new Sample("s9", "s9", Sample.Concentration.dil20x, "con9", Sample.Culture.library, "clo9");
        Sample[] a = new Sample[]{s1, null, s2, s3};
        Sample[] b = new Sample[]{s4, s9, s5, s6};
        Sample[] c = new Sample[]{s7, s9, s8, s9};
        Sample[] d = new Sample[]{null, null, null, null};
        Sample[][] samples = new Sample[][]{a, b, c, d};
        return new Box("Box1", "a test box", "N/A", samples);
    }


    
    public static void main(String[] args) throws Exception {
        //Read in an example inventory
        ParseInventory parser = new ParseInventory();
        parser.initiate();
        Inventory inv = parser.run("C:\\Users\\Milo\\Documents\\134\\inventory-proj4\\src\\org\\ucb\\c5\\labplanner\\LabPlannerData\\inventory");
        
        //Print out contents of inventory
        List<Box> boxes = inv.getBoxes();
        for(Box abox : boxes) {
            System.out.println(abox.getName());
        }
    }
}

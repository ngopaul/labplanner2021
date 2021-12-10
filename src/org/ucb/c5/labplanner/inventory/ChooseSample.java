package org.ucb.c5.labplanner.inventory;

import org.ucb.c5.labplanner.inventory.model.Inventory;
import org.ucb.c5.labplanner.inventory.model.Location;
import org.ucb.c5.labplanner.inventory.model.Sample.Concentration;

import java.util.Map;
import java.util.Set;

import static org.ucb.c5.labplanner.inventory.model.Sample.Concentration.dil20x;

/**
 * Searches an Inventory for a Sample matching the specification
 * and returns a Location to the Sample
 *
 * @author Carol Gao carolgyz
 * @author J. Christopher Anderson
 */
public class ChooseSample {
    /**
     * @param inv           The Inventory being searched
     * @param construct     The name of the DNA, ie pLYC33K
     * @param clone         The name of the clone, or null if unstated
     * @param conc          The desired concentration
     * @return  Returns the location of the chosen sample, or null if none exists
     * @throws Exception
     */
    public Location run(Inventory inv, String construct, String clone, Concentration conc) throws Exception {


        Map<String, Set<Location>> constructToLoc = inv.getConstructToLocations();
        if(!constructToLoc.containsKey(construct)){
            throw new IllegalArgumentException("the given construct does not exist in the inventory provided");
        }
        Map<Location, String> locToClone = inv.getLocToClone();
        Map<Location, Concentration> locToConc = inv.getLocToConc();


        Location ret = null;
        Set<Location> locations = constructToLoc.get(construct);


        for (Location location : locations){
            if (check_clone(locToClone,clone,location) && check_conc(locToConc, conc, location)) {
                ret = location;
                break;
            }
        }

        if (ret == null){
            throw new IllegalArgumentException("No construct sample with the given concentration found in the inventory");
        }
            return ret;
    }

    private boolean check_clone(Map<Location, String> loc, String clone, Location location){
        if (clone == "" || loc.get(location) == clone){
            return true;
        }
        else{
            return false;
        }
    }

    private boolean check_conc(Map<Location, Concentration> map, Concentration concentration, Location location){
        if (concentration == null || map.get(location) == concentration){
            return true;
        }
        else{
            return false;
        }
    }
    public static void main(String[] args) throws Exception {
        //Read in an example inventory
        ParseInventory parser = new ParseInventory();
        parser.initiate();
        Inventory inv = parser.run("C:\\Users\\Milo\\Documents\\134\\inventory-proj4\\src\\org\\ucb\\c5\\labplanner\\LabPlannerData\\inventory");

        ChooseSample chooser = new ChooseSample();
        Location outcome = chooser.run(inv,"p20N31","",dil20x);
        System.out.println(outcome.getRow());
        System.out.println(outcome.getCol());

    }
}
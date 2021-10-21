package org.ucb.c5.labplanner.inventory;

import org.ucb.c5.labplanner.inventory.model.Inventory;
import org.ucb.c5.labplanner.inventory.model.Location;
import org.ucb.c5.labplanner.inventory.model.Sample.Concentration;

/**
 * Searches an Inventory for a Sample matching the specification
 * and returns a Location to the Sample
 * 
 * TODO: add authors
 * @author J. Christopher Anderson
 */
public class ChooseSample {
    
    public void initiate()  throws Exception {
        //TODO: write me, or delete comment if not needed
    }
    
    /**
     * @param inv           The Inventory being searched
     * @param construct     The name of the DNA, ie pLYC33K
     * @param clone         The name of the clone, or null if unstated
     * @param conc          The desired concentration
     * @return  Returns the location of the chosen sample, or null if none exists
     * @throws Exception 
     */
    public Location run(Inventory inv, String construct, String clone, Concentration conc) throws Exception {
        //TODO: write me
        return null;
    }
    
    public static void main(String[] args) throws Exception {
        //Read in an example inventory
        ParseInventory parser = new ParseInventory();
        parser.initiate();
        Inventory inv = parser.run("example/inventory");   //TODO:  MAKE RESOURCE VERSION
        
        //Print out contents of inventory
        ChooseSample chooser = new ChooseSample();
        chooser.initiate();
        Location result = chooser.run(inv, "aname", "aclone", Concentration.miniprep);
    }
}

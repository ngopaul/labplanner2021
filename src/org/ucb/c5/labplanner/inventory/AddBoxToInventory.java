package org.ucb.c5.labplanner.inventory;

import org.ucb.c5.labplanner.inventory.model.Box;
import org.ucb.c5.labplanner.inventory.model.Inventory;
import org.ucb.c5.labplanner.inventory.model.Location;
import org.ucb.c5.labplanner.inventory.model.Sample;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Inputs a box to be inserted into a provided inventory.  Returns
 * a new Inventory instance with the Box inside it.  This is also
 * responsible for updating the lookup tables in the Inventory.
 * 
 * @author J. Christopher Anderson
 */
public class AddBoxToInventory {
    
    public void initiate() throws Exception {};
    
    /**
     * 
     * @param abox  The box being inserted
     * @param inv   The inventory being modified
     * @return
     * @throws Exception 
     */
    public Inventory run(Box abox, Inventory inv) throws Exception {
        // A box has a name, description, location, and samples (Sample[][])
        // An inventory has:
        //    private final List<Box> boxes;
        //    private final Map<String, Set<Location>> constructToLocations;   //Quick lookup of samples by construct name
        //    private final Map<Location, Concentration> locToConc;   //Quick lookup by Concentration
        //    private final Map<Location, String> locToClone;   //Quick lookup by Clone
        //    private final Map<Location, Culture> locToCulture;   //Quick lookup by Culture
        // A location has
        //    private final String boxname;    //The name of the box a sample is in
        //    private final int row;   //The row within the box, starting with 0
        //    private final int col;   //The column within the box, starting with 0
        //    private final String label;   //What's written on the top of the tube
        //    private final String sidelabel; //What's written on the side of the tube
        // A sample has
        //    private final String label;   //What's written on the top of the tube
        //    private final String sidelabel;  //What's written on the side of the tube
        //    private final Sample.Concentration concentration;  //The amount or type of DNA present
        //    private final String construct;  //The name of the DNA matching the construction file
        //    private final Sample.Culture culture;  //For minipreps only, how many rounds of isolation
        //    private final String clone;   //Which isolate of several of the same construct

        // Get copy of inventory
        Inventory newInventory = inv.getCopy();

        // Add box to list of boxes
        newInventory.getBoxes().add(abox);

        // iterate through locations
        for (int row = 0; row < abox.getSamples().length; row++) {
            for (int col = 0; col < abox.getSamples()[0].length; col++) {
                Sample sample = abox.getSamples()[row][col];
                if (sample == null) continue;

                String label = sample.getLabel();
                String sideLabel = sample.getSidelabel();
                String construct = sample.getConstruct();

                Location currentLocation = new Location(abox.getName(), row, col, label, sideLabel);

                // Add to constructToLocations
                if (newInventory.getConstructToLocations().containsKey(construct)) {
                    Set<Location> locationSet = newInventory.getConstructToLocations().get(construct);
                    locationSet.add(currentLocation);
                } else {
                    newInventory.getConstructToLocations().put(construct, new HashSet<>(Arrays.asList(currentLocation)));
                }

                // Add to locToConc, locToClone, locToCulture
                newInventory.getLocToConc().put(currentLocation, sample.getConcentration());
                newInventory.getLocToClone().put(currentLocation, sample.getClone());
                newInventory.getLocToCulture().put(currentLocation, sample.getCulture());
            }
        }

        return newInventory;
    }
    
    public static void main(String[] args) {
         //TODO: Create an example showing usage of run
    }
}

package org.ucb.c5.labplanner.inventory.model;

import java.util.*;

import org.ucb.c5.labplanner.inventory.model.Sample.Concentration;
import org.ucb.c5.labplanner.inventory.model.Sample.Culture;

/**
 * Representation of the complete set of boxes and samples.  Information
 * is doubly represented to facilitate both iteration and lookup.
 * 
 * @author J. Christopher Anderson
 */
public class Inventory {
    private final List<Box> boxes;  // all the boxes in the inventory
    private final Map<String, Set<Location>> constructToLocations;   //Quick lookup of samples by construct name
    private final Map<Location, Concentration> locToConc;   //Quick lookup by Concentration
    private final Map<Location, String> locToClone;   //Quick lookup by Clone
    private final Map<Location, Culture> locToCulture;   //Quick lookup by Culture

    public Inventory(List<Box> boxes, Map<String, Set<Location>> constructToLocations, Map<Location, Concentration> locToConc, Map<Location, String> locToClone, Map<Location, Culture> locToCulture) {
        this.boxes = boxes;
        this.constructToLocations = constructToLocations;
        this.locToConc = locToConc;
        this.locToClone = locToClone;
        this.locToCulture = locToCulture;
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    public Map<String, Set<Location>> getConstructToLocations() {
        return constructToLocations;
    }

    public Map<Location, Concentration> getLocToConc() {
        return locToConc;
    }

    public Map<Location, String> getLocToClone() {
        return locToClone;
    }

    public Map<Location, Culture> getLocToCulture() {
        return locToCulture;
    }

    public Inventory getCopy() {
        List<Box> boxesCopy = new ArrayList<>();
        Map<String, Set<Location>> constructToLocationsCopy = new HashMap<>();
        Map<Location, Concentration> locToConcCopy = new HashMap<>();
        Map<Location, String> locToCloneCopy = new HashMap<>();
        Map<Location, Culture> locToCultureCopy = new HashMap<>();

        for (int i = 0; i < this.boxes.size(); i++) {
            boxesCopy.add(this.boxes.get(i).getCopy());
        }
        for (String key : this.constructToLocations.keySet()) {
            Set locationSetCopy = new HashSet();
            for (Location location : this.constructToLocations.get(key)) {
                locationSetCopy.add(location.getCopy());
            }
            constructToLocationsCopy.put(key, locationSetCopy);
        }
        for (Location location : this.locToConc.keySet()) {
            locToConcCopy.put(location.getCopy(), this.locToConc.get(location));
        }
        for (Location location : this.locToClone.keySet()) {
            locToCloneCopy.put(location.getCopy(), this.locToClone.get(location));
        }
        for (Location location : this.locToCulture.keySet()) {
            locToCultureCopy.put(location.getCopy(), this.locToCulture.get(location));
        }
        return new Inventory(boxesCopy, constructToLocationsCopy, locToConcCopy, locToCloneCopy, locToCultureCopy);
    }
}

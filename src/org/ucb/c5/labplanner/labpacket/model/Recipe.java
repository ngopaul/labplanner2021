package org.ucb.c5.labplanner.labpacket.model;

import java.util.Map;

/**
 * The ingredients for setting up a DNA modification reaction, such
 * as a PCR, digest, or the like
 * 
 * A mastermix is premixed reagents that are then dispensed to individual
 * reactions.  One of the Reagents is itself mastermix for use in the 
 * reaction field.
 * 
 * The mastermix may be null if none is needed
 * 
 * @author J. Christopher Anderson
 */
public class Recipe {
    private final Map<Reagent, Double>  mastermix;  //The reagent and volume in uL
    private final Map<Reagent, Double>  reaction;  //The reagent and volume in uL

    public Recipe(Map<Reagent, Double> mastermix, Map<Reagent, Double> reaction) {
        this.mastermix = mastermix;
        this.reaction = reaction;
    }

    public Map<Reagent, Double> getMastermix() {
        return mastermix;
    }

    public Map<Reagent, Double> getReaction() {
        return reaction;
    }
}

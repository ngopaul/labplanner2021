package org.ucb.c5.labplanner.labpacket;

import org.ucb.c5.constructionfile.model.Step;
import org.ucb.c5.labplanner.inventory.model.Location;
import org.ucb.c5.labplanner.labpacket.model.LabSheet;
import org.ucb.c5.labplanner.labpacket.model.Recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LabSheetFactory {
    public LabSheetFactory() {

    }

    public void initiate() {
        // TODO
    }

    public LabSheet run(List<Step> steps, List<Location> sources, List<Location> destinations) {
        // TODO
//        private final String title;    //What is displayed at the top of the LabSheet
//        private final List<Step> steps;  //ConstructionFile Steps executed on this sheet
//        private final List<Location> sources; //Samples to retrieve from Boxes
//        private final List<Location> destinations;  //Samples to add new to Boxes
//        private final String program;  //Program to run on a thermocycler
//        private final String protocol;  //Specific protocol to use, ie PrimeStar or Phusion
//        private final String instrument; //Which instrument to put reactions/plates into
//        private final List<String> notes;  //Any notes to display as alerts
//        private final Recipe reaction;  //Ingredients for setting up the reaction

        String title = "Dummy Title";
        // steps already passed in
        // sources passed in
        // destinations passed in
        String program = "dummy program";
        String protocol = "dummy protocol";
        String instrument = "dummy instrument";
        List<String> notes = new ArrayList<>(Arrays.asList("Dummy Note"));
        Recipe reaction = new Recipe(new HashMap<>(), new HashMap<>());

        return new LabSheet(title, steps, sources, destinations, program, protocol, instrument, notes, reaction);
    }
}

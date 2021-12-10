package org.ucb.c5.labplanner.inventory;

import org.ucb.c5.labplanner.inventory.model.Box;
import org.ucb.c5.labplanner.inventory.model.Sample;

// TODO is this broken?
//import org.ucb.c5.labplanner.utils.FileUtils;
import org.ucb.c5.utils.FileUtils;

import java.util.*;

/**
 * Reads in a TSV file representing a Box of Samples. It reads in the file and
 * returns and instance of Box
 *
 * TODO: add authors
 *
 * @author Baden Dense bdense
 * @author J. Christopher Anderson
 */
public class ParseBox {

    private final AddSampleToBox addSampleToBox = new AddSampleToBox();

    public void initiate() throws Exception {
        addSampleToBox.initiate();
    }

    public Box run(String text) throws Exception {
        String data = FileUtils.readFile(text);
        if (data.isEmpty()) {
            throw new Exception("data file is empty");
        }

        String[] data_lines = data.split("\\r|\\r?\\n");

        // get the dimensions of the sample space
        int[] dimensions = getBoxDimensions(data_lines);
        int number_of_rows = dimensions[0];
        int number_of_columns = dimensions[1];

        // iterate through the lines at the top, parse them if they have data, else continue until we get to the data
        String name = null;
        String description = null;
        String location = null;
        int i = 0;
        Map<List<Integer>, String> address_to_name_map = new HashMap<>();
        Map<List<Integer>, String> address_to_side_label_map = new HashMap<>();
        Map<List<Integer>, String> address_to_construct_map = new HashMap<>();
        Map<List<Integer>, String> address_to_concentration_map = new HashMap<>();
        Map<List<Integer>, String> address_to_culture_map = new HashMap<>();
        Map<List<Integer>, String> address_to_clone_map = new HashMap<>();

        for (int lineCounter = 0; lineCounter < data_lines.length; lineCounter++) {
            String currLine = data_lines[lineCounter];
            if (currLine.startsWith(">name")) {
                name = currLine.split("\t")[1];
            }
            if (currLine.startsWith(">description")) {
                description = currLine.split("\t")[1];
            }
            if (currLine.startsWith(">location")) {
                location = currLine.split("\t")[1];
            }
            if (currLine.startsWith(">>label")) {
                lineCounter++;
                currLine = data_lines[lineCounter];
                for (i = 0; i < number_of_rows; i++) {
                    String[] this_data_row = data_lines[i + lineCounter].split("\t", -1);
                    for (int j = 0; j < number_of_columns; j++) {
                        String sample_name = this_data_row[j + 1];
                        List<Integer> address = new ArrayList<>();
                        address.add(i);
                        address.add(j);
                        if (!sample_name.isEmpty()) {
                            address_to_name_map.put(address, sample_name);
                        }
                    }
                }
                lineCounter += i;
            }
            if (currLine.startsWith(">>side_label")) {
                lineCounter++;
                currLine = data_lines[lineCounter];
                for (i = 0; i < number_of_rows; i++) {
                    String[] this_data_row = data_lines[i + lineCounter].split("\t", -1);
                    for (int j = 0; j < number_of_columns; j++) {
                        String side_label = this_data_row[j + 1];
                        List<Integer> address = new ArrayList<>();
                        address.add(i);
                        address.add(j);
                        if (!side_label.isEmpty()) {
                            address_to_side_label_map.put(address, side_label);
                        }
                    }
                }
                lineCounter += i;
            }
            if (currLine.startsWith(">>construct")) {
                lineCounter++;
                currLine = data_lines[lineCounter];
                for (i = 0; i < number_of_rows; i++) {
                    String[] this_data_row = data_lines[i + lineCounter].split("\t", -1);
                    // fill in unfilled rows at the end
                    if (this_data_row.length < number_of_columns + 1) {
                        int original_length = this_data_row.length;
                        this_data_row = Arrays.copyOf(this_data_row, number_of_columns + 1);
                        for (int j = original_length; j < number_of_columns + 1; j++) {
                            this_data_row[j] = "";
                        }
                    }
                    for (int j = 0; j < number_of_columns; j++) {
                        String construct = this_data_row[j + 1];
                        List<Integer> address = new ArrayList<>();
                        address.add(i);
                        address.add(j);
                        if (!construct.isEmpty()) {
                            address_to_construct_map.put(address, construct);
                        }
                    }
                }
                lineCounter += i;
            }
            if (currLine.startsWith(">>concentration")) {
                lineCounter++;
                currLine = data_lines[lineCounter];
                for (i = 0; i < number_of_rows; i++) {
                    String[] this_data_row = data_lines[i + lineCounter].split("\t", -1);
                    for (int j = 0; j < number_of_columns; j++) {
                        String concentration = this_data_row[j + 1];
                        List<Integer> address = new ArrayList<>();
                        address.add(i);
                        address.add(j);
                        if (!concentration.isEmpty()) {
                            address_to_concentration_map.put(address, concentration);
                        }
                    }
                }
                lineCounter += i;
            }
            if (currLine.startsWith(">>culture")) {
                lineCounter++;
                currLine = data_lines[lineCounter];
                for (i = 0; i < number_of_rows; i++) {
                    String[] this_data_row = data_lines[i + lineCounter].split("\t", -1);
                    for (int j = 0; j < number_of_columns; j++) {
                        String culture = this_data_row[j + 1];
                        List<Integer> address = new ArrayList<>();
                        address.add(i);
                        address.add(j);
                        if (!culture.isEmpty()) {
                            address_to_culture_map.put(address, culture);
                        }
                    }
                }
                lineCounter += i;
            }
            if (currLine.startsWith(">>clone")) {
                lineCounter++;
                currLine = data_lines[lineCounter];
                for (i = 0; i < number_of_rows; i++) {
                    String[] this_data_row = data_lines[i + lineCounter].split("\t", -1);
                    for (int j = 0; j < number_of_columns; j++) {
                        String clone = this_data_row[j + 1];
                        List<Integer> address = new ArrayList<>();
                        address.add(i);
                        address.add(j);
                        if (!clone.isEmpty()) {
                            address_to_clone_map.put(address, clone);
                        }
                    }
                }
            }
        }

        // next, make the samples and return the box full of them
        Sample[][] samples = new Sample[number_of_rows][number_of_columns];
        Box out = new Box(name, description, location, samples);

        for (List<Integer> address : address_to_name_map.keySet()) {
            String given_concentration = address_to_concentration_map.get(address);
            Sample.Concentration concentration;
            if (given_concentration != null) {
                switch (given_concentration) {
                    case "100uM":
                        concentration = Sample.Concentration.uM100;
                        break;
                    case "10uM":
                        concentration = Sample.Concentration.uM10;
                        break;
                    case "2.66uM":
                        concentration = Sample.Concentration.uM266;
                        break;
                    case "miniprep":
                        concentration = Sample.Concentration.miniprep;
                        break;
                    case "zymo":
                        concentration = Sample.Concentration.zymo;
                        break;
                    case "dil20x":
                        concentration = Sample.Concentration.dil20x;
                        break;
                    case "gene":
                        concentration = Sample.Concentration.gene;
                    default:
                        throw new IllegalStateException("Unexpected concentration: " + given_concentration);
                }
            } else {
                concentration = null;
            }
            String given_culture = address_to_culture_map.get(address);
            Sample.Culture culture;
            if (given_culture != null) {
                switch (given_culture) {
                    case "primary":
                        culture = Sample.Culture.primary;
                        break;
                    case "secondary":
                        culture = Sample.Culture.secondary;
                        break;
                    case "tertiary":
                        culture = Sample.Culture.tertiary;
                        break;
                    case "library":
                        culture = Sample.Culture.library;
                        break;
                    default:
                        throw new IllegalStateException("Unexpected culture:" + given_culture);
                }
            } else {
                culture = null;
            }
            Sample sample = new Sample(
                    address_to_name_map.get(address),
                    address_to_side_label_map.get(address),
                    concentration,
                    address_to_construct_map.get(address),
                    culture,
                    address_to_clone_map.get(address));
            out = addSampleToBox.run(sample, out, address.get(0), address.get(1));
        }
        return out;
    }

    private int[] getBoxDimensions(String[] boxLines) {
        // assuming TSV file format is standard, data headers start on the row that starts with '>>label'
        int lineCounter = 0;
        String currLine = boxLines[lineCounter];
        while (!currLine.startsWith(">>label")) {
            lineCounter++;
            currLine = boxLines[lineCounter];
        }
        String[] column_headers = currLine.split("\t");
        int col = column_headers.length - 1;

        // next are rows - there are as many rows of data as there are lines until the next blank space
        lineCounter++;
        currLine = boxLines[lineCounter];
        int row = 0;
        while (!currLine.startsWith(">>")) {
            row++;
            lineCounter++;
            currLine = boxLines[lineCounter];
        }
        row--;
        return new int[]{row, col};
    }

    public static void main(String[] args) throws Exception {
        //Read in an example inventory
        ParseBox parser = new ParseBox();
        parser.initiate();
        Box abox = parser.run("../../../Downloads/LabPlannerData/inventory/Box_Lyc6.txt");

        //Print out contents of box
        System.out.println(abox.getName());
        System.out.println(abox.getDescription());
        System.out.println(abox.getLocation());
        System.out.println(Arrays.toString(abox.getSamples()));
    }
}

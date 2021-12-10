package org.ucb.c5.labplanner.inventory;

import org.ucb.c5.labplanner.inventory.model.Box;
import org.ucb.c5.labplanner.inventory.model.Sample;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


/**
 * Serialize a Box to human-readable text file
 *
 * @author Dexter Lai dexterlai@berkeley.edu
 * @author J. Christopher Anderson
 */
public class SerializeBox {

    /**
     * Stores whatever is in the StringBuilder into the BufferedWriter and makes a new line.
     * Sets the StringBuilder to length 0 to restart it
     * @param sb StringBuilder with some string in it
     * @param bw The BufferedWriter for the file
     * @throws Exception
     */
    private void write(StringBuilder sb, BufferedWriter bw) throws Exception {
        bw.write(sb.toString());
        bw.newLine();
        sb.setLength(0);
    }
    /**
     *
     * @param abox The Box to be serialized
     * @param path The path and filename for the destination file
     * @throws Exception
     */
    public void run(Box abox, String path) throws Exception {
        if (abox == null|| path == null) {
            throw new IllegalArgumentException("There is either no box or no file path available.");
        }
        File filePath = new File(path);
        filePath.createNewFile();
        FileWriter fw = new FileWriter(filePath);
        BufferedWriter bw = new BufferedWriter(fw);
        StringBuilder sb = new StringBuilder();
        sb.append("The box's name is: ");
        sb.append(abox.getName());
        write(sb, bw);
        sb.append("The box's description is: ");
        sb.append(abox.getDescription());
        write(sb, bw);
        sb.append("The box's location is: ");
        sb.append(abox.getLocation());
        write(sb, bw);
        bw.newLine();
        int i = 0;
        for (Sample[] s : abox.getSamples()) {
            for (Sample a : s) {
                if (a == null) {
                    continue;
                }
                i += 1;
                sb.append("Sample " );
                sb.append(i);
                write(sb, bw);
                sb.append(" The label is: ");
                sb.append(a.getLabel());
                write(sb, bw);
                sb.append(" The side label is: ");
                sb.append(a.getSidelabel());
                write(sb, bw);
                sb.append(" The concentration is: ");
                sb.append(a.getConcentration());
                write(sb, bw);
                sb.append(" The construct is: ");
                sb.append(a.getConstruct());
                write(sb, bw);
                sb.append(" The culture is: ");
                sb.append(a.getCulture());
                write(sb, bw);
                sb.append(" The clone is: ");
                sb.append(a.getClone());
                write(sb, bw);
                bw.newLine();
            }
        }
        bw.flush();
    }

    public static void main(String[] args) throws Exception {
        //Create an example instance of Box
        Sample[][] sampleBox = new Sample[1][1];
        Sample example = new Sample("Gucci", "Gang", Sample.Concentration.miniprep, "Yo mama", Sample.Culture.library, "So fat");
        sampleBox[0][0] = example;
        Box abox = new Box("She needed two lines of code", "To tell the joke", "Haha",  sampleBox);
        String abspath = "boxname.txt";
        //Serialize the box
        SerializeBox parser = new SerializeBox();
        parser.run(abox, abspath);
    }
}

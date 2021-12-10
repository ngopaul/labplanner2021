/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ucb.c5.labplanner.inventory;
import org.ucb.c5.labplanner.inventory.model.*;

import java.util.Arrays;

import static org.ucb.c5.labplanner.inventory.model.Sample.Concentration.*;
import static org.ucb.c5.labplanner.inventory.model.Sample.Culture.*;

/**
 * Adds a sample to a box.  In the process, it checks that the position in the
 * Box is a valid position and unoccupied.  Since the inventory is immutable,
 * this returns a new Instance of Box containing the new Sample.
 *
 * @author Amy Wang
 * @author J. Christopher Anderson
 */
public class AddSampleToBox {

    public void initiate() {

    }

    /**
     * @param sample    The sample being inserted
     * @param box       The Box it is being inserted into
     * @param row       The row in which it is inserted
     * @param col       The column in which it is inserted
     * @return          The new instance of Box containing the new Sample
     * @throws Exception 
     */
    public Box run(Sample sample, Box box, int row, int col) throws Exception {
        //TODO:
        //1. Check that box.get_Samples() is a valid position and is unoccupied- otherwise throw exception
        //2. If it's valid and unoccupied, create new instance of Box that's copy of the input and insert Sample
        //3. Return new instance of the Box with the new sample
        //Need to create examples using run and a unit test to check edge cases (input row/column isn't valid, position is occupied, sample is empty, etc.)

        if (row > box.getSamples().length | col > box.getSamples()[0].length) {
            throw new ArrayIndexOutOfBoundsException ("input row or col is out of bounds");
        }
        if (box.getSamples()[row][col]!=null) {
            throw new Exception("location is occupied");
        }
        if (sample == null) {
            throw new Exception("sample is empty");
        }

        //add sample and create copy of box
        Sample[][] samples_copy = box.getSamples();
        samples_copy[row][col] = sample;
        Box box_copy = new Box(box.getName(), box.getDescription(), box.getLocation(), samples_copy);

        return box_copy;
    }
    
    public static void main(String[] args) throws Exception {
        //TODO: Create an example showing usage of run
        //add sample to empty box
        Sample testSample1 = new Sample("testLabel1", "testSideLabel1", miniprep, "testConstruct1", library, "testClone1");

        Sample[][] samples = new Sample[9][9];
        Box emptyBox = new Box("emptyBox", "empty box created for test", "testLoc1", samples);
        AddSampleToBox adder = new AddSampleToBox();
        Box exampleBox = adder.run(testSample1, emptyBox, 0, 0);
        System.out.println(Arrays.deepToString(exampleBox.getSamples()));
    }
}

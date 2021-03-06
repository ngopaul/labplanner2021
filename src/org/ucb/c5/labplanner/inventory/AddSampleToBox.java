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

    public void initiate() throws Exception {}

    /**
     * @param sample    The sample being inserted
     * @param box       The Box it is being inserted into
     * @param row       The row in which it is inserted
     * @param col       The column in which it is inserted
     * @return          The new instance of Box containing the new Sample
     * @throws Exception
     */
    public Box run(Sample sample, Box box, int row, int col) throws Exception {
        Box boxCopy = box.getCopy();
        if (boxCopy.getSamples()[row][col] != null) {
            throw new Exception("Location is not empty");
        }
        boxCopy.getSamples()[row][col] = sample;
        return boxCopy;
    }

    public static void main(String[] args) throws Exception {
        //TODO: Create an example showing usage of run
        Sample testSample1 = new Sample("testLabel1", "testSideLabel1", gene, "testConstruct1", Sample.Culture.primary, "testClone1");
        Sample testSample2 = new Sample("testLabel2", "testSideLabel2", zymo, "testConstruct2", Sample.Culture.secondary, "testClone2");

        Sample[][] samples = new Sample[9][9];
        Box emptyBox = new Box("testBox1", "Test empty box", "testLoc1", samples);
        AddSampleToBox adder = new AddSampleToBox();
        Box testBox = adder.run(testSample1, emptyBox, 1, 4);
        assert(testBox.getSamples()[1][4]!=null);

        Box testBox2 = adder.run(testSample2, testBox, 2, 6);
        assert(testBox2.getSamples()[2][6]!=null);
    }
}

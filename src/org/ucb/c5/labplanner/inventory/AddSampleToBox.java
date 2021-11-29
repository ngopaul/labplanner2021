/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ucb.c5.labplanner.inventory;

import org.ucb.c5.labplanner.inventory.model.Box;
import org.ucb.c5.labplanner.inventory.model.Sample;

/**
 * Adds a sample to a box.  In the process, it checks that the position in the
 * Box is a valid position and unoccupied.  Since the inventory is immutable,
 * this returns a new Instance of Box containing the new Sample.
 * 
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
        //TODO:  write me
        if (box.getSamples()[row][col] != null) {
            throw new Exception("Location is not empty");
        }
        Sample[][] sample_copy = box.getSamples();
        sample_copy[row][col] = sample;
        Box boxcopy = new Box(box.getName(), box.getDescription(), box.getLocation(), sample_copy);
        return boxcopy;
    }
    
    public static void main(String[] args) {
        //TODO: Create an example showing usage of run
        Sample testSample1 = new Sample("testLabel1", "testSideLabel1", gene, "testConstruct1", Sample.Culture.primary, "testClone1");
        Sample testSample2 = new Sample("testLabel2", "testSideLabel2", zymo, "testConstruct2", Sample.Culture.secondary, "testClone2");

        Sample[][] samples = new Sample[9][9];
        Box emptyBox = new Box("testBox1", "Test empty box", "testLoc1", samples);
        AddSampleToBox adder = new AddSampleToBox();
        Box testBox = adder.run(testSample1, emptyBox, 1, 4);
        assert(testBox.getSamples()[0][0]!=null);

        Box testBox2 = adder.run(testSample2, testBox, 2, 6);
        assert(testBox2.getSamples()[9][9]!=null);
    }
}

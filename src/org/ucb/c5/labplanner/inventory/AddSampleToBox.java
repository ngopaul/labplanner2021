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
        return null;
    }
    
    public static void main(String[] args) {
        //TODO: Create an example showing usage of run
    }
}

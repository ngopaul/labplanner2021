package org.ucb.c5.labplanner.inventory;

import org.ucb.c5.labplanner.inventory.model.Box;

/**
 * Serialize a Box to human-readable text file
 *
 * TODO: add authors
 * @author J. Christopher Anderson
 */
public class SerializeBox {

    public void initiate() throws Exception {
        //TODO: write me, or delete comment if not needed
    }

    /**
     *
     * @param abox The Box to be serialized
     * @param abspath The path and filename for the destination file
     * @throws Exception
     */
    public void run(Box abox, String path) throws Exception {
        //TODO: write me
    }

    public static void main(String[] args) throws Exception {
        //Create an example instance of Box
        Box abox = null;  //TODO:  create an instance
        String abspath = "insertpath/boxname.txt"; //TODO:  put in a path
        //Serialize the box
        SerializeBox parser = new SerializeBox();
        parser.initiate();
        parser.run(abox, abspath);
    }
}

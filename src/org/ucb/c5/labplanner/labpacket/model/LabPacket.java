package org.ucb.c5.labplanner.labpacket.model;

import java.util.List;
import org.ucb.c5.labplanner.inventory.model.Box;

/**
 * Wrapper class for the bolus of LabSheets
 * and modified Box files resulting from
 * simulation of an entire Experiment
 * 
 * @author J. Christopher Anderson
 */
public class LabPacket {
    private final List<LabSheet> sheets;
    private final List<Box> modifiedboxes;

    public LabPacket(List<LabSheet> sheets, List<Box> modifiedboxes) {
        this.sheets = sheets;
        this.modifiedboxes = modifiedboxes;
    }

    public List<LabSheet> getSheets() {
        return sheets;
    }

    public List<Box> getModifiedboxes() {
        return modifiedboxes;
    }
    
    
}

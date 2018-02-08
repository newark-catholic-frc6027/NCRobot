package org.usfirst.frc.team6027.robot.field;

public class Plate {

    private boolean assignedToUs = false;
    
    protected Plate(boolean assignedToUs) {
        this.assignedToUs = assignedToUs;
    }
    
    public boolean isAssignedToUs() {
        return this.assignedToUs;
    }
    
    public boolean isAssignedToThem() {
        return ! this.isAssignedToUs();
    }
}

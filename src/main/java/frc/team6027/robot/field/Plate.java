package frc.team6027.robot.field;

public class Plate {

    public static final double PLATE_WIDTH_INCHES = 48.0;
    public static final double PLATE_DEPTH_INCHES = 36.0;
    
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

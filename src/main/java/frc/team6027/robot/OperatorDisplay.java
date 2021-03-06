package frc.team6027.robot;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.command.Command;

/**
 * An interface used to define the operations necessary for displaying information and feedback to the robot human 
 * driver.  Using this interface allows us to change the underlying implementation of how information is displayed
 * to the robot driver.
 */
public interface OperatorDisplay {
	public static final String ELEVATOR_MAX = "Elevator MAX?";
    public static final String ELEVATOR_MIN = "Elevator MIN?";
    public static final String MAST_FORWARD_MAX = "Mast Forward MAX?";
    public static final String MAST_BACKWARD_MAX = "Mast Backward MAX?";
    
    
    public enum ChooserName {
        Position,
        Scenario("Auto Scenario"),
        DontDoOption("Dont Do Option");
        
        private String displayName;
        
        private ChooserName() {
        }
        
        private ChooserName(String displayName) {
            this.displayName = displayName;
        }
        
        public String displayName() {
            if (this.displayName == null ) {
                return this.name();
            } else {
                return this.displayName;
            }
        }
        
    }



    /**
     * Sets a new numeric value on the display.
     * @param fieldName The name of the field whose value should be changed.
     * @param value The new field value.
     */
//    public void setNumericFieldValue(String fieldName, Double numValue);

//    public void setNumericFieldValue(String fieldName, int numValue);

    void setData(Sendable sendable);
    void setData(String name, Command command);

    void setFieldValue(String fieldName, Double numValue);

    void setFieldValue(String fieldName, int numValue);

    void setFieldValue(String fieldName, String value);

    void setFieldValue(String fieldName, boolean value);


    void registerAutoScenario(String displayName);
    void registerAutoScenario(String displayName, boolean isDefaultCommand);
        
    String getSelectedAutoScenario();
    
    Integer getSelectedPosition();

    
}

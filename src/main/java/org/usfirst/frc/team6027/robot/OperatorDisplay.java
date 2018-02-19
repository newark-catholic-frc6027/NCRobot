package org.usfirst.frc.team6027.robot;

import edu.wpi.first.wpilibj.NamedSendable;
import edu.wpi.first.wpilibj.command.Command;

/**
 * An interface used to define the operations necessary for displaying information and feedback to the robot human 
 * driver.  Using this interface allows us to change the underlying implementation of how information is displayed
 * to the robot driver.
 */
public interface OperatorDisplay {
    public static final String PID_LOOP_OUPUT_LABEL = "PID Loop Output Value";
    public static final String TELEOP_MOTOR_POWER_PREF = "teleopMotorPower";
    
    public enum ChooserName {
        Position,
        Scenario("Auto Scenario");
        
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
     * The name of the field representing how far the robot has traveled.
     */
    String DISTANCE_FIELD_NAME = "Distance";

    /**
     * Sets a new numeric value on the display.
     * @param fieldName The name of the field whose value should be changed.
     * @param value The new field value.
     */
//    public void setNumericFieldValue(String fieldName, Double numValue);

//    public void setNumericFieldValue(String fieldName, int numValue);

    public void setData(NamedSendable sendable);

    void setFieldValue(String fieldName, Double numValue);

    void setFieldValue(String fieldName, int numValue);

    void setFieldValue(String fieldName, String value);

    void setFieldValue(String fieldName, boolean value);

    void registerAutoCommand(Command command);
    void registerAutoCommand(String displayName, Command command);
    void registerAutoCommand(Command command, boolean isDefaultCommand);
    void registerAutoCommand(String displayName, Command command, boolean isDefaultCommand);

    Command getSelectedAutoCommand();
    
    Integer getSelectedPosition();
    
}

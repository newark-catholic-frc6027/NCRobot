package org.usfirst.frc.team6027.robot;

/**
 * The RobotConfigConstants class maps the various robot sensors and actuator ports into
 * a constant. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotConfigConstants {

    /** The device identifier for the Cantalon speed controller which controls the front left
     * drive motor.  */
    public static final int FRONT_LEFT_CANTALON_DRIVE_ID = 5;
    /** The device identifier for the Cantalon speed controller which controls the rear left
     * drive motor.  */
    public static final int REAR_LEFT_CANTALON_DRIVE_ID = 2;
    /** The device identifier for the Cantalon speed controller which controls the front right
     * drive motor.  */
    public static final int FRONT_RIGHT_CANTALON_DRIVE_ID = 7;
    /** The device identifier for the Cantalon speed controller which controls the rear right
     * drive motor.  */
    public static final int REAR_RIGHT_CANTALON_DRIVE_ID = 6;

    /** The joystick port number corresponds to the 'USB Order' serial number on the
     * FRC Driver Station.  It is used on construction of a new Joystick object. */
    public static final int JOYSTICK_PORT_NUMBER = 0;

    /**
     * The first DIO channel that the right optical encoder is assigned to.
     */
    public static final int RIGHT_OPTICAL_ENCODER_DIO_CHANNEL_A = 0;
    /**
     * The second DIO channel that the right optical encoder is assigned to.
     */
    public static final int RIGHT_OPTICAL_ENCODER_DIO_CHANNEL_B = 1;
    /**
     * The first DIO channel that the left optical encoder is assigned to.
     */
    public static final int LEFT_OPTICAL_ENCODER_DIO_CHANNEL_A = 2;
    /**
     * The second DIO channel that the left optical encoder is assigned to.
     */
    public static final int LEFT_OPTICAL_ENCODER_DIO_CHANNEL_B = 3;    
    /**The identifier of the left analog joystick on the Xbox controller used for ArcadeDrive
     */ 
    public static final int LEFT_ANALOG_STICK = 1;
    /**The identifier of the right analog joystick on the Xbox controller used for ArcadeDrive
     */ 
    public static final int RIGHT_ANALOG_STICK = 4;
}

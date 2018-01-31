package org.usfirst.frc.team6027.robot;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SerialPort.Port;

/**
 * The RobotConfigConstants class maps the various robot sensors and actuator ports into
 * a constant. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotConfigConstants {

//    /** The device identifier for the Cantalon speed controller which controls the front left
//     * drive motor.  */
//    public static final int FRONT_LEFT_CANTALON_DRIVE_ID = 5;
//    /** The device identifier for the Cantalon speed controller which controls the rear left
//     * drive motor.  */
//    public static final int REAR_LEFT_CANTALON_DRIVE_ID = 2;
//    /** The device identifier for the Cantalon speed controller which controls the front right
//     * drive motor.  */
//    public static final int FRONT_RIGHT_CANTALON_DRIVE_ID = 6;
//    /** The device identifier for the Cantalon speed controller which controls the rear right
//     * drive motor.  */
//    public static final int REAR_RIGHT_CANTALON_DRIVE_ID = 7;

	/** The device identifier for the Talon SRX speed controller right gear box drive motor 1
	 * <pre>
	 *      3
	 *     1 2
	 * </pre>    
	 */
	public static final int RIGHT_GEARBOX_CIM_1_ID = 5;
	
	/** The device identifier for the Talon SRX speed controller right gear box drive motor 2
	 * <pre>
	 *      3
	 *     1 2
	 * </pre>    
	 */
	public static final int RIGHT_GEARBOX_CIM_2_ID = 2;
	
	/** The device identifier for the Talon SRX speed controller right gear box drive motor 3
	 * <pre>
	 *      3
	 *     1 2
	 * </pre>    
	 */
	public static final int RIGHT_GEARBOX_CIM_3_ID = 3;
	
	/** The device identifier for the Talon SRX speed controller left gear box drive motor 1
	 * <pre>
	 *      3
	 *     1 2
	 * </pre>    
	 */
	public static final int LEFT_GEARBOX_CIM_1_ID = 1;
	
	/** The device identifier for the Talon SRX speed controller left gear box drive motor 2
	 * <pre>
	 *      3
	 *     1 2
	 * </pre>    
	 */
	public static final int LEFT_GEARBOX_CIM_2_ID = 4;
	
	/** The device identifier for the Talon SRX speed controller left gear box drive motor 3
	 * <pre>
	 *      3
	 *     1 2
	 * </pre>    
	 */
	public static final int LEFT_GEARBOX_CIM_3_ID = 0;
	
	
	 
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
    
    /** ********************************************************************
     * GYRO constants
     * ******************************************************************* */
    
    /**The port that the gyro sensor connects to
     */
    public static final Port GYRO_PORT = SerialPort.Port.kUSB;
    
    
    /** ********************************************************************
     * SOLENOID constants
     * ******************************************************************* */
    public static final int SOLENOID_1_MODULE_NUMBER = 10;
    public static final int SOLENOID_1_PORT_A = 1;
    public static final int SOLENOID_1_PORT_B = 2;
    
    
}

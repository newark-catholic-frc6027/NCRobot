package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.OperatorInterface;
import frc.team6027.robot.commands.CubeDeliveryCommand.DeliveryMode;
import frc.team6027.robot.commands.DropCarriageCommand.DropFunction;
import frc.team6027.robot.commands.autonomous.AutoDriveToVisionTarget;
import frc.team6027.robot.controls.XboxJoystick;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import frc.team6027.robot.subsystems.PneumaticSubsystem;
import frc.team6027.robot.subsystems.RearLiftSubsystem;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class TeleopManager extends Command {
    private final Logger logger = LogManager.getLogger(getClass());
    protected static final int LOG_REDUCTION_MOD = 10;

    private OperatorInterface operatorInterface;
    private SensorService sensorService;
    private XboxJoystick joystick;
    private XboxJoystick joystick2;
    private DrivetrainSubsystem drivetrain;
    private Preferences prefs = Preferences.getInstance();
    private PneumaticSubsystem pneumaticSubsystem;
    private RearLiftSubsystem rearLiftSubsystem;
    private OperatorDisplay operatorDisplay;
    
    

    //private VisionTurnCommand visionTurnCommand;


    private JoystickButton shiftGearButton;
    private JoystickButton leftBumperButton;
    private JoystickButton backButton;
    private JoystickButton aButton;
    private JoystickButton bButton;
    private JoystickButton yButton;
    private JoystickButton xButton;
    private JoystickButton startButton;

    private JoystickButton yButton2;
    

    private ShiftGearCommand shiftGearCommand;
    private ToggleGrippersCommand toggleGrippersCommand;
    private ToggleShiftElevatorCommand toggleShiftElevatorCommand;
    

    protected int execCount = 0;

    

    public TeleopManager(OperatorInterface operatorInterface, SensorService sensorService,
            DrivetrainSubsystem drivetrain, /*PneumaticSubsystem pneumaticSubsystem, ElevatorSubsystem elevator,*/
            OperatorDisplay operatorDisplay) {
        // Identify the subsystems we will be using in this command and this
        // command
        // only
        requires(drivetrain);
//        requires(elevator);

        // Hang onto references of the components we will need during teleop
        this.sensorService = sensorService;
        
        this.operatorInterface = operatorInterface;
        
        this.joystick = this.operatorInterface.getJoystick1();
//        this.joystick2 = this.operatorInterface.getJoystick2();
        this.drivetrain = drivetrain;
//        this.pneumaticSubsystem = pneumaticSubsystem;
//        this.elevatorSubsystem = elevator;
        this.operatorDisplay = operatorDisplay;
                                                                                                         
        // Create the commands we will be using during teleop
        /*
        shiftGearCommand = new ShiftGearCommand(this.pneumaticSubsystem);
        toggleGrippersCommand = new ToggleGrippersCommand(this.pneumaticSubsystem);
        */
        /*
        this.toggleShiftElevatorCommand = new ToggleShiftElevatorCommand(pneumaticSubsystem);
        */
        //this.visionTurnCommand=new VisionTurnCommand();

        // Set up the commands on the Joystick buttons
        initializeJoystick();
//        initializeJoystick2();
    }

    protected void initializeJoystick() {
/*
        this.shiftGearButton = new JoystickButton(this.joystick, this.joystick.getRightBumperButtonNumber());
        shiftGearButton.whenPressed(this.shiftGearCommand);

        this.yButton = new JoystickButton(this.joystick, this.joystick.getYButtonNumber());   
        this.yButton.whenPressed(new AutoDriveToVisionTarget(24.0, 0.6, this.sensorService, this.drivetrain, this.operatorDisplay)); 
        */   
        /*
        this.yButton.whenPressed(new Command(){   
            @Override
            protected boolean isFinished() {
                return true;
            }

            @Override
            protected void execute() { 
                sensorService.getGyroSensor().reset();
                sensorService.getEncoderSensors().reset(); 
            }

        });
        */
            
        
        /*

        this.aButton = new JoystickButton(this.joystick, this.joystick.getAButtonNumber());
        this.aButton.whenPressed(this.toggleGrippersCommand);

        this.leftBumperButton = new JoystickButton(this.joystick, this.joystick.getLeftBumperButtonNumber());
        this.leftBumperButton.whenPressed(this.toggleShiftElevatorCommand);

        // for testing
        // DeliveryMode.valueOf(this.prefs.getString("cubeDelivery.mode",
        // DeliveryMode.DropThenKick.toString())),
        // this.prefs.getInt("cubeDelivery.msdelay", 10),

        this.xButton = new JoystickButton(this.joystick, this.joystick.getXButtonNumber());
        this.xButton.whenPressed(new CubeDeliveryCommand(DeliveryMode.DropThenKick, 10, this.pneumaticSubsystem));

        this.backButton = new JoystickButton(this.joystick, this.joystick.getBackButtonNumber());
        this.backButton.whenPressed(new PrepareForClimbCommand(this.pneumaticSubsystem));
        
        this.startButton = new JoystickButton(this.joystick, this.joystick.getStartButtonNumber());
        this.startButton.whenPressed(new DropCarriageCommand(DropFunction.DropForDelivery, DriverStation.getInstance(), this.pneumaticSubsystem, null ));
        // this.backButton.whenPressed(new
        // DropCarriageCommand(DropFunction.DropForDelivery,
        // DriverStation.getInstance(), pneumaticSubsystem, null, false));
        // Add new button assignments here
        */
    }
    
   
   
      protected void initializeJoystick2() {
        this.yButton2 = new JoystickButton(this.joystick2, this.joystick2.getYButtonNumber());   
        this.yButton2.whenPressed(new PrintMessageCommand("yButtonWasPressed"));  
    
    } 

    @Override
    protected boolean isFinished() {
        return false;
    }

    @Override
    protected void end() {
        this.clearRequirements();
        // This will only get called if isFinished returns true
        // this.drivetrain.stopArcadeDrive();
    }

    @Override
    protected void interrupted() {
        logger.info("Teleop interrupted");
    }

    @Override
    protected void execute() {
        this.execCount++;
        this.drive();
//        this.runElevatorIfRequired();

        // this.logData();
    }

    private void drive() {
//        this.logger.debug("Drive invoked. left axis: {}, right axis: {}", this.joystick.getLeftAxis(), this.joystick.getRightAxis());
        this.drivetrain.doArcadeDrive(this.joystick.getLeftAxis(), this.joystick.getRightAxis());
    }

    protected void logData() {

        if (this.execCount % LOG_REDUCTION_MOD == 0) {
            logger.trace("Ultrasonic dist: {}",
                    String.format("%.3f", this.sensorService.getUltrasonicSensor().getDistanceInches()));
        }

    }
/*
    private void runElevatorIfRequired() {
        if (this.joystick.getTriggerAxis(Hand.kLeft) > .05) {
            this.elevatorSubsystem.elevatorDown(this.joystick.getTriggerAxis(Hand.kLeft));
        } else {
            this.elevatorSubsystem.elevatorUp(this.joystick.getTriggerAxis(Hand.kRight));
        }
    }
*/ 
    public OperatorDisplay getOperatorDisplay() {
        return this.operatorInterface.getOperatorDisplay();
    }

}

package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;

import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.OperatorInterface;
import frc.team6027.robot.commands.autonomous.AutoDeliverHatchToRocket;
import frc.team6027.robot.commands.autonomous.AutonomousCommandManager;
import frc.team6027.robot.commands.autonomous.AutonomousPreference;
import frc.team6027.robot.commands.autonomous.KillCurrentAutoCommand;
import frc.team6027.robot.commands.autonomous.ScheduleCommand;
import frc.team6027.robot.commands.autonomous.AutoDeliverHatchToCargoShipFrontFromCenterPosition;
import frc.team6027.robot.field.StationPosition;

import frc.team6027.robot.controls.XboxJoystick;
import frc.team6027.robot.field.Field;
import frc.team6027.robot.field.LevelSelection;
import frc.team6027.robot.field.ObjectSelection;
import frc.team6027.robot.field.OperationSelection;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.ArmSubsystem;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import frc.team6027.robot.subsystems.ElevatorSubsystem;
import frc.team6027.robot.subsystems.PneumaticSubsystem;
import frc.team6027.robot.subsystems.ArmSubsystem.MotorDirection;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;

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
    private ElevatorSubsystem elevatorSubsystem;
    private ArmSubsystem armSubsystem;
    private OperatorDisplay operatorDisplay;
    
    

    //private VisionTurnCommand visionTurnCommand;


    private JoystickButton leftBumperButton;
    private JoystickButton rightBumperButton;
    private JoystickButton backButton;
    private JoystickButton aButton;
    private JoystickButton bButton;
    private JoystickButton yButton;
    private JoystickButton xButton;
    private JoystickButton startButton;

    private JoystickButton aButton2;
    private JoystickButton bButton2;
    private JoystickButton xButton2;
    private JoystickButton yButton2;
    private JoystickButton leftBumperButton2;
    private JoystickButton rightBumperButton2;
    private JoystickButton startButton2;
    private JoystickButton backButton2;


    

    private ShiftGearCommand shiftGearCommand;

    // private ToggleShiftElevatorCommand toggleShiftElevatorCommand;
    

    protected int execCount = 0;
    protected double turnPowerScaleFactor = 1.0;
    private Field field;

    public TeleopManager(OperatorInterface operatorInterface, SensorService sensorService,
            DrivetrainSubsystem drivetrain, ArmSubsystem armSubsystem, PneumaticSubsystem pneumaticSubsystem, ElevatorSubsystem elevator,
            OperatorDisplay operatorDisplay, Field field) {
        // Identify the subsystems we will be using in this command and this
        // command
        // only
        requires(drivetrain);
        requires(armSubsystem);

        // Hang onto references of the components we will need during teleop
        this.sensorService = sensorService;
        this.field = field;
        this.operatorInterface = operatorInterface;
        
        this.joystick = this.operatorInterface.getJoystick1();
        this.joystick2 = this.operatorInterface.getJoystick2();
//        this.rearLiftSubsystem = rearLiftSubsystem;
        this.drivetrain = drivetrain;
        this.pneumaticSubsystem = pneumaticSubsystem;
        this.elevatorSubsystem = elevator;
        this.armSubsystem = armSubsystem;
        this.operatorDisplay = operatorDisplay;
                                                                                                         
        // Create the commands we will be using during teleop
        /*
        shiftGearCommand = new ShiftGearCommand(this.pneumaticSubsystem);
        toggleGrippersCommand = new ToggleGrippersCommand(this.pneumaticSubsystem);
        */
        /*
        this.toggleShiftElevatorCommand = new ToggleShiftElevatorCommand(pneumaticSubsvystem);
        */
        //this.visionTurnCommand=new VisionTurnCommand();

        // Set up the commands on the Joystick buttons
        initializeJoystick();
        initializeJoystick2();
    }

    protected void initializeJoystick() {
        // **** Right bumper button - spins arm motor IN
        this.rightBumperButton = new JoystickButton(this.joystick, this.joystick.getRightBumperButtonNumber());   
        this.rightBumperButton.whileHeld(new ArmMotorCommand(this.armSubsystem, MotorDirection.In));

        // **** Left bumper button - spins arm motor OUT
        this.leftBumperButton = new JoystickButton(this.joystick, this.joystick.getLeftBumperButtonNumber());   
        this.leftBumperButton.whileHeld(new ArmMotorCommand(this.armSubsystem, MotorDirection.Out));

        // **** Back button - Kills current autonomous command
        this.backButton = new JoystickButton(this.joystick, this.joystick.getBackButtonNumber());
        this.backButton.whenPressed(new KillCurrentAutoCommand());

        // **** Back button - Run vision turn command
        this.startButton = new JoystickButton(this.joystick, this.joystick.getStartButtonNumber());
        this.startButton.whenPressed(new VisionTurnCommand(this.sensorService, this.drivetrain, this.operatorDisplay));

        // **** X button - Kicks the hatch
        this.xButton = new JoystickButton(this.joystick, this.joystick.getXButtonNumber());   
        this.xButton.whenPressed(new ToggleKickHatchCommand(this.pneumaticSubsystem));    
        this.xButton.whenReleased(new ToggleKickHatchCommand(this.pneumaticSubsystem));    

        // **** B button - Executes driver assist command
        this.bButton = new JoystickButton(this.joystick, this.joystick.getBButtonNumber());   
        this.bButton.whenPressed(new ScheduleCommand(() -> AutonomousCommandManager.instance().chooseDriverAssistCommand()));
        
/*
        this.shiftGearButton = new JoystickButton(this.joystick, this.joystick.getRightBumperButtonNumber());
        shiftGearButton.whenPressed(this.shiftGearCommand);

        this.yButton = new JoystickButton(this.joystick, this.joystick.getYButtonNumber());   
//        this.yButton.whenPressed(new AutoDriveToVisionTarget(24.0, 0.6, this.sensorService, this.drivetrain, this.operatorDisplay));    
        this.yButton.whenPressed(new AutoDriveToVisionTarget(24.0, 0.6, this.sensorService, this.drivetrain,this.operatorDisplay));    
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
        this.leftBumperButton2 = new JoystickButton(this.joystick2, this.joystick2.getLeftBumperButtonNumber());
        this.leftBumperButton2.toggleWhenPressed(new AutoDeliverHatchToRocket(StationPosition.Left, this.sensorService, this.drivetrain, 
            this.pneumaticSubsystem, this.elevatorSubsystem, this.operatorDisplay, this.field)
        );
        
        this.rightBumperButton2 = new JoystickButton(this.joystick2, this.joystick2.getRightBumperButtonNumber());
/*        this.rightBumperButton2.whenPressed(  new AutoDeliverHatchToCargoShipSide(StationPosition.Left, 
            this.sensorService, this.drivetrain, this.pneumaticSubsystem, this.elevatorSubsystem, this.operatorDisplay, this.field)
        );*/
        this.rightBumperButton2.whenPressed( new AutoDeliverHatchToCargoShipFrontFromCenterPosition(AutonomousPreference.CargoFrontLeft, this.sensorService, this.drivetrain, 
            this.pneumaticSubsystem, this.elevatorSubsystem, this.operatorDisplay, this.field)
        );
        
        /*
        this.xButton2 = new JoystickButton(this.joystick2, this.joystick2.getXButtonNumber());   
        this.xButton2.whenPressed(new ToggleObjectSelectionCommand());
        */
        this.yButton2 = new JoystickButton(this.joystick2, this.joystick2.getYButtonNumber());  
        this.yButton2.whenPressed(new SelectionCommand(LevelSelection.Upper));

        this.bButton2 = new JoystickButton(this.joystick2, this.joystick2.getBButtonNumber());
        this.bButton2.whenPressed(new SelectionCommand(LevelSelection.Middle));

        this.aButton2 = new JoystickButton(this.joystick2, this.joystick2.getAButtonNumber());
        this.aButton2.whenPressed(new SelectionCommand(LevelSelection.Lower));

        /*
        this.startButton2 = new JoystickButton(this.joystick2, this.joystick2.getStartButtonNumber());   
        this.startButton2.whenPressed(new ToggleOperationSelectionCommand());
        */

        this.backButton2 = new JoystickButton(this.joystick2, this.joystick2.getBackButtonNumber());   
        this.backButton2.whenPressed(new ClearDriverSelectionsCommand());
        
    } 

    @Override
    public void start() {
        updatePreferences();
        super.start();
    }

    public void updatePreferences() {
        this.turnPowerScaleFactor = this.prefs.getDouble("teleop.turnPowerScaleFactor", 1.0);
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
    public void cancel() {
        this.updatePreferences();
        super.cancel();
    }

    @Override
    protected void interrupted() {
        logger.info("Teleop interrupted");
        this.updatePreferences();
    }

    @Override
    protected void execute() {
        this.execCount++;
        this.drive();
        this.runMastSlideIfRequired();
        this.runElevatorIfRequired();
        this.updateDriverAssistSelections();

        // this.logData();
    }

    protected void drive() {
//        this.logger.debug("Drive invoked. left axis: {}, right axis: {}", this.joystick.getLeftAxis(), this.joystick.getRightAxis());
        this.drivetrain.doArcadeDrive(this.joystick.getLeftAxis(), this.joystick.getRightAxis() * this.turnPowerScaleFactor);
    }

    protected void runMastSlideIfRequired() {
        logger.trace("POV0: {}", this.joystick.getPOV(0));
        // POV(0) return an angle for the pad based on which direction was pressed   
        int povValue = this.joystick.getPOV(0);
        if (povValue >= 225 && povValue <= 315) {
            logger.info("Mast Backward: {}", povValue);
            this.elevatorSubsystem.mastBackward(1.0);
        } else if (povValue >= 45 && povValue <= 135) {
            logger.info("Mast Forward: {}", povValue);
            this.elevatorSubsystem.mastForward(1.0);
        }
    }

    protected void runElevatorIfRequired() {
        if (this.joystick.getTriggerAxis(Hand.kLeft) > .05) {
            this.elevatorSubsystem.elevatorDown(this.joystick.getTriggerAxis(Hand.kLeft));
        } else {
            this.elevatorSubsystem.elevatorUp(this.joystick.getTriggerAxis(Hand.kRight));
        }
    }

    protected void updateDriverAssistSelections() {
        double leftAxisValue = this.joystick2.getLeftAxis();
        double rightAxisValue = this.joystick2.getRightAxis();
        // Up is negative
        if (leftAxisValue <= -0.25) {
            AutonomousCommandManager.instance().setObjectSelection(ObjectSelection.Ball);
        } else if (leftAxisValue >= 0.25) {
            AutonomousCommandManager.instance().setObjectSelection(ObjectSelection.Hatch);
        }

        if (rightAxisValue <= -0.25) {
            AutonomousCommandManager.instance().setOperationSelection(OperationSelection.Pickup);
        } else if (rightAxisValue >= 0.25) {
            AutonomousCommandManager.instance().setOperationSelection(OperationSelection.Deliver);
        }

    }

    public OperatorDisplay getOperatorDisplay() {
        return this.operatorInterface.getOperatorDisplay();
    }

}

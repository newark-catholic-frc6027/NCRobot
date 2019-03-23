package frc.team6027.robot.commands.autonomous;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.commands.DriveStraightCommand;
import frc.team6027.robot.commands.VisionTurnCommand;
import frc.team6027.robot.commands.DriveStraightCommand.DriveDistanceMode;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import frc.team6027.robot.subsystems.ElevatorSubsystem;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class DriverAssistHatchPickupCommand extends CommandGroup implements KillableAutoCommand {
    private final Logger logger = LogManager.getLogger(getClass());

    private SensorService sensorService;
    private ElevatorSubsystem elevatorSubsystem;
    private DrivetrainSubsystem drivetrainSubsystem;

    boolean done = false;

    public DriverAssistHatchPickupCommand(DrivetrainSubsystem drivetrainSubsystem,
			ElevatorSubsystem elevatorSubsystem, SensorService sensorService, OperatorDisplay operatorDisplay) {

            this.sensorService = sensorService;
            this.elevatorSubsystem = elevatorSubsystem;
    
            // Run elevator to set point
    //        this.addSequential(makeElevatorCommand(levelSelection));
            // Drive in
            this.addSequential(new VisionTurnCommand(this.sensorService, this.drivetrainSubsystem, operatorDisplay));
            this.addSequential(new DriveStraightCommand("F-L1-DriverAssist-HatchPickup", DriveDistanceMode.DistanceFromObject, 
                "F-P1-DriverAssist-HatchPickup", null, this.sensorService, drivetrainSubsystem, operatorDisplay));
            this.addSequential(new VisionTurnCommand(this.sensorService, this.drivetrainSubsystem, operatorDisplay));
            this.addSequential(new DriveStraightCommand("F-L2-DriverAssist-HatchPickup", DriveDistanceMode.DistanceFromObject, 
                "F-P2-DriverAssist-HatchPickup", null, this.sensorService, drivetrainSubsystem, operatorDisplay));
	}

    @Override
    public void start() {
        this.registerAsKillable();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command STARTING", this.getClass().getSimpleName());
        super.start();
    }

	@Override
    protected boolean isFinished() {
        return true;
    }
    
    protected void execute() {
    }

    @Override
    public void registerAsKillable() {
        this.default_registerAsKillable();
    }

    @Override
    public void onComplete() {
        this.default_onComplete();
    }

    @Override
    public void end() {
        this.onComplete();
        super.end();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command ENDED", this.getClass().getSimpleName());
    }

    @Override
    public void cancel() {
        this.onComplete();
        super.cancel();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command CANCELED", this.getClass().getSimpleName());
    }

    @Override
    protected void interrupted() {
        this.onComplete();
        super.interrupted();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command INTERRUPTED", this.getClass().getSimpleName());
    }

}

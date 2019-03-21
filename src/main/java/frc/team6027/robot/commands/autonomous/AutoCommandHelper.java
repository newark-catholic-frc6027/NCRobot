package frc.team6027.robot.commands.autonomous;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import frc.team6027.robot.commands.PneumaticsInitializationCommand;
import frc.team6027.robot.commands.ResetSensorsCommand;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoCommandHelper {
    private static final Logger logger = LogManager.getLogger(AutoCommandHelper.class);

    public static boolean isInMatch() {
        DriverStation ds = DriverStation.getInstance();
        return ds.getMatchNumber() > 0 || ds.getMatchTime() > -0.1;
    }

    public static void addAutoInitCommands(CommandGroup group, PneumaticSubsystem pneumaticSubsystem,
        SensorService sensorService) {
        group.addSequential(new PneumaticsInitializationCommand(pneumaticSubsystem));
        group.addSequential(new ResetSensorsCommand(sensorService));
    }
    /*
    public static boolean hasFieldDataChangedSinceAutoStart(Field initializedField) {
        // Don't even bother to check unless we are actually in a match
        if (! isInMatch()) {
            return false;
        }
        
        // String initialAssignment = initializedField.getAssignmentData();
        String currentAssignment = DriverStation.getInstance().getGameSpecificMessage();
        
        boolean dataChanged = currentAssignment != null && ! currentAssignment.equals(initialAssignment);
        if (dataChanged) {
            logger.error("!!!! GAME FIELD DATA CHANGED since initially checked!!  Initial value: {}, current value: {}", initialAssignment, currentAssignment);
        }
        
        return dataChanged;
    }
    */
/*    
    public static Command createDropCarriageForDeliveryCommand(PneumaticSubsystem pneumaticSubsystem, Field field) {
        return new DropCarriageCommand(DropFunction.DropForDelivery, DriverStation.getInstance(), pneumaticSubsystem, field, true);
    }
*/
/*   
    public static Command createElevatorUpForDeliveryCommand(ElevatorSubsystem elevator, DrivetrainSubsystem drivetrainSubsystem, SensorService sensorService) {
        return new ElevatorCommand(ElevatorDirection.Up, 1.0, sensorService, elevator, drivetrainSubsystem);
    }
    
    public static Command createElevatorDownForDeliveryCommand(ElevatorSubsystem elevator,
            DrivetrainSubsystem drivetrainSubsystem, SensorService sensorService) {
        return new ElevatorCommand(ElevatorDirection.Down, 0.6, sensorService, elevator, drivetrainSubsystem);
    }
*/
/*    
    public static Command createCubeDeliveryCommand(PneumaticSubsystem pneumaticSubsystem, Field field) {
        return new CubeDeliveryCommand(DeliveryMode.DropThenKick, 10, pneumaticSubsystem, field, true);
    }
*/
    
    
    
}

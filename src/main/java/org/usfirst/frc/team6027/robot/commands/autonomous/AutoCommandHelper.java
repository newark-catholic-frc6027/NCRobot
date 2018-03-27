package org.usfirst.frc.team6027.robot.commands.autonomous;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.commands.CubeDeliveryCommand;
import org.usfirst.frc.team6027.robot.commands.DropCarriageCommand;
import org.usfirst.frc.team6027.robot.commands.ElevatorCommand;
import org.usfirst.frc.team6027.robot.commands.CubeDeliveryCommand.DeliveryMode;
import org.usfirst.frc.team6027.robot.commands.DropCarriageCommand.DropFunction;
import org.usfirst.frc.team6027.robot.commands.ElevatorCommand.ElevatorDirection;
import org.usfirst.frc.team6027.robot.field.Field;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;
import org.usfirst.frc.team6027.robot.subsystems.ElevatorSubsystem;
import org.usfirst.frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;

public class AutoCommandHelper {
    private static final Logger logger = LoggerFactory.getLogger(AutoCommandHelper.class);

    public static boolean hasFieldDataChangedSinceAutoStart(Field initializedField) {
        String initialAssignment = initializedField.getAssignmentData();
        String currentAssignment = DriverStation.getInstance().getGameSpecificMessage();
        
        boolean dataChanged = currentAssignment != null && ! currentAssignment.equals(initialAssignment);
        if (dataChanged) {
            logger.error("!!!! GAME FIELD DATA CHANGED since initially checked!!  Initial value: {}, current value: {}", initialAssignment, currentAssignment);
        }
        
        return dataChanged;
    }
    
    public static Command createDropCarriageForDeliveryCommand(PneumaticSubsystem pneumaticSubsystem, Field field) {
        return new DropCarriageCommand(DropFunction.DropForDelivery, DriverStation.getInstance(), pneumaticSubsystem, field, true);
    }
   
    public static Command createElevatorUpForDeliveryCommand(ElevatorSubsystem elevator, DrivetrainSubsystem drivetrainSubsystem, SensorService sensorService) {
        // Reduce power some in Auto since it will be running in high gear
        return new ElevatorCommand(ElevatorDirection.Up, 0.6, sensorService, elevator, drivetrainSubsystem);
    }
    
    public static Command createElevatorDownForDeliveryCommand(ElevatorSubsystem elevator,
            DrivetrainSubsystem drivetrainSubsystem, SensorService sensorService) {
        return new ElevatorCommand(ElevatorDirection.Down, 0.6, sensorService, elevator, drivetrainSubsystem);
    }
    
    public static Command createCubeDeliveryCommand(PneumaticSubsystem pneumaticSubsystem, Field field) {
        return new CubeDeliveryCommand(DeliveryMode.DropThenKick, 10, pneumaticSubsystem, field, true);
    }

    
    
    
}

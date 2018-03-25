package org.usfirst.frc.team6027.robot.commands.autonomous;

import org.usfirst.frc.team6027.robot.commands.CubeDeliveryCommand;
import org.usfirst.frc.team6027.robot.commands.DropCarriageCommand;
import org.usfirst.frc.team6027.robot.commands.ElevatorCommand;
import org.usfirst.frc.team6027.robot.commands.CubeDeliveryCommand.DeliveryMode;
import org.usfirst.frc.team6027.robot.commands.DropCarriageCommand.DropFunction;
import org.usfirst.frc.team6027.robot.commands.ElevatorCommand.ElevatorDirection;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;
import org.usfirst.frc.team6027.robot.subsystems.ElevatorSubsystem;
import org.usfirst.frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;

public class AutoCommandHelper {

    
    public static Command createDropCarriageForDeliveryCommand(PneumaticSubsystem pneumaticSubsystem) {
        return new DropCarriageCommand(DropFunction.DropForDelivery, DriverStation.getInstance(), pneumaticSubsystem);
    }
   
    public static Command createElevatorUpForDeliveryCommand(ElevatorSubsystem elevator, DrivetrainSubsystem drivetrainSubsystem, SensorService sensorService) {
        // Reduce power some in Auto since it will be running in high gear
        return new ElevatorCommand(ElevatorDirection.Up, 0.6, sensorService, elevator, drivetrainSubsystem);
    }
    
    public static Command createCubeDeliveryCommand(PneumaticSubsystem pneumaticSubsystem) {
        return new CubeDeliveryCommand(DeliveryMode.DropThenKick, 10, pneumaticSubsystem);
    }
    
    
    
}

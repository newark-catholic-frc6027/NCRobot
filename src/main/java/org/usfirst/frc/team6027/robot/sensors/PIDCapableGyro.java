package org.usfirst.frc.team6027.robot.sensors;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.interfaces.Gyro;

public interface PIDCapableGyro extends Gyro {
    PIDSource getPIDSource();
}

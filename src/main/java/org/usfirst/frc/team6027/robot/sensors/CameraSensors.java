package org.usfirst.frc.team6027.robot.sensors;

import edu.wpi.first.wpilibj.CameraServer;

public class CameraSensors {

    public CameraSensors() {
        CameraServer.getInstance().startAutomaticCapture(0);
        CameraServer.getInstance().startAutomaticCapture(1);
    }
}

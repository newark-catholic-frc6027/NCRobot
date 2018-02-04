package org.usfirst.frc.team6027.robot.sensors;

import edu.wpi.first.wpilibj.CameraServer;

public class CameraSensor {

    public CameraSensor() {
        CameraServer.getInstance().startAutomaticCapture();
    }
}

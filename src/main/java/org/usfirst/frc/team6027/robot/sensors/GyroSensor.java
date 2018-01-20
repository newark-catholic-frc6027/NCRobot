package org.usfirst.frc.team6027.robot.sensors;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.interfaces.Gyro;

public class GyroSensor {
    
    
    private Gyro gyro = new AnalogGyro(1);
    
    public GyroSensor() {
        initialize();
    }

    protected void initialize() {
        // TODO: define as constants
        this.gyro.reset();
    }



    public void setGyro(Gyro gyro) {
        this.gyro = gyro;
    }

    public Gyro getGyro() {      
        return gyro;
    }
  



}

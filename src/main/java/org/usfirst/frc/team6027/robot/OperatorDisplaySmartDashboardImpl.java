package org.usfirst.frc.team6027.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.wpi.first.wpilibj.NamedSendable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * An implementation of the OperatorDisplay interface which uses the SmartDashboard to display information back to the
 * robot human driver.
 */
public class OperatorDisplaySmartDashboardImpl implements OperatorDisplay {
    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public void setData(NamedSendable sendable) {
        SmartDashboard.putData(sendable);
    }

    @Override
    public void setFieldValue(String fieldName, Double numValue) {
        SmartDashboard.putNumber(fieldName, numValue);
    }
        
    @Override
    public void setFieldValue(String fieldName, int numValue) {
        SmartDashboard.putNumber(fieldName, numValue);
    }
    
    @Override
    public void setFieldValue(String fieldName, String value) {
        SmartDashboard.putString(fieldName, value);
    }
    
    @Override
    public void setFieldValue(String fieldName, boolean value) {
        SmartDashboard.putBoolean(fieldName, value);
    }
    
}

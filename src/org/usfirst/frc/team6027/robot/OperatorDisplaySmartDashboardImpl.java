package org.usfirst.frc.team6027.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * An implementation of the OperatorDisplay interface which uses the SmartDashboard to display information back to the
 * robot human driver.
 */
public class OperatorDisplaySmartDashboardImpl implements OperatorDisplay {
    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void setNumericFieldValue(String fieldName, Double value) {
        SmartDashboard.putNumber(fieldName, value);
    }

}

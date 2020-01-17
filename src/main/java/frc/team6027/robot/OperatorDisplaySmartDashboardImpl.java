package frc.team6027.robot;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * An implementation of the OperatorDisplay interface which uses the SmartDashboard to display information back to the
 * robot human driver.
 */
public class OperatorDisplaySmartDashboardImpl implements OperatorDisplay {
    @SuppressWarnings("unused")
    private final Logger logger = LogManager.getLogger(getClass());


    private SendableChooser<Integer> positionChooser = new SendableChooser<>();
    private SendableChooser<String> scenarioChooser = new SendableChooser<>();
    
    @SuppressWarnings("rawtypes")
    private Map<ChooserName, SendableChooser> chooserCache = new HashMap<>();
    
    public OperatorDisplaySmartDashboardImpl() {
       initPositionChooser();
       initScenarioChooser();
    }

    protected void initScenarioChooser() {
        this.chooserCache.put(ChooserName.Scenario, this.scenarioChooser);
        this.registerAutoScenario("NO SELECTION", true);
        SmartDashboard.putData(ChooserName.Scenario.displayName(), this.scenarioChooser);
    }
        
    protected void initPositionChooser() {
        this.chooserCache.put(ChooserName.Position, this.positionChooser);
        this.positionChooser.setDefaultOption("NO SELECTION", Integer.valueOf(0));
        this.positionChooser.addOption("Left", Integer.valueOf(1));
        this.positionChooser.addOption("Center", Integer.valueOf(2));
        this.positionChooser.addOption("Right", Integer.valueOf(3));
        
        SmartDashboard.putData(ChooserName.Position.displayName(), this.positionChooser);
    }

    @Override
    public void setData(Sendable sendable) {
        SmartDashboard.putData(sendable);
    }

    @Override
    public void setData(String name, Command command) {
        SmartDashboard.putData(name, command);
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

    @Override
    public void registerAutoScenario(String displayName) {
        this.registerAutoScenario(displayName, false);
    }
    
    @Override
    public void registerAutoScenario(String displayName, boolean isDefaultCommand) {
        if (isDefaultCommand) {
            this.scenarioChooser.addDefault(displayName, displayName);
        } else {
            this.scenarioChooser.addObject(displayName, displayName);
        }
    }
    
    @Override
    public String getSelectedAutoScenario() {
        return (String) this.scenarioChooser.getSelected();
    }

    @Override
    public Integer getSelectedPosition() {
        return (Integer) this.positionChooser.getSelected();
    }
}

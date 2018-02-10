package org.usfirst.frc.team6027.robot;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.wpi.first.wpilibj.NamedSendable;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * An implementation of the OperatorDisplay interface which uses the SmartDashboard to display information back to the
 * robot human driver.
 */
public class OperatorDisplaySmartDashboardImpl implements OperatorDisplay {
    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(getClass());


    private SendableChooser<Integer> positionChooser = new SendableChooser<>();
    private SendableChooser<Command> scenarioChooser = new SendableChooser<>();
    
    @SuppressWarnings("rawtypes")
    private Map<ChooserName, SendableChooser> chooserCache = new HashMap<>();
    
    public OperatorDisplaySmartDashboardImpl() {
       initPositionChooser();
       initScenarioChooser();
    }

    protected void initScenarioChooser() {
        this.chooserCache.put(ChooserName.Scenario, this.scenarioChooser);
        DefaultNoOpCommand noOpCommand = new DefaultNoOpCommand();
        this.registerAutoCommand(noOpCommand, true);
        SmartDashboard.putData(ChooserName.Scenario.displayName(), this.scenarioChooser);
    }
    
    protected void initPositionChooser() {
        this.chooserCache.put(ChooserName.Position, this.positionChooser);
        
        this.positionChooser.addDefault("Pos 1", new Integer(1));
        this.positionChooser.addObject("Pos 2", new Integer(2));
        this.positionChooser.addObject("Pos 3", new Integer(3));
        
        SmartDashboard.putData(ChooserName.Position.displayName(), this.positionChooser);
    }

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

    @Override
    public void registerAutoCommand(Command command) {
        this.registerAutoCommand(command.getName(), command);
    }

    @Override
    public void registerAutoCommand(Command command, boolean isDefaultCommand) {
        this.registerAutoCommand(command.getName(), command, isDefaultCommand);
    }
    
    @Override
    public void registerAutoCommand(String displayName, Command command) {
        this.registerAutoCommand(displayName, command, false);
    }
    
    @Override
    public void registerAutoCommand(String displayName, Command command, boolean isDefaultCommand) {
        if (isDefaultCommand) {
            this.scenarioChooser.addDefault(displayName, command);
        } else {
            this.scenarioChooser.addObject(displayName, command);
        }
    }
    
    @Override
    public Command getSelectedAutoCommand() {
        return (Command) this.scenarioChooser.getSelected();
    }

    @Override
    public Integer getSelectedPosition() {
        return (Integer) this.positionChooser.getSelected();
    }

    
    private class DefaultNoOpCommand extends Command {
        public DefaultNoOpCommand() {
            this.setName("Do Nothing");
        }
        
        @Override
        protected boolean isFinished() {
            return true;
        }
        
    }
    
}

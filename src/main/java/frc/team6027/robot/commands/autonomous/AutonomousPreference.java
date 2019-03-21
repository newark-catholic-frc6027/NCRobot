package frc.team6027.robot.commands.autonomous;

import java.util.Arrays;

public enum AutonomousPreference {
    NoPreference("NO SELECTION"),
    Rocket("Rocket"), 
    CargoFrontLeft("Cargo Front LEFT"),
    CargoFrontRight("Cargo Front RIGHT"),                
    CargoSide("Cargo Side")
    ;
    
    private String displayName;
    
    private AutonomousPreference() {
    }
    
    private AutonomousPreference(String displayName) {
        this.displayName = displayName;
    }
    
    public String displayName() {
        if (this.displayName == null ) {
            return this.name();
        } else {
            return this.displayName;
        }
    }
    
    public static AutonomousPreference fromDisplayName(String displayName) {
        return Arrays.asList(AutonomousPreference.values()).stream().filter(a -> displayName.equals(a.displayName())).findFirst().orElse(null);
    }
    
}

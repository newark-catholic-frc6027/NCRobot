package frc.team6027.robot.field;

import edu.wpi.first.wpilibj.Preferences;

public enum ElevatorLevel {
    RocketBallLower("Level.RocketBallBottom"),
    RocketBallMiddle("Level.RocketBallMiddle"),
    RocketBallUpper("Level.RocketBallTop"),
    RocketHatchUpper("Level.RocketHatchTop"),
    RocketHatchMiddle("Level.RocketHatchMiddle"),
    RocketHatchLower("Level.RocketHatchBottom"),
    CargoBall("Level.CargoBall"),
    CargoHatch("Level.CargoHatch"),
    LoadingHatch("Level.LoadingHatch");

    private static final Preferences prefs = Preferences.getInstance();
    private String prefName;

    private ElevatorLevel(String pref) {
        this.prefName = pref;
    }

    public Double level() {
        return prefs.getDouble(this.prefName, -1.0);
    }
}
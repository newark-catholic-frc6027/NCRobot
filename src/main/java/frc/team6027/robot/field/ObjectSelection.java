package frc.team6027.robot.field;

public enum ObjectSelection {
    Ball,
    Hatch
    ;

    public ObjectSelection toggle() {
        return values()[this.ordinal() < values().length - 1 ? this.ordinal() + 1 : 0];
    }
}
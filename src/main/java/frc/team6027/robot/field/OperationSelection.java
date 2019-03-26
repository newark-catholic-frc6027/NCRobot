package frc.team6027.robot.field;

public enum OperationSelection {
    Deliver
    //,
//    Pickup
    ;

    public OperationSelection toggle() {
        return values()[this.ordinal() < values().length - 1 ? this.ordinal() + 1 : 0];
    }

}
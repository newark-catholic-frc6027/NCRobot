package frc.team6027.robot.field;

import java.util.Arrays;

public enum StationPosition {
    Left(1),
    Center(2),
    Right(3)
    ;

    private int positionNumber;
        
    private StationPosition(int position) {
        this.positionNumber = position;
    }
                
    public int intValue() {
        return this.positionNumber;
    }

    public static StationPosition fromInt(int position) {
        return Arrays.asList(StationPosition.values()).stream()
            .filter(s -> s.positionNumber == position).findFirst().orElse(null);
    }
}

package frc.team6027.robot.field;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Field {
    private final Logger logger = LogManager.getLogger(getClass());

    private StationPosition ourStationPosition = null;
    
    public Field() {
    }
    
    public boolean isOurStationCenter() {
        return ourStationPosition == StationPosition.Center;
    }
    public StationPosition getOurStationPosition() {
        return ourStationPosition;
    }

    public void setOurStationPosition(StationPosition stationPosition) {
        this.ourStationPosition = stationPosition;
    }

}

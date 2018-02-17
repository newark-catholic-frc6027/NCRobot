package org.usfirst.frc.team6027.robot.field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Field {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public enum PlatePosition {
        OurSwitchLeft,
        OurSwitchRight, 
        
        ScaleLeft, 
        ScaleRight, 
        
        TheirSwitchLeft, 
        TheirSwitchRight
    }

    public static final double DISTANCE_FROM_OUR_WALL_TO_AUTOLINE_INCHES = 120.0;
    
    protected static final int OUR_SWITCH_POS = 0;
    protected static final int OUR_SWITCH_LEFT_PLATE = 0;
    protected static final int OUR_SWITCH_RIGHT_PLATE = 1;

    protected static final int SCALE_POS = 1;
    protected static final int SCALE_LEFT_PLATE = 0;
    protected static final int SCALE_RIGHT_PLATE = 1;

    protected static final int THEIR_SWITCH_POS = 2;
    protected static final int THEIR_SWITCH_LEFT_PLATE = 0;
    protected static final int THEIR_SWITCH_RIGHT_PLATE = 1;

    protected static final String LEFT_ASSIGNMENT_FLAG = "L";
    protected static final String RIGHT_ASSIGNMENT_FLAG = "R";

    protected Plate[][] plates = new Plate[3][2]; // 3 rows, 2 cols

    private String assignmentData = null;

    public Field() {

    }

    public String getAssignmentData() {
        return this.assignmentData;
    }
    
    public void doFieldAssignments(String assignmentData) {
        if (null != assignmentData && assignmentData.length() == 3) {
            this.assignmentData = assignmentData;
            logger.info("Assignment data received.  Value: {}", this.assignmentData);
            String ourSwitchAssignment = new String(assignmentData.substring(0, 1));
            String scaleAssignment = new String(assignmentData.substring(1, 2));
            String theirSwitchAssignment = new String(assignmentData.substring(2));

            plates[OUR_SWITCH_POS][OUR_SWITCH_LEFT_PLATE] = new Plate(
                    ourSwitchAssignment.equalsIgnoreCase(LEFT_ASSIGNMENT_FLAG));
            plates[OUR_SWITCH_POS][OUR_SWITCH_RIGHT_PLATE] = new Plate(
                    ourSwitchAssignment.equalsIgnoreCase(RIGHT_ASSIGNMENT_FLAG));

            plates[SCALE_POS][SCALE_LEFT_PLATE] = new Plate(scaleAssignment.equalsIgnoreCase(LEFT_ASSIGNMENT_FLAG));
            plates[SCALE_POS][SCALE_RIGHT_PLATE] = new Plate(scaleAssignment.equalsIgnoreCase(RIGHT_ASSIGNMENT_FLAG));

            plates[THEIR_SWITCH_POS][THEIR_SWITCH_LEFT_PLATE] = new Plate(
                    theirSwitchAssignment.equalsIgnoreCase(LEFT_ASSIGNMENT_FLAG));
            plates[THEIR_SWITCH_POS][THEIR_SWITCH_RIGHT_PLATE] = new Plate(
                    theirSwitchAssignment.equalsIgnoreCase(RIGHT_ASSIGNMENT_FLAG));

            this.logFieldAssignments();
        } else {
            logger.trace("Assignment data not ready yet or not valid.  Value: {}", assignmentData);
            this.assignmentData = null;
        }
    }

    public void logFieldAssignments() {
        
        logger.info("_____________________________");
        logger.info("|                            |");
        logger.info("|    *        *        *     |");
        logger.info("|                            |");
        logger.info("|                            |");
        logger.info("|                            |");
        logger.info("|                            |");
        logger.info("|        {}        {}          |", this.isPlateAssignedToUs(PlatePosition.TheirSwitchLeft) ? "X" : "-", this.isPlateAssignedToUs(PlatePosition.TheirSwitchRight) ?  "X" : "-");
        logger.info("|                            |");
        logger.info("|                            |");
        logger.info("|                            |");
        logger.info("|                            |");
        logger.info("|        {}        {}          |", this.isPlateAssignedToUs(PlatePosition.ScaleLeft) ? "X" : "-", this.isPlateAssignedToUs(PlatePosition.ScaleRight) ?  "X" : "-");
        logger.info("|                            |");
        logger.info("|                            |");
        logger.info("|                            |");
        logger.info("|                            |");
        logger.info("|        {}        {}          |", this.isPlateAssignedToUs(PlatePosition.OurSwitchLeft) ? "X" : "-", this.isPlateAssignedToUs(PlatePosition.OurSwitchRight) ?  "X" : "-");
        logger.info("|                            |");
        logger.info("|                            |");
        logger.info("|                            |");
        logger.info("|                            |");
        logger.info("|    1        2        3     |");
        logger.info("|____________________________|");
    }
    
    public boolean isPlateAssignedToUs(PlatePosition platePosition) {
        int[] pos = this.platePositionToArrayIndices(platePosition);
        int row = pos[0];
        int col = pos[1];
        Plate plate = this.plates[row][col];
        return plate.isAssignedToUs();
    }

    public boolean isPlateAssignedToThem(PlatePosition platePosition) {
        return ! isPlateAssignedToUs(platePosition);
    }

    public void clearAssignmentData() {
        this.assignmentData = null;
    }

    public boolean hasAssignmentData() {
        return this.assignmentData != null && this.assignmentData.length() > 0;
    }
    
    protected int[] platePositionToArrayIndices(PlatePosition platePosition) {
        switch (platePosition) {
            case OurSwitchLeft:
                return new int[] { OUR_SWITCH_POS, OUR_SWITCH_LEFT_PLATE };
            case OurSwitchRight:
                return new int[] { OUR_SWITCH_POS, OUR_SWITCH_RIGHT_PLATE };
            case ScaleLeft:
                return new int[] { SCALE_POS, SCALE_LEFT_PLATE };
            case ScaleRight:
                return new int[] { SCALE_POS, SCALE_RIGHT_PLATE };
            case TheirSwitchLeft:
                return new int[] { THEIR_SWITCH_POS, THEIR_SWITCH_LEFT_PLATE };
            case TheirSwitchRight:
                return new int[] { THEIR_SWITCH_POS, THEIR_SWITCH_RIGHT_PLATE };
            default:
                logger.error("{} is not a supported plate position", platePosition);
                return new int[] { -1, -1 };
        }
    }

    private static void testAssignment(String assignmentStr) {
        char ourSwitchAssignment = assignmentStr.charAt(0);
        char scaleAssignment = assignmentStr.charAt(1);
        char theirSwitchAssignment = assignmentStr.charAt(2);
        
        Field fld = new Field();
        fld.doFieldAssignments(assignmentStr);
        
        if (ourSwitchAssignment == 'L') {
            assert(fld.isPlateAssignedToUs(PlatePosition.OurSwitchLeft));
            assert(! fld.isPlateAssignedToThem(PlatePosition.OurSwitchLeft));
            
            assert(! fld.isPlateAssignedToUs(PlatePosition.OurSwitchRight));
            assert(fld.isPlateAssignedToThem(PlatePosition.OurSwitchRight));
        } else if (ourSwitchAssignment == 'R') {
            assert(!fld.isPlateAssignedToUs(PlatePosition.OurSwitchLeft));
            assert(fld.isPlateAssignedToThem(PlatePosition.OurSwitchLeft));
            
            assert(fld.isPlateAssignedToUs(PlatePosition.OurSwitchRight));
            assert(! fld.isPlateAssignedToThem(PlatePosition.OurSwitchRight));
        } else {
            assert false : "Can't handle '" + ourSwitchAssignment + "' our switch assignment";
        }

        if (scaleAssignment == 'L') {
            assert(fld.isPlateAssignedToUs(PlatePosition.ScaleLeft));
            assert(! fld.isPlateAssignedToThem(PlatePosition.ScaleLeft));
            
            assert(! fld.isPlateAssignedToUs(PlatePosition.ScaleRight));
            assert(fld.isPlateAssignedToThem(PlatePosition.ScaleRight));
        } else if (scaleAssignment == 'R') {
            assert(!fld.isPlateAssignedToUs(PlatePosition.ScaleLeft));
            assert(fld.isPlateAssignedToThem(PlatePosition.ScaleLeft));
            
            assert(fld.isPlateAssignedToUs(PlatePosition.ScaleRight));
            assert(! fld.isPlateAssignedToThem(PlatePosition.ScaleRight));
        } else {
            assert false : "Can't handle '" + scaleAssignment + "' for scale assignment";
        }

        if (theirSwitchAssignment == 'L') {
            assert(fld.isPlateAssignedToUs(PlatePosition.TheirSwitchLeft));
            assert(! fld.isPlateAssignedToThem(PlatePosition.TheirSwitchLeft));
           
            assert(! fld.isPlateAssignedToUs(PlatePosition.TheirSwitchRight));
            assert(fld.isPlateAssignedToThem(PlatePosition.TheirSwitchRight));
            
        } else if (theirSwitchAssignment == 'R') {
            assert(! fld.isPlateAssignedToUs(PlatePosition.TheirSwitchLeft));
            assert(fld.isPlateAssignedToThem(PlatePosition.TheirSwitchLeft));
           
            assert(fld.isPlateAssignedToUs(PlatePosition.TheirSwitchRight));
            assert(! fld.isPlateAssignedToThem(PlatePosition.TheirSwitchRight));
        } else {
            assert false : "Can't handle '" + theirSwitchAssignment + "' their switch assignment";
        }
    }
    
    public static void main(String args[]) {
        testAssignment("LLL");
        testAssignment("RRR");
        
        testAssignment("LRL");
        testAssignment("LLR");
        testAssignment("LRR");

        testAssignment("RRL");
        testAssignment("RLR");
        testAssignment("RLL");
    }


    
}

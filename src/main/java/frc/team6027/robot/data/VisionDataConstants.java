package frc.team6027.robot.data;

public final class VisionDataConstants {
    /**
     * Datahub name for the vision data
     */
    public static final String VISION_DATA_KEY = "vision";


    /**
     * Key for the number of contours found by vision processing.
     */
    public static final String NUM_CONTOURS_KEY = "numContours"; 
    /**
     * Key for the estimated distance to the vision target provided by vision processing.
     */
    public static final String TARGET_DISTANCE_KEY = "distanceToTargetInches"; 

    /**
     * Key for the X coordinate of the centers between the contours
     */
    public static final String CONTOURS_CENTER_X_KEY = "contoursCenterX"; 
    /**
     * Key for the Y coordinate of the centers between the contours
     */
    public static final String CONTOURS_CENTER_Y_KEY = "contoursCenterY"; 
    /**
     * Key for the area of the left hand contour
     */
    public static final String CONTOUR_AREA_LEFT = "contourAreaLeft"; 
    /**
     * Key for the area of the right hand contour
     */
    public static final String CONTOUR_AREA_RIGHT = "contourAreaRight"; 

}
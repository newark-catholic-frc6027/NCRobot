package frc.team6027.robot.data;

public class LimelightDataConstants {

    /**
     * Datahub name for the vision data
     */
    public static final String LIMELIGHT_DATAHUB_KEY = "limelight";

    public static final String NUM_TARGETS_KEY = "tv";

    public static final String TARGET_HORIZ_OFFSET_DEG_KEY = "tx";

    public static final String TARGET_VERT_OFFSET_DEG_KEY = "ty";

    public static final String TARGET_AREA_IMAGE_PCT_KEY = "ta";


    enum LedMode {
        Default(0),
        Off(1),
        Blink(2),
        On(3);

        public final int value;

        private LedMode(int value) {
            this.value = value;
        }


    }

}
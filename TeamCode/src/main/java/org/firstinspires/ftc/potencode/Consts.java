package org.firstinspires.ftc.potencode;

public class Consts {
    public static final int ARM_TPS = 50;
    public static final int MOVE_TPS = 900;

    public static final double DEFAULT_ARM_POWER = 0.4;
    public static final double MIN_ARM_POWER = 0.2;
    public static final double DEFAULT_DRIVE_POWER = 0.4;
    public static final double MIN_DRIVE_POWER = 0.2;

    public static final int MIN_ARM_BAG_POS = -7180;
    public static final int MAX_ARM_BAG_POS = 0;
    public static final int MIN_ARM_SLIDE_POS = -3700;
    public static final int MAX_ARM_SLIDE_POS = 0;

    public static final int PICKUP_ARM_POS = -200;
    public static final int LOW_ARM_POS = -1250;
    public static final int MID_ARM_POS = -2300;
    public static final int HIGH_ARM_POS = -3350;

    public static final double POWER_PER_P = 0.01;

    public static final int SLIDE_VEL = 1000;

    public static final double TICKS_PER_POWER = 2000;
    public static final double TICKS_PER_CM = 24.97406315350325;
    public static final double CM_PER_INCH = 2.54;
    public static final double CM_PER_TILE = 60.325;

    public static final double pi = 3.141592653589793238462643383279502884197169399375105820974944592;

    public static final float MIN_RESULT_CONFIDENCE = 0.45f;
    public static final int TFOD_INPUT_SIZE = 600;

    public static final String VUFORIA_KEY = "ASAgPkT/////AAABmcTllI2PFk1wiMjhlIY1WS8Ovl54qUtjzOSa3fzMnC9V2C5Ow73wnC6xQbPR2agidsoI2fC8QGvo7TXT03j1B6dUlZ4azcy/1gOlzGwY9ZahRTEz7Ey9uuCKTh4sZrXVjD5oxAzVYIVo+3GR+YzwLtT843sIZGRx0eBlHyokJiAyb+fAkwnwIMg137n6Jxeyw4Opm18oWD+GgtZVCg25IhpIf53nrPC0ABvVuL1Lz4qErWFcQbMEXqNbMQVSIBMuX1LkrVoFPPmhMDY1c7bTI4VPLmPyUfGauoYw9DIW7c/ZfwAGs4cf+va2oJZBOwUyd3CPomicBKcsMgey6RBaKLHO4oNhNazpvFvxnr399ckg";

    public static final String[] CONE_LABELS = new String[] { "drax", "spring", "ryan" }; // 0 1 2
    public static final String CONE_MODEL_FILE  = "/sdcard/FIRST/tflitemodels/mechjeb_lite3.tflite";

}

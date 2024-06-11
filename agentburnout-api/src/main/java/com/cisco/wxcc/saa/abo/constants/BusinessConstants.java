package com.cisco.wxcc.saa.abo.constants;

public class BusinessConstants {
    public static final Integer MAX_BREAKS_PER_SHIFT = 2;
    public static final Integer MINIMUM_GAP_BETWEEN_TWO_CONSECUTIVE_BREAKS_IN_HOURS = 2;
    public static final String VIDEO_PLAYED = "video-played";

    public static final String AUTOMATED_BREAKS_OFF = "Automated breaks are toggled off by supervisor";
    public static final String MINIMUM_GAP_BETWEEN_TWO_CONSECUTIVE_BREAKS_NOT_ELAPSED = "Minimum gap between two consecutive breaks not elapsed";
    public static final String MAX_BREAKS_PER_SHIFT_EXHAUSTED = "Maximum breaks per shift exhausted";

    private BusinessConstants() {
    }
}

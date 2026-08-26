package com.spatulox;

/**
 * Potion durations, expressed in Minecraft ticks.
 *
 * <p>Deliberately free of any Minecraft import. {@link ExtendedTimePotion} cannot be
 * touched from a plain JVM — reading even a constant off it runs its static
 * initialisers, which call into the registries — so any arithmetic that lives there is
 * unreachable from {@code src/test}. Here it is ordinary Java, and
 * {@code ./gradlew test} covers it.
 *
 * <p>{@link #format(int)} is what the durations are actually judged by: the release
 * checklist reads "golden nugget 8:00 -> 11:00", and that is a rendering of these
 * numbers rather than of the numbers themselves.
 */
public final class PotionDuration {

    /** Minecraft runs at a fixed 20 ticks per second. */
    public static final int TICKS_PER_SECOND = 20;

    public static final int TICKS_PER_MINUTE = TICKS_PER_SECOND * 60;

    /** What a golden nugget upgrades a long potion to. */
    public static final int ELEVEN_MINUTES = minutes(11);

    /** What a golden carrot upgrades an eleven minute potion to. */
    public static final int FIFTEEN_MINUTES = minutes(15);

    private PotionDuration() {
    }

    /**
     * @throws IllegalArgumentException on a negative duration, which would silently
     *     produce a potion that expires the instant it is drunk.
     */
    public static int minutes(int minutes) {
        if (minutes < 0) {
            throw new IllegalArgumentException("a potion cannot last " + minutes + " minutes");
        }
        return minutes * TICKS_PER_MINUTE;
    }

    /** Renders a tick count the way the game shows it on the potion tooltip: "11:00". */
    public static String format(int ticks) {
        if (ticks < 0) {
            throw new IllegalArgumentException("negative duration: " + ticks);
        }
        int totalSeconds = ticks / TICKS_PER_SECOND;
        return String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60);
    }
}

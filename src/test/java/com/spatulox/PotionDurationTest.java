package com.spatulox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The durations the whole mod is about.
 *
 * <p>Worth testing for the same reason the CI's decision layer is: a mistake here does
 * not crash. A potion registered with the wrong tick count builds, boots, passes the
 * headless server test and reaches a player's inventory saying "8:00" where the store
 * page promised "11:00".
 *
 * <p>Nothing here touches Minecraft, which is the entire point — see
 * {@link PotionDuration}.
 */
class PotionDurationTest {

    @Test
    @DisplayName("the two durations the mod advertises")
    void theAdvertisedDurations() {
        // 11:00 and 15:00 are what the store page and the release checklist promise.
        assertEquals("11:00", PotionDuration.format(PotionDuration.ELEVEN_MINUTES));
        assertEquals("15:00", PotionDuration.format(PotionDuration.FIFTEEN_MINUTES));
    }

    @Test
    @DisplayName("a golden carrot always extends a golden nugget")
    void theUpgradeGoesUpwards() {
        // The brewing chain is LONG -> nugget -> 11:00 -> carrot -> 15:00. Swapping
        // the two constants would still register fifty potions and still boot.
        assertTrue(
            PotionDuration.FIFTEEN_MINUTES > PotionDuration.ELEVEN_MINUTES,
            "the golden carrot step must last longer than the golden nugget step");
    }

    @Test
    @DisplayName("the tick counts the mod is built on")
    void minutesConvertToTicks() {
        assertEquals(20 * 60, PotionDuration.minutes(1));
        assertEquals(13200, PotionDuration.minutes(11));
        assertEquals(18000, PotionDuration.minutes(15));
        assertEquals(0, PotionDuration.minutes(0));
    }

    @Test
    @DisplayName("formatting pads the seconds")
    void formatPadsSeconds() {
        assertEquals("0:00", PotionDuration.format(0));
        assertEquals("0:01", PotionDuration.format(20));
        assertEquals("1:05", PotionDuration.format(PotionDuration.minutes(1) + 100));
        // truncation, not rounding: a partial tick is not a second yet
        assertEquals("0:00", PotionDuration.format(19));
    }

    @Test
    @DisplayName("a negative duration is refused rather than registered")
    void negativeDurationsAreRefused() {
        assertThrows(IllegalArgumentException.class, () -> PotionDuration.minutes(-1));
        assertThrows(IllegalArgumentException.class, () -> PotionDuration.format(-1));
    }
}

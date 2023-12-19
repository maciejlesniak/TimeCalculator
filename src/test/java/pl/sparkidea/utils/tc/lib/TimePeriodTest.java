package pl.sparkidea.utils.tc.lib;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TimePeriodTest {

    private static Stream<Arguments> timePeriodParseCases() {
        return Stream.of(
                Arguments.of(
                        "0y 0M 1d 3h 6m 8s",
                        new TimePeriod(
                                8 * 1000 +
                                        6 * 60 * 1000 +
                                        3 * 60 * 60 * 1000 +
                                        24 * 60 * 60 * 1000L)
                ),
                Arguments.of(
                        "1d 3h 6m 8s",
                        new TimePeriod(
                                8 * 1000 +
                                        6 * 60 * 1000 +
                                        3 * 60 * 60 * 1000 +
                                        24 * 60 * 60 * 1000L)
                ),
                Arguments.of(
                        " 3h 1d 8s 6m",
                        new TimePeriod(
                                8 * 1000 +
                                        6 * 60 * 1000 +
                                        3 * 60 * 60 * 1000 +
                                        24 * 60 * 60 * 1000L)
                ),
                Arguments.of(
                        "3h 8s",
                        new TimePeriod(
                                8 * 1000 +
                                        3 * 60 * 60 * 1000L)
                ),
                Arguments.of(
                        "1d6m",
                        new TimePeriod(
                                6 * 60 * 1000L +
                                        24 * 60 * 60 * 1000L)
                ),
                Arguments.of(
                        "3s",
                        new TimePeriod(3 * 1000L)
                ),
                Arguments.of(
                        "3m",
                        new TimePeriod(3 * 60 * 1000L)
                ),
                Arguments.of(
                        "3h",
                        new TimePeriod(3 * 60 * 60 * 1000L)
                ),
                Arguments.of(
                        "3d",
                        new TimePeriod(3 * 24 * 60 * 60 * 1000L)
                ),
                Arguments.of(
                        "3M",
                        new TimePeriod(3 * 30 * 24 * 60 * 60 * 1000L) // 7776000000
                ),
                Arguments.of(
                        "3y",
                        new TimePeriod(3 * 12 * 30 * 24 * 60 * 60 * 1000L)
                )
        );
    }

    private static Stream<Arguments> fromZonedDateTimesReject() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(ZonedDateTime.now(), null),
                Arguments.of(null, ZonedDateTime.now())
        );
    }

    @Test
    void toString_shouldReturnHourWithMinutes_whenNoneYearsAndMonthsGiven() {

        var actual = new TimePeriod(60 * 60 * 1000L + 2 * 60 * 1000L).toString();
        assertEquals("1h 2m", actual);
    }

    @Test
    void toString_shouldReturnHourWithMinutes_whenNegatigeValueGiven() {

        var actual = new TimePeriod(-1 * 60 * 60 * 1000L).toString();
        assertEquals("-1h", actual);
    }

    @Test
    void toString_shouldReturn0m_whenTpZero() {

        var actual = TimePeriod.ZERO.toString();
        assertEquals("0m", actual);
    }

    @ParameterizedTest
    @MethodSource("timePeriodParseCases")
    void fromString_shouldConstructTimePeriod_whenStringGiven(String timePeriodAsString, TimePeriod expected) {
        assertEquals(expected, TimePeriod.from(timePeriodAsString));
    }

    @ParameterizedTest
    @MethodSource("fromZonedDateTimesReject")
    void fromZonedDateTimes_shouldReject_whenAnyFieldIsNull(ZonedDateTime stat, ZonedDateTime end) {
        assertThrows(NullPointerException.class, () -> TimePeriod.from(stat, end));
    }

    @Test
    void fromZonedDateTimes_shouldConstructTimePeriod_whenDatesGiven() {

        var dtStart = ZonedDateTime.parse("2023-12-18T12:30:21.505+01:00");
        var dtFinish = ZonedDateTime.parse("2023-12-18T12:31:22.505+00:00");

        var tp = TimePeriod.from(dtStart, dtFinish);

        assertEquals(new TimePeriod(
                (long) 60 * 1000                      // +1 minute
                        + (long) 60 * 60 * 1000       // +1 hour
                        + (long) 1000                 // +1 second
        ), tp);
    }

    @Test
    void fromZonedDateTimes_shouldConstructTimePeriod_whenDatesMillisDiffGiven() {

        var dtStart = ZonedDateTime.parse("2023-12-18T12:30:21.505+00:00");
        var dtFinish = ZonedDateTime.parse("2023-12-18T12:30:21.605+00:00");

        var tp = TimePeriod.from(dtStart, dtFinish);

        assertEquals(new TimePeriod(100L), tp);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1, 2,      3",
            "1, -2,    -1",
    })
    void plus_shouldAddPeriods(String first, String second, String product) {

        var p1 = new TimePeriod(Long.parseLong(first));
        var p2 = new TimePeriod(Long.parseLong(second));

        var actual = p1.plus(p2);

        var expected = new TimePeriod(Long.parseLong(product));
        assertEquals(expected, actual);
    }

    @Test
    void plus_shouldReturnUnderlyingObject_whenAddedIsNull() {

        var p1 = new TimePeriod(1L);

        assertSame(p1, p1.plus(null));
    }

    @Test
    void asDuration_shouldReturnDuration() {
        assertEquals(Duration.ofMillis(3000), TimePeriod.from("3s").asDuration());
        assertEquals(Duration.ofMillis(3 * 30 * 24 * 60 * 60 * 1000L), TimePeriod.from("3M").asDuration());
    }

    @Test
    void constructor_shouldRejectNullValues() {
        assertThrows(NullPointerException.class, () -> new TimePeriod(null));
    }

    @Test
    void compareTo_shouldReflectLongContract() {

        var ts = new TreeSet<>();
        ts.add(TimePeriod.ZERO);
        ts.add(TimePeriod.from("1h"));
        ts.add(TimePeriod.from("-1h"));

        var tsArray = ts.toArray();

        assertArrayEquals(new TimePeriod[]{
                new TimePeriod(-60L * 60 * 1000),
                new TimePeriod(0L),
                new TimePeriod(60L * 60 * 1000),
        }, tsArray);

    }

    @Test
    void hashCode_reflectsEqualsAndHashCodeContract() {
        var hs = new HashSet<>();
        hs.add(TimePeriod.ZERO);
        hs.add(TimePeriod.from("1h"));
        hs.add(TimePeriod.from("-1h"));
        hs.add(TimePeriod.from("-1h"));
        hs.add(TimePeriod.from("-1h"));

        assertEquals(3, hs.size());
        assertTrue(hs.contains(new TimePeriod(0L)));
    }
}

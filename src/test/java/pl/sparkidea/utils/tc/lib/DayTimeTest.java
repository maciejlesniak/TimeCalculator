package pl.sparkidea.utils.tc.lib;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.HashSet;

import static java.lang.Integer.parseInt;
import static org.junit.jupiter.api.Assertions.*;

class DayTimeTest {

    @ParameterizedTest
    @CsvSource({
            "0, 00:00:00.000",
            "1, 00:00:00.001",
            "61000, 00:01:01.000",
            "3661000, 01:01:01.000",
            "90061000, 01:01:01.000",
    })
    void toString_shouldReflectNoonMiliseconds(String givenString, String expectedString) {
        var current = new DayTime(Long.parseLong(givenString)).toString();
        assertEquals(expectedString, current);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 00:00:00.000",
            "1, 00:00:00.001",
            "61000, 00:01:01.000",
            "3661000, 01:01:01.000",
            "90061000, 01:01:01.000",
            "86399999, 23:59:59.999"
    })
    void fromString_shouldParseStringThatRepresentsDatTime(String expectedString, String givenString) {
        var current = DayTime.from(givenString);
        assertEquals(new DayTime(Long.parseLong(expectedString)), current);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "00:00:00.1000",
            "00:00:60.999",
            "00:60:59.999",
            "24:59:59.999",
    })
    void from_shouldRejectMalformedStrings(String givenString) {
        assertThrows(IllegalStateException.class, () -> DayTime.from(givenString));
    }

    @ParameterizedTest
    @CsvSource({
            "0,0,0,0,           00:00:00.000",
            "0,0,0,1,           00:00:00.001",
            "0,1,1,0,           00:01:01.000",
            "1,1,1,0,           01:01:01.000",
            "23,59,59,999,      23:59:59.999"
    })
    void fromHHMMSSmmmm_shouldCreateDayTimeObject(
            String hours,
            String minutes,
            String seconds,
            String millis,
            String dayTimeStringRepresentation) {

        var current = DayTime.from(
                parseInt(hours),
                parseInt(minutes),
                parseInt(seconds),
                parseInt(millis)
        );
        assertEquals(DayTime.from(dayTimeStringRepresentation), current);
    }

    @ParameterizedTest
    @CsvSource({
            "00:00:00.000,  00:00:00.000,   PT0H0M0S",
            "00:00:00.000,  00:00:00.001,   PT0.001S",
            "00:00:00.001,  00:00:00.000,   PT-0.001S",
            "11:12:13.000,  12:13:14.000,   PT1H1M1S",
    })
    void diff_shouldReturnDurationBetweenTwoDatTimes(
            String start,
            String stop,
            String duration
    ) {
        var startDt = DayTime.from(start);
        var stopDt = DayTime.from(stop);
        var current = startDt.diff(stopDt);
        var expected = Duration.parse(duration);

        assertEquals(expected, current);
    }

    @Test
    void hashCode_reflectsEqualsAndHashCodeContract() {
        var hs = new HashSet<>();
        hs.add(DayTime.ZERO);
        hs.add(DayTime.from("1:30"));
        hs.add(DayTime.from("1:30"));
        hs.add(DayTime.from("1:30"));

        assertEquals(2, hs.size());
        assertTrue(hs.contains(new DayTime(0L)));
    }

}

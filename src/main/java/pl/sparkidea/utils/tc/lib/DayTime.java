package pl.sparkidea.utils.tc.lib;

import java.time.Duration;

import static java.lang.Integer.parseInt;

public record DayTime(long noonMillis) {

    public static final DayTime ZERO = new DayTime(0L);
    private static final long SECOND = 1000L;
    private static final long MINUTE = 60L * SECOND;
    private static final long HOUR = 60L * MINUTE;

    public DayTime(long noonMillis) {
        this.noonMillis = noonMillis % (24 * HOUR);
    }

    public static DayTime from(int hours, int minutes, int seconds, int millis) {
        if (hours > 23) throw new IllegalStateException("Hours must be in boundaries [0,24)");
        if (minutes > 59) throw new IllegalStateException("Minutes must be in boundaries [0,60)");
        if (seconds > 59) throw new IllegalStateException("Seconds must be in boundaries [0,60)");
        if (millis > 999) throw new IllegalStateException("Millis must be in boundaries [0,60)");

        return new DayTime(
                millis                            // millis
                        + seconds * SECOND     // seconds
                        + minutes * MINUTE     // minutes
                        + hours * HOUR         // hours
        );
    }

    public static DayTime from(String wallClockTime) {

        var time = wallClockTime.split("[:.]");
        var hours = parseInt(time[0]);
        var minutes = parseInt(time[1]);
        var seconds = 0;
        var millis = 0;
        if (time.length > 2) {
            seconds = parseInt(time[2]);
        }
        if (time.length > 3) {
            millis = parseInt(time[3]);
        }

        return from(hours, minutes, seconds, millis);
    }

    @Override
    public String toString() {

        var r = noonMillis;
        var hours = r / HOUR;
        r -= hours * HOUR;
        var minutes = r / MINUTE;
        r -= minutes * MINUTE;
        var seconds = r / SECOND;
        r -= seconds * SECOND;
        var millis = r;

        return "%02d:%02d:%02d.%03d".formatted(hours, minutes, seconds, millis);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DayTime dayTime)) return false;

        return noonMillis == dayTime.noonMillis;
    }

    @Override
    public int hashCode() {
        return (int) (noonMillis ^ (noonMillis >>> 32));
    }

    public Duration diff(DayTime otherDayTime) {
        return Duration.ofMillis(otherDayTime.noonMillis - this.noonMillis);
    }
}

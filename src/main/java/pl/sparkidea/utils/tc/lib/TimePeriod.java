package pl.sparkidea.utils.tc.lib;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.pow;
import static java.util.Objects.requireNonNull;

public record TimePeriod(Long millis) implements Comparable<TimePeriod> {

    private static final long SECOND_MS = 1000;
    private static final long MINUTE_MS = 60L * SECOND_MS;
    private static final long HOUR_MS = 60L * MINUTE_MS;
    private static final long DAY_MS = 24L * HOUR_MS;
    private static final long MONTH_MS = 30L * DAY_MS;
    private static final long YEAR_MS = 12L * MONTH_MS;
    public static TimePeriod ZERO = new TimePeriod(0L);

    public TimePeriod {
        requireNonNull(millis, "Cannot construct time period based on NULL");
    }

    @SuppressWarnings("java:S135")
    public static TimePeriod from(String str) {

        var ints = new ArrayList<Integer>();
        var tpMillis = 0L;
        var isPositive = true;

        for (var ch : str.toCharArray()) {
            if (ch == ' ') {
                continue;
            }

            if (ch == '-') {
                isPositive = false;
                continue;
            }

            if (ch >= 48 && ch <= 57) {
                ints.add(ch - 48);
                continue;
            }

            tpMillis += switch (ch) {
                case 'y' -> arrToLong(ints) * YEAR_MS;
                case 'M' -> arrToLong(ints) * MONTH_MS;
                case 'd' -> arrToLong(ints) * DAY_MS;
                case 'h' -> arrToLong(ints) * HOUR_MS;
                case 'm' -> arrToLong(ints) * MINUTE_MS;
                case 's' -> arrToLong(ints) * SECOND_MS;
                default -> throw new IllegalStateException("Unexpected value: " + ch);
            };

            ints = new ArrayList<>();
        }

        return new TimePeriod(isPositive ? tpMillis : tpMillis * -1);
    }

    public static TimePeriod from(ZonedDateTime start, ZonedDateTime finish) {
        requireNonNull(start, "Start instant must not be null");
        requireNonNull(finish, "End instant must not be null");
        var diffMillis = finish.toInstant().toEpochMilli() - start.toInstant().toEpochMilli();
        return new TimePeriod(diffMillis);
    }

    private static Long arrToLong(List<Integer> ints) {
        if (ints == null || ints.isEmpty()) {
            return null;
        }

        var valueLong = 0L;
        for (var i = 0; i < ints.size(); i++) {
            valueLong += ints.get(i) * pow10(ints.size() - i - 1);
        }

        return valueLong;
    }

    private static long pow10(int a) {
        return (long) pow(10, a);
    }

    public TimePeriod plus(TimePeriod timePeriod) {
        if (timePeriod == null) {
            return this;
        }
        return new TimePeriod(this.millis + timePeriod.millis);
    }

    @Override
    public String toString() {

        if (millis == 0) {
            return "0m";
        }

        var isPositive = millis > 0;

        var tp = isPositive ? (double) millis : millis * -1D;
        var arr = new ArrayList<String>(6);

        var r = (long) Math.floor(tp / YEAR_MS);
        arr.add(r > 0 ? "%dy".formatted(r) : "");
        tp -= r * YEAR_MS;

        r = (long) Math.floor(tp / MONTH_MS);
        arr.add(r > 0 ? "%dM".formatted(r) : "");
        tp -= r * MONTH_MS;

        r = (long) Math.floor(tp / DAY_MS);
        arr.add(r > 0 ? "%dd".formatted(r) : "");
        tp -= r * DAY_MS;

        r = (long) Math.floor(tp / HOUR_MS);
        arr.add(r > 0 ? "%dh".formatted(r) : "");
        tp -= r * HOUR_MS;

        r = (long) Math.floor(tp / MINUTE_MS);
        arr.add(r > 0 ? "%dm".formatted(r) : "");
        tp -= r * MINUTE_MS;

        r = (long) Math.floor(tp / SECOND_MS);
        arr.add(r > 0 ? "%ds".formatted(r) : "");
        r -= r * SECOND_MS;

        arr.add(r > 0 ? ".%d".formatted(r) : "");

        var str = String.join(" ", arr).trim();

        return isPositive ? str : "-%s".formatted(str);
    }

    public Duration asDuration() {
        return Duration.ofMillis(this.millis);
    }

    @Override
    public int compareTo(TimePeriod other) {
        return this.millis.compareTo(other.millis);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimePeriod that)) return false;

        return millis.equals(that.millis);
    }

    @Override
    public int hashCode() {
        return millis.hashCode();
    }
}

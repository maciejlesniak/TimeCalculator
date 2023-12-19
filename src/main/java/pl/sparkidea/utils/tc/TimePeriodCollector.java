package pl.sparkidea.utils.tc;

import pl.sparkidea.utils.tc.lib.DayTime;
import pl.sparkidea.utils.tc.lib.TimePeriod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class TimePeriodCollector {

    private static final Pattern HOURS_SPAN_PATTERN = Pattern.compile("^([\\d]{1,2}):([\\d]{1,2})( )*-( )*([\\d]{1,2}):([\\d]{1,2})$");
    private static final Pattern PERIOD_PATTERN = Pattern.compile("^(-?)(\\d+[yMdhms]\\w*){1,6}$");

    private TimePeriod timePeriod;

    public TimePeriodCollector(TimePeriod timePeriod) {
        this.timePeriod = timePeriod;
    }

    public TimePeriodCollector() {
        this(TimePeriod.ZERO);
    }

    public TimePeriod collect(InputStream in) throws IOException {
        try (var buffer = new BufferedReader(new InputStreamReader(in))) {
            var line = buffer.readLine();
            while (line != null && !line.isEmpty()) {
                add(line.trim());
                line = buffer.readLine();
            }
        }

        return this.timePeriod;
    }

    private void add(String line) {

        if (HOURS_SPAN_PATTERN.matcher(line).matches()) {
            var dayTimes = line.split("-");
            var start = DayTime.from(dayTimes[0].trim());
            var stop = DayTime.from(dayTimes[1].trim());
            this.timePeriod = timePeriod.plus(new TimePeriod(start.diff(stop).toMillis()));
            return;
        }

        if (PERIOD_PATTERN.matcher(line).matches()) {
            this.timePeriod = timePeriod.plus(TimePeriod.from(line));
            return;
        }

        throw new IllegalStateException("Unrecognized pattern: [%s]".formatted(line));
    }
}

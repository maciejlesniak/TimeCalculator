package pl.sparkidea.utils.tc;

import org.junit.jupiter.api.Test;
import pl.sparkidea.utils.tc.lib.TimePeriod;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimePeriodCollectorTest {

    @Test
    void collect_shouldAddTimePeriods_whenStreamGiven() throws IOException {

        var resourceStream = new ByteArrayInputStream("""
                 5m
                18:35- 19:40
                  1h
                30m
                -3h
                """.getBytes());
        var actualTp = new TimePeriodCollector().collect(resourceStream);

        assertEquals(TimePeriod.from("-20m"), actualTp);
    }
}

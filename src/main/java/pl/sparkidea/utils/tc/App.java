package pl.sparkidea.utils.tc;

import java.io.IOException;

@SuppressWarnings("java:S106")
public class App {

    public static void main(String[] args) throws IOException {
        var collectedTp = new TimePeriodCollector().collect(System.in);
        System.out.println(collectedTp);
    }


}

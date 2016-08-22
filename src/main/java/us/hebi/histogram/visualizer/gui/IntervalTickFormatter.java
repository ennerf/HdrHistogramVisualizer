package us.hebi.histogram.visualizer.gui;

import javafx.util.StringConverter;

import java.util.concurrent.TimeUnit;

/**
 * @author Florian Enner < florian @ hebirobotics.com >
 * @since 21 Aug 2016
 */
enum IntervalTickFormatter {

    Seconds("sec"),
    HH_MM("HH:MM"),
    HH_MM_SS("HH:MM:SS");

    public StringConverter<Number> getConverter() {
        return converter;
    }

    private final StringConverter<Number> converter = new StringConverter<Number>() {
        @Override
        public String toString(Number object) {

            long totalSec = object.longValue();
            long seconds = totalSec;

            long hours = TimeUnit.SECONDS.toHours(seconds);
            seconds -= TimeUnit.HOURS.toSeconds(hours);

            long minutes = TimeUnit.SECONDS.toMinutes(seconds);
            seconds -= TimeUnit.MINUTES.toSeconds(minutes);

            switch (IntervalTickFormatter.this) {

                case HH_MM:
                    return String.format("%02d:%02d", hours, minutes);

                case HH_MM_SS:
                    return String.format("%02d:%02d:%02d", hours, minutes, seconds);

                case Seconds:
                default:
                    return String.valueOf(totalSec);

            }
        }

        @Override
        public Number fromString(String string) {
            return null;
        }
    };

    IntervalTickFormatter(String string) {
        this.string = string;
    }

    final String string;

    @Override
    public String toString() {
        return string;
    }

}

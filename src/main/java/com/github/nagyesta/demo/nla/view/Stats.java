package com.github.nagyesta.demo.nla.view;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.ToIntFunction;

public class Stats<T> {

    private final String sumAsString;
    private final String minAsString;
    private final String avgAsString;
    private final String maxAsString;

    public Stats(final Collection<T> population, final ToIntFunction<T> metricFunction) {
        Objects.requireNonNull(population);
        Objects.requireNonNull(metricFunction);

        final int sum = population.stream().mapToInt(metricFunction).sum();
        this.sumAsString = String.format("%6d", sum);

        final OptionalInt min = population.stream().mapToInt(metricFunction).min();
        this.minAsString = min.isPresent() ? String.format("%5d", min.getAsInt()) : "  N/A";

        final OptionalDouble avg = population.stream().mapToInt(metricFunction).average();
        this.avgAsString = avg.isPresent() ? String.format("%7.1f", avg.getAsDouble()) : "    N/A";

        final OptionalInt max = population.stream().mapToInt(metricFunction).max();
        this.maxAsString = max.isPresent() ? String.format("%5d", max.getAsInt()) : "  N/A";
    }

    public String format(final String messageFormat) {
        return MessageFormat.format(Objects.requireNonNull(messageFormat), sumAsString, minAsString, avgAsString, maxAsString);
    }
}

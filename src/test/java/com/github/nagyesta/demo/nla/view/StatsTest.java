package com.github.nagyesta.demo.nla.view;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

class StatsTest {

    private static final String BASIC_FORMAT_MIN_AVG_MAX_SUM = "min={1}, avg={2}, max={3}, sum={0}";
    private static final String ORDERED_FORMAT_MIN_AVG_MAX_SUM = "sum={0}, min={1}, avg={2}, max={3}";
    private static final String FORMAT_WITH_GAPS_MIN_MAX = "sum={0}, min={1}, avg={2}, max={3}";
    private static final ToIntFunction<Integer> IDENTITY = i -> i;

    private static Stream<Arguments> calculationProvider() {
        return Stream.of(BASIC_FORMAT_MIN_AVG_MAX_SUM, ORDERED_FORMAT_MIN_AVG_MAX_SUM, FORMAT_WITH_GAPS_MIN_MAX)
                .flatMap(format -> Stream.<Arguments>builder()
                        .add(Arguments.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), IDENTITY, format,
                                MessageFormat.format(format, "    55", "    1", "    5.5", "   10")))
                        .add(Arguments.of(Arrays.asList(9, 5, 1, 7, 3), IDENTITY, format,
                                MessageFormat.format(format, "    25", "    1", "    5.0", "    9")))
                        .add(Arguments.of(Collections.singletonList(9), IDENTITY, format,
                                MessageFormat.format(format, "     9", "    9", "    9.0", "    9")))
                        .add(Arguments.of(Collections.emptyList(), IDENTITY, format,
                                MessageFormat.format(format, "     0", "  N/A", "    N/A", "  N/A")))
                        .build());
    }

    @ParameterizedTest
    @MethodSource("calculationProvider")
    void testFormatShouldUseCalculatedMetricWhenCalled(
            final Collection<Integer> collection, final ToIntFunction<Integer> function,
            final String format, final String expected) {
        //given

        //when
        final Stats<Integer> underTest = new Stats<>(collection, function);
        final String actual = underTest.format(format);

        //then
        Assertions.assertEquals(expected, actual);
    }
}

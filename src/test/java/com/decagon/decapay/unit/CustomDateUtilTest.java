package com.decagon.decapay.unit;

import com.decagon.decapay.utils.CustomDateUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(MockitoExtension.class)
public class CustomDateUtilTest {

//    @Test
    @ParameterizedTest
    @MethodSource("getMonthProvider")
    void testShouldGetDateFromMonth( short year, short month, LocalDateTime expected) {
        assert expected.equals(CustomDateUtil.getDateFromMonth(year, month, firstDayOfMonth()));
        //Given
    }
    //short year, short month, TemporalAdjuster adjuster

    static Stream<Arguments> getMonthProvider() {
        return Stream.of(
                arguments((short) 2022, (short) 1, LocalDateTime.of(2022,1, 1, 0, 0, 0, 0)),
                arguments((short) 2022, (short) 2, LocalDateTime.of(2022,2, 1, 0, 0, 0, 0)),
                arguments((short) 2010, (short) 4, LocalDateTime.of(2022,2, 1, 0, 0, 0, 0)),
//                arguments((short) 2010, (short) 4, LocalDateTime.of(2022,2, 1, 0, 0, 0, 0)),
                arguments((short) LocalDateTime.now().getYear(), (short) LocalDateTime.now().getMonthValue(), LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue(), 1, 0, 0, 0, 0))
//                arguments((short) LocalDateTime.now().getYear(), (short) LocalDateTime.now().getMonthValue(), LocalDateTime.now().truncatedTo(ChronoUnit.DAYS))
        );
    }
}

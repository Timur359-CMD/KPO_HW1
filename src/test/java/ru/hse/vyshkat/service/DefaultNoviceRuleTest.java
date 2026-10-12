package ru.hse.vyshkat.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.hse.vyshkat.domain.Bicycle;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultNoviceRuleTest {

    private final NoviceRule rule = new DefaultNoviceRule(6, 20);

    @ParameterizedTest(name = "простота {0}, износ {1}% -> подходит: {2}")
    @CsvSource({"5,0,false", "6,0,true", "10,20,true", "10,21,false", "6,20,true", "1,0,false"})
    void boundaries(int ease, int wear, boolean expected) {
        assertEquals(expected, rule.isSuitable(new Bicycle("B", ease, wear)));
    }
}
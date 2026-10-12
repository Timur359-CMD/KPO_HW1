package ru.hse.vyshkat.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.hse.vyshkat.domain.Bicycle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class StandardServiceCenterTest {

    private final ServiceCenter center = new StandardServiceCenter(30);

    @ParameterizedTest(name = "износ {0}% -> принят: {1}")
    @CsvSource({"0,true", "30,true", "31,false", "100,false"})
    void decisionDependsOnWearThreshold(int wear, boolean expectedAccepted) {
        InspectionResult result = center.inspect(new Bicycle("B", 5, wear));

        assertEquals(expectedAccepted, result.accepted());
    }

    @Test
    void rejectionExplainsWhy() {
        InspectionResult result = center.inspect(new Bicycle("B", 5, 60));

        assertFalse(result.accepted());
        assertEquals("износ 60% выше допустимых 30%", result.reason());
    }
}
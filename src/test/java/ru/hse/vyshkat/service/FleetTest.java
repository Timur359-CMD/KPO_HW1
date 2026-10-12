package ru.hse.vyshkat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hse.vyshkat.domain.Helmet;
import ru.hse.vyshkat.domain.Scooter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FleetTest {

    @Mock
    ServiceCenter serviceCenter;
    @Mock
    InventoryNumberGenerator numbers;

    private Fleet fleet;

    @BeforeEach
    void setUp() {
        fleet = new Fleet(serviceCenter, numbers);
    }

    @Test
    void acceptedTransportGetsNumberAndEntersFleet() {
        Scooter scooter = new Scooter("S", 8, 10, 0.5);
        when(serviceCenter.inspect(scooter)).thenReturn(InspectionResult.accept());
        when(numbers.next()).thenReturn("VK-0001");

        InspectionResult result = fleet.acceptTransport(scooter);

        assertTrue(result.accepted());
        assertEquals("VK-0001", scooter.getInventoryNumber());
        assertEquals(List.of(scooter), fleet.getTransport());
    }

    @Test
    void rejectedTransportNeitherEntersFleetNorGetsNumber() {
        Scooter scooter = new Scooter("S", 8, 10, 0.5);
        when(serviceCenter.inspect(scooter)).thenReturn(InspectionResult.reject("тормоза не работают"));

        InspectionResult result = fleet.acceptTransport(scooter);

        assertFalse(result.accepted());
        assertEquals("тормоза не работают", result.reason());
        assertTrue(fleet.getTransport().isEmpty());
        assertThrows(IllegalStateException.class, scooter::getInventoryNumber);
        verifyNoInteractions(numbers);
    }

    @Test
    void equipmentGetsNumberWithoutInspection() {
        Helmet helmet = new Helmet("H");
        when(numbers.next()).thenReturn("VK-0001");

        fleet.registerEquipment(helmet);

        assertEquals("VK-0001", helmet.getInventoryNumber());
        assertEquals(List.of(helmet), fleet.getEquipment());
        verifyNoInteractions(serviceCenter);
    }

    @Test
    void allInventoriedListsTransportFirstThenEquipment() {
        Scooter scooter = new Scooter("S", 8, 10, 0.5);
        Helmet helmet = new Helmet("H");
        when(serviceCenter.inspect(scooter)).thenReturn(InspectionResult.accept());
        when(numbers.next()).thenReturn("VK-0001", "VK-0002");

        fleet.registerEquipment(helmet);
        fleet.acceptTransport(scooter);

        assertEquals(List.of(scooter, helmet), fleet.getAllInventoried());
    }

    @Test
    void fakeServiceCenterCanBeALambda() {
        Fleet strictFleet = new Fleet(t -> InspectionResult.reject("fake: всё отклонять"),
                new SequentialInventoryNumberGenerator("T-"));

        InspectionResult result = strictFleet.acceptTransport(new Scooter("S", 8, 0, 0.5));

        assertFalse(result.accepted());
        assertTrue(strictFleet.getAllInventoried().isEmpty());
    }
}
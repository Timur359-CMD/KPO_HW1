package ru.hse.vyshkat.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainTest {

    @Test
    void easeOutsideOneToTenIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Bicycle("B", 0, 10));
        assertThrows(IllegalArgumentException.class, () -> new Bicycle("B", 11, 10));
        assertDoesNotThrow(() -> {
            new Bicycle("B", 1, 10);
            new Bicycle("B", 10, 10);
        });
    }

    @Test
    void wearOutsideZeroToHundredIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Bicycle("B", 5, -1));
        assertThrows(IllegalArgumentException.class, () -> new Bicycle("B", 5, 101));
    }

    @Test
    void blankNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Helmet("  "));
        assertThrows(IllegalArgumentException.class, () -> new Helmet(null));
    }

    @Test
    void negativeEnergyIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Scooter("S", 5, 5, -0.1));
        assertThrows(IllegalArgumentException.class, () -> new ChargingCabinet("C", -1));
    }

    @Test
    void inventoryNumberCanBeAssignedOnlyOnce() {
        Helmet helmet = new Helmet("H");
        helmet.assignInventoryNumber("VK-0001");

        assertEquals("VK-0001", helmet.getInventoryNumber());
        assertThrows(IllegalStateException.class, () -> helmet.assignInventoryNumber("VK-0002"));
        assertEquals("VK-0001", helmet.getInventoryNumber());
    }

    @Test
    void inventoryNumberIsUnavailableBeforeAssignment() {
        Helmet helmet = new Helmet("H");

        assertThrows(IllegalStateException.class, helmet::getInventoryNumber);
    }

    @Test
    void onlyElectricDevicesConsumeEnergy() {
        Object scooter = new Scooter("S", 5, 5, 0.5);
        Object bicycle = new Bicycle("B", 5, 5);
        Object electricBicycle = new ElectricBicycle("E", 5, 5, 0.4);
        Object helmet = new Helmet("H");
        Object cabinet = new ChargingCabinet("C", 2.0);

        assertInstanceOf(EnergyConsumer.class, scooter);
        assertFalse(bicycle instanceof EnergyConsumer);
        assertInstanceOf(EnergyConsumer.class, electricBicycle);
        assertFalse(helmet instanceof EnergyConsumer);
        assertInstanceOf(EnergyConsumer.class, cabinet);
    }

    @Test
    void electricBicycleIsStillABicycle() {
        ElectricBicycle electric = new ElectricBicycle("E", 7, 5, 0.4);

        assertInstanceOf(Bicycle.class, electric);
        assertEquals("Электровелосипед", electric.getKind());
        assertEquals(0.4, electric.getDailyEnergyKwh(), 1e-9);
    }

    @Test
    void kindsCreateMatchingObjects() {
        assertInstanceOf(Scooter.class, TransportKind.SCOOTER.create("S", 5, 5, 0.5));
        assertInstanceOf(Bicycle.class, TransportKind.BICYCLE.create("B", 5, 5, 0));
        assertInstanceOf(ElectricBicycle.class, TransportKind.ELECTRIC_BICYCLE.create("E", 5, 5, 0.5));
        assertInstanceOf(Helmet.class, EquipmentKind.HELMET.create("H", 0));
        assertInstanceOf(ChargingCabinet.class, EquipmentKind.CHARGING_CABINET.create("C", 3));
        assertTrue(TransportKind.SCOOTER.needsEnergy());
        assertFalse(TransportKind.BICYCLE.needsEnergy());
        assertFalse(EquipmentKind.HELMET.needsEnergy());
    }
}
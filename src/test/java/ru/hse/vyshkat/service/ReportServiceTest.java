package ru.hse.vyshkat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hse.vyshkat.domain.Bicycle;
import ru.hse.vyshkat.domain.ChargingCabinet;
import ru.hse.vyshkat.domain.ElectricBicycle;
import ru.hse.vyshkat.domain.Helmet;
import ru.hse.vyshkat.domain.Inventoriable;
import ru.hse.vyshkat.domain.Scooter;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportServiceTest {

    private Scooter scooter;
    private ElectricBicycle electricBicycle;
    private ReportService reports;

    @BeforeEach
    void setUp() {
        Fleet fleet = new Fleet(t -> InspectionResult.accept(), new SequentialInventoryNumberGenerator("VK-"));
        scooter = new Scooter("Scooter", 8, 10, 0.5);
        electricBicycle = new ElectricBicycle("EBike", 7, 5, 0.4);
        fleet.acceptTransport(scooter);
        fleet.acceptTransport(new Bicycle("Bike", 3, 5));
        fleet.acceptTransport(electricBicycle);
        fleet.registerEquipment(new Helmet("Helmet"));
        fleet.registerEquipment(new ChargingCabinet("Cabinet", 3.5));
        reports = new ReportService(fleet, new DefaultNoviceRule(6, 20));
    }

    @Test
    void totalUnitsCountsTransportAndEquipment() {
        assertEquals(5, reports.totalUnits());
    }

    @Test
    void countByKindGroupsUnits() {
        Map<String, Long> expected = Map.of(
                "Самокат", 1L, "Велосипед", 1L, "Электровелосипед", 1L, "Шлем", 1L, "Зарядный шкаф", 1L);

        assertEquals(expected, reports.countByKind());
    }

    @Test
    void totalEnergySumsOnlyEnergyConsumers() {
        // 0.5 + 0.4 + 3.5; обычный велосипед и шлем энергию не потребляют
        assertEquals(4.4, reports.totalDailyEnergyKwh(), 1e-9);
    }

    @Test
    void noviceListContainsOnlySuitableTransport() {
        assertEquals(List.of(scooter, electricBicycle), reports.noviceFriendlyTransport());
    }

    @Test
    void inventoryListHasNumbersForEverythingInOrder() {
        List<String> numbers = reports.inventoryList().stream().map(Inventoriable::getInventoryNumber).toList();

        assertEquals(List.of("VK-0001", "VK-0002", "VK-0003", "VK-0004", "VK-0005"), numbers);
    }

    @Test
    void rejectedTransportDoesNotAffectReports() {
        Fleet strict = new Fleet(t -> InspectionResult.reject("нет"), new SequentialInventoryNumberGenerator("VK-"));
        strict.acceptTransport(new Scooter("S", 9, 0, 2.0));
        ReportService empty = new ReportService(strict, new DefaultNoviceRule(6, 20));

        assertEquals(0, empty.totalUnits());
        assertEquals(0.0, empty.totalDailyEnergyKwh(), 1e-9);
        assertTrue(empty.noviceFriendlyTransport().isEmpty());
    }
}
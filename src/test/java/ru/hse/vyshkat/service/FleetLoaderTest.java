package ru.hse.vyshkat.service;

import org.junit.jupiter.api.Test;
import ru.hse.vyshkat.domain.EnergyConsumer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FleetLoaderTest {

    private final Fleet fleet = new Fleet(new StandardServiceCenter(30), new SequentialInventoryNumberGenerator("VK-"));
    private final FleetLoader loader = new FleetLoader(fleet);

    @Test
    void transportAndEquipmentAreLoaded() {
        List<FleetLoader.Message> messages = loader.load(List.of(
                "SCOOTER;Xiaomi;8;10;0.55",
                "BICYCLE;Stels;9;5;",
                "HELMET;Livall;;;",
                "CHARGING_CABINET;ZSh;;;3.5"));

        assertEquals(4, messages.size());
        assertTrue(messages.stream().allMatch(FleetLoader.Message::ok));
        assertEquals(2, fleet.getTransport().size());
        assertEquals(2, fleet.getEquipment().size());
    }

    @Test
    void commentsAndBlankLinesAreSkipped() {
        List<FleetLoader.Message> messages = loader.load(List.of("# комментарий", "", "   "));

        assertTrue(messages.isEmpty());
    }

    @Test
    void transportRejectedByInspectionIsReportedAndNotAdded() {
        List<FleetLoader.Message> messages = loader.load(List.of("SCOOTER;Old;7;60;0.5"));

        assertFalse(messages.get(0).ok());
        assertTrue(messages.get(0).text().contains("отклонён"));
        assertTrue(fleet.getTransport().isEmpty());
    }

    @Test
    void badLinesAreReportedWithLineNumbersAndLoadingContinues() {
        List<FleetLoader.Message> messages = loader.load(List.of(
                "UNKNOWN;X;1;1;1",
                "SCOOTER;X;abc;10;1",
                "SCOOTER;X;11;10;1",
                "SCOOTER;X;5;10;",
                "BICYCLE;Good;5;5;",
                "SCOOTER;OnlyThreeFields;5"));

        assertEquals(6, messages.size());
        assertEquals(1, messages.get(0).lineNumber());
        assertFalse(messages.get(0).ok());
        assertFalse(messages.get(1).ok());
        assertFalse(messages.get(2).ok());
        assertFalse(messages.get(3).ok());
        assertTrue(messages.get(4).ok());
        assertFalse(messages.get(5).ok());
        assertEquals(1, fleet.getTransport().size());
    }

    @Test
    void commaIsAcceptedAsDecimalSeparator() {
        loader.load(List.of("SCOOTER;S;5;5;0,5"));

        EnergyConsumer scooter = (EnergyConsumer) fleet.getTransport().get(0);
        assertEquals(0.5, scooter.getDailyEnergyKwh(), 1e-9);
    }

    @Test
    void kindIsCaseInsensitive() {
        List<FleetLoader.Message> messages = loader.load(List.of("helmet;Livall;;;"));

        assertTrue(messages.get(0).ok());
    }
}
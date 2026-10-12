package ru.hse.vyshkat.service;

import com.google.inject.Inject;
import ru.hse.vyshkat.domain.Equipment;
import ru.hse.vyshkat.domain.EquipmentKind;
import ru.hse.vyshkat.domain.Transport;
import ru.hse.vyshkat.domain.TransportKind;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class FleetLoader {

    public record Message(int lineNumber, boolean ok, String text) {
    }

    private static final int FIELDS = 5;

    private final Fleet fleet;

    @Inject
    public FleetLoader(Fleet fleet) {
        this.fleet = fleet;
    }

    public List<Message> load(List<String> lines) {
        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            int lineNumber = i + 1;
            try {
                messages.add(loadLine(lineNumber, line));
            } catch (IllegalArgumentException | IllegalStateException e) {
                messages.add(new Message(lineNumber, false, "ошибка: " + e.getMessage()));
            }
        }
        return messages;
    }

    private Message loadLine(int lineNumber, String line) {
        String[] fields = line.split(";", -1);
        if (fields.length != FIELDS) {
            throw new IllegalArgumentException("нужно " + FIELDS + " полей через «;», найдено " + fields.length);
        }
        String code = fields[0].trim().toUpperCase(Locale.ROOT);
        String name = fields[1].trim();

        TransportKind transportKind = findKind(TransportKind.values(), code);
        if (transportKind != null) {
            Transport transport = transportKind.create(name,
                    parseInt(fields[2], "простота"),
                    parseInt(fields[3], "износ"),
                    parseEnergy(fields[4], transportKind.needsEnergy()));
            InspectionResult result = fleet.acceptTransport(transport);
            if (result.accepted()) {
                return new Message(lineNumber, true, "принят: " + transport + " -> " + transport.getInventoryNumber());
            }
            return new Message(lineNumber, false, "отклонён: " + transport + " (" + result.reason() + ")");
        }

        EquipmentKind equipmentKind = findKind(EquipmentKind.values(), code);
        if (equipmentKind != null) {
            Equipment item = equipmentKind.create(name, parseEnergy(fields[4], equipmentKind.needsEnergy()));
            fleet.registerEquipment(item);
            return new Message(lineNumber, true, "на балансе: " + item + " -> " + item.getInventoryNumber());
        }

        throw new IllegalArgumentException("неизвестный вид «" + fields[0].trim() + "»");
    }

    private static <E extends Enum<E>> E findKind(E[] kinds, String code) {
        for (E kind : kinds) {
            if (kind.name().equals(code)) {
                return kind;
            }
        }
        return null;
    }

    private static int parseInt(String text, String field) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(field + ": нужно целое число, получено «" + text.trim() + "»");
        }
    }

    private static double parseEnergy(String text, boolean required) {
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            if (required) {
                throw new IllegalArgumentException("не указан расход энергии");
            }
            return 0;
        }
        try {
            return Double.parseDouble(trimmed.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("расход энергии: нужно число, получено «" + trimmed + "»");
        }
    }
}
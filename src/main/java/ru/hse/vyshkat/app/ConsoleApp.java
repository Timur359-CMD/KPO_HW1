package ru.hse.vyshkat.app;

import com.google.inject.Inject;
import ru.hse.vyshkat.domain.Equipment;
import ru.hse.vyshkat.domain.EquipmentKind;
import ru.hse.vyshkat.domain.EnergyConsumer;
import ru.hse.vyshkat.domain.Inventoriable;
import ru.hse.vyshkat.domain.Transport;
import ru.hse.vyshkat.domain.TransportKind;
import ru.hse.vyshkat.service.Fleet;
import ru.hse.vyshkat.service.FleetLoader;
import ru.hse.vyshkat.service.InspectionResult;
import ru.hse.vyshkat.service.ReportService;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Function;

public class ConsoleApp {

    private final Fleet fleet;
    private final ReportService reports;
    private final FleetLoader loader;
    private final Scanner in;
    private final PrintStream out;

    @Inject
    public ConsoleApp(Fleet fleet, ReportService reports, FleetLoader loader, Scanner in, PrintStream out) {
        this.fleet = fleet;
        this.reports = reports;
        this.loader = loader;
        this.in = in;
        this.out = out;
    }

    public void run() {
        out.println("ВышКат — учёт парка. «Доедем до дедлайна»");
        try {
            while (true) {
                printMenu();
                switch (ask("> ")) {
                    case "1" -> safely(this::addTransport);
                    case "2" -> safely(this::addEquipment);
                    case "3" -> safely(this::loadFromFile);
                    case "4" -> printSummary();
                    case "5" -> printNoviceList();
                    case "6" -> printInventory();
                    case "0" -> {
                        out.println("До встречи!");
                        return;
                    }
                    default -> out.println("Неизвестная команда");
                }
            }
        } catch (NoSuchElementException endOfInput) {
            out.println("Ввод завершён.");
        }
    }

    private void printMenu() {
        out.println();
        out.println("1. Принять транспорт (с техосмотром)");
        out.println("2. Поставить вещь на баланс");
        out.println("3. Загрузить данные из файла (CSV)");
        out.println("4. Отчёт: число единиц и суммарная энергия");
        out.println("5. Техника для новичков");
        out.println("6. Инвентарные номера техники и вещей");
        out.println("0. Выход");
    }

    private void addTransport() {
        TransportKind kind = askChoice("Вид транспорта", TransportKind.values(), TransportKind::getLabel);
        String name = ask("Название/модель: ");
        int ease = askInt("Простота для новичка (1-10): ");
        int wear = askInt("Износ, % (0-100): ");
        double energy = kind.needsEnergy() ? askDouble("Суточный расход энергии, кВт·ч: ") : 0;

        Transport transport = kind.create(name, ease, wear, energy);
        InspectionResult result = fleet.acceptTransport(transport);
        if (result.accepted()) {
            out.println("  [ПРИНЯТ]    " + transport + " -> " + transport.getInventoryNumber());
        } else {
            out.println("  [ОТКЛОНЁН]  " + transport + ": " + result.reason());
        }
    }

    private void addEquipment() {
        EquipmentKind kind = askChoice("Вид вещи", EquipmentKind.values(), EquipmentKind::getLabel);
        String name = ask("Название: ");
        double energy = kind.needsEnergy() ? askDouble("Суточный расход энергии, кВт·ч: ") : 0;

        Equipment item = kind.create(name, energy);
        fleet.registerEquipment(item);
        out.println("  Поставлено на баланс: " + item + " -> " + item.getInventoryNumber());
    }
    private void loadFromFile() {
        String path = ask("Путь к файлу (например, sample-data/fleet.csv): ");
        List<String> lines;
        try {
            lines = Files.readAllLines(Path.of(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            out.println("  Не удалось прочитать файл «" + path + "»");
            return;
        }
        List<FleetLoader.Message> messages = loader.load(lines);
        for (FleetLoader.Message message : messages) {
            out.println("  строка " + message.lineNumber() + ": " + message.text());
        }
        long ok = messages.stream().filter(FleetLoader.Message::ok).count();
        out.println("Обработано строк: " + messages.size() + ", успешно: " + ok);
    }

    private <T> T askChoice(String title, T[] items, Function<T, String> label) {
        out.println(title + ":");
        for (int i = 0; i < items.length; i++) {
            out.println("  " + (i + 1) + ". " + label.apply(items[i]));
        }
        int number = askInt("Номер: ");
        if (number < 1 || number > items.length) {
            throw new IllegalArgumentException("Выберите число от 1 до " + items.length);
        }
        return items[number - 1];
    }

    private String ask(String prompt) {
        out.print(prompt);
        return in.nextLine().trim();
    }

    private int askInt(String prompt) {
        String text = ask(prompt);
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Нужно целое число, получено: «" + text + "»");
        }
    }

    private double askDouble(String prompt) {
        String text = ask(prompt);
        try {
            return Double.parseDouble(text.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Нужно число, получено: «" + text + "»");
        }
    }

    private void safely(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException | IllegalStateException e) {
            out.println("  Ошибка: " + e.getMessage());
        }
    }

    private void printSummary() {
        out.println("--- Отчёт по парку ---");
        out.println("Всего единиц на балансе: " + reports.totalUnits());
        reports.countByKind().forEach((kind, count) -> out.println("  " + kind + ": " + count));
        out.println(String.format(Locale.ROOT,
                "Суммарное суточное потребление энергии: %.2f кВт·ч", reports.totalDailyEnergyKwh()));
    }

    private void printNoviceList() {
        out.println("--- Техника для новичков ---");
        List<Transport> suitable = reports.noviceFriendlyTransport();
        if (suitable.isEmpty()) {
            out.println("  (подходящей техники нет)");
        }
        for (Transport t : suitable) {
            out.println(String.format(Locale.ROOT, "  %s  %s | простота %d/10, износ %d%%",
                    t.getInventoryNumber(), t, t.getNoviceEase(), t.getWearPercent()));
        }
    }

    private void printInventory() {
        out.println("--- Инвентарные номера: техника и вещи ---");
        List<Inventoriable> all = reports.inventoryList();
        if (all.isEmpty()) {
            out.println("  (на балансе пока ничего нет)");
        }
        for (Inventoriable item : all) {
            String energy = item instanceof EnergyConsumer consumer
                    ? String.format(Locale.ROOT, " | %.2f кВт·ч/сут", consumer.getDailyEnergyKwh())
                    : "";
            out.println("  " + item.getInventoryNumber() + "  " + item + energy);
        }
    }
}
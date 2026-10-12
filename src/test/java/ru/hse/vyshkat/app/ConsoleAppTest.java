package ru.hse.vyshkat.app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.hse.vyshkat.service.DefaultNoviceRule;
import ru.hse.vyshkat.service.Fleet;
import ru.hse.vyshkat.service.FleetLoader;
import ru.hse.vyshkat.service.ReportService;
import ru.hse.vyshkat.service.SequentialInventoryNumberGenerator;
import ru.hse.vyshkat.service.StandardServiceCenter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleAppTest {

    private static String run(String input) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        Fleet fleet = new Fleet(new StandardServiceCenter(30), new SequentialInventoryNumberGenerator("VK-"));
        ReportService reports = new ReportService(fleet, new DefaultNoviceRule(6, 20));
        new ConsoleApp(fleet, reports, new FleetLoader(fleet), new Scanner(input), out).run();
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void exitCommandStopsTheApp() {
        assertTrue(run("0\n").contains("До встречи!"));
    }

    @Test
    void endOfInputStopsTheAppGracefully() {
        assertTrue(run("").contains("Ввод завершён"));
    }

    @Test
    void unknownCommandIsReportedAndAppContinues() {
        String output = run("xyz\n0\n");

        assertTrue(output.contains("Неизвестная команда"));
        assertTrue(output.contains("До встречи!"));
    }

    @Test
    void healthyScooterIsAcceptedAndCounted() {
        // 1 = принять транспорт, 1 = самокат, имя, простота, износ, энергия; 4 = отчёт; 0 = выход
        String output = run("1\n1\nXiaomi\n8\n10\n0.5\n4\n0\n");

        assertTrue(output.contains("[ПРИНЯТ]    Самокат «Xiaomi» -> VK-0001"));
        assertTrue(output.contains("Всего единиц на балансе: 1"));
        assertTrue(output.contains("Суммарное суточное потребление энергии: 0.50 кВт·ч"));
    }

    @Test
    void wornOutBicycleIsRejectedByInspection() {
        String output = run("1\n2\nOld\n7\n60\n0\n");

        assertTrue(output.contains("[ОТКЛОНЁН]"));
        assertTrue(output.contains("износ 60%"));
    }

    @Test
    void notANumberShowsErrorAndAppContinues() {
        String output = run("1\n2\nX\nabc\n0\n");

        assertTrue(output.contains("Нужно целое число, получено: «abc»"));
        assertTrue(output.contains("До встречи!"));
    }

    @Test
    void wrongKindNumberShowsError() {
        assertTrue(run("1\n9\n0\n").contains("Выберите число от 1 до 3"));
    }

    @Test
    void electricEquipmentCountsInEnergyReport() {
        String output = run("2\n2\nZSh-8\n3,5\n4\n0\n");

        assertTrue(output.contains("Поставлено на баланс: Зарядный шкаф «ZSh-8» -> VK-0001"));
        assertTrue(output.contains("Суммарное суточное потребление энергии: 3.50 кВт·ч"));
    }

    @Test
    void emptyFleetReportsAreFriendly() {
        String output = run("5\n6\n0\n");

        assertTrue(output.contains("подходящей техники нет"));
        assertTrue(output.contains("на балансе пока ничего нет"));
    }

    @Test
    void fleetIsLoadedFromFile(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("fleet.csv");
        Files.writeString(file, "SCOOTER;Xiaomi;8;10;0.55\nSCOOTER;Old;7;60;0.5\nHELMET;Livall;;;\n",
                StandardCharsets.UTF_8);

        String output = run("3\n" + file + "\n6\n0\n");

        assertTrue(output.contains("строка 1: принят: Самокат «Xiaomi» -> VK-0001"));
        assertTrue(output.contains("строка 2: отклонён"));
        assertTrue(output.contains("Обработано строк: 3, успешно: 2"));
        assertTrue(output.contains("VK-0002  Шлем «Livall»"));
    }

    @Test
    void missingFileIsReportedAndAppContinues() {
        String output = run("3\nno-such-file.csv\n0\n");

        assertTrue(output.contains("Не удалось прочитать файл «no-such-file.csv»"));
        assertTrue(output.contains("До встречи!"));
    }
}
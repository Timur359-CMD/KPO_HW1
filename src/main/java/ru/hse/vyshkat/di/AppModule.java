package ru.hse.vyshkat.di;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import ru.hse.vyshkat.service.DefaultNoviceRule;
import ru.hse.vyshkat.service.Fleet;
import ru.hse.vyshkat.service.InventoryNumberGenerator;
import ru.hse.vyshkat.service.NoviceRule;
import ru.hse.vyshkat.service.SequentialInventoryNumberGenerator;
import ru.hse.vyshkat.service.ServiceCenter;
import ru.hse.vyshkat.service.StandardServiceCenter;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class AppModule extends AbstractModule {

    public static final int MAX_WEAR_FOR_INSPECTION = 30;
    public static final int NOVICE_MIN_EASE = 6;
    public static final int NOVICE_MAX_WEAR = 20;

    @Override
    protected void configure() {
        bind(ServiceCenter.class).toInstance(new StandardServiceCenter(MAX_WEAR_FOR_INSPECTION));
        bind(NoviceRule.class).toInstance(new DefaultNoviceRule(NOVICE_MIN_EASE, NOVICE_MAX_WEAR));
        bind(InventoryNumberGenerator.class).toInstance(new SequentialInventoryNumberGenerator("VK-"));
        bind(Fleet.class).in(Singleton.class);
        bind(Scanner.class).toInstance(new Scanner(System.in, StandardCharsets.UTF_8));
        bind(PrintStream.class).toInstance(new PrintStream(System.out, true, StandardCharsets.UTF_8));
    }
}
package ru.hse.vyshkat;

import com.google.inject.Guice;
import ru.hse.vyshkat.app.ConsoleApp;
import ru.hse.vyshkat.di.AppModule;

public class Main {

    public static void main(String[] args) {
        Guice.createInjector(new AppModule()).getInstance(ConsoleApp.class).run();
    }
}
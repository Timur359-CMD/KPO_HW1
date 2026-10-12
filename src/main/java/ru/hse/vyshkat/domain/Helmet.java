package ru.hse.vyshkat.domain;

public class Helmet extends Equipment {

    public Helmet(String name) {
        super(name);
    }

    @Override
    public String getKind() {
        return "Шлем";
    }
}

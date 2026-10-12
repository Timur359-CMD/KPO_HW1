package ru.hse.vyshkat.domain;

public class Bicycle extends Transport {

    public Bicycle(String name, int noviceEase, int wearPercent) {
        super(name, noviceEase, wearPercent);
    }

    @Override
    public String getKind() {
        return "Велосипед";
    }
}
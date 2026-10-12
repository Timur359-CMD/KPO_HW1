package ru.hse.vyshkat.service;

public class SequentialInventoryNumberGenerator implements InventoryNumberGenerator {

    private final String prefix;
    private int counter = 0;

    public SequentialInventoryNumberGenerator(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String next() {
        counter++;
        return String.format("%s%04d", prefix, counter);
    }
}
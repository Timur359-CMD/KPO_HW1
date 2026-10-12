package ru.hse.vyshkat.domain;

public abstract class Transport extends InventoryItem {

    private final int noviceEase;
    private final int wearPercent;

    protected Transport(String name, int noviceEase, int wearPercent) {
        super(name);
        if (noviceEase < 1 || noviceEase > 10) {
            throw new IllegalArgumentException("Простота для новичка: от 1 до 10, получено " + noviceEase);
        }
        if (wearPercent < 0 || wearPercent > 100) {
            throw new IllegalArgumentException("Износ: от 0 до 100%, получено " + wearPercent);
        }
        this.noviceEase = noviceEase;
        this.wearPercent = wearPercent;
    }

    public int getNoviceEase() {
        return noviceEase;
    }

    public int getWearPercent() {
        return wearPercent;
    }
}

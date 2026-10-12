package ru.hse.vyshkat.domain;

public class Scooter extends Transport implements EnergyConsumer {

    private final double dailyEnergyKwh;

    public Scooter(String name, int noviceEase, int wearPercent, double dailyEnergyKwh) {
        super(name, noviceEase, wearPercent);
        this.dailyEnergyKwh = requireNonNegative(dailyEnergyKwh, "Расход энергии");
    }

    @Override
    public double getDailyEnergyKwh() {
        return dailyEnergyKwh;
    }

    @Override
    public String getKind() {
        return "Самокат";
    }
}

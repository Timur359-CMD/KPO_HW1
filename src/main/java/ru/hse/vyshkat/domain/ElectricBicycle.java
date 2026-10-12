package ru.hse.vyshkat.domain;

public class ElectricBicycle extends Bicycle implements EnergyConsumer {

    private final double dailyEnergyKwh;

    public ElectricBicycle(String name, int noviceEase, int wearPercent, double dailyEnergyKwh) {
        super(name, noviceEase, wearPercent);
        this.dailyEnergyKwh = requireNonNegative(dailyEnergyKwh, "Расход энергии");
    }

    @Override
    public double getDailyEnergyKwh() {
        return dailyEnergyKwh;
    }

    @Override
    public String getKind() {
        return "Электровелосипед";
    }
}

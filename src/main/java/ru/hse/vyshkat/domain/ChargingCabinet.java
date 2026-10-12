package ru.hse.vyshkat.domain;

public class ChargingCabinet extends Equipment implements EnergyConsumer {

    private final double dailyEnergyKwh;

    public ChargingCabinet(String name, double dailyEnergyKwh) {
        super(name);
        this.dailyEnergyKwh = requireNonNegative(dailyEnergyKwh, "Расход энергии");
    }

    @Override
    public double getDailyEnergyKwh() {
        return dailyEnergyKwh;
    }

    @Override
    public String getKind() {
        return "Зарядный шкаф";
    }
}

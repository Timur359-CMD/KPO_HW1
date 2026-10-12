package ru.hse.vyshkat.domain;

public enum TransportKind {

    SCOOTER("Самокат", true) {
        @Override
        public Transport create(String name, int noviceEase, int wearPercent, double dailyEnergyKwh) {
            return new Scooter(name, noviceEase, wearPercent, dailyEnergyKwh);
        }
    },
    BICYCLE("Велосипед", false) {
        @Override
        public Transport create(String name, int noviceEase, int wearPercent, double dailyEnergyKwh) {
            return new Bicycle(name, noviceEase, wearPercent);
        }
    },
    ELECTRIC_BICYCLE("Электровелосипед", true) {
        @Override
        public Transport create(String name, int noviceEase, int wearPercent, double dailyEnergyKwh) {
            return new ElectricBicycle(name, noviceEase, wearPercent, dailyEnergyKwh);
        }
    };

    private final String label;
    private final boolean needsEnergy;

    TransportKind(String label, boolean needsEnergy) {
        this.label = label;
        this.needsEnergy = needsEnergy;
    }

    public String getLabel() {
        return label;
    }

    public boolean needsEnergy() {
        return needsEnergy;
    }

    public abstract Transport create(String name, int noviceEase, int wearPercent, double dailyEnergyKwh);
}

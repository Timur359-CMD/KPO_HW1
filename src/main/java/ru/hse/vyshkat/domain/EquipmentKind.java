package ru.hse.vyshkat.domain;

public enum EquipmentKind {

    HELMET("Шлем", false) {
        @Override
        public Equipment create(String name, double dailyEnergyKwh) {
            return new Helmet(name);
        }
    },
    CHARGING_CABINET("Зарядный шкаф", true) {
        @Override
        public Equipment create(String name, double dailyEnergyKwh) {
            return new ChargingCabinet(name, dailyEnergyKwh);
        }
    };

    private final String label;
    private final boolean needsEnergy;

    EquipmentKind(String label, boolean needsEnergy) {
        this.label = label;
        this.needsEnergy = needsEnergy;
    }

    public String getLabel() {
        return label;
    }

    public boolean needsEnergy() {
        return needsEnergy;
    }

    public abstract Equipment create(String name, double dailyEnergyKwh);
}

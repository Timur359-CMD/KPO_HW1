package ru.hse.vyshkat.domain;

public abstract class InventoryItem implements Inventoriable {

    private final String name;
    private String inventoryNumber;

    protected InventoryItem(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название не может быть пустым");
        }
        this.name = name.trim();
    }

    protected static double requireNonNegative(double value, String field) {
        if (Double.isNaN(value) || value < 0) {
            throw new IllegalArgumentException(field + ": значение не может быть отрицательным, получено " + value);
        }
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getInventoryNumber() {
        if (inventoryNumber == null) {
            throw new IllegalStateException("Инвентарный номер не присвоен: " + this);
        }
        return inventoryNumber;
    }

    @Override
    public void assignInventoryNumber(String number) {
        if (inventoryNumber != null) {
            throw new IllegalStateException("Инвентарный номер уже присвоен: " + inventoryNumber);
        }
        this.inventoryNumber = number;
    }

    @Override
    public String toString() {
        return getKind() + " «" + name + "»";
    }
}

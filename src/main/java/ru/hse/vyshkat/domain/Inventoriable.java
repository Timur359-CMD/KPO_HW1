package ru.hse.vyshkat.domain;

public interface Inventoriable {

    String getKind();

    String getName();

    String getInventoryNumber();

    void assignInventoryNumber(String number);
}

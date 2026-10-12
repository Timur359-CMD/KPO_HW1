package ru.hse.vyshkat.service;

import ru.hse.vyshkat.domain.Transport;

public class StandardServiceCenter implements ServiceCenter {

    private final int maxWearPercent;

    public StandardServiceCenter(int maxWearPercent) {
        this.maxWearPercent = maxWearPercent;
    }

    @Override
    public InspectionResult inspect(Transport transport) {
        if (transport.getWearPercent() > maxWearPercent) {
            return InspectionResult.reject(
                    "износ " + transport.getWearPercent() + "% выше допустимых " + maxWearPercent + "%");
        }
        return InspectionResult.accept();
    }
}

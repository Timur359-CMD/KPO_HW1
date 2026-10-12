package ru.hse.vyshkat.service;

import ru.hse.vyshkat.domain.Transport;

public class DefaultNoviceRule implements NoviceRule {

    private final int minEase;
    private final int maxWearPercent;

    public DefaultNoviceRule(int minEase, int maxWearPercent) {
        this.minEase = minEase;
        this.maxWearPercent = maxWearPercent;
    }

    @Override
    public boolean isSuitable(Transport transport) {
        return transport.getNoviceEase() >= minEase && transport.getWearPercent() <= maxWearPercent;
    }
}
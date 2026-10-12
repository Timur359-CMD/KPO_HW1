package ru.hse.vyshkat.service;

import ru.hse.vyshkat.domain.Transport;

public interface NoviceRule {
    boolean isSuitable(Transport transport);
}
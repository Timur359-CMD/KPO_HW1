package ru.hse.vyshkat.service;

import ru.hse.vyshkat.domain.Transport;

@FunctionalInterface
public interface ServiceCenter {

    InspectionResult inspect(Transport transport);
}
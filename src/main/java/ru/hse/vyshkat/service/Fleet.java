package ru.hse.vyshkat.service;

import com.google.inject.Inject;
import ru.hse.vyshkat.domain.Equipment;
import ru.hse.vyshkat.domain.Inventoriable;
import ru.hse.vyshkat.domain.Transport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Fleet {

    private final ServiceCenter serviceCenter;
    private final InventoryNumberGenerator numbers;
    private final List<Transport> transport = new ArrayList<>();
    private final List<Equipment> equipment = new ArrayList<>();

    @Inject
    public Fleet(ServiceCenter serviceCenter, InventoryNumberGenerator numbers) {
        this.serviceCenter = serviceCenter;
        this.numbers = numbers;
    }

    public InspectionResult acceptTransport(Transport candidate) {
        InspectionResult result = serviceCenter.inspect(candidate);
        if (result.accepted()) {
            candidate.assignInventoryNumber(numbers.next());
            transport.add(candidate);
        }
        return result;
    }

    public void registerEquipment(Equipment item) {
        item.assignInventoryNumber(numbers.next());
        equipment.add(item);
    }

    public List<Transport> getTransport() {
        return Collections.unmodifiableList(transport);
    }

    public List<Equipment> getEquipment() {
        return Collections.unmodifiableList(equipment);
    }

    public List<Inventoriable> getAllInventoried() {
        List<Inventoriable> all = new ArrayList<>(transport);
        all.addAll(equipment);
        return all;
    }
}
package ru.hse.vyshkat.service;

import com.google.inject.Inject;
import ru.hse.vyshkat.domain.EnergyConsumer;
import ru.hse.vyshkat.domain.Inventoriable;
import ru.hse.vyshkat.domain.Transport;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ReportService {

    private final Fleet fleet;
    private final NoviceRule noviceRule;

    @Inject
    public ReportService(Fleet fleet, NoviceRule noviceRule) {
        this.fleet = fleet;
        this.noviceRule = noviceRule;
    }

    public int totalUnits() {
        return fleet.getAllInventoried().size();
    }

    public Map<String, Long> countByKind() {
        return fleet.getAllInventoried().stream()
                .collect(Collectors.groupingBy(Inventoriable::getKind, TreeMap::new, Collectors.counting()));
    }

    public double totalDailyEnergyKwh() {
        return fleet.getAllInventoried().stream()
                .filter(EnergyConsumer.class::isInstance)
                .mapToDouble(item -> ((EnergyConsumer) item).getDailyEnergyKwh())
                .sum();
    }

    public List<Transport> noviceFriendlyTransport() {
        return fleet.getTransport().stream().filter(noviceRule::isSuitable).toList();
    }

    public List<Inventoriable> inventoryList() {
        return fleet.getAllInventoried();
    }
}

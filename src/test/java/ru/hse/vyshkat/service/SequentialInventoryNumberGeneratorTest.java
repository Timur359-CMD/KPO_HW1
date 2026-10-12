package ru.hse.vyshkat.service;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SequentialInventoryNumberGeneratorTest {

    @Test
    void numbersGoInOrderWithPrefixAndPadding() {
        InventoryNumberGenerator generator = new SequentialInventoryNumberGenerator("VK-");

        assertEquals("VK-0001", generator.next());
        assertEquals("VK-0002", generator.next());
        assertEquals("VK-0003", generator.next());
    }

    @Test
    void numbersAreUnique() {
        InventoryNumberGenerator generator = new SequentialInventoryNumberGenerator("VK-");
        Set<String> seen = new HashSet<>();

        for (int i = 0; i < 1000; i++) {
            seen.add(generator.next());
        }

        assertEquals(1000, seen.size());
    }
}
package ru.hse.vyshkat.service;

public record InspectionResult(boolean accepted, String reason) {

    public static InspectionResult accept() {
        return new InspectionResult(true, "техосмотр пройден");
    }

    public static InspectionResult reject(String reason) {
        return new InspectionResult(false, reason);
    }
}
package model;

import util.Validator;

public class Car {
    private final String plateNumber;
    private final long parkedAt;

    public Car(String plateNumber) {
        if (!Validator.isValidPlate(plateNumber)) {
            throw new IllegalArgumentException("Invalid license plate format");
        }
        this.plateNumber = plateNumber;
        this.parkedAt = System.currentTimeMillis();
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public long getParkedAt() {
        return parkedAt;
    }
}
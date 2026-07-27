package domain.entities;

import util.Validator;

import java.util.Objects;

public final class Car {
    private final String licensePlate;

    public Car(String licensePlate) {
        String normalizedPlate = Validator.normalizePlate(licensePlate);

        if (!Validator.isValidPlate(normalizedPlate)) {
            throw new IllegalArgumentException("Invalid license plate");
        }

        this.licensePlate = normalizedPlate;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getPlateNumber() {
        return licensePlate;
    }

    @Override
    public String toString() {
        return licensePlate;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Car car)) {
            return false;
        }

        return Objects.equals(licensePlate, car.licensePlate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(licensePlate);
    }
}
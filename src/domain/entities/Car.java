package domain.entities;

import domain.valueobjects.LicensePlate;

import java.util.Objects;

public final class Car {
    private final LicensePlate licensePlate;

    public Car(String licensePlate) {
        this(LicensePlate.of(licensePlate));
    }

    public Car(LicensePlate licensePlate) {
        if (licensePlate == null) {
            throw new IllegalArgumentException("License plate cannot be null");
        }

        this.licensePlate = licensePlate;
    }

    public LicensePlate getLicensePlateValue() {
        return licensePlate;
    }

    public String getLicensePlate() {
        return licensePlate.getValue();
    }

    public String getPlateNumber() {
        return licensePlate.getValue();
    }

    public boolean hasPlate(String rawLicensePlate) {
        return licensePlate.matches(rawLicensePlate);
    }

    @Override
    public String toString() {
        return licensePlate.getValue();
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
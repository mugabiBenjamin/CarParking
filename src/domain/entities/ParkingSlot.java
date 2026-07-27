package domain.entities;

import domain.enums.SlotStatus;
import domain.exceptions.ParkingSlotEmptyException;
import domain.exceptions.ParkingSlotOccupiedException;
import domain.valueobjects.LicensePlate;

import java.util.Objects;
import java.util.Optional;

public final class ParkingSlot {
    private final int number;
    private Car car;

    public ParkingSlot(int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("Slot number must be greater than zero");
        }

        this.number = number;
        this.car = null;
    }

    public void park(Car car) {
        if (car == null) {
            throw new IllegalArgumentException("Car cannot be null");
        }

        if (isOccupied()) {
            throw new ParkingSlotOccupiedException(number);
        }

        this.car = car;
    }

    public Car unpark() {
        if (!isOccupied()) {
            throw new ParkingSlotEmptyException(number);
        }

        Car removedCar = car;
        car = null;
        return removedCar;
    }

    public boolean isOccupied() {
        return car != null;
    }

    public boolean isEmpty() {
        return car == null;
    }

    public SlotStatus getStatus() {
        if (isOccupied()) {
            return SlotStatus.OCCUPIED;
        }

        return SlotStatus.EMPTY;
    }

    public Car getCar() {
        return car;
    }

    public Optional<Car> findCar() {
        return Optional.ofNullable(car);
    }

    public int getNumber() {
        return number;
    }

    public boolean hasCar(String rawLicensePlate) {
        if (rawLicensePlate == null || rawLicensePlate.trim().isEmpty() || car == null) {
            return false;
        }

        return car.hasPlate(rawLicensePlate);
    }

    public boolean hasCar(LicensePlate licensePlate) {
        if (licensePlate == null || car == null) {
            return false;
        }

        return car.getLicensePlateValue().equals(licensePlate);
    }

    @Override
    public String toString() {
        return "Slot " + number + " - " + (isOccupied() ? car.getLicensePlate() : "EMPTY");
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof ParkingSlot slot)) {
            return false;
        }

        return number == slot.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}
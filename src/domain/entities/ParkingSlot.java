package domain.entities;

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
            throw new IllegalStateException("Slot " + number + " is already occupied");
        }

        this.car = car;
    }

    public void unpark() {
        if (!isOccupied()) {
            throw new IllegalStateException("Slot " + number + " is already empty");
        }

        this.car = null;
    }

    public boolean isOccupied() {
        return car != null;
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

    public boolean hasCar(String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty() || car == null) {
            return false;
        }

        return car.getLicensePlate().equalsIgnoreCase(licensePlate.trim());
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
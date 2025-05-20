package model;

public class ParkingSlot {
    private int number;
    private Car car; // null = free

    public ParkingSlot(int number) {
        this.number = number;
        this.car = null;
    }

    public boolean isOccupied() {
        return car != null;
    }

    public void parkCar(Car car) {
        this.car = car;
    }

    public void removeCar() {
        this.car = null;
    }

    public Car getCar() {
        return car;
    }

    public int getNumber() {
        return number;
    }
}

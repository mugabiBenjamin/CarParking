package application.repositories;

import domain.entities.ParkingLot;

@FunctionalInterface
public interface ParkingLotUpdateCommand<T> {
    T execute(ParkingLot parkingLot);
}
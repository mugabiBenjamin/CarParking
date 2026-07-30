package application.repositories;

import domain.entities.ParkingLot;

public interface ParkingLotRepository {
    ParkingLot load(int size);

    <T> ParkingLotUpdateResult<T> update(int size, ParkingLotUpdateCommand<T> command);
}
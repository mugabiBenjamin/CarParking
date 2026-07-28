package application.repositories;

import domain.entities.ParkingLot;

public interface ParkingLotRepository {
    ParkingLot load(int size);

    boolean save(ParkingLot parkingLot);
}
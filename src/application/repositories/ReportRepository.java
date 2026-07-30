package application.repositories;

import domain.entities.ParkingLot;

public interface ReportRepository {
    String generate(ParkingLot parkingLot);
}
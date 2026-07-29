package application.usecases;

import application.dto.LoadParkingDataResponse;
import application.dto.OperationResult;
import application.services.ParkingService;

public final class LoadParkingDataUseCase {
    private final ParkingService parkingService;

    public LoadParkingDataUseCase(ParkingService parkingService) {
        if (parkingService == null) {
            throw new IllegalArgumentException("Parking service cannot be null");
        }

        this.parkingService = parkingService;
    }

    public OperationResult<LoadParkingDataResponse> execute() {
        return parkingService.loadParkingData();
    }
}
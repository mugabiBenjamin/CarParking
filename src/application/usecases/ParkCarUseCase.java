package application.usecases;

import application.dto.OperationResult;
import application.dto.ParkCarRequest;
import application.dto.ParkCarResponse;
import application.services.ParkingService;

public final class ParkCarUseCase {
    private final ParkingService parkingService;

    public ParkCarUseCase(ParkingService parkingService) {
        if (parkingService == null) {
            throw new IllegalArgumentException("Parking service cannot be null");
        }

        this.parkingService = parkingService;
    }

    public OperationResult<ParkCarResponse> execute(ParkCarRequest request) {
        return parkingService.parkCar(request);
    }
}
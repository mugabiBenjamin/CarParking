package application.usecases;

import application.dto.OperationResult;
import application.dto.UnparkCarRequest;
import application.dto.UnparkCarResponse;
import application.services.ParkingService;

public final class UnparkCarUseCase {
    private final ParkingService parkingService;

    public UnparkCarUseCase(ParkingService parkingService) {
        if (parkingService == null) {
            throw new IllegalArgumentException("Parking service cannot be null");
        }

        this.parkingService = parkingService;
    }

    public OperationResult<UnparkCarResponse> execute(UnparkCarRequest request) {
        return parkingService.unparkCar(request);
    }
}
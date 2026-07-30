package application.usecases;

import application.dto.FindCarRequest;
import application.dto.FindCarResponse;
import application.dto.OperationResult;
import application.services.ParkingService;

public final class FindCarUseCase {
    private final ParkingService parkingService;

    public FindCarUseCase(ParkingService parkingService) {
        if (parkingService == null) {
            throw new IllegalArgumentException("Parking service cannot be null");
        }

        this.parkingService = parkingService;
    }

    public OperationResult<FindCarResponse> execute(FindCarRequest request) {
        return parkingService.findCar(request);
    }
}
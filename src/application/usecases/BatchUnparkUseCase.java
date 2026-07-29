package application.usecases;

import application.dto.BatchUnparkRequest;
import application.dto.BatchUnparkResponse;
import application.dto.OperationResult;
import application.services.ParkingService;

public final class BatchUnparkUseCase {
    private final ParkingService parkingService;

    public BatchUnparkUseCase(ParkingService parkingService) {
        if (parkingService == null) {
            throw new IllegalArgumentException("Parking service cannot be null");
        }

        this.parkingService = parkingService;
    }

    public OperationResult<BatchUnparkResponse> execute(BatchUnparkRequest request) {
        return parkingService.batchUnpark(request);
    }
}
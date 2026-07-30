package application.validators;

import application.dto.BatchUnparkRequest;
import application.dto.OperationResult;
import application.dto.ParkCarRequest;
import application.dto.UnparkCarRequest;

public final class ParkingRequestValidator {
    private final LicensePlateFormatValidator licensePlateValidator;

    public ParkingRequestValidator(LicensePlateFormatValidator licensePlateValidator) {
        if (licensePlateValidator == null) {
            throw new IllegalArgumentException("License plate validator cannot be null");
        }

        this.licensePlateValidator = licensePlateValidator;
    }

    public OperationResult<Void> validateParkRequest(ParkCarRequest request) {
        if (request == null) {
            return OperationResult.failure(
                    "INVALID_REQUEST",
                    "Park request cannot be null",
                    "Enter a license plate and try again."
            );
        }

        OperationResult<?> validationResult = licensePlateValidator.validate(request.getLicensePlate());

        if (validationResult.isFailure()) {
            return OperationResult.failure(
                    "INVALID_LICENSE_PLATE",
                    validationResult.getMessage(),
                    "Enter a valid Uganda license plate and try again."
            );
        }

        return OperationResult.success("Park request is valid");
    }

    public OperationResult<Void> validateUnparkRequest(UnparkCarRequest request) {
        if (request == null) {
            return OperationResult.failure(
                    "INVALID_REQUEST",
                    "Unpark request cannot be null",
                    "Choose an occupied slot and try again."
            );
        }

        if (request.getSlotNumber() <= 0) {
            return OperationResult.failure(
                    "INVALID_SLOT_NUMBER",
                    "Slot number must be greater than zero",
                    "Choose a valid occupied slot and try again."
            );
        }

        return OperationResult.success("Unpark request is valid");
    }

    public OperationResult<Void> validateBatchUnparkRequest(BatchUnparkRequest request) {
        if (request == null || request.getSlotNumbers().isEmpty()) {
            return OperationResult.failure(
                    "NO_SLOTS_SELECTED",
                    "No slots selected for batch unpark",
                    "Select at least one occupied slot and try again."
            );
        }

        for (Integer slotNumber : request.getSlotNumbers()) {
            if (slotNumber == null || slotNumber <= 0) {
                return OperationResult.failure(
                        "INVALID_SLOT_NUMBER",
                        "Batch unpark contains an invalid slot number",
                        "Select valid occupied slots and try again."
                );
            }
        }

        return OperationResult.success("Batch unpark request is valid");
    }
}
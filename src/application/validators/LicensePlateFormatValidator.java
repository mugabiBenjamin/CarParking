package application.validators;

import application.dto.OperationResult;
import domain.exceptions.InvalidLicensePlateException;
import domain.valueobjects.LicensePlate;

public final class LicensePlateFormatValidator {
    public OperationResult<LicensePlate> validate(String rawLicensePlate) {
        try {
            LicensePlate licensePlate = LicensePlate.of(rawLicensePlate);
            return OperationResult.success("Valid license plate", licensePlate);
        } catch (InvalidLicensePlateException exception) {
            return OperationResult.failure(
                    "INVALID_LICENSE_PLATE",
                    exception.getMessage(),
                    "Use a supported Uganda license plate format and try again."
            );
        } catch (Exception exception) {
            return OperationResult.failure(
                    "LICENSE_PLATE_VALIDATION_ERROR",
                    "Unable to validate license plate",
                    "Check the license plate and try again."
            );
        }
    }

    public String normalize(String rawLicensePlate) {
        return LicensePlate.normalize(rawLicensePlate);
    }
}
package application.services;

import application.dto.OperationResult;
import application.validators.LicensePlateFormatValidator;
import domain.valueobjects.LicensePlate;

public final class PlateValidationService {
    private final LicensePlateFormatValidator validator;

    public PlateValidationService(LicensePlateFormatValidator validator) {
        if (validator == null) {
            throw new IllegalArgumentException("License plate validator cannot be null");
        }

        this.validator = validator;
    }

    public OperationResult<LicensePlate> validate(String rawLicensePlate) {
        return validator.validate(rawLicensePlate);
    }

    public String normalize(String rawLicensePlate) {
        return validator.normalize(rawLicensePlate);
    }
}
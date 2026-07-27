package domain.valueobjects;

import domain.exceptions.InvalidLicensePlateException;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public final class LicensePlate {
    private static final Pattern ORDINARY_PRIVATE_PLATE = Pattern.compile("^UA\\s\\d{3}[A-Z]{2}$");
    private static final Pattern LEGACY_PRIVATE_PLATE = Pattern.compile("^U[A-Z]{2}\\s\\d{3}[A-Z]$");
    private static final Pattern GOVERNMENT_PLATE = Pattern.compile("^UG\\s\\d{2}\\s\\d{5}$");
    private static final Pattern LEGACY_GOVERNMENT_PLATE = Pattern.compile("^UG\\s\\d{3}[A-Z]$");
    private static final Pattern DIPLOMATIC_PLATE = Pattern.compile("^CD\\s\\d{2}\\s\\d{2}\\s[A-Z]$");
    private static final Pattern MOTORCYCLE_PLATE = Pattern.compile("^UM[A-Z]\\s\\d{3}[A-Z]{2}$");
    private static final Pattern PERSONALIZED_PLATE = Pattern.compile("^[A-Z][A-Z0-9\\s]{1,7}$");
    private static final Pattern SAFE_CHARACTERS = Pattern.compile("^[A-Z0-9\\s]+$");

    private final String value;

    private LicensePlate(String value) {
        this.value = value;
    }

    public static LicensePlate of(String rawValue) {
        String normalizedValue = normalize(rawValue);
        validate(normalizedValue);
        return new LicensePlate(normalizedValue);
    }

    public static boolean isValid(String rawValue) {
        try {
            of(rawValue);
            return true;
        } catch (InvalidLicensePlateException exception) {
            return false;
        }
    }

    public static String normalize(String rawValue) {
        if (rawValue == null) {
            return "";
        }

        return rawValue
                .trim()
                .replaceAll("\\s+", " ")
                .toUpperCase(Locale.ROOT);
    }

    public String getValue() {
        return value;
    }

    public boolean matches(String rawValue) {
        return value.equals(normalize(rawValue));
    }

    private static void validate(String value) {
        if (value.isEmpty()) {
            throw new InvalidLicensePlateException("License plate is required");
        }

        if (!SAFE_CHARACTERS.matcher(value).matches()) {
            throw new InvalidLicensePlateException("License plate contains unsupported characters");
        }

        if (ORDINARY_PRIVATE_PLATE.matcher(value).matches()) {
            return;
        }

        if (LEGACY_PRIVATE_PLATE.matcher(value).matches()) {
            return;
        }

        if (GOVERNMENT_PLATE.matcher(value).matches()) {
            return;
        }

        if (LEGACY_GOVERNMENT_PLATE.matcher(value).matches()) {
            return;
        }

        if (DIPLOMATIC_PLATE.matcher(value).matches()) {
            return;
        }

        if (MOTORCYCLE_PLATE.matcher(value).matches()) {
            return;
        }

        if (isPersonalizedPlate(value)) {
            return;
        }

        throw new InvalidLicensePlateException(
                "Invalid plate format. Use formats like UA 001AA, UG 32 00042, CD 01 02 U, UMA 001AA, UAA 123B, or a valid personalized plate"
        );
    }

    private static boolean isPersonalizedPlate(String value) {
        return PERSONALIZED_PLATE.matcher(value).matches()
                && value.length() >= 2
                && value.length() <= 8;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof LicensePlate licensePlate)) {
            return false;
        }

        return Objects.equals(value, licensePlate.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
package application.dto;

public final class ParkCarRequest {
    private final String licensePlate;

    public ParkCarRequest(String licensePlate) {
        this.licensePlate = normalize(licensePlate);
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }
}
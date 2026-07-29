package application.dto;

public final class FindCarResponse {
    private final boolean found;
    private final int slotNumber;
    private final String licensePlate;

    public FindCarResponse(boolean found, int slotNumber, String licensePlate) {
        if (found && slotNumber <= 0) {
            throw new IllegalArgumentException("Found car response must include a valid slot number");
        }

        this.found = found;
        this.slotNumber = slotNumber;
        this.licensePlate = normalize(licensePlate);
    }

    public boolean isFound() {
        return found;
    }

    public int getSlotNumber() {
        return slotNumber;
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
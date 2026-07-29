package application.dto;

public final class UnparkCarResponse {
    private final int slotNumber;
    private final String licensePlate;
    private final ParkingLotViewData parkingLot;

    public UnparkCarResponse(int slotNumber, String licensePlate, ParkingLotViewData parkingLot) {
        if (slotNumber <= 0) {
            throw new IllegalArgumentException("Slot number must be greater than zero");
        }

        this.slotNumber = slotNumber;
        this.licensePlate = normalize(licensePlate);
        this.parkingLot = parkingLot;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public ParkingLotViewData getParkingLot() {
        return parkingLot;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }
}
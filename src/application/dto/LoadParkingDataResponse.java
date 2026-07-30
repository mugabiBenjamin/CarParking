package application.dto;

public final class LoadParkingDataResponse {
    private final ParkingLotViewData parkingLot;

    public LoadParkingDataResponse(ParkingLotViewData parkingLot) {
        this.parkingLot = parkingLot;
    }

    public ParkingLotViewData getParkingLot() {
        return parkingLot;
    }
}
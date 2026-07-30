package application.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BatchUnparkResponse {
    private final int unparkedCount;
    private final List<String> unparkedLicensePlates;
    private final ParkingLotViewData parkingLot;

    public BatchUnparkResponse(int unparkedCount, List<String> unparkedLicensePlates, ParkingLotViewData parkingLot) {
        if (unparkedCount < 0) {
            throw new IllegalArgumentException("Unparked count cannot be negative");
        }

        this.unparkedCount = unparkedCount;
        this.unparkedLicensePlates = unparkedLicensePlates == null
                ? new ArrayList<>()
                : new ArrayList<>(unparkedLicensePlates);
        this.parkingLot = parkingLot;
    }

    public int getUnparkedCount() {
        return unparkedCount;
    }

    public List<String> getUnparkedLicensePlates() {
        return Collections.unmodifiableList(unparkedLicensePlates);
    }

    public ParkingLotViewData getParkingLot() {
        return parkingLot;
    }
}
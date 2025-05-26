package controller;

public interface ParkingListener {
    void onParkResult(ParkResult result);

    void onUnparkResult(UnparkResult result);

    void onBatchUnparkResult(BatchUnparkResult result);

    void onFindCarResult(FindCarResult result);

    void onStatusUpdate(String message);

    void onReportResult(ReportResult result);

    void onLoadDataResult(LoadDataResult result);
}
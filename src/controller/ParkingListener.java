package controller;

public interface ParkingListener {
    void onParkResult(Result result);

    void onUnparkResult(Result result);

    void onBatchUnparkResult(Result result);

    void onFindCarResult(Result result);

    void onReportResult(Result result);

    void onLoadDataResult(Result result);

    void onStatusUpdate(String message);
}
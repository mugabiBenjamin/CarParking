package util;

import model.Car;
import model.ParkingLot;
import model.ParkingSlot;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileHelper {
    private final String filePath;

    public FileHelper(String filePath) {
        this.filePath = "data/" + filePath;
    }

    public void loadFromFile(ParkingLot lot) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            Logger.log("No existing parking data found. Starting with empty lot.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Pattern pattern = Pattern.compile("\\((\\d+),\\s*([^)]*)\\)");
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;

                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    try {
                        int slotNumber = Integer.parseInt(matcher.group(1));
                        String licensePlate = matcher.group(2).trim();
                        if (!licensePlate.equalsIgnoreCase("EMPTY") && !licensePlate.isEmpty()) {
                            Car car = new Car(licensePlate);
                            lot.getSlot(slotNumber - 1).ifPresent(slot -> slot.parkCar(car));
                            Logger.log("Loaded car: " + licensePlate + " in slot " + slotNumber);
                        } else {
                            Logger.log("Slot " + slotNumber + " is empty");
                        }
                    } catch (NumberFormatException e) {
                        Logger.error("Invalid slot number in line: " + line);
                    } catch (IllegalArgumentException e) {
                        Logger.error("Invalid license plate in line: " + line + ": " + e.getMessage());
                    }
                } else {
                    Logger.error("Invalid line format: " + line);
                }
            }
        }
    }

    public void save(ParkingLot lot) throws IOException {
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (ParkingSlot slot : lot.getSlots()) {
                String line = "(" + slot.getNumber() + ", "
                        + (slot.isOccupied() ? slot.getCar().getPlateNumber() : "EMPTY") + ")";
                writer.write(line);
                writer.newLine();
            }
            Logger.log("Parking lot data saved to " + filePath);
        }
    }

    public void generateReport(ParkingLot lot) throws IOException {
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String reportFilePath = "data/parking_lot_report.csv";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFilePath))) {
            writer.write("Slot Number,Occupancy Status,License Plate,Parked At");
            writer.newLine();
            for (ParkingSlot slot : lot.getSlots()) {
                String status = slot.isOccupied() ? "Occupied" : "Empty";
                String licensePlate = slot.isOccupied() ? slot.getCar().getPlateNumber() : "";
                String parkedAt = slot.isOccupied() ? sdf.format(new Date(slot.getCar().getParkedAt())) : "";
                writer.write(slot.getNumber() + "," + status + "," + licensePlate + "," + parkedAt);
                writer.newLine();
            }
            Logger.log("Report generated at " + reportFilePath);
        }
    }
}
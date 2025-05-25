package util;

import model.Car;
import model.ParkingLot;
import model.ParkingSlot;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileHelper {
    private final String filePath;

    public FileHelper(String filePath) {
        this.filePath = filePath;
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
}
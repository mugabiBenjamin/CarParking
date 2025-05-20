package model;

import java.io.*;
import java.util.*;

public class FileHelper {

    private static final String FILE_PATH = "src/data/parking_lot.txt";

    // Load slot occupancy state from file
    public static List<String> loadSlotData() {
        List<String> data = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return data; // return empty list if file doesn't exist
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                data.add(line.trim()); // each line: slotNumber,plateNumber or "EMPTY"
            }
        } catch (IOException e) {
            System.err.println("Error reading parking lot data: " + e.getMessage());
        }

        return data;
    }

    // Save current state of all slots
    public static void saveSlotData(List<ParkingSlot> slots) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (ParkingSlot slot : slots) {
                if (slot.isOccupied()) {
                    writer.write(slot.getNumber() + "," + slot.getCar().getPlateNumber());
                } else {
                    writer.write(slot.getNumber() + ",EMPTY");
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing parking lot data: " + e.getMessage());
        }
    }

    // Ensure data folder exists
    public static void ensureDataFolderExists() {
        File dir = new File("src/data");
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
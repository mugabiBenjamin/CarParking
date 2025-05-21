# CarParking

![Java](https://img.shields.io/badge/Java-17-blue)
![Swing](https://img.shields.io/badge/GUI-Swing-orange)

This Java Swing-based desktop parking system runs on Windows, macOS, and Linux. It allows users to park, unpark, and search cars by license plate, providing real-time feedback and automatic slot management. Parking data is stored in local text files for persistence across sessions. Designed mainly for educational purposes, it demonstrates object-oriented programming, file I/O, and GUI development, offering a simple, user-friendly parking lot management tool.

## Features

- **User-friendly GUI** developed with Java Swing, showing color-coded parking slots (green for available, red for occupied) for at-a-glance status and live updates on parking and unparking actions.
- **Real-time parking management** with visual display of occupied and available slots, along with vehicle registration by license plate during parking.
- **Search functionality** to locate parked vehicles by license plate number, with visual highlighting and feedback.
- **Persistent storage** using file-based data, configurable via `config.properties`, ensuring parking data is retained across sessions.
- **Car plate validation** to ensure correct format during parking operations.
- **MVC-like architecture** for clear separation of concerns, enhancing maintainability.
- **Logging system** for tracking parking, unparking, and search actions, aiding in monitoring system activity.

## Prerequisites

- [Java Development Kit (JDK) 8 or higher](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) _(**Recommended:** [OpenJDK](https://adoptium.net/))_
- A Java IDE (e.g., [IntelliJ IDEA](https://www.jetbrains.com/idea/download/), [Eclipse](https://www.eclipse.org/downloads/), [VS Code](https://code.visualstudio.com/Download)) or terminal access
- [Git](https://git-scm.com/downloads) (optional, for cloning the repository)

## Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/mugabiBenjamin/CarParking.git
   cd CarParking
   ```

2. Create the configuration file: **Linux/macOS:**

   **Note:** Ensure the `config.properties` file is in the root directory when running the app.

   ```bash
   echo "# Application Configuration
   parking.data.file=data/parking_lot.txt" > config.properties
   ```

   **Windows (Command Prompt):**

   ```cmd
   echo # Application Configuration > config.properties
   echo parking.data.file=data/parking_lot.txt >> config.properties
   ```

   **Windows (Powershell):**

   ```powershell
   echo # Application Configuration > config.properties
   echo parking.data.file=data/parking_lot.txt >> config.properties
   ```

3. Compile the project:

   ```bash
   javac -d bin src/**/*.java src/*.java
   ```

4. Run the application:

   ```bash
   java -cp bin Main
   ```

_The GUI should launch, allowing you to manage parking slots._

## Usage

### Managing Vehicles

1. **Parking a Car:**

   - Enter the license plate number in the `Plate Number` field.
   - Click the `Park` button to assign the vehicle to the first available slot.

2. **Finding a Car:**

   - Enter the license plate number in the "`Search Plate` field.
   - Click the `Search` button to locate the parked vehicle.
   - If found, the slot will be highlighted in blue for 2 seconds.

3. **Removing a Car:**

   - Click on an occupied parking slot (red) to remove the parked vehicle.
   - Confirm the removal when prompted.

### User Interface Guide

The interface consists of three main sections:

- **Control Panel:** Located at the top of the window, containing the input fields and buttons for parking and finding vehicles.
- **Parking Slots Display:** The central area shows all parking slots with their current status.
- **Status Bar:** Located at the bottom, providing feedback on operations.

## Configuration

The `config.properties` file contains application-specific settings that determine where data is stored. This file should not be pushed to GitHub as it may contain user-specific paths. You may customize the storage location by modifying the `parking.data.file` property. The path can be:

- Relative to the application's working directory (recommended for portability)

  ```properties
  # Application Configuration
  parking.data.file=data/parking_lot.txt
  ```

- **parking.data.file:** Specifies the path to the parking data file (relative to the project root)

The application will automatically create any necessary directories specified in this path.

## Project Structure

```plaintext
CarParking/
├─ src/
│  ├─ controller/
│  │  └─ ParkingController.java           # Handles user actions and updates model/view
│  ├─ data/
│  │  ├─ parking_lot.txt                  # Stores parking slot data (generated at runtime)
│  │  └─ .gitkeep                         # Ensures data/ directory is tracked by git
│  ├─ model/
│  │  ├─ Car.java                         # Represents a car with license plate info
│  │  ├─ FileHelper.java                  # Utility for reading/writing parking data files
│  │  ├─ ParkingLot.java                  # Manages parking slots and car assignments
│  │  └─ ParkingSlot.java                 # Represents an individual parking slot
│  ├─ util/
│  │  ├─ Logger.java                      # Logs parking, unparking, and search actions
│  │  └─ Validator.java                   # Validates license plate formats and inputs
│  ├─ view/
│  │  ├─ MessageBox.java                  # Utility for displaying dialog messages
│  │  ├─ ParkingSlotPanel.java            # GUI component for displaying a parking slot
│  │  └─ ParkingView.java                 # Main GUI window and layout
│  └─ Main.java                           # Application entry point
├─ config.properties                      # Configuration file for data storage path
├─ .gitignore                             # Specifies files/folders to ignore in git
├─ LICENSE                                # Project license (MIT)
└─ README.md                              # Project documentation
```

## Development Notes

### Advantages of the Current Structure

#### Src-based Structure

- Industry-standard convention compatible with build tools like Maven and Gradle
- Clear separation of source code from project artifacts
- Easier integration with CI/CD pipelines
- Support for conventional test directory structure
- Cleaner project root directory

#### Data Persistence Implementation

- The application uses a configuration-based approach for data storage locations
- Path resolution is handled automatically across different operating systems
- Directories are created automatically if they don't exist

## Contributing

Contributions are welcome! To contribute:

1. Fork the repository

2. Create a new branch

   ```bash
   git checkout -b feature/your-feature
   ```

3. Commit your changes

   ```bash
   git commit -am 'Add new feature'
   ```

4. Push to the branch

   ```bash
   git push origin feature/your-feature
   ```

5. Open a pull request

## Issues

If you encounter any issues or bugs, feel free to [open an issue](https://github.com/mugabiBenjamin/CarParking/issues).

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Acknowledgements

- Java Swing documentation for GUI components
- Open-source Java tutorials and resources
- Contributors and testers for feedback and improvements

[Back to top](#carparking)

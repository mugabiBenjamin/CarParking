# CarParking

![Java](https://img.shields.io/badge/Java-17-blue)
![Swing](https://img.shields.io/badge/GUI-Swing-orange)
![License](https://img.shields.io/badge/License-MIT-green)

This Java Swing-based desktop parking system runs on Windows, macOS, and Linux. It allows users to park, unpark, and search cars by license plate, providing real-time feedback and automatic slot management. Parking data is stored in local text files for persistence across sessions. Designed for educational purposes, it demonstrates object-oriented programming, file I/O, and GUI development, offering a simple, user-friendly parking lot management tool.

## Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [Configuration](#configuration)
- [Project Structure](#project-structure)
- [Development Notes](#development-notes)
- [Contributing](#contributing)
- [Issues](#issues)
- [License](#license)
- [Acknowledgments](#acknowledgments)

## Features

- **Intuitive GUI** built with Java Swing:

  - Displays 10 parking slots in a 2x5 grid, color-coded for status:
    - **Light green**: Empty slots (non-clickable, check icon).
    - **Light red**: Occupied slots (clickable for unparking, car icon).
    - **Blue**: Found slots (highlighted for 2 seconds after search).
  - Control panels for parking, searching, batch operations, and help, with titled borders.
  - Status bar for action feedback, auto-clears after 5 seconds.
  - Help dialog with detailed user guide, accessible via a button.

- **Parking Management**:

  - Park cars in the first available slot using a license plate input.
  - Unpark cars from specific slots with confirmation dialogs (irreversible action).
  - Batch unpark multiple selected slots with confirmation.
  - Search for cars by license plate, highlighting the slot if found.

- **License Plate Validation**:

  - Supports three formats:
    - **Normal**: `UAA 123B` (U, two letters, space, three digits, letter).
    - **Government**: `UG 123B` (UG, space, three digits, letter).
    - **Personalized**: 2–8 characters starting with a letter (e.g., `ABC123`).
  - Real-time validation with visual feedback (green check for valid, red X with tooltip for invalid).
  - Preserves invalid inputs for correction with refocused input field.

- **Data Persistence**:

  - Stores parking data in `data/parking_lot.txt` (format: `(slotNumber, licensePlate or EMPTY)`).
  - Generates CSV reports (`data/parking_lot_report.csv`) with slot number, status, and license plate.
  - Configurable data file path via `config.properties`.

- **MVC Architecture**:

  - Separates model (`Car`, `ParkingLot`, `ParkingSlot`), view (Swing panels), and controller (`ParkingController`).
  - Uses `ParkingListener` for event-driven communication between controller and view.

- **Accessibility Features**:

  - High-contrast colors (light red/green, blue) for visibility.
  - Tooltips for all interactive elements, compatible with screen readers.
  - Keyboard shortcuts (Enter key for park/search actions).
  - Error dialogs with recovery steps for user guidance.

- **Error Handling and Logging**:
  - Handles invalid inputs, file I/O errors, and disk space issues with descriptive dialogs.
  - Logs actions, errors, and warnings with timestamps via `Logger` for debugging.

## Prerequisites

- [Java Development Kit (JDK) 17](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html) (Recommended: [OpenJDK](https://adoptium.net/))
- A Java IDE (e.g., [IntelliJ IDEA](https://www.jetbrains.com/idea/download/), [Eclipse](https://www.eclipse.org/downloads/), [VS Code](https://code.visualstudio.com/Download)) or terminal access

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

3. Verify icon resources in `src/resources/icons/`:

   ```plaintext
   car.png, check.png, check-green.png, help.png, report.png, search.png, unpark.png, x.png
   ```

4. Compile the project:

   ```bash
   javac -d bin src/**/*.java src/*.java
   ```

5. Run the application:

   ```bash
   java -cp bin Main
   ```

The GUI should launches. The `data/parking_lot.txt` file is created automatically if it doesn't exist.

## Usage

### Parking a Car

- In the **Park a Car** panel:
  - Enter a license plate (e.g., `UAA 123B`, `UG 123B`, `ABC123`).
  - Click **Park** or press **Enter.**
  - Feedback: Green check for valid input, red X with tooltip for invalid input.
  - On success, the car is parked in the first available slot, and the status bar updates.

### Searching for a Car

- In the **Search for a Car** panel:
  - Enter a license plate.
  - Click **Search** or press **Enter.**
  - If found, the slot highlights blue for 2 seconds; otherwise, a dialog shows the result.
  - Invalid inputs trigger an error dialog with preserved input.

### Batch Unparking

- In the **Batch Operations** panel:
  - Select occupied slots using checkboxes.
  - Click **Batch Unpark** and confirm the action.
  - Selected slots are cleared, and the status bar updates.

### Generating Reports

- In the **Batch Operations** panel, click **Generate Report.**
- A CSV file (`data/parking_lot_report.csv`) is created with slot details.

### Accessing Help

- Click the **Help** button to view a detailed user guide in a scrollable dialog.
- Access the GitHub repository via the **Online Help** menu for further documentation.

## Configuration

The `config.properties` file in the project root specifies the data file path:

```properties
# Application Configuration
parking.data.file=data/parking_lot.txt
```

- **parking.data.file:** Relative path to the parking data file.
- The `data/` directory and `parking_lot.txt` are created automatically if missing.
- Listed in `.gitignore` to avoid committing sensitive paths.

## Project Structure

```plaintext
CarParking/
├─ src/
│  ├─ controller/
│  │  └─ ParkingController.java              # Handles user actions and updates model/view (main app logic)
│  ├─ data/
│  │  ├─ .gitkeep                            # Ensures the data directory exists in Git
│  │  └─ parking_lot.txt                     # Stores persistent parking slot data (auto-created)
│  ├─ model/
│  │  ├─ Car.java                            # Represents a car and its license plate
│  │  ├─ ParkingLot.java                     # Manages all parking slots and cars (core data model)
│  │  └─ ParkingSlot.java                    # Represents a single parking slot (occupied/empty)
│  ├─ resources/
│  │  └─ icons/                              # Contains icon image files for the GUI
│  ├─ util/
│  │  ├─ FileHelper.java                     # File I/O utilities for reading/writing parking data
│  │  ├─ IconUtil.java                       # Loads and manages icon resources
│  │  ├─ Logger.java                         # Logs parking, unparking, and search actions (WIP)
│  │  ├─ MessageBox.java                     # Utility for showing dialogs and messages
│  │  └─ Validator.java                      # Validates license plate input formats
│  ├─ view/
│  │  ├─ HelpPanel.java                      # Help dialog content and layout
│  │  ├─ ParkingSlotPanel.java               # Visual representation of a single parking slot
│  │  ├─ ParkingView.java                    # Main application window and layout
│  │  ├─ ParkPanel.java                      # "Park a Car" input section
│  │  ├─ RoundedBorder.java                  # Custom border for rounded UI elements
│  │  ├─ SearchPanel.java                    # "Search for a Car" input section
│  │  └─ SlotPanel.java                      # Base panel for slot-related UI
│  └─ Main.java                              # Application entry point (launches the GUI)
├─ .gitignore                                # Git ignore rules (e.g., config, build files)
├─ config.properties                         # Configurable properties (e.g., data file path)
├─ LICENSE                                   # Project license (MIT)
└─ README.md                                 # Project documentation
```

## Development Notes

### Advantages of the Current Structure

#### Src-based Structure

- Industry-standard, compatible with Maven/Gradle.
- Separates source code from artifacts, supporting CI/CD pipelines.
- Clean project root, with clear package organization (`controller/`, `model/`, `view/`, `util/`).

#### Data Persistence

- Configurable via `config.properties` for flexible storage locations.
- Automatic directory creation for `data/parking_lot.txt`.
- Cross-platform path resolution.

## Contributing

Contributions are welcome! To contribute:

1. Fork the repository

2. Create a new branch with a descriptive name (e.g., feature/add-logging, fix/bug-name):

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

5. Open a pull request, ensuring compilation (`javac`) and runtime tests pass.

## Issues

If you encounter any issues or bugs, feel free to [open an issue](https://github.com/mugabiBenjamin/CarParking/issues).

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Java Swing documentation for GUI components
- Open-source Java tutorials and resources
- Contributors and testers for feedback and improvements

[Back to top](#carparking)

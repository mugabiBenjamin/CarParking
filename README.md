# CarParking

![Java](https://img.shields.io/badge/Java-17-blue)
![Swing](https://img.shields.io/badge/GUI-Swing-orange)

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

- **User-friendly GUI** developed with Java Swing, featuring:

  - Color-coded parking slots (`light green` for available, `light red` for occupied, `blue` for search results).
  - Center-aligned control sections with TitledBorder grouping (`Park a Car`, `Search for a Car`).
  - Real-time updates on parking, unparking, and search actions.
  - Help dialog for user guidance.

- **Real-time parking management:**

  - Visual display of 10 parking slots with vehicle license plates.
  - Park cars in the first available slot via license plate input.

- **Search functionality:**

  - Locate parked vehicles by license plate, with slots highlighted blue for 2 seconds.
  - Real-time validation feedback (green check for valid input, red X with tooltip for invalid).

- **Persistent storage** using file-based data (`data/parking_lot.txt`), configurable via `config.properties`, retaining parking data across sessions.

- **Car plate validation:**

  - Supports normal (`UAA 123B`), government (`UG 123B`), and personalized (2–8 chars, e.g., `ABC123`) formats.
  - Real-time validation with non-destructive error handling (input preserved, refocus on errors).

- **MVC-like architecture** for clear separation of concerns, enhancing maintainability.

- **Accessibility features:**

  - High-contrast colors, screen-reader-compatible tooltips, and tab navigation.
  - Error dialogs and status bar feedback (clears after 5 seconds) for user guidance.

- **Logging system** (work-in-progress) for tracking parking, unparking, and search actions.

## Prerequisites

- [Java Development Kit (JDK) 17](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) _(**Recommended:** [OpenJDK](https://adoptium.net/))_
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

3. Verify icon resources in `src/resources/icons/`:

   ```plaintext
   CarParking/src/resources/icons/
   ├─ car.png
   ├─ check.png
   ├─ check-green.png
   ├─ x.png
   ├─ search.png
   └─ help.png
   ```

4. Compile the project:

   ```bash
   javac -d bin src/**/*.java src/*.java
   ```

5. Run the application:

   ```bash
   java -cp bin Main
   ```

_The GUI should launch, allowing you to manage parking slots._
_The `data/parking_lot.txt` file is created automatically if it doesn't exist._

## Usage

### Managing Vehicles

1. **Parking a Car:**

   - In the "Park a Car" section, enter a license plate (e.g., `UAA 123B`, `UG 123B`, `ABC123`) in the License Plate field.
   - Click `Park` or press Enter to assign the vehicle to the first available slot.
   - Validation feedback: Green check for valid input, red X with tooltip for invalid input.

2. **Finding a Car:**

   - In the "Search for a Car" section, enter a license plate in the License Plate field.
   - Click `Search` or press Enter to locate the parked vehicle.
   - If found, the slot highlights blue for 2 seconds; otherwise, an info dialog appears.

3. **Removing a Car:**

   - Click an occupied parking slot `light red` to unpark the vehicle.
   - Confirm the removal in the dialog (irreversible action).
   - The slot turns `light green`, and the status bar updates.

### User Interface Guide

- **Control Panel:** Top section with "Park a Car" and "Search for a Car" fields/buttons, plus a Help button (right-aligned).

- **Parking Slots Display:** Central grid (2x5) showing 10 slots:

  - Light green (empty, not clickable, check icon).
  - Light red (occupied, clickable, car icon).
  - Blue (found via search, temporary).

- **Status Bar:** Bottom, shows action feedback (e.g., "Parked car ABC123 in slot 1"), clears after 5 seconds.
- **Help Dialog:** Click Help to view a 600x500 guide (vertical scrollbar if needed).
- **Error Handling:** Invalid inputs trigger dialogs with tooltips, preserve input, and refocus the field.

## Configuration

The `config.properties` file in the project root (`CarParking/`) specifies the data storage path. Example:

```properties
# Application Configuration
parking.data.file=data/parking_lot.txt
```

- **parking.data.file:** Path to the parking data file, relative to the project root.
- The `data/` directory and `parking_lot.txt` are created automatically if missing.
- Use relative paths for portability; avoid absolute paths.
- Do not commit `config.properties` to Git (listed in `.gitignore`).

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
│  ├─ resources/
│  │  └─ icons/
│  ├─ util/
│  │  ├─ Logger.java                      # Logs parking, unparking, and search actions
│  │  ├─ MessageBox.java                  # Utility for displaying dialog messages
│  │  └─ Validator.java                   # Validates license plate formats and inputs
│  ├─ view/
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

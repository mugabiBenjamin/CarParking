# CarParking

![Java](https://img.shields.io/badge/Java-17-blue)
![Swing](https://img.shields.io/badge/GUI-Swing-orange)
![License](https://img.shields.io/badge/License-MIT-green)

CarParking is a Java Swing desktop parking management system for Windows, macOS, and Linux. It allows users to park, unpark, batch-unpark, search cars by license plate, generate parking reports, and receive real-time feedback through a graphical interface.

Parking data is persisted locally using text files, making the application simple to run without requiring a database. The project is designed for educational purposes and demonstrates object-oriented programming, Swing GUI development, file I/O, input validation, event-driven UI updates, and basic application error handling.

## Table of Contents

- [CarParking](#carparking)
  - [Table of Contents](#table-of-contents)
  - [Features](#features)
    - [Graphical User Interface](#graphical-user-interface)
    - [Parking Management](#parking-management)
    - [Uganda License Plate Validation](#uganda-license-plate-validation)
    - [Data Persistence](#data-persistence)
    - [Architecture](#architecture)
    - [Accessibility and UX](#accessibility-and-ux)
    - [Error Handling and Logging](#error-handling-and-logging)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Usage](#usage)
    - [Parking a Car](#parking-a-car)
    - [Searching for a Car](#searching-for-a-car)
    - [Unparking a Car](#unparking-a-car)
    - [Batch Unparking](#batch-unparking)
    - [Generating Reports](#generating-reports)
    - [Accessing Help](#accessing-help)
  - [Project Structure](#project-structure)
  - [Development Notes](#development-notes)
  - [Potential Improvements](#potential-improvements)
  - [Contributing](#contributing)
  - [Issues](#issues)
  - [License](#license)
  - [Acknowledgments](#acknowledgments)

## Features

### Graphical User Interface

- Built with Java Swing.
- Displays 10 parking slots in a 2x5 grid.
- Uses color-coded slot states:

  - Light green: empty slot.
  - Light red: occupied slot.
  - Blue: found slot highlighted temporarily after search.
- Includes panels for:

  - Parking a car.
  - Searching for a car.
  - Batch operations.
  - Help and user guidance.
- Status bar displays action feedback and automatically clears after a short delay.
- Help dialog provides usage guidance inside the application.

### Parking Management

- Parks cars in the first available slot.
- Prevents duplicate active license plates.
- Unparks cars from occupied slots after confirmation.
- Supports batch unparking of selected occupied slots.
- Searches parked cars by license plate.
- Highlights the matching slot when a car is found.

### Uganda License Plate Validation

The application validates and normalizes supported Uganda license plate formats.

Supported examples include:

- Ordinary private: `UA 001AA`
- Legacy private: `UAA 123B`
- Government: `UG 32 00042`
- Legacy government: `UG 123B`
- Diplomatic: `CD 01 02 U`
- Motorcycle: `UMA 001AA`
- Personalized plates: 2–8 characters, starting with a letter

Validation feedback includes:

- Green check icon for valid input.
- Red X icon and tooltip for invalid input.
- Error dialogs with recovery guidance.
- Preserved invalid inputs for easy correction.

### Data Persistence

- Saves parking data locally in:

  ```plaintext
  data/parking_lot.txt
  ```

- Parking data format:

  ```plaintext
  (slotNumber, licensePlate or EMPTY)
  ```

- Generates CSV reports at:

  ```plaintext
  data/parking_lot_report.csv
  ```

- Report columns:

  ```plaintext
  Slot Number,Status,License Plate
  ```

- Automatically creates the `data/` directory and parking data file when needed.

### Architecture

The current project follows a simple MVC-style structure:

- `model`: core parking entities.
- `view`: Swing UI components.
- `controller`: coordination between UI and parking operations.
- `util`: reusable utilities for validation, logging, dialogs, icons, and file persistence.

The code has been cleaned to improve separation of concerns, reduce duplicated validation behavior, centralize constants, improve logging, and handle errors more gracefully.

### Accessibility and UX

- High-contrast slot colors.
- Tooltips for interactive controls.
- Keyboard support using the Enter key for park and search actions.
- Confirmation dialogs for irreversible actions.
- User-friendly error messages with recovery steps.
- Status bar feedback for user actions.

### Error Handling and Logging

- Handles invalid input, missing files, file format issues, and file I/O failures.
- Logs information, warnings, and errors with timestamps.
- Redacts sensitive-looking values such as passwords, tokens, secrets, and keys from logs.
- Provides graceful fallback behavior where possible.

## Prerequisites

- Java Development Kit 17 or newer.
- Recommended: OpenJDK 17 or OpenJDK 21.
- Terminal access or a Java IDE such as IntelliJ IDEA, Eclipse, or VS Code.

Check Java installation:

```bash
java -version
javac -version
```

On Ubuntu, install Java if missing:

```bash
sudo apt update
sudo apt install openjdk-21-jdk
```

## Installation

Clone the repository:

```bash
git clone https://github.com/mugabiBenjamin/CarParking.git
cd CarParking
```

Confirm icon resources exist:

```plaintext
src/resources/icons/car.png
src/resources/icons/check.png
src/resources/icons/check-green.png
src/resources/icons/help.png
src/resources/icons/report.png
src/resources/icons/search.png
src/resources/icons/unpark.png
src/resources/icons/x.png
```

Compile the project:

```bash
rm -rf out
mkdir -p out
javac -d out $(find src -name "-.java")
```

Copy resources into the compiled output:

```bash
cp -r src/resources out/
```

Run the application:

```bash
java -cp out Main
```

The GUI should launch. The `data/parking_lot.txt` file is created automatically if it does not already exist.

## Usage

### Parking a Car

1. Go to the **Park a Car** panel.
2. Enter a supported license plate.
3. Click **Park** or press **Enter**.
4. If valid, the car is parked in the first available slot.
5. If invalid, the application shows validation feedback and recovery guidance.

Example plates:

```plaintext
UA 001AA
UAA 123B
UG 32 00042
UG 123B
CD 01 02 U
UMA 001AA
ABC123
```

### Searching for a Car

1. Go to the **Search for a Car** panel.
2. Enter the license plate.
3. Click **Search** or press **Enter**.
4. If found, the occupied slot is highlighted in blue temporarily.
5. If not found, the application displays a message.

### Unparking a Car

1. Find an occupied slot.
2. Click the unpark button on that slot.
3. Confirm the action.
4. The slot becomes available again.

### Batch Unparking

1. Select occupied slots using the available checkboxes.
2. Click **Batch Unpark**.
3. Confirm the action.
4. Selected occupied slots are cleared.

### Generating Reports

1. Go to the **Batch Operations** panel.
2. Click **Generate Report**.
3. The report is generated at:

```plaintext
data/parking_lot_report.csv
```

### Accessing Help

Click the **Help** button to open the built-in user guide.

## Project Structure

```plaintext
CarParking/
├─ src/
│  ├─ controller/
│  ├─ data/
│  │  └─ .gitkeep
│  ├─ model/
│  ├─ resources/
│  │  └─ icons/
│  ├─ util/
│  ├─ view/
│  └─ Main.java
├─ .gitignore
├─ LICENSE
└─ README.md
```

## Development Notes

- The application currently uses Swing, not JavaFX.
- Persistence is currently file-based, not database-backed.
- Parking data is stored in a local `data/` directory created at runtime.
- The controller still coordinates several responsibilities and should be further refactored in later phases.
- The project is currently suitable as a cleaned educational desktop application, not a full production-grade client-server system yet.

## Potential Improvements

Future improvements may include:

- JavaFX UI migration.
- PostgreSQL persistence through a backend API.
- Authentication and authorization.
- Role-based access control.
- Audit logging.
- PDF and date-filtered reports.
- Dynamic parking lot size.
- Configuration management.
- Unit and integration tests.
- Cross-platform installers using `jpackage`.
- CI/CD pipeline.
- Better layered architecture with services and repository interfaces.

## Contributing

Contributions are welcome.

1. Fork the repository.
2. Create a feature branch:

```bash
git checkout -b feature/your-feature
```

1. Commit your changes:

```bash
git commit -am "Add new feature"
```

1. Push to the branch:

```bash
git push origin feature/your-feature
```

1. Open a pull request.

Before opening a pull request, ensure the project compiles and runs successfully.

## Issues

Report bugs or suggest features at:

```plaintext
https://github.com/mugabiBenjamin/CarParking/issues
```

## License

This project is licensed under the MIT License. See the [`LICENSE`](./LICENSE) file for details.

## Acknowledgments

- Java Swing documentation.
- Java file I/O documentation.
- Open-source Java learning resources.
- Contributors and testers who provide feedback.

[Back to top](#carparking)

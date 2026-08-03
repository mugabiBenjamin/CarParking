# CarParking

![Java](https://img.shields.io/badge/Java-17-blue)
![Swing](https://img.shields.io/badge/GUI-Swing-orange)
![Architecture](https://img.shields.io/badge/Architecture-Layered-lightgrey)
![License](https://img.shields.io/badge/License-MIT-green)

CarParking is a Java Swing desktop parking management system for Windows, macOS, and Linux. It allows users to park cars, unpark cars, batch-unpark selected slots, search by license plate, generate CSV reports, and receive clear feedback through a graphical interface.

The application uses local file persistence, so it can run without a database. It is designed for educational purposes and demonstrates Java OOP, Swing GUI development, layered architecture, file I/O, validation, graceful error handling, and simple local persistence.

## Table of Contents

- [CarParking](#carparking)
  - [Table of Contents](#table-of-contents)
  - [Features](#features)
    - [Graphical User Interface](#graphical-user-interface)
    - [Parking Management](#parking-management)
    - [Uganda License Plate Validation](#uganda-license-plate-validation)
    - [Persistence](#persistence)
    - [Error Handling and Logging](#error-handling-and-logging)
  - [Architecture](#architecture)
    - [Domain Layer](#domain-layer)
    - [Application Layer](#application-layer)
    - [Infrastructure Layer](#infrastructure-layer)
    - [Presentation Layer](#presentation-layer)
  - [Project Structure](#project-structure)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Running the Application](#running-the-application)
  - [Usage](#usage)
    - [Parking a Car](#parking-a-car)
    - [Searching for a Car](#searching-for-a-car)
    - [Unparking a Car](#unparking-a-car)
    - [Batch Unparking](#batch-unparking)
    - [Generating Reports](#generating-reports)
    - [Accessing Help](#accessing-help)
  - [Data Persistence](#data-persistence)
  - [Configuration](#configuration)
  - [Development Notes](#development-notes)
  - [Potential Improvements](#potential-improvements)
  - [Contributing](#contributing)
  - [Issues](#issues)
  - [License](#license)
  - [Acknowledgments](#acknowledgments)

## Features

### Graphical User Interface

- Built with Java Swing.
- Displays parking slots in a grid layout.
- Uses color-coded slot states:

  - Light green: empty slot.
  - Light red: occupied slot.
  - Blue: searched slot highlight.
- Includes panels for:

  - Parking a car.
  - Searching for a car.
  - Batch operations.
  - Help and user guidance.
- Provides status bar feedback after user actions.
- Includes confirmation dialogs for unpark and batch-unpark actions.

### Parking Management

- Parks cars in the first available slot.
- Prevents duplicate active license plates.
- Unparks cars from occupied slots.
- Supports batch unparking of selected occupied slots.
- Searches parked cars by license plate.
- Highlights the matching slot when a parked car is found.

### Uganda License Plate Validation

The application validates and normalizes supported Uganda license plate formats.

Supported examples include:

```plaintext
UA 001AA
UAA 123B
UG 32 00042
UG 123B
CD 01 02 U
UMA 001AA
ABC123
```

Supported categories include:

- Ordinary private plates.
- Legacy private plates.
- Government plates.
- Legacy government plates.
- Diplomatic plates.
- Motorcycle plates.
- Personalized plates.

Validation feedback includes:

- Green check icon for valid input.
- Red X icon for invalid input.
- Tooltips explaining validation issues.
- Error dialogs with recovery guidance.

### Persistence

- Stores parking data locally.
- Generates CSV reports.
- Automatically creates required data files and folders when needed.
- Uses an atomic repository update contract for parking mutations.
- Uses temporary-file replacement to reduce partial-write corruption risk.

### Error Handling and Logging

- Handles invalid inputs gracefully.
- Handles missing or malformed parking data files.
- Handles file I/O errors with fallback behavior where possible.
- Logs information, warnings, and errors.
- Redacts sensitive-looking values such as passwords, tokens, secrets, and keys from logs.

## Architecture

The project is refactored toward a layered architecture:

```plaintext
Presentation Layer
↓
Application Layer
↓
Domain Layer
↓
Infrastructure Layer
```

### Domain Layer

Contains core business entities, value objects, enums, and domain exceptions.

Responsibilities:

- Represent cars, parking slots, and parking lots.
- Enforce parking rules.
- Validate license plates through the `LicensePlate` value object.
- Raise meaningful domain exceptions.

### Application Layer

Contains use cases, services, DTOs, validators, and repository interfaces.

Responsibilities:

- Coordinate parking operations.
- Expose request and response DTOs.
- Validate application requests.
- Keep UI and file persistence out of business workflows.
- Define repository contracts used by infrastructure.

### Infrastructure Layer

Contains file persistence, report generation, logging, and configuration.

Responsibilities:

- Load and save parking lot data.
- Generate CSV reports.
- Implement repository interfaces.
- Provide logging.
- Load application configuration.

### Presentation Layer

Contains Swing UI screens, panels, dialogs, and the presentation controller.

Responsibilities:

- Render the graphical interface.
- Capture user actions.
- Display success, validation, and error feedback.
- Delegate application operations to use cases through `ParkingViewController`.

## Project Structure

```plaintext
CarParking/
├─ src/
│  ├─ application/
│  │  ├─ dto/
│  │  ├─ repositories/
│  │  ├─ services/
│  │  ├─ usecases/
│  │  └─ validators/
│  ├─ domain/
│  │  ├─ entities/
│  │  ├─ enums/
│  │  ├─ exceptions/
│  │  └─ valueobjects/
│  ├─ infrastructure/
│  │  ├─ config/
│  │  ├─ file/
│  │  └─ logging/
│  ├─ presentation/
│  │  └─ swing/
│  │     ├─ components/
│  │     ├─ dialogs/
│  │     ├─ panels/
│  │     ├─ resources/
│  │     ├─ ParkingView.java
│  │     ├─ ParkingViewController.java
│  │     └─ SwingApplication.java
│  ├─ resources/
│  │  └─ icons/
│  └─ Main.java
├─ data/
├─ .gitignore
├─ LICENSE
└─ README.md
```

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

## Running the Application

Compile the project:

```bash
rm -rf out
mkdir -p out
javac -d out $(find src -name "*.java")
```

Copy resources into the compiled output:

```bash
cp -r src/resources out/ 2>/dev/null || true
```

Run the application:

```bash
java -cp out Main
```

The GUI should launch. The parking data file is created automatically if it does not already exist.

## Usage

### Parking a Car

1. Go to the **Park a Car** panel.
2. Enter a supported license plate.
3. Click **Park** or press **Enter**.
4. The car is parked in the first available slot.
5. If the plate is invalid or duplicated, the application shows an error with recovery guidance.

### Searching for a Car

1. Go to the **Search for a Car** panel.
2. Enter the license plate.
3. Click **Search** or press **Enter**.
4. If found, the matching occupied slot is highlighted temporarily.
5. If not found, the application displays a clear message.

### Unparking a Car

1. Find an occupied slot.
2. Click **Unpark** on that slot.
3. Confirm the action.
4. The slot becomes available again.

### Batch Unparking

1. Select occupied slots using the available checkboxes.
2. Click **Batch Unpark**.
3. Confirm the action.
4. The selected occupied slots are cleared.

### Generating Reports

1. Go to the **Batch Operations** panel.
2. Click **Generate Report**.
3. The report is generated as a CSV file.

### Accessing Help

Click the **Help** button to open the built-in user guide.

## Data Persistence

Parking data is saved locally using this format:

```plaintext
(slotNumber, licensePlate or EMPTY)
```

Example:

```plaintext
(1, UA 001AA)
(2, EMPTY)
(3, UAA 123B)
```

By default, parking data is stored at:

```plaintext
data/parking_lot.txt
```

CSV reports are generated at:

```plaintext
data/parking_lot_report.csv
```

Report columns:

```plaintext
Slot Number,Status,License Plate
```

## Configuration

The application can use default configuration values or load values from:

```plaintext
config.properties
```

Example configuration:

```properties
parking.lot.size=10
data.directory=data
parking.lot.file=parking_lot.txt
report.file=parking_lot_report.csv
app.title=Car Parking System
```

If the configuration file is missing or invalid, the application falls back to safe defaults.

## Development Notes

- The application uses Java Swing, not JavaFX.
- Persistence is file-based, not database-backed.
- The project is structured using layered architecture.
- Domain logic is separated from UI and file persistence.
- Application services use repository interfaces instead of depending directly on file utilities.
- Infrastructure implements file persistence and report generation.
- Presentation delegates user actions to use cases through `ParkingViewController`.
- Parking mutations use an atomic repository update contract to reduce lost-update risks within the same application process.

## Potential Improvements

Future improvements may include:

- Unit and integration tests.
- Stronger file locking for multiple application instances.
- PostgreSQL persistence.
- Backend API support.
- Authentication and authorization.
- Role-based access control.
- Audit logging.
- PDF reports.
- Date-filtered reports.
- Dynamic parking lot configuration through the UI.
- JavaFX UI migration.
- Cross-platform installers using `jpackage`.
- CI/CD pipeline.

## Contributing

Contributions are welcome.

1. Fork the repository.
2. Create a feature branch:

    ```bash
    git checkout -b feature/your-feature
    ```

3. Commit your changes:

    ```bash
    git commit -am "Add new feature"
    ```

4. Push to the branch:

    ```bash
    git push origin feature/your-feature
    ```

5. Open a pull request.

Before opening a pull request, ensure the project compiles and runs successfully:

```bash
rm -rf out
mkdir -p out
javac -d out $(find src -name "*.java")
cp -r src/resources out/ 2>/dev/null || true
java -cp out Main
```

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

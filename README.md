# CarParking

![Java](https://img.shields.io/badge/Java-17-blue)
![Swing](https://img.shields.io/badge/GUI-Swing-orange)

This Java Swing-based desktop parking system runs on Windows, macOS, and Linux. It allows users to park, unpark, and search cars by license plate, providing real-time feedback and automatic slot management. Parking data is stored in local text files for persistence across sessions. Designed mainly for educational purposes, it demonstrates object-oriented programming, file I/O, and GUI development, offering a simple, user-friendly parking lot management tool.

## Features

- Add and remove cars from parking slots
- Visual representation of parking slots and their status
- Persistent storage of parking lot data
- Input validation for car details
- User-friendly alerts and messages
- Simple logging for actions and errors

## Prerequisites

- [Java Development Kit (JDK) 8 or higher](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) _(**Recommended:** [OpenJDK](https://adoptium.net/))_
- A Java IDE (e.g., [IntelliJ IDEA](https://www.jetbrains.com/idea/download/), [Eclipse](https://www.eclipse.org/downloads/), [VS Code](https://code.visualstudio.com/Download)) or terminal access
- [Git](https://git-scm.com/downloads) (optional, for cloning the repository)

## Installation and Usage

1. **Clone the repository:**

   ```bash
   git clone https://github.com/mugabiBenjamin/CarParking.git
   cd CarParkingSystem
   ```

2. **Compile the project:**

   ```bash
   javac Main.java
   ```

3. **Run the application:**

   ```bash
   java Main
   ```

   The GUI should launch, allowing you to manage parking slots.

## Project Structure

```plaintext
CarParking/
├─ src/
│  ├─ controller/
│  │  └─ ParkingController.java        # Handles user actions and updates the model/view
│  ├─ data/
│  │  └─ parking_lot.txt               # Stores persistent parking lot data
│  ├─ model/
│  │  ├─ Car.java                      # Represents a car object
│  │  ├─ FileHelper.java               # Utility for file read/write operations
│  │  ├─ ParkingLot.java               # Manages parking slots and cars
│  │  └─ ParkingSlot.java              # Represents a single parking slot
│  ├─ util/
│  │  ├─ Logger.java                   # Simple logging utility for actions/errors
│  │  └─ Validator.java                # Validates user input and car details
│  ├─ view/
│  │  ├─ MessageBox.java               # Displays alerts and messages to the user
│  │  ├─ ParkingSlotPanel.java         # GUI component for individual parking slots
│  │  └─ ParkingView.java              # Main GUI window for the application
│  └─ Main.java                        # Application entry point
├─ .gitignore                          # Git ignore rules
├─ LICENSE                             # Project license
└─ README.md                           # Project documentation
```

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

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Acknowledgements

- Java Swing documentation for GUI components
- Open-source Java tutorials and resources
- Contributors and testers for feedback and improvements

[Back to top](#carparking)

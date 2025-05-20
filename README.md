# CarParking

CarParking is a simple Java-based parking lot management system. It provides a graphical user interface (GUI) for managing parking slots, registering cars, and tracking slot availability. The system is designed for educational purposes and demonstrates basic principles of object-oriented programming, file I/O, and GUI development in Java.

## Features

- Add and remove cars from parking slots
- Visual representation of parking slots and their status
- Persistent storage of parking lot data
- Input validation for car details
- User-friendly alerts and messages
- Simple logging for actions and errors

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- A Java IDE (e.g., IntelliJ IDEA, Eclipse, VS Code) or terminal access
- Git (optional, for cloning the repository)

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

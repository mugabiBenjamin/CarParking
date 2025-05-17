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
CarParkingSystem/
│
├── Main.java                             # Entry point
│
├── controller/
│   └── ParkingController.java            # Handles user actions
│
├── model/
│   ├── Car.java                          # Car object
│   ├── ParkingSlot.java                  # Individual slot
│   ├── ParkingLot.java                   # Manages all slots
│   └── FileHelper.java                   # File I/O utilities
│
├── view/
│   ├── ParkingView.java                  # Main GUI
│   ├── ParkingSlotPanel.java            # Visual slot unit
│   └── MessageBox.java                   # Alerts/info popups
│
├── util/
│   ├── Validator.java                    # Input validation helpers
│   └── Logger.java                       # Simple logging class
│
├── data/
│   └── parking_lot.txt                   # Saves slot status
│
└── README.md                             # Project overview + instructions
```

## Contributing

Contributions are welcome! To contribute:

1. Fork the repository
2. Create a new branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Open a pull request

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Acknowledgements

- Java Swing documentation for GUI components
- Open-source Java tutorials and resources
- Contributors and testers for feedback and improvements

[Back to top](#carparking)

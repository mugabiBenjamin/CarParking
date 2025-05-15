# CarParking

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

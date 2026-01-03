#Paradise Resorts - Resort Reservation System [JAVA AAT]

A comprehensive Java Swing application for managing resort reservations with user authentication, admin panel, and invoice generation.

## Features

- **User Authentication**: Login/Register with role-based access (Customer/Admin)
- **Resort Browsing**: View available resorts with details
- **Room Selection**: Choose rooms with real-time pricing from database
- **Cuisine Options**: Select meal plans (Local, Italian, Continental)
- **Booking Management**: Complete reservation flow
- **Invoice Generation**: Automatic invoice creation stored in database
- **Admin Panel**: CRUD operations for resorts, rooms, food options, and bookings

## Project Structure

```
ResortBookingSystem/
├── src/
│   ├── ResortReservationApp.java      # Main entry point
│   ├── database/
│   │   └── DatabaseManager.java       # SQLite connection & setup
│   ├── utils/
│   │   └── PasswordUtils.java         # Password hashing
│   ├── models/                        # Data models
│   ├── dao/                           # Data Access Objects
│   └── views/                         # Swing UI components
│       ├── customer/                  # Customer panels
│       └── admin/                     # Admin panels
├── lib/
│   └── sqlite-jdbc-3.x.x.jar         # SQLite JDBC driver
└── data/
    └── resort_system.db              # SQLite database (auto-created)
```

## Prerequisites

- Java 11 or higher
- SQLite JDBC driver (download from Maven Repository)

## Setup

1. **Download SQLite JDBC Driver**:
   - Download from: https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc
   - Place `sqlite-jdbc-3.x.x.jar` in the `lib/` folder

2. **Create lib folder**:
   ```bash
   mkdir lib
   ```

## Compilation

```bash
# Windows (PowerShell)
javac -cp "lib/*" -d out -sourcepath src src/ResortReservationApp.java src/**/*.java
```

Or use the provided build script.

## Running the Application

```bash
# Windows
java -cp "out;lib/*" ResortReservationApp
```

## Default Credentials

| Role     | Username | Password  |
|----------|----------|-----------|
| Admin    | admin    | admin123  |
| Customer | (register new) | (any) |

## Sample Data

The application auto-creates sample data on first run:
- 3 Resorts (Paradise Beach, Mountain View, Tropical Oasis)
- 9 Room configurations
- 6 Food/Cuisine options

## Admin Features

- **Resorts Tab**: Add, edit, delete resorts
- **Rooms Tab**: Manage rooms, update prices and availability
- **Food Options Tab**: Configure cuisine and meal plans with pricing
- **Bookings Tab**: View all bookings, update status

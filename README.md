# Association Assignment System

A client-server desktop application for managing volunteer service preferences and assignments, built in Java using JavaFX.

## 💡 Project Overview

This system allows volunteers to:
- Select 5 preferred services from a dropdown
- View their assigned service
- See overall assignment distribution (general view)
- View all volunteer-to-service mappings (detailed view)
- Refresh their data dynamically

The application simulates **multiple clients** connecting to a **central server**, using HTTP requests.

## 🧱 Technologies Used

- Java 21
- JavaFX (FXML)
- Gradle
- Java HTTP Client (no external libraries)
- MVC-like structure

## 🛠️ Running the Project

### Prerequisites
- JDK 21+
- JavaFX configured
- Gradle (wrapper included)

### Run the Server

```bash
./gradlew run
```

### Run a Client (in a new terminal)

```bash
./gradlew runClient
```
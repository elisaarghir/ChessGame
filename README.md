<div align="center">

# ChessGame
</div>

## About

A Chess game built in Java, featuring both a **terminal-based interface** and a **Swing GUI**, with full move validation and game logic. The project is structured around Object-Oriented Programming principles, with each chess piece modeled as a class and the board as a central game manager.

Player accounts and game history are persisted locally using JSON.

---

## Features

- Full chess move validation for all piece types
- Two interface modes: terminal and Java Swing GUI
- Player account system with login and registration
- Game persistence via JSON (accounts and game history saved locally)
- OOP architecture - each piece type is its own class extending a common base

---

## Project Structure

```
ChessGame/
├── src/
│   ├── Main.java           # Entry point
│   ├── Board.java          # Game board and move logic
│   ├── ChessPiece.java     # Abstract base class for pieces
│   └── ...                 # Individual piece classes (King, Queen, Rook, etc.)
├── lib/
│   └── json-simple-1.1.1.jar   # JSON library for data persistence
├── input/
│   ├── accounts.json       # Saved player accounts
│   └── games.json          # Saved game history
└── .gitignore
```

---

## Prerequisites

- **Java JDK 8+** - required to compile and run the project
- **json-simple-1.1.1.jar** - must be present in `lib/` for data persistence

---

## Getting Started

### Compile

```bash
cd src/

# Windows
javac -cp ".;..\lib\json-simple-1.1.1.jar" Main.java

# Linux / Mac
javac -cp ".:../lib/json-simple-1.1.1.jar" Main.java
```

### Run

```bash
# Windows
java -cp ".;..\lib\json-simple-1.1.1.jar" Main

# Linux / Mac
java -cp ".:../lib/json-simple-1.1.1.jar" Main
```

---

## Usage

On startup, the main menu offers:

1. **Login** - access an existing player account to resume progress or view stats
2. **New Account** - create a new player profile
3. **Exit** - terminate the application

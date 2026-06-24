# Chess Game (Java Terminal Implementation)
Chess game implementation in Java, focusing on core game logic and move validation. This project uses a terminal-based interface and incorporates Object-Oriented Programming (OOP) principles.

## Project Structure
- src/: Contains all Java source files (Main.java, Board.java, ChessPiece.java, etc.).

- lib/: Contains external dependencies, specifically json-simple-1.1.1.jar.

- input/: Stores data persistence files like accounts.json and games.json.

- .gitignore: Configured to exclude IDE settings (.idea/) and compiled binaries (.class).

## Prerequisites
- Java JDK: Version 8 or higher is required to compile and run the project.

- JSON Library: The json-simple-1.1.1.jar library must be present in the lib/ directory for data persistence to work.

## How to Run
Follow these steps to compile and execute the game from your terminal:

1. Navigate to the source directory. Open your terminal and enter the src folder:

cd src/

2. Compile the project.
Run the following command to compile the source code and link the external JSON library:

javac -cp ".;..\lib\json-simple-1.1.1.jar" Main.java

3. Run the application.
Launch the game using this command:

java -cp ".;..\lib\json-simple-1.1.1.jar" Main

**Note for Linux/Mac users: Replace the semicolon (;) with a colon (:) and backslashes with forward slashes in the -cp command.**

## Usage
Upon starting the application, you will be greeted by the Main Menu:

1. Login: Access an existing player account to resume progress or view stats.

2. New Account: Create a new player profile.

3. Exit: Safely terminate the application.

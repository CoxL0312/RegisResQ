# RegisResQ

**RegisResQ** is a Java desktop application for tracking and managing adoptable animals at a rescue shelter.  
It provides an intuitive interface for adding, editing, sorting, and viewing pets, backed by a MySQL database.

## Author
Lindsey Cox
Computer Science @ Regis University
https://www.linkedin.com/in/lindseycox1234/

---

## Features

- Add, edit, delete, and view adoptable animals  
- Sort animals by type, breed, name, sterilization status, or arrival date  
- Filter to view only cats or dogs  
- Confirm before deletion to prevent accidental data loss  
- Layered architecture for maintainability:
  - `application` — Core logic and controllers  
  - `persistence` — Database access (DAO classes)  
  - `presentation` — GUI (Swing-based)

---

## Database

**Database:** `animals`  
**Table:** `adoptable_pets`

| Field Name    | Type         | Description                  |
|----------------|--------------|------------------------------|
| id             | INT (PK)     | Unique identifier            |
| type           | VARCHAR(10)  | Dog or Cat                   |
| name           | VARCHAR(50)  | Animal's name                |
| breed          | VARCHAR(50)  | Breed of the animal          |
| sterilized     | BOOLEAN      | True if spayed/neutered      |
| date_arrived   | DATE         | Arrival date at the shelter  |

---

## Testing

Unit tests are implemented using **TestNG**.  
Each domain class (e.g. `Dog`, `Cat`) includes validation logic tested through:
- Boundary value analysis  
- Equivalence class partitioning  
- Leap-year date validation  


## Setup and Run

Prerequisites:
-Java 21+
-MySQL 8+
-Any Java IDE

Steps:
1. Clone the repository
     git clone https://github.com/<your-username>/RegisResQ.git
    cd RegisResQ
2. Create the MySQL database
   CREATE DATABASE animals;
    USE animals;
    CREATE TABLE adoptable_pets (
      id INT AUTO_INCREMENT PRIMARY KEY,
      type VARCHAR(10),
      name VARCHAR(50),
      breed VARCHAR(50),
      sterilized BOOLEAN,
      date_arrived DATE
    );
3. Compile and run
    javac -d bin $(find src -name "*.java")
java -cp bin RegisResQ.presentation.Main



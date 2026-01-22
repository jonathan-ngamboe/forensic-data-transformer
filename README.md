# Forensic Data Transformer

A Java CLI engine designed to ingest hierarchical forensic evidence and transform it into structured, analyzable formats.

## Architecture

The application follows a linear ETL (Extract, Transform, Load) pipeline, orchestrated by a CLI interface.

### 1. Loader (Extract)

* **Package:** `loader`
* **Role:** Opens the evidence file and streams content line-by-line using `Files.lines()` for memory efficiency (Lazy Loading).

### 2. Parser (Transform)

* **Package:** `parser`
* **Role:** Contains the core business logic.
* **Implementation:** `ChatParser` implements a **State Machine**. It retains context (Block ID, Date, Platform ID) across lines and merges this metadata with message lines to create fully hydrated `ChatMessage` records.
* **Integrity:** Reconstructs broken relationships between parent blocks and child messages.

### 3. Exporter (Load)

* **Package:** `exporter`
* **Role:** Serializes the structured data to the target format.
* **Implementations:**
* `CsvChatExporter`: Generates Excel-compatible CSVs.
* `JsonChatExporter`: Generates structured JSON arrays.



### 4. CLI (Interface)

* **Package:** `cli`
* **Role:** Handles user arguments validation and application wiring using **Picocli**.
* **Flexibility:** Allows the user to select the output format at runtime.

## Data Model

**Input Structure (Hierarchical Source):**

```text
APD93823,                                      <-- Block ID (State Start)
Conversation Identifier:,8755-UUID...          <-- Metadata (Conversation)
Platform Call ID:,AdV/cBM...                   <-- Metadata (Platform)
Date and time:,10/10/19 4:10:12 PM             <-- Metadata (Time)
anonym@anonym.fr,Good luck with the exercise!  <-- Message 1
other@test.com,Thanks!                         <-- Message 2

```

**Output Structure (Normalized):**

*CSV Mode (Tabular):*

```csv
BlockID,ConversationID,PlatformID,Date,Sender,Message
APD93823,8755-UUID...,AdV/cBM...,10/10/19 4:10:12 PM,anonym@anonym.fr,Good luck with the exercise!

```

*JSON Mode (Structured):*

```json
[
  {
    "blockId": "APD93823",
    "conversationId": "8755-UUID...",
    "sender": "anonym@anonym.fr",
    "message": "Good luck with the exercise!"
  }
]

```

## Getting Started

### Prerequisites

* Java 21 or higher.
* Maven (required for managing Jackson and Picocli dependencies).

### Building the Project

Since the project uses external libraries, use Maven to build a fat JAR (including dependencies):

```bash
mvn clean package

```

### Running the Tool

The application accepts the input file, output file, and an optional format flag.

**Basic Usage (CSV Default):**

```bash
java -jar target/forensic-transformer-1.0.jar "evidence.csv" "report.csv"

```

**JSON Export:**

```bash
java -jar target/forensic-transformer-1.0.jar "evidence.csv" "report.json" --format JSON

```

**Help Menu:**

```bash
java -jar target/forensic-transformer-1.0.jar --help

```

## Testing

The project follows a **TDD** approach using JUnit 6.

* **Unit Tests:** Cover all critical components (`ChatParser`, `JsonChatExporter`, `CsvChatExporter`).
* **Edge Cases:** Specific tests handle interrupted blocks, nested CSV delimiters, and JSON escaping.

To run tests:

```bash
mvn test

```

## Project Structure

```text
src/
├── main/java/
│   ├── cli/            # Picocli command logic & Argument parsing
│   ├── exporter/       # DataExporter interface, CSV & JSON implementations
│   ├── loader/         # Generic file reading interfaces
│   ├── model/          # Immutable data structures (Records)
│   ├── parser/         # State machine logic
│   ├── util/           # Centralized Regex patterns
│   └── Main.java       # Application entry point
└── test/java/          # JUnit 6 test suite

```
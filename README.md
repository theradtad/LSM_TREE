# LSM Tree Implementation

A Java implementation of a Log-Structured Merge Tree (LSM Tree) developed to study write-optimized storage tradeoffs. LSM Trees prioritize write performance through sequential I/O and in-memory buffering, accepting higher read amplification and space usage as tradeoffs for improved write throughput compared to traditional B-Tree based storage systems.

## Core Architecture

- **Memtable**: In-memory write buffer using TreeMap
- **SSTable**: Sorted, immutable on-disk files created from memtable flushes
- **WAL**: Write-ahead log ensuring durability and crash recovery

### Write Flow
Data is first written to the in-memory memtable and simultaneously logged to the WAL for crash recovery. When the memtable reaches its size limit, it's flushed to disk as an immutable SSTable file. Background compaction merges overlapping SSTables to control space amplification while maintaining write performance.

### Read Flow
Reads check the memtable first for the most recent data. If not found, they search through SSTables from newest to oldest, potentially checking multiple files (read amplification). Bloom filters and sparse indexes optimize lookups by quickly eliminating irrelevant SSTables and providing efficient key positioning within files.

## Requirements

- JDK 17 or higher
- Maven 3.6 or higher

## Building the Project

To build the project, run:

```bash
mvn clean install
```

## Running Tests

To run the tests:

```bash
mvn test
```

## Project Structure

- `src/main/java` - Main source code
- `src/test/java` - Test source code
- `pom.xml` - Maven project configuration

# LSM Tree Optimization Features

This document outlines the implementation plan for two key optimization features in our LSM Tree: Bloom Filters and Sparse Indexes.

## Features Overview

### 1. Bloom Filters
A Bloom filter is a space-efficient probabilistic data structure used to test whether an element is a member of a set. In LSM Trees, it helps avoid unnecessary disk reads by quickly determining if a key definitely does NOT exist in an SSTable.

### 2. Sparse Indexes
A sparse index stores only a subset of keys and their file offsets, reducing memory usage while still enabling efficient data retrieval from SSTables.

## Implementation Details

### File Structure
For each SSTable, we will maintain three files:
```
data/
  sstable_N.txt      # Main data file
  sstable_N.bloom    # Bloom filter
  sstable_N.index    # Sparse index
```

### Method Flows

#### 1. Put Operation
```
put(key, value):
1. Check memstore size
2. If full, trigger flushMemstore()
3. Add to memstore
```

#### 2. Flush Operation
```
flushMemstore():
1. Get new SSTable path
2. Create Bloom filter:
   - Initialize new filter
   - Add each key from memstore
   - Save to sstable_N.bloom

3. Create sparse index:
   - Track file offsets while writing
   - Every Nth key:
     * Store key, offset, block size
   - Save to sstable_N.index

4. Write SSTable data
5. Clear memstore
```

#### 3. Get Operation
```
get(key):
1. Check memstore
2. If not found, for each SSTable (newest first):
   a. Check Bloom filter
      - If "no", skip SSTable
      - If "yes", proceed to index
   
   b. Use sparse index
      - Find closest index entry ≤ target key
      - Get file offset
   
   c. Read from offset
      - Scan until key found/passed

3. Return value or null
```

#### 4. Compaction Operation
```
compact():
1. Select SSTables to merge
2. Create new merged SSTable:
   - Generate new Bloom filter
   - Create new sparse index
   - Write merged data

3. Cleanup:
   - Delete old SSTable files
   - Delete old Bloom filters
   - Delete old indexes

4. Update file manager
```

## Technical Specifications

### Bloom Filter
- False positive probability: 1%
- Number of hash functions: 7
- Bit array size: Calculated based on expected number of elements
- Storage format: Binary file

### Sparse Index
- Indexing frequency: Every 1000th key
- Index entry format:
  ```
  {
    key: String,
    fileOffset: long,
    blockSize: int
  }
  ```
- Storage format: Binary file with fixed-length records

## Performance Expectations

### Bloom Filters
- Memory usage: ~10 bits per key
- False positive rate: 1%
- Lookup time: O(k) where k is number of hash functions

### Sparse Indexes
- Memory usage: Reduced by factor of 1000 compared to full index
- Worst-case lookup: Read 1000 entries
- Average seek time: Improved by ~50% compared to full file scan

## Implementation Priority
1. Bloom filters implementation
2. Sparse index implementation
3. Integration with existing operations
4. Performance testing and tuning

## Note
Both features are optional optimizations but highly recommended for production use of LSM Trees. They significantly improve read performance by reducing unnecessary disk I/O operations.

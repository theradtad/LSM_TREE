## Bloom Filter Implementation Steps
1. Create the bloom filter class and it's implementation.
    - Implement creating a new bloom filter and adding the hashes of all elements from a TreeMap.
    - Implement storing the new filter in a file.
    - Implement loading a bloom filter from a file.
    - Implement checking membership of an element using the bloom filter.
    - Implement retrieving the hash function of an element.

2. Create the sparse index class and its implementation.
    - Implement creating a new sparse index.
        - Sparse index is stores key value pairs using a tree map in java.
    - Implement storing the new index in a file.
    - Implement loading an index from a file.
    - Implement adding an entry to the sparse index.
    - Implement retrieving an entry from the sparse index.
    
3. During the SSTable creation - flushMemstore() - we will:
    - Create a new bloom filter and add all keys from the memstore.
    - Create a new sparse index and add every Nth key from the memstore.
    - Write the SSTable data to a file.
    - Store the bloom filter and sparse index in their respective files.

4. Implement the get() method to:
    - Check the memstore first.
    - If not found, iterate through SSTables in reverse order.
    - Use the bloom filter to check if the key might exist in the SSTable.
    - If it might exist, use the sparse index to find the closest entry and read from the file.
    - Return the value or null if not found.
5. Implement the compaction proccess to:
    - Merge multiple SSTablles into a new one.
    - Create a new bloom filter and sparse index for the merged SSTable.
    - Store the new SSTable, bloom filter, and sparse index.
    - Clean up old SSTables, bloom filters, and indexes.
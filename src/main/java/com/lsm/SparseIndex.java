package com.lsm;

import java.util.TreeMap;

public class SparseIndex {
    private TreeMap<String, FilePosition> index;
    private static final int INDEXING_FREQUENCY = 2; // Index every 2nd key
    private int keyCounter;
    
    public static class FilePosition {
        private final long offset;

        public FilePosition(long offset) {
            this.offset = offset;
        }

        public long getOffset() { return offset; }
    }

    public SparseIndex() {
        this.index = new TreeMap<>();
        this.keyCounter = 0;
    }

    public void addEntry(String key, long fileOffset) {
        keyCounter++;
        if (keyCounter % INDEXING_FREQUENCY == 0) {
            index.put(key, new FilePosition(fileOffset));
        }
    }

    public FilePosition getClosestEntry(String searchKey) {
        // Get the largest key less than or equal to searchKey
        String floorKey = index.floorKey(searchKey);
        return floorKey != null ? index.get(floorKey) : null;
    }

    public TreeMap<String, FilePosition> getIndex() {
        return new TreeMap<>(index); // Return a copy to prevent modification
    }

    public void saveToFile(String filePath) {
        // Convert FilePosition objects to string values for TreeMapWriter
        TreeMap<String, String> serializedIndex = new TreeMap<>();
        for (var entry : index.entrySet()) {
            FilePosition pos = entry.getValue();
            serializedIndex.put(entry.getKey(), String.valueOf(pos.getOffset()));
        }
        TreeMapWriter.persist(filePath, serializedIndex);
    }

    public void createSparseIndex(TreeMap<String, String> data) {
        long offset = 0;
        for (var entry : data.entrySet()) {
            addEntry(entry.getKey(), offset);
            offset += entry.getValue().length(); // Assuming value length as block size
        }
    }

    public static SparseIndex loadFromFile(String filePath) {
        TreeMap<String, String> serializedIndex = TreeMapWriter.loadFile(filePath);
        SparseIndex sparseIndex = new SparseIndex();
        
        for (var entry : serializedIndex.entrySet()) {
            String[] parts = entry.getValue().split(",");
            long offset = Long.parseLong(parts[0]);
            sparseIndex.index.put(entry.getKey(), new FilePosition(offset));
        }

        return sparseIndex;
    }
}

package com.lsm;

import java.io.*;
import java.util.BitSet;
import java.util.TreeMap;

public class BloomFilter implements Serializable {
    private BitSet bitSet;
    private int numHashFunctions;
    private int size;
    private static final double FALSE_POSITIVE_PROBABILITY = 0.01; // 1%

    public BloomFilter(int expectedElements) {
        // Calculate optimal size and number of hash functions for given false positive probability
        this.size = calculateOptimalSize(expectedElements, FALSE_POSITIVE_PROBABILITY);
        this.numHashFunctions = calculateOptimalHashFunctions(expectedElements, size);
        this.bitSet = new BitSet(size);
    }

    // Create and populate Bloom filter from TreeMap
    public static BloomFilter createFromTreeMap(TreeMap<String, String> data) {
        BloomFilter filter = new BloomFilter(data.size());
        for (String key : data.keySet()) {
            filter.add(key);
        }
        return filter;
    }

    private void add(String key) {
        for (int i = 0; i < numHashFunctions; i++) {
            bitSet.set(getHash(key, i));
        }
    }

    public boolean mightContain(String key) {
        for (int i = 0; i < numHashFunctions; i++) {
            if (!bitSet.get(getHash(key, i))) {
                return false;
            }
        }
        return true;
    }

    private int getHash(String key, int seed) {
        int h1 = key.hashCode();
        int h2 = h1 >>> 16; // Use upper 16 bits for variation
        return Math.abs((h1 * seed + h2) % size);
    }

    public void saveToFile(String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filePath))) {
            oos.writeObject(this);
        }
    }

    public static BloomFilter loadFromFile(String filePath) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath))) {
            return (BloomFilter) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Failed to load Bloom filter", e);
        }
    }

    // Calculate optimal size of bit array
    private static int calculateOptimalSize(int n, double p) {
        return (int) (-n * Math.log(p) / (Math.log(2) * Math.log(2)));
    }

    // Calculate optimal number of hash functions
    private static int calculateOptimalHashFunctions(int n, int m) {
        return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
    }

    // For testing purposes
    protected BitSet getBitSet() {
        return bitSet;
    }

    protected int getSize() {
        return size;
    }

    protected int getNumHashFunctions() {
        return numHashFunctions;
    }
}

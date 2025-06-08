package com.lsm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

public class TestBloomFilter {
    private BloomFilter bloomFilter;
    private TreeMap<String, String> testData;
    private static final String TEST_FILTER_PATH = "data/test_bloom.filter";

    @BeforeEach
    public void setUp() {
        testData = new TreeMap<>();
        testData.put("key1", "value1");
        testData.put("key2", "value2");
        testData.put("key3", "value3");
        bloomFilter = BloomFilter.createFromTreeMap(testData);
    }

    @Test
    public void testBloomFilterCreation() {
        assertNotNull(bloomFilter);
        assertTrue(bloomFilter.getSize() > 0);
        assertTrue(bloomFilter.getNumHashFunctions() > 0);
    }

    @Test
    public void testMightContain() {
        // Test existing keys
        assertTrue(bloomFilter.mightContain("key1"));
        assertTrue(bloomFilter.mightContain("key2"));
        assertTrue(bloomFilter.mightContain("key3"));

        // Test non-existing key
        // Note: might return true due to false positives, but usually should be false
        String nonExistingKey = "nonexistent_key_" + System.currentTimeMillis();
        // We don't assert this as it might be a false positive
        // But we can print it for observation
        System.out.println("False positive test: " + bloomFilter.mightContain(nonExistingKey));
    }

    @Test
    public void testSaveAndLoad() throws IOException {
        // Save the filter
        bloomFilter.saveToFile(TEST_FILTER_PATH);

        // Load it back
        BloomFilter loadedFilter = BloomFilter.loadFromFile(TEST_FILTER_PATH);

        // Test existing keys with loaded filter
        assertTrue(loadedFilter.mightContain("key1"));
        assertTrue(loadedFilter.mightContain("key2"));
        assertTrue(loadedFilter.mightContain("key3"));

        // Cleanup
        new File(TEST_FILTER_PATH).delete();
    }
}

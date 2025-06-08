package com.lsm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.TreeMap;

public class TestSparseIndex {
    private SparseIndex sparseIndex;
    private static final String TEST_INDEX_PATH = "data/test_sparse.index";

    @BeforeEach
    public void setUp() {
        sparseIndex = new SparseIndex();
        // Ensure data directory exists
        new File("data").mkdirs();
    }

    @AfterEach
    public void tearDown() {
        // Clean up test files
        new File(TEST_INDEX_PATH).delete();
    }

    @Test
    public void testCreateNewSparseIndex() {
        assertNotNull(sparseIndex);
        assertTrue(sparseIndex.getIndex().isEmpty());
    }

    @Test
    public void testAddEntry() {
        // Add 1001 entries, only the 1000th entry should be indexed
        long offset = 0;
        for (int i = 0; i < 10; i++) {
            String key = String.format("key%d", i);
            sparseIndex.addEntry(key, offset);
            offset += 10;
        }

        TreeMap<String, SparseIndex.FilePosition> index = sparseIndex.getIndex();
        assertEquals(5, index.size()); // Only one entry should be indexed (1000th entry)
        assertTrue(index.containsKey("key9")); // 10th entry (0-based index)
    }

    @Test
    public void testGetClosestEntry() {
        // Add entries with offsets
        for (int i = 0; i < 30; i++) {
            sparseIndex.addEntry("key" + i, i * 10);
        }

        // Test exact match
        SparseIndex.FilePosition pos1 = sparseIndex.getClosestEntry("key9");
        assertNotNull(pos1);
        assertEquals(90L, pos1.getOffset()); // 9 * 10

        // Test getting closest smaller key
        SparseIndex.FilePosition pos2 = sparseIndex.getClosestEntry("key15");
        assertNotNull(pos2);
        assertEquals(140L, pos2.getOffset()); // Should get key14's position

        // Test key before first index entry
        SparseIndex.FilePosition pos3 = sparseIndex.getClosestEntry("key1");
        assertNull(pos3); // No entry before this key
    }

    @Test
    public void testSaveAndLoadIndex() {
        // Add some entries
        for (int i = 0; i < 2000; i++) {
            sparseIndex.addEntry("key" + i, i * 10);
        }

        // Save the index
        sparseIndex.saveToFile(TEST_INDEX_PATH);

        // Load it back
        SparseIndex loadedIndex = SparseIndex.loadFromFile(TEST_INDEX_PATH);

        // Verify the loaded index
        TreeMap<String, SparseIndex.FilePosition> originalIndex = sparseIndex.getIndex();
        TreeMap<String, SparseIndex.FilePosition> loadedIndexMap = loadedIndex.getIndex();

        assertEquals(originalIndex.size(), loadedIndexMap.size());

        for (String key : originalIndex.keySet()) {
            SparseIndex.FilePosition originalPos = originalIndex.get(key);
            SparseIndex.FilePosition loadedPos = loadedIndexMap.get(key);

            assertNotNull(loadedPos);
            assertEquals(originalPos.getOffset(), loadedPos.getOffset());
        }
    }

    @Test
    public void testIndexingFrequency() {
        // Add 30 entries
        for (int i = 0; i < 30; i++) {
            sparseIndex.addEntry("key" + i, i * 10L);
        }

        TreeMap<String, SparseIndex.FilePosition> index = sparseIndex.getIndex();
        assertEquals(15, index.size()); // Should have entries for 999, 1999, 2999
        assertTrue(index.containsKey("key9"));
        assertTrue(index.containsKey("key19"));
        assertTrue(index.containsKey("key29"));
    }
}

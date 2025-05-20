package com.lsm;

import java.util.TreeMap;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTreeMapWriter {

    @Test
    public void testPersistAndLoad() {
        TreeMap<String, String> treeMap = new TreeMap<>();
        treeMap.put("1", "1");
        treeMap.put("2", "2");
        TreeMapWriter.persist("./data/test.txt", treeMap);

        TreeMap<String, String> loadedMap = TreeMapWriter.loadFile("./data/test.txt");
        assertEquals(2, loadedMap.size());
        assertEquals("1", loadedMap.get("1"));
        assertEquals("2", loadedMap.get("2"));
    }
}    

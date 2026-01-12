package com.lsm;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestLSMTree {
    LSMTree lsmTree;

    @BeforeEach
    public void setUp() {
        lsmTree = new LSMTree();
    }

    @AfterEach
    public void tearDown() throws Exception {
        FileManager.deleteSSTableFiles();
        FileManager.resetSSTableId();
        if (lsmTree != null) {
            lsmTree.close();
        }
        lsmTree = null;
    }

    @Test
    public void testPut() {
        lsmTree.setMaxMemstoreSize(5);
        lsmTree.put("key1", "value1");
        lsmTree.put("key2", "value2");
        lsmTree.put("key3", "value3");
        lsmTree.put("key4", "value4");
        lsmTree.put("key5", "value5");
        assertTrue(lsmTree.get("key1").equals("value1"));
        assertTrue(lsmTree.get("key2").equals("value2"));

        lsmTree.put("key6", "value6");
        lsmTree.put("key7", "value7");

        assertTrue(lsmTree.get("key3").equals("value3"));
        assertTrue(lsmTree.get("key4").equals("value4"));
        assertTrue(lsmTree.get("key6").equals("value6"));
    }

    @Test
    public void testGetNonExistentKey() {
        assertTrue(lsmTree.get("non_existent_key") == null);
    }

    @Test
    public void testMergeFiles() throws Exception{
        lsmTree.setMaxMemstoreSize(5);
        lsmTree.put("key1", "value1");
        lsmTree.put("key2", "value2");
        lsmTree.put("key3", "value3");
        lsmTree.flushMemstore();
        lsmTree.put("key4", "value4");
        lsmTree.put("key5", "value5");
        lsmTree.flushMemstore();
        lsmTree.put("key6", "value6");
        assertTrue(lsmTree.get("key1").equals("value1"));
        assertTrue(lsmTree.get("key5").equals("value5"));
        lsmTree.compact();
        lsmTree.put("key7", "value7");
        assertTrue(lsmTree.get("key2").equals("value2"));
        lsmTree.flushMemstore();
        assertTrue(lsmTree.get("key7").equals("value7"));
        lsmTree.compact();
        assertTrue(lsmTree.get("key3").equals("value3"));

    }
}

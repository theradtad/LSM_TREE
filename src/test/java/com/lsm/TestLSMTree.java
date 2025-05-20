package com.lsm;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestLSMTree {
    LSMTree lsmTree;

    @BeforeEach
    public void setUp() {
        lsmTree = new LSMTree();
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
}

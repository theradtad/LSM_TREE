package com.lsm;

public class Main {
    public static void main(String[] args) {
        System.out.println("LSM Tree Implementation with WAL");

        // Create LSM Tree
        LSMTree lsmTree = new LSMTree();
        lsmTree.setMaxMemstoreSize(5);

        // Add some data
        System.out.println("Adding data...");
        lsmTree.put("key1", "value1");
        lsmTree.put("key2", "value2");
        lsmTree.put("key3", "value3");
        lsmTree.put("key4", "value4");
        lsmTree.put("key5", "value5"); // This should trigger flush

        // Add more data
        lsmTree.put("key6", "value6");
        lsmTree.put("key7", "value7");

        // Retrieve data
        System.out.println("Retrieving data...");
        System.out.println("key1: " + lsmTree.get("key1"));
        System.out.println("key6: " + lsmTree.get("key6"));
        System.out.println("key7: " + lsmTree.get("key7"));

        // Close properly
        try {
            lsmTree.close();
        } catch (Exception e) {
            System.err.println("Error closing LSM Tree: " + e.getMessage());
        }

        System.out.println("Demo completed. WAL ensures durability even after crashes.");
    }
}

package com.lsm;

import java.util.TreeMap;

public class LSMTree {

    private Memstore memstore;
    private int MAX_MEMSTORE_SIZE = 30;
    private static final String MEMSTORE_FILE_PATH = "./data/memstore.txt";

    public LSMTree() {
        this.memstore = new Memstore();
    }

    public void setMaxMemstoreSize(int maxMemstoreSize) {
        this.MAX_MEMSTORE_SIZE = maxMemstoreSize;
    }
    
    public void put(String key, String value) {
        if (getMemstoreSize() >= MAX_MEMSTORE_SIZE) {
            flushMemstore();
        }
        memstore.put(key, value);
    }

    public String get(String key) {
        if(memstore.get(key) != null) {
            return memstore.get(key);
        }
        return TreeMapWriter.loadFile(MEMSTORE_FILE_PATH).get(key);
    }
    
    public int getMemstoreSize() {
        return memstore.getMemstoreSize();
    }

    public TreeMap<String, String> getMemstoreTreeMap() {
        return memstore.getTreeMap();
    }

    public void clearMemstore() {
        memstore.clearMemstore();
    }

    public void flushMemstore() {
        TreeMapWriter.persist(MEMSTORE_FILE_PATH, getMemstoreTreeMap());
        clearMemstore();
    }
}

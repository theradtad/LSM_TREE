package com.lsm;

import java.util.TreeMap;

public class Memstore {

    private TreeMap<String, String> treeMap;

    public Memstore() {
        this.treeMap = new TreeMap<>();
    }

    public void put(String key, String value) {
        treeMap.put(key, value);
    }

    public String get(String key) {
        return treeMap.get(key);
    }

    public int getMemstoreSize() {
        return treeMap.size();
    }

    public TreeMap<String, String> getTreeMap() {
        return treeMap;
    }

    public void clearMemstore() {
        treeMap.clear();
    }
}
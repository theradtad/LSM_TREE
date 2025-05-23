package com.lsm;

import java.util.List;
import java.util.TreeMap;

public class LSMTree {

    private Memstore memstore;
    private int MAX_MEMSTORE_SIZE = 30;;

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
        List<String> fileList = FileManager.getSSTableFileList();
        for (String filePath : fileList) {
            TreeMap<String, String> data = TreeMapWriter.loadFile(filePath);
            if (data != null && data.containsKey(key)) {
                return data.get(key);
            }
        }

        return null;
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
        try {
            String filePath = FileManager.getSSTableFilePath();
            TreeMapWriter.persist(filePath, memstore.getTreeMap());
            System.out.println("Memstore flushed to file: " + filePath);
        } catch (LimitExceeded e) {
            System.out.println("Error while flushing memstore: " + e.getMessage());
            try {
                compact();
                String filePath = FileManager.getSSTableFilePath();
                TreeMapWriter.persist(filePath, memstore.getTreeMap());
            } catch (LimitExceeded e1) {
                System.out.println("Error while merging files: " + e1.getMessage());
            };
        }
        clearMemstore();
    }

    public void compact() throws LimitExceeded {
        FileMerger fileMerger = new FileMerger();
        fileMerger.mergeFiles(FileManager.getSSTableFileList());
    }
}

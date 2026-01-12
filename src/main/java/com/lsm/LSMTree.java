package com.lsm;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

public class LSMTree {

    private Memstore memstore;
    private WAL wal;
    private int MAX_MEMSTORE_SIZE = 30;;

    public LSMTree() {
        try {
            this.wal = new WAL();
            this.memstore = new Memstore();
            recoverFromWAL();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize WAL", e);
        }
    }

    private void recoverFromWAL() throws IOException {
        List<WALEntry> entries = wal.readAll();
        for (WALEntry entry : entries) {
            if (entry.getOperation() == WALEntry.Operation.PUT) {
                memstore.put(entry.getKey(), entry.getValue());
            }
        }
        System.out.println("Recovered " + entries.size() + " entries from WAL");
    }

    public void setMaxMemstoreSize(int maxMemstoreSize) {
        this.MAX_MEMSTORE_SIZE = maxMemstoreSize;
    }
    
    public void put(String key, String value) {
        try {
            // Log to WAL first for durability
            wal.append(new WALEntry(WALEntry.Operation.PUT, key, value));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to WAL", e);
        }

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

            // Clear WAL after successful flush
            wal.clear();
        } catch (LimitExceeded e) {
            System.out.println("Error while flushing memstore: " + e.getMessage());
            try {
                compact();
                String filePath = FileManager.getSSTableFilePath();
                TreeMapWriter.persist(filePath, memstore.getTreeMap());

                // Clear WAL after successful flush (including after compaction)
                wal.clear();
            } catch (LimitExceeded e1) {
                System.out.println("Error while merging files: " + e1.getMessage());
            } catch (IOException e1) {
                System.out.println("Error while clearing WAL: " + e1.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Error while clearing WAL: " + e.getMessage());
        }
        clearMemstore();
    }

    public void compact() throws LimitExceeded {
        FileMerger fileMerger = new FileMerger();
        fileMerger.mergeFiles(FileManager.getSSTableFileList());
    }

    public void close() throws IOException {
        if (wal != null) {
            wal.close();
        }
    }
}

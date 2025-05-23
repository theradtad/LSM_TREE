package com.lsm;

import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;

public class FileMerger {
    private ConcurrentSkipListMap<String, String> mergedData;

    public FileMerger() {
        this.mergedData = new ConcurrentSkipListMap<>();
    }

    public void mergeFiles(List<String> fileList) throws LimitExceeded {
        if (fileList == null || fileList.isEmpty()) {
            System.out.println("No files to merge.");
            return;
        }
        if (fileList.size() < 2) {
            System.out.println("Not enough files to merge.");
            return;
        }
        
        ExecutorService executorService = java.util.concurrent.Executors.newFixedThreadPool(fileList.size());
        for (String filePath : fileList) {
            executorService.execute(new FileReader(filePath));
        }

        executorService.shutdown();
        while (!executorService.isTerminated()) {
            // Wait for all threads to finish
        }

        FileManager.deleteSSTableFiles();
        FileManager.resetSSTableId();
        String mergedFilePath = FileManager.getSSTableFilePath();
        TreeMapWriter.persist(mergedFilePath, new TreeMap<>(mergedData));
        System.out.println("Merged file created at: " + mergedFilePath);
        System.out.println("Files merged successfully ");
    }

    public class FileReader implements Runnable {
        private String filePath;

        public FileReader(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public void run() {
            TreeMap<String, String> data = TreeMapWriter.loadFile(filePath);
            if (data == null) {
                System.out.println("Failed to read file: " + filePath);
                return;
            }
            for (String key: data.keySet()) {
                mergedData.put(key, data.get(key));
            }
            System.out.println("File read successfully: " + filePath);
        }
    }

}

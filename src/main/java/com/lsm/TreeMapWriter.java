package com.lsm;

import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class TreeMapWriter {

    public static final String DATA_DIR = "./data";

    public static boolean doesExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }
    
    public static void createDataDir() {
        File file = new File(DATA_DIR);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public static void persist(String filePath, TreeMap<String, String> treeMap) {
        BufferedWriter writer = null;
        try {
            createDataDir();
            writer = new BufferedWriter(new FileWriter(filePath));
            for (Map.Entry<String, String> entry : treeMap.entrySet()) {
                writer.write(entry.getKey() + " " + entry.getValue() +"\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static TreeMap<String, String> loadFile(String filePath) {
        if (!doesExist(filePath)) {
            throw new IllegalArgumentException("File does not exist");
        }
        
        TreeMap<String, String> treeMap = new TreeMap<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                treeMap.put(parts[0], parts[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return treeMap;
    }
}
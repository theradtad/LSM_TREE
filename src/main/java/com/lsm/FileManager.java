package com.lsm;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class FileManager {
    public static final String DATA_DIR = "./data";
    public static int  ssTableFileId = 0;

    public static String getSSTableFilePath() throws LimitExceeded {
        ++ssTableFileId;
        if (ssTableFileId > 100) {
            throw new LimitExceeded("SSTable file ID exceeded limit");
        }
        return DATA_DIR + "/sstable_" + ssTableFileId + ".txt";
    }

    public static void resetSSTableId() {
        ssTableFileId = 0;
    }

    public static List<String> getSSTableFileList() {
        List<String> fileList = new ArrayList<>();
        File dir = new File(DATA_DIR);
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.getName().startsWith("sstable_")) {
                    fileList.add(file.getAbsolutePath());
                }
            }
        }
        return fileList;
    }

    public static void deleteSSTableFiles() {
        File dir = new File(DATA_DIR);
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.getName().startsWith("sstable_")) {
                    file.delete();
                }
            }
        }
    }   
}

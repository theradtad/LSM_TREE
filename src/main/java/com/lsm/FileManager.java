package com.lsm;

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
}

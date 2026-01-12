package com.lsm;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WAL {
    private static final String WAL_DIR = "./data";
    private static final String WAL_FILE = "wal.log";
    private static final String WAL_PATH = WAL_DIR + "/" + WAL_FILE;

    private FileOutputStream fos;
    private ObjectOutputStream oos;
    private boolean isOpen = false;

    public WAL() throws IOException {
        ensureWALDirectory();
        openWAL();
    }

    private void ensureWALDirectory() {
        Path walDir = Paths.get(WAL_DIR);
        if (!Files.exists(walDir)) {
            try {
                Files.createDirectories(walDir);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create WAL directory", e);
            }
        }
    }

    private void openWAL() throws IOException {
        if (isOpen) {
            return;
        }

        // Open in append mode
        fos = new FileOutputStream(WAL_PATH, true);
        // Only create ObjectOutputStream if file is empty (to avoid stream header issues)
        File walFile = new File(WAL_PATH);
        if (walFile.length() == 0) {
            oos = new ObjectOutputStream(fos);
        } else {
            oos = new AppendingObjectOutputStream(fos);
        }
        isOpen = true;
    }

    public void append(WALEntry entry) throws IOException {
        if (!isOpen) {
            throw new IllegalStateException("WAL is not open");
        }

        oos.writeObject(entry);
        oos.flush();
        // Force to disk for durability
        fos.getFD().sync();
    }

    public List<WALEntry> readAll() throws IOException {
        List<WALEntry> entries = new ArrayList<>();

        if (!Files.exists(Paths.get(WAL_PATH))) {
            return entries;
        }

        File walFile = new File(WAL_PATH);
        if (walFile.length() == 0) {
            return entries; // Empty file
        }

        try (FileInputStream fis = new FileInputStream(WAL_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            while (true) {
                try {
                    WALEntry entry = (WALEntry) ois.readObject();
                    entries.add(entry);
                } catch (EOFException e) {
                    break; // End of file reached
                } catch (ClassNotFoundException e) {
                    throw new IOException("Failed to deserialize WAL entry", e);
                }
            }
        }

        return entries;
    }

    public void clear() throws IOException {
        close();
        Files.deleteIfExists(Paths.get(WAL_PATH));
        openWAL();
    }

    public void close() throws IOException {
        if (!isOpen) {
            return;
        }

        if (oos != null) {
            oos.close();
        }
        if (fos != null) {
            fos.close();
        }
        isOpen = false;
    }

    /**
     * Custom ObjectOutputStream that allows appending to existing files
     * without writing stream headers
     */
    private static class AppendingObjectOutputStream extends ObjectOutputStream {
        public AppendingObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }

        @Override
        protected void writeStreamHeader() throws IOException {
            // Do nothing - don't write stream header when appending
            reset();
        }
    }
}

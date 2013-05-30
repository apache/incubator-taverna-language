package org.purl.wf4ever.robundle.fs;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MemoryEfficiencyIT {

    private Runtime rt = Runtime.getRuntime();
    
    // delays to allow garbage collection to run
    private static final long DC_DELAY = 300;
    private static final int kiB = 1024;

    private static final int MiB = 1024*1024;
    private static final int GiB = 1024*1024*1024;
    private BundleFileSystem fs;

    @Before
    public void newFS() throws Exception {
        fs = BundleFileSystemProvider.newFileSystemFromTemporary();
        System.out.println(fs.getSource());
    }

    @After
    public void closeFS() throws IOException {
        fs.close();
//        Files.deleteIfExists(fs.getSource());
    }

    Random rand = new Random();

    int MAX_WORKERS = 100;

    
    @Test
    public void writeManyFiles() throws Exception {

        long usedBefore = usedMemory();

        for (int i=0 ; i<100 ; i++) {
            Path dir = fs.getPath("dir" + i);
            Files.createDirectory(dir);
        }

        final byte[] pattern = new byte[kiB];
        rand.nextBytes(pattern);

        ExecutorService pool = Executors.newFixedThreadPool(MAX_WORKERS);
        try {
            int numFiles = 10000;
            System.out.println("Writing " + numFiles + " files in parallell over max " + MAX_WORKERS + " threads");
            for (int i=0; i< numFiles; i++) {
                int folderNo = i % 100;
                final Path file = fs.getPath("dir" + folderNo, "file" + i);
                pool.submit(new Runnable() {
                    public void run() {
                        try (OutputStream newOutputStream = Files.newOutputStream(file)) {
                            newOutputStream.write(pattern);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    };
                });
            }
            System.out.println();
            pool.shutdown();
            assertTrue("Timed out waiting for threads", pool.awaitTermination(60, TimeUnit.SECONDS));
        } finally {
            pool.shutdownNow();
        }
        long usedAfterCloseFile = usedMemory();
        assertTrue(usedAfterCloseFile - usedBefore < 10 * MiB);

        fs.close();
        long zipSize = Files.size(fs.getSource());
        System.out.println("ZIP: " + zipSize / MiB + " MiB");
        long usedAfterCloseFS = usedMemory();
        assertTrue(usedAfterCloseFS - usedBefore < 10*MiB);
        assertTrue(usedAfterCloseFS < zipSize);
    }
    
    
    @Test
    public void writeBigFile() throws Exception {

        long usedBefore = usedMemory();
        long size = sufficientlyBig();
        long limit = size/2;
        
        Path file = fs.getPath("bigfile");

        // Big enough random bytes to blow ZIP's compression buffer
        byte[] pattern = new byte[MiB];
        rand.nextBytes(pattern);
        
        long written = 0;
        try (OutputStream newOutputStream = Files.newOutputStream(file)) {
            while (written < size) {
                newOutputStream.write(pattern);
                written += pattern.length;
            }
            pattern = null;
            rand = null;
            long usedAfterWrite = usedMemory();
            assertTrue(usedAfterWrite - usedBefore < limit);
        }
        long fileSize = Files.size(file);
        assertTrue(fileSize >= size) ;
//        System.out.println("Written " + fileSize/MiB + ", needed " + size/MiB);
        long usedAfterCloseFile = usedMemory();
        assertTrue(usedAfterCloseFile - usedBefore < limit);

        fs.close();
        long zipSize = Files.size(fs.getSource());
        System.out.println("ZIP: " + zipSize / MiB + " MiB");
        assertTrue(zipSize > limit);
        
        long usedAfterCloseFS = usedMemory();
        assertTrue(usedAfterCloseFS - usedBefore < 10*MiB);
    }

    private long sufficientlyBig() throws IOException {
        long usableSpace = fs.getFileStore().getUsableSpace();
        long need = 64 * MiB;
        if (need*2 > usableSpace) {
            String msg = "Not enough disk space (%s MiB < %s)";
            long freeSpace = usableSpace / MiB;
            long needSpace = need*2 / MiB;            
            throw new IllegalStateException(String.format(msg, freeSpace, needSpace)); 
        }
        return need;
    }

    @Test
    public void testUsedMemory() throws Exception {
        long before = usedMemory();
        byte[] waste = new byte[50 * MiB];
        waste[0] = 13;
        waste[waste.length-1] = 37;
        long after = usedMemory();

        assertTrue((after - before) > 10 * MiB);
        waste = null;
        long now = usedMemory();
        // and it should have been freed again
        assertTrue((after-now) > 10 * MiB);
        
    }

    public long usedMemory() throws InterruptedException {
        runGC();
        long used = rt.totalMemory() - rt.freeMemory();
        System.out.println("Used memory: " + used/MiB + " MiB");
        return used;
    }

    public void runGC() {
        System.gc();
        try {
            Thread.sleep(DC_DELAY);
            System.gc();
            Thread.sleep(300);
        } catch (InterruptedException e) {
        }
    }
}

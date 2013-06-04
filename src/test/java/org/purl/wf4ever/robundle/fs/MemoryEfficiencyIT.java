package org.purl.wf4ever.robundle.fs;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.purl.wf4ever.robundle.Bundles;
import org.purl.wf4ever.robundle.utils.RecursiveCopyFileVisitor.RecursiveCopyOption;

public class MemoryEfficiencyIT extends Helper {

    private Runtime rt = Runtime.getRuntime();
    
    // delays to allow garbage collection to run
    private static final long GC_DELAY = 300;
    private static final int kiB = 1024;

    private static final int MiB = 1024*1024;
    private static final long GiB = 1024l*1024l*1024l;
    private BundleFileSystem fs;

    Random rand = new Random();

    int MAX_WORKERS = 100;
    
    
    @Test
    public void writeManyFiles() throws Exception {

        long usedBefore = usedMemory();

        Path folder = fs.getPath("folder");
        
        for (int i=0 ; i<100 ; i++) {
            Path dir = folder.resolve("dir" + i);
            Files.createDirectories(dir);
        }

        final byte[] pattern = new byte[8*kiB];
        rand.nextBytes(pattern);

        ExecutorService pool = Executors.newFixedThreadPool(MAX_WORKERS);
        try {
            int numFiles = 10000;
            System.out.println("Writing " + numFiles + " files in parallell over max " + MAX_WORKERS + " threads");
            for (int i=0; i< numFiles; i++) {
                int folderNo = i % 100;
                Path dir = folder.resolve("dir" + folderNo);
                final Path file = dir.resolve("file" + i);
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
            pool.shutdown();
            assertTrue("Timed out waiting for threads", pool.awaitTermination(60, TimeUnit.SECONDS));
            System.out.println("Done");
        } finally {
            pool.shutdownNow();
        }
        long usedAfterCloseFile = usedMemory();
        assertTrue(usedAfterCloseFile - usedBefore < 10 * MiB);

        Date startedRecurse = new Date();
        System.out.println("Recursively copying folder");
        Bundles.copyRecursively(folder, fs.getPath("copy"),
                RecursiveCopyOption.IGNORE_ERRORS);
        long duration = new Date().getTime() - startedRecurse.getTime();
        System.out.println("Done in " + duration/1000 + "s");
        
        long usedAfterRecursive = usedMemory();
        assertTrue(usedAfterRecursive - usedBefore < 20 * MiB);
        
        fs.close();
        long zipSize = Files.size(fs.getSource());
        System.out.println("ZIP: " + zipSize / MiB + " MiB");
        long usedAfterCloseFS = usedMemory();
        assertTrue(usedAfterCloseFS - usedBefore < 20*MiB);
        assertTrue(usedAfterCloseFS < zipSize);
      
        
    }
    
    @Test
    public void writeGigaFile() throws Exception {

        long usedBefore = usedMemory();
        
        Path file = fs.getPath("bigfile");
        long size = 5l * GiB;
        if (fs.getFileStore().getUsableSpace() < size) {
            throw new IllegalStateException("This test requires at least " + size/GiB + "GiB free disk space");
        }
        System.out.println("Writing " + size/GiB + "GiB to bundle");
        
        // We'll use FileChannel as it allows calling .position. This should
        // be very fast on UNIX which allows zero-padding, but on Windows
        // this will still take a while as it writes 5 GiB of \00s to disk. 
        
        // Another downside is that the ZipFileProvider compresses the file
        // once the file channel is closed, requiring ~5 GB disk space
        try (FileChannel bc = FileChannel.open(file, 
                StandardOpenOption.WRITE, 
                StandardOpenOption.SPARSE,
                StandardOpenOption.CREATE_NEW)) {
            bc.position(size);
            ByteBuffer src = ByteBuffer.allocateDirect(1024);
            bc.write(src);
        }

        long fileSize = Files.size(file);
        assertTrue(fileSize > size) ;
        System.out.println("Written " + fileSize/MiB);
        long usedAfterCloseFile = usedMemory();
        assertTrue(usedAfterCloseFile - usedBefore < 10*MiB);

        fs.close();
        long zipSize = Files.size(fs.getSource());
        System.out.println("ZIP: " + zipSize / MiB + " MiB");
        // Zeros should compress fairly well
        assertTrue(zipSize < 10 * MiB);
        
        long usedAfterCloseFS = usedMemory();
        assertTrue(usedAfterCloseFS - usedBefore < 10*MiB);
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
            Thread.sleep(GC_DELAY);
            System.gc();
            Thread.sleep(GC_DELAY);
        } catch (InterruptedException e) {
        }
    }
}

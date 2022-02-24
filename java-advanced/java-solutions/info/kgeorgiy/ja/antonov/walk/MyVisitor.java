package info.kgeorgiy.ja.antonov.walk;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class MyVisitor implements FileVisitor<Path> {

    private final BufferedWriter writer;

    public MyVisitor(BufferedWriter writer){
        this.writer = writer;
    }


    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        // :NOTE: copy paste
        /*
        long hash = 0L;
        try (InputStream reader = Files.newInputStream(file)) {
            byte[] bytes = new byte[1024];
            int len = reader.read(bytes, 0, bytes.length);
            while (len != -1) {
                hash = ElfHash(bytes, len, hash);
                len = reader.read(bytes);
            }
        } catch (IOException e) {
            hash = 0L;
            //throw new IOException("Troubles with reading file: " + filePath + ". For Hash." + e.getMessage());
        }
        try {
            writer.write(String.format("%016x %s", hash, file.toString()));
            writer.newLine();
        } catch (IOException e){
            throw new IOException("Troubles with writing to outputFile with file: " + file + ". For Hash." + e.getMessage());
        }*/
        RecursiveWalk.writeHashOfFile(file, writer);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        long hash = 0L;
        try {
            writer.write(String.format("%016x %s", hash, file.toString()));
            writer.newLine();
        } catch (IOException e){
            throw new IOException("Troubles with writing to outputFile with file: " + file + ". For Hash." + e.getMessage());
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        return FileVisitResult.CONTINUE;
    }

}

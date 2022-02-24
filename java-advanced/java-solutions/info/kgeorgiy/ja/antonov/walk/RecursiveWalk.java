package info.kgeorgiy.ja.antonov.walk;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class RecursiveWalk{

    public static void main(String[] args) {

        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.err.println("Incorrect input. Try <входной файл> <выходной файл>");
            return;
        }

        Path inputFilePath, outputFilePath;

        try {
            inputFilePath = getInputFilePath(args[0]);
        } catch (IOException e) {
            System.err.println("Can't open input file. " + e.getMessage());
            return;
        }


        try {
            outputFilePath = getOrCreateOutputFilePath(args[1]);
        } catch (IOException e) {
            System.err.println("Can't open output file. " + e.getMessage());
            return;
        }

        if(outputFilePath == null || inputFilePath == null){
            System.err.println("Invalid path fot argument files.");
            return;
        }

        try (BufferedReader inputReader = Files.newBufferedReader(inputFilePath, StandardCharsets.UTF_8)) {
            try (BufferedWriter outputWriter = Files.newBufferedWriter(outputFilePath, StandardCharsets.UTF_8)) {
                try {
                    String line = inputReader.readLine();
                    while (line != null) {
                        startSearch(line, outputWriter);
                        line = inputReader.readLine();
                    }
                } catch (IOException e) {
                    System.err.println("Exception while walking. " + e.getMessage());
                }
            } catch (IOException e) {
                System.err.println("Troubles with opening BufferedWriter. " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Troubles with opening BufferedReader. " + e.getMessage());
        }
    }

    //pre: fileName : String
    //ex: no file
    //    dir
    //post: filePath : Path
    private static Path getFilePath(String fileName) throws IOException {

        Path path;

        try {
            path = Path.of(fileName);
        } catch (InvalidPathException e){
            return null;
        }

        if (!Files.exists(path)) {
            throw new NoSuchFileException("No Such File: " + fileName);
        }
        if (Files.isDirectory(path)) {
            throw new NoSuchFileException(fileName + "is not a file. It's a directory");
        }

        return path;
    }

    //pre: fileName : String
    //ex: no file
    //    dir
    //    can't read
    //post: filePath : Path
    private static Path getInputFilePath(String fileName) throws IOException {
        Path path = getFilePath(fileName);
        if(path == null){
            return null;
        }
        if (!Files.isReadable(path)) {
            throw new IOException("Can't read from file: " + fileName);
        }
        return path;
    }

    //pre: fileName : String
    //ex: can't write
    //post: filePath : Path and it's exists
    private static Path getOrCreateOutputFilePath(String fileName) throws IOException {
        Path path;
        try {
            path = Path.of(fileName);
        } catch (InvalidPathException e){
            return null;
        }
        if(Files.notExists(path)){
            Path parentPath = path.getParent();
            if(parentPath != null && Files.notExists(parentPath)){
                try{
                    Files.createDirectory(parentPath);
                } catch (IOException e){
                    throw new IOException("Can't create directory for file: " + fileName + ". " + e.getMessage());
                }
            }
            try {
                Files.createFile(path);
            } catch (IOException e){
                throw new IOException("Can't create file: " + fileName + ". " + e.getMessage());
            }
        }
        if (!Files.isWritable(path)) {
            throw new IOException("Can't write to file: " + fileName);
        }
        return path;
    }

    protected static void startSearch(String fileName, BufferedWriter writer) throws IOException{
        Path path;
        try {
            path = Path.of(fileName);
        } catch (InvalidPathException e){
            path = null;
        }
        if(path != null && Files.isDirectory(path)){
            Files.walkFileTree(path, new MyVisitor(writer));
        } else if(path != null && Files.exists(path)){
            writeHashOfFile(path, writer);
        } else {
            try {
                writer.write(String.format("%016x %s", 0L, fileName));
                writer.newLine();
            } catch (IOException e){
                throw new IOException("Troubles with writing to outputFile with file: " + fileName + ". For Hash." + e.getMessage());
            }
        }
    }

    public static void writeHashOfFile(Path filePath, BufferedWriter writer) throws IOException {
        long hash = 0L;
        try (InputStream reader = Files.newInputStream(filePath)) {
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
            writer.write(String.format("%016x %s", hash, filePath.toString()));
            writer.newLine();
        } catch (IOException e){
            throw new IOException("Troubles with writing to outputFile with file: " + filePath + ". For Hash." + e.getMessage());
        }
    }

    private static long ElfHash(byte[] s, int len, long hash) {
        long h = hash, high;
        for (int i = 0; i < len; i++) {
            h = (h << 8) + (s[i] & 0xFF);
            high = (h & 0xFF00000000000000L);
            if (high != 0)
                h ^= high >> 48;
            h &= ~high;
        }
        return h;
    }

}

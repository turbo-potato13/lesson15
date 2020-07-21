package com.vtb.kortunov.lesson15;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Main {
    public static void main(String[] args) {
        //Task 1
        Path path = Paths.get("task1", "t.txt");
        try {
            System.out.println(countWord(path, "hello"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Task 2
        Path path1 = Paths.get("task2");
        try {
            mergingFiles(path1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Task 3
        Path path2 = Paths.get("task3");
        deleteDirectories(path2);
    }

    //Task 1
    public static int countWord(Path path, String str) throws IOException {
        char[] chars = str.toCharArray();
        RandomAccessFile file = new RandomAccessFile(String.valueOf(path), "r");
        FileChannel fileChannel = file.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(8192);
        int byteRead = fileChannel.read(byteBuffer);

        int count = 0;
        while (byteRead != -1) {
            byteBuffer.flip();
            for (int i = 0; i < byteBuffer.limit(); i++) {
                int lengthWord = 0;
                int j = i;
                while (lengthWord < chars.length) {
                    if (byteBuffer.get(j) != chars[lengthWord]) {
                        break;
                    }
                    lengthWord++;
                    j++;
                }
                if (lengthWord == chars.length) {
                    count++;
                }
            }
            byteBuffer.clear();
            byteRead = fileChannel.read(byteBuffer);
        }
        file.close();
        return count;
    }

    //Task 2
    public static void mergingFiles(Path path) throws IOException {
        if (!Files.isDirectory(path)) {
            throw new RuntimeException();
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path file : stream) {
                if (file.getFileName().toString().contains("txt")) {
                    Files.copy(file, Paths.get(String.valueOf(path), "merge.txt"), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    //Task 3
    public static void deleteDirectories(Path directory) {
        try {
            Files.walkFileTree(directory, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (!dir.getFileName().equals(directory.getFileName())) {
                        Files.delete(dir);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
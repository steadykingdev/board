package com.study.board.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class LocalFileStorage implements FileStorage{

    private final String fileLocation;

    private final String COMMON_IMG = new File("").getAbsolutePath();

    public LocalFileStorage(@Value("${file.upload.location}") String fileLocation) {
        this.fileLocation = fileLocation;
    }

    @Override
    public String store(MultipartFile file) throws IOException {
        UUID uuid = UUID.randomUUID();
        String fileName = uuid.toString() + "_" + file.getOriginalFilename();
        File savedFile = new File(fileLocation, fileName);

        file.transferTo(savedFile);

        return fileName;
    }

    @Override
    public byte[] getImage(String imgPath) throws Exception {
        Path path = Paths.get(imgPath);

        try {
            byte[] data = Files.readAllBytes(path);
            return data;
        } catch (IOException e) {
            throw e;
        }
    }
}

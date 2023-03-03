package com.study.board.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

@Component
public class LocalFileStorage implements FileStorage{

    private final String fileLocation;

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
        FileInputStream inputStream = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            inputStream = new FileInputStream(imgPath);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("해당 파일을 찾을 수 없습니다.");
        }

        int readCount = 0;
        byte[] buffer = new byte[1024];
        byte[] fileArray = null;

        try {
            while ((readCount = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, readCount);
            }
            fileArray = outputStream.toByteArray();
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            throw new Exception("파일을 변환하는데 문제가 생겼습니다.");
        }
        return fileArray;
    }
}

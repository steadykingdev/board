package com.study.board.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorage {
    String store(MultipartFile file) throws IOException;

    byte[] getImage(String imgPath) throws Exception;
}

package com.kuang.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface UserService {

    boolean batchImport(String fileName, MultipartFile file) throws Exception;

}

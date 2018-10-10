package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by ${aimimi2015} on 2017/6/15.
 */
public interface IFileService {
    String upload(MultipartFile file, String path);
}

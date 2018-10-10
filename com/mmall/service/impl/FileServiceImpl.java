package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by ${aimimi2015} on 2017/6/15.
 */
@Service("iFileService")
@Slf4j
public class FileServiceImpl implements IFileService{



    public String upload(MultipartFile file,String path){

        // path = /user/Iverson/IDEA15/mmall_learning/mmall的副本/target/mmall/upload

        String fileName = file.getOriginalFilename();
        //扩展名
        //abc.jpg


        //获取文件后缀
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(",")+1);
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        log.info("开始上传文件,文件的文件名:{},上传到的路径:{},新文件名:{}",fileName,path,uploadFileName);
        File fileDir = new File(path);
        if (!fileDir.exists()){
            fileDir.setWritable(true);   //设置权限
            fileDir.mkdirs();

        }
        File targetFile = new File(path,uploadFileName);

        try {
            file.transferTo(targetFile);  //这一步是去上传文件
            //文件已经上传成功

            // 将targetFile上传到我们的ftp服务器
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            // 上传完后,删除upload下面的文件
            targetFile.delete();

        } catch (IOException e) {
            log.error("上传文件异常",e);
            return null;
        }

        return targetFile.getName();
        //targetFile.getName() = 0df1a820-3bcd-463b-b548-b01924ae81c4.test.html

    }
}


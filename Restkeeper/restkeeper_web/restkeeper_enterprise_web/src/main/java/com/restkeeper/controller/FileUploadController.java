package com.restkeeper.controller;

import com.aliyun.oss.OSSClient;
import com.restkeeper.utils.Result;
import io.swagger.annotations.Api;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@Api(tags = {"图片上传通用接口"})
@RefreshScope
public class FileUploadController {

  @Autowired
  private OSSClient ossClient;

  @Value("${bucketName}")
  private String bucketName;

  @Value("${spring.cloud.alicloud,oss.endpoint}")
  private String endpoint;


  public Result fileUpload(@RequestParam("file")MultipartFile multipartFile) {
    Result result = new Result();
    String fileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();

    try {
      ossClient.putObject(bucketName, fileName, multipartFile.getInputStream());
      String logoPath = "https://" + bucketName + "." + endpoint + "/" + fileName;
    } catch (IOException e) {
      e.printStackTrace();
      log.error(e.getMessage());
      result.error("文件上传失败");
    }
    return  result;
  }

}

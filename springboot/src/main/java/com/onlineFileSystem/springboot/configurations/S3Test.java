package com.onlineFileSystem.springboot.configurations;

import java.io.File;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3Test {
    public static void main(String[] args) {
        String bucketName = "coen241projectfiles";
        String fileName = "test.txt";
        String path = "/Users/Haoyuan/Desktop/COEN 241/AWSOnlineFileStorageSystem/springboot/src/main/java/com/onlineFileSystem/springboot/testfiles/" + fileName;
        AWSConfig conf = new AWSConfig();
        AmazonS3 s3Client = conf.amazonS3();
        PutObjectRequest request = new PutObjectRequest(bucketName, fileName, new File(path));
        s3Client.putObject(request);
    }
}

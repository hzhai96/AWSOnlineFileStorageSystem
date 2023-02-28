package com.onlineFileSystem.springboot.controller;

import java.util.List;

import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.onlineFileSystem.springboot.services.S3Service;

import software.amazon.awssdk.services.s3.model.S3Object;

@RestController
public class S3Controller {
    
    @Autowired
    S3Service s3Service;

    @GetMapping("/files")
    public String listFiles(@RequestParam String path) {
        String jsonString = "";
        JSONArray ja = new JSONArray();
        List<S3Object> objects = s3Service.listObjects(path);

        ja.put("key");
        ja.put("size");

        for (S3Object object : objects) {
            jsonString += object.key() + ", " + String.valueOf(object.size()) + " \n";
        }

        return CDL.toJSONArray(ja, jsonString).toString();
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam MultipartFile file, @RequestParam String key) {
        JSONObject jo = new JSONObject();
        String contentType = file.getContentType();

        try {
            s3Service.uploadOject(key, contentType, file.getInputStream());
            jo.put("status", "success");
            jo.put("message", "The file has been successfully uploaded.");
        } catch (Exception e) {
            jo.put("status", "error");
            jo.put("message", "Error when uploading the file: " + e.getMessage());
        }

        return jo.toString();
    }

    @PostMapping("/createFolder")
    public String uploadFile(@RequestParam String key) {
        JSONObject jo = new JSONObject();

        try {
            s3Service.createFoler(key);
            jo.put("status", "success");
            jo.put("message", "The folder has been successfully created.");
        } catch (Exception e) {
            jo.put("status", "error");
            jo.put("message", "Error when creating the folder: " + e.getMessage());
        }

        return jo.toString();
    }
}

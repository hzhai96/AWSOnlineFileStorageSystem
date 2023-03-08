package com.onlineFileSystem.springboot.controller;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.onlineFileSystem.springboot.common.AuthenticationUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.onlineFileSystem.springboot.services.S3Service;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.s3.model.S3Object;

@RestController
@RequestMapping("/users")
@Slf4j
@CrossOrigin("http://localhost:3000")
public class S3Controller {
    
    @Autowired
    S3Service s3Service;

    @GetMapping("/{userName}/files")
    public String listFiles(@PathVariable String userName, @RequestBody Map<String, String> body) {
        AuthenticationUtil.authorizeUser(userName);
        String jsonString = "";
        JSONObject jo = new JSONObject();
        JSONArray ja = new JSONArray();
        String path = body.get("path");

        ja.put("key");
        ja.put("name");
        ja.put("size");
        ja.put("type");

        try {
            List<S3Object> objects = s3Service.listObjects(path);
            for (S3Object object : objects) {
                String type, name;
                String[] dir = object.key().split("/");

                if (dir[dir.length - 1] == "") {
                    name = dir[dir.length - 2];
                    type = "foler";
                }
                else {
                    name = dir[dir.length - 1];
                    type = "file";
                }

                jsonString += object.key() + "," + name + "," + String.valueOf(object.size()) + "," + type + "\n";
            }
            jo.put("status", "success");
            jo.put("content", new JSONObject("result", CDL.toJSONArray(ja, jsonString).toString()).toString());
        } catch (Exception e) {
            jo.put("status", "error");
            jo.put("message", "Error when listing files: " + e.getMessage());
        }

        return jo.toString();
    }

    @GetMapping("/{userName}/shared")
    public String listSharedFiles(@PathVariable String userName, @RequestBody Map<String, String> body) {
        AuthenticationUtil.authorizeUser(userName);
        String jsonString = "";
        JSONObject jo = new JSONObject();
        JSONArray ja = new JSONArray();
        String key = body.get("key");

        ja.put("key");
        ja.put("size");

        try {
            List<String> objects = s3Service.listObjectsFromFile(key);
            for (String object : objects) {
                String[] information = object.split(",");
                jsonString += information[0] + "," + information[1] + " \n";
            }
            jo.put("status", "success");
            jo.put("content", new JSONObject("result", CDL.toJSONArray(ja, jsonString).toString()).toString());
        } catch (Exception e) {
            jo.put("status", "error");
            jo.put("message", "Error when listing files: " + e.getMessage());
        }

        return jo.toString();
    }

    @PostMapping("/{userName}/uploadFile")
    public String uploadFile(@RequestParam(name = "file") MultipartFile file, @PathVariable String userName, @RequestBody Map<String, String> body) {
        log.info("Uploading file");
        AuthenticationUtil.authorizeUser(userName);
        String key = body.get("key");
        JSONObject jo = new JSONObject();
        String type = file.getContentType();

        try {
            s3Service.uploadObject(key, type, file.getInputStream());
            jo.put("status", "success");
            jo.put("message", "The file has been successfully uploaded.");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return jo.toString();
    }

    @PostMapping("/{userName}/createFile")
    public String newFile(@PathVariable String userName, @RequestBody Map<String, String> body) {
        AuthenticationUtil.authorizeUser(userName);
        JSONObject jo = new JSONObject();
        String key = body.get("key");
        String type = body.get("type");

        try {
            s3Service.uploadObject(key, type, InputStream.nullInputStream());
            jo.put("status", "success");
            jo.put("message", "The file has been successfully created.");
        } catch (Exception e) {
            jo.put("status", "error");
            jo.put("message", "Error when creating the file: " + e.getMessage());
        }

        return jo.toString();
    }

    @PostMapping("/{userName}/createFolder")
    public String createFolder(@PathVariable String userName, @RequestBody Map<String, String> body) {
        AuthenticationUtil.authorizeUser(userName);
        JSONObject jo = new JSONObject();
        String key = body.get("key");

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

    @PostMapping("/{userName}/rename")
    public String renameFile(@PathVariable String userName, @RequestBody Map<String, String> body) {
        AuthenticationUtil.authorizeUser(userName);
        JSONObject jo = new JSONObject();
        String key = body.get("key");
        String newKey = body.get("newKey");

        try {
            s3Service.renameObject(key, newKey);
            jo.put("status", "success");
            jo.put("message", "The file has been successfully renamed.");
        } catch (Exception e) {
            jo.put("status", "error");
            jo.put("message", "Error when renaming the file: " + e.getMessage());
        }

        return jo.toString();
    }

    @PostMapping("/{sender}/share/{receiver}")
    public String shareFile(@PathVariable String sender, @PathVariable String receiver, @RequestBody Map<String, String> body) {
        AuthenticationUtil.authorizeUser(sender);
        JSONObject jo = new JSONObject();
        String senderKey = body.get("senderKey");

        try {
            s3Service.shareObject(sender, senderKey, receiver);
            jo.put("status", "success");
            jo.put("message", "The file has been successfully shared.");
        } catch (Exception e) {
            jo.put("status", "error");
            jo.put("message", "Error when sharing the file: " + e.getMessage());
        }
        
        return jo.toString();
    }
}

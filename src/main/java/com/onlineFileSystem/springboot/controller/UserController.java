package com.onlineFileSystem.springboot.controller;


import com.onlineFileSystem.springboot.common.AuthenticationUtil;
import com.onlineFileSystem.springboot.model.User;
import com.onlineFileSystem.springboot.services.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;


@RestController
public class UserController {

    @Autowired
    UserManager userManager;

    //get all users
    @GetMapping("/users")
    public List<User> getUsers(){
        return userManager.getAllUsers();
    }

    //get user info
    //used to check credentials for login
    //auth is stored in browser memory
    //remove this auth after logout
    @GetMapping("/users/{userName}")
    public User getUser(@PathVariable String userName){
        AuthenticationUtil.authorizeUser(userName);
        return userManager.getUser(userName);
    }

    //create new user
    @PostMapping("/register")
    public User postUser(@RequestBody Map<String, String> body){
        //check required field
        String userName = body.get("userName");
        if(userName == null || userName.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userName not provided");
        }
        String password = body.get("password");
        if(password == null || password.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password not provided");
        }

        return userManager.createUser(userName, password);
    }

    //update password
    @PutMapping("/users/{userName}")
    public User putUser(@PathVariable String userName, @RequestBody Map<String, String> body){
        //check required field
        AuthenticationUtil.authorizeUser(userName);
        String password = body.get("password");
        if(password == null || password.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password not provided");
        }
        return userManager.updateUser(userName, password);
    }
}

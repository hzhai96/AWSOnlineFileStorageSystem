package com.onlineFileSystem.springboot.services;

import com.onlineFileSystem.springboot.model.User;
import com.onlineFileSystem.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserManager implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUser(String userName){
        var result = userRepository.findById(userName);
        return result.isPresent() ? result.get(): null;
    }

    public User createUser(String userName, String password){
        //check whether userName exists
        if(userRepository.existsById(userName)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user already exists");
        }

        //create new user
        return userRepository.save(new User(userName, password));
    }

    public User updateUser(String userName, String password){
        //check whether userName exists
        if(!userRepository.existsById(userName)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user doesn't exists");
        }

        //update user
        return userRepository.save(new User(userName, password));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!userRepository.existsById(username)){
            throw new UsernameNotFoundException(username);
        }
        User usr = getUser(username);
        return usr;
    }
}

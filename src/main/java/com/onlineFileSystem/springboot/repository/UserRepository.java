package com.onlineFileSystem.springboot.repository;

import com.onlineFileSystem.springboot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    //table

}

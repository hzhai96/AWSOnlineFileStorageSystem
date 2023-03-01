package com.onlineFileSystem.springboot.common;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@UtilityClass
@Slf4j
public class AuthenticationUtil {

    public static void authorizeUser(String userName) throws ResponseStatusException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(!auth.isAuthenticated() || !Objects.equals(auth.getName(), userName)){
            log.error("forbidden user {} accessing {}", auth.getName(), userName);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}

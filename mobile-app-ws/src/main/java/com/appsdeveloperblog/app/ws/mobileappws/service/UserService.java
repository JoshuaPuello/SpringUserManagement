package com.appsdeveloperblog.app.ws.mobileappws.service;

import com.appsdeveloperblog.app.ws.mobileappws.shared.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserDTO getUserByUserId(String userId);
    UserDTO createUser(UserDTO user);
    UserDTO getUser(String email);

}

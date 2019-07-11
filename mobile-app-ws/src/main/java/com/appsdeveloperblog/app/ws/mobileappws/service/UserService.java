package com.appsdeveloperblog.app.ws.mobileappws.service;

import com.appsdeveloperblog.app.ws.mobileappws.shared.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    UserDTO getUserByUserId(String userId);
    UserDTO createUser(UserDTO user);
    UserDTO getUser(String email);
    UserDTO updateUser(String userId, UserDTO user);
    void deleteUser(String userId);
    List<UserDTO> getUsers(int page, int limit);

}

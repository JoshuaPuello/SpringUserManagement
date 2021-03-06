package com.appsdeveloperblog.app.ws.mobileappws.service;

import com.appsdeveloperblog.app.ws.mobileappws.shared.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface UserService extends UserDetailsService {

    UserDTO getUserByUserId(String userId);
    UserDTO createUser(UserDTO user) throws Exception;
    UserDTO getUser(String email);
    UserDTO updateUser(String userId, UserDTO user);
    void deleteUser(String userId);
    List<UserDTO> getUsers(int page, int limit);
    boolean verifyEmailToken(String token);
    boolean requestPasswordReset(String email);
    boolean resetPassword(String token, String password);

}

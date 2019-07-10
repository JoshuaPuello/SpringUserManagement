package com.appsdeveloperblog.app.ws.mobileappws.service.impl;

import com.appsdeveloperblog.app.ws.mobileappws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.mobileappws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.mobileappws.io.repository.UserRepository;
import com.appsdeveloperblog.app.ws.mobileappws.service.UserService;
import com.appsdeveloperblog.app.ws.mobileappws.shared.Utils;
import com.appsdeveloperblog.app.ws.mobileappws.shared.dto.UserDTO;
import com.appsdeveloperblog.app.ws.mobileappws.ui.model.response.ErrorMessages;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDTO getUserByUserId(String userId) {

        UserDTO returnUser = new UserDTO();
        UserEntity userEntity = userRepository.findUserByUserId(userId);

        if (userEntity == null)
            throw new UserServiceException(String.format(
                    ErrorMessages.NO_RECORD_FOUND.getErrorMessage(),
                    "id", userId));

        BeanUtils.copyProperties(userEntity, returnUser);

        return returnUser;

    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {

        if (userRepository.findUserByEmail(userDTO.getEmail()) != null)
            throw new UserServiceException(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userDTO, userEntity);

        String generatedUserId = utils.generateUserId(30);
        userEntity.setUserId(generatedUserId);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));

        UserEntity storedUser = userRepository.save(userEntity);

        UserDTO returnUser = new UserDTO();
        BeanUtils.copyProperties(storedUser, returnUser);

        return returnUser;
    }

    @Override
    public UserDTO getUser(String email) {
        UserEntity userEntity = userRepository.findUserByEmail(email);

        if (userEntity == null)
            throw new UserServiceException(String.format(
                    ErrorMessages.NO_RECORD_FOUND.getErrorMessage(),
                    "email", email));

        UserDTO returnUser = new UserDTO();
        BeanUtils.copyProperties(userEntity, returnUser);

        return returnUser;
    }

    @Override
    public UserDTO updateUser(String userId, UserDTO user) {

        UserDTO resultUser = new UserDTO();

        UserEntity userEntity = userRepository.findUserByUserId(userId);

        if (userEntity == null)
            throw new UserServiceException(String.format(
                    ErrorMessages.NO_RECORD_FOUND.getErrorMessage(),
                    "id", userId));

        if (user.getFirstName().isEmpty() || user.getLastName().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());

        UserEntity updatedUser = userRepository.save(userEntity);
        BeanUtils.copyProperties(updatedUser, resultUser);

        return resultUser;
    }

    @Override
    public void deleteUser(String userId) {

        UserEntity userEntity = userRepository.findUserByUserId(userId);

        if (userEntity == null)
            throw new UserServiceException(String.format(
                    ErrorMessages.NO_RECORD_FOUND.getErrorMessage(),
                    "id", userId));

        userRepository.delete(userEntity);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findUserByEmail(email);

        if (userEntity == null) throw new UsernameNotFoundException(email);

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
    }
}

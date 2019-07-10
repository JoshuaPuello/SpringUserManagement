package com.appsdeveloperblog.app.ws.mobileappws.service.impl;

import com.appsdeveloperblog.app.ws.mobileappws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.mobileappws.io.repository.UserRepository;
import com.appsdeveloperblog.app.ws.mobileappws.service.UserService;
import com.appsdeveloperblog.app.ws.mobileappws.shared.Utils;
import com.appsdeveloperblog.app.ws.mobileappws.shared.dto.UserDTO;
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

        if (userEntity == null) throw new UsernameNotFoundException(userId);

        BeanUtils.copyProperties(userEntity, returnUser);

        return returnUser;

    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {

        if (userRepository.findUserByEmail(userDTO.getEmail()) != null)
            throw new RuntimeException("Record already exists!");

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

        if (userEntity == null) throw new UsernameNotFoundException(email);

        UserDTO returnUser = new UserDTO();
        BeanUtils.copyProperties(userEntity, returnUser);

        return returnUser;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findUserByEmail(email);

        if (userEntity == null) throw new UsernameNotFoundException(email);

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
    }
}

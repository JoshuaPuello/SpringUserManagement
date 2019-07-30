package com.appsdeveloperblog.app.ws.mobileappws.service.impl;

import com.appsdeveloperblog.app.ws.mobileappws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.mobileappws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.mobileappws.io.repository.UserRepository;
import com.appsdeveloperblog.app.ws.mobileappws.service.UserService;
import com.appsdeveloperblog.app.ws.mobileappws.shared.AmazonSES;
import com.appsdeveloperblog.app.ws.mobileappws.shared.Utils;
import com.appsdeveloperblog.app.ws.mobileappws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.mobileappws.shared.dto.UserDTO;
import com.appsdeveloperblog.app.ws.mobileappws.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final Utils utils;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserRepository userRepository, Utils utils, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.utils = utils;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

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
    public UserDTO createUser(UserDTO userDTO) {

        if (userRepository.findUserByEmail(userDTO.getEmail()) != null)
            throw new UserServiceException(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());

        if (userDTO.getAddresses() != null) {
            for (int i = 0; i < userDTO.getAddresses().size(); i++) {
                AddressDTO address = userDTO.getAddresses().get(i);
                address.setUserDetails(userDTO);
                address.setAddressId(utils.generateAddressId(30));
                userDTO.getAddresses().set(i, address);
            }
        }

        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(userDTO, UserEntity.class);

        String publicUserId = utils.generateUserId(30);
        userEntity.setUserId(publicUserId);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
        userEntity.setEmailVerificationStatus(false);

        UserEntity storedUser = userRepository.save(userEntity);

        UserDTO mappedUser = modelMapper.map(storedUser, UserDTO.class);

        new AmazonSES().verifyEmail(mappedUser);

        return mappedUser;
    }

    @Override
    public UserDTO updateUser(String userId, UserDTO user) {

        UserDTO resultUser = new UserDTO();

        UserEntity userEntity = userRepository.findUserByUserId(userId);

        if (userEntity == null)
            throw new UserServiceException(String.format(
                    ErrorMessages.NO_RECORD_FOUND.getErrorMessage(),
                    "id", userId));

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
    public List<UserDTO> getUsers(int page, int limit) {

        List<UserDTO> resultUsers = new ArrayList<>();
        Pageable pageableRequest = PageRequest.of(page, limit);

        Page<UserEntity> userEntitiesPage = userRepository.findAll(pageableRequest);
        List<UserEntity> userEntities = userEntitiesPage.getContent();

        for (UserEntity userEntity : userEntities) {
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(userEntity, userDTO);
            resultUsers.add(userDTO);
        }

        return resultUsers;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findUserByEmail(email);

        if (userEntity == null) throw new UsernameNotFoundException(email);

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), userEntity.getEmailVerificationStatus(),
                true, true, true, new ArrayList<>());
    }

    @Override
    public boolean verifyEmailToken(String token) {

        boolean returnValue = false;

        UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);

        if (userEntity != null) {
            boolean hasTokenExpired = Utils.hasTokenExpired(token);
            if (!hasTokenExpired) {
                userEntity.setEmailVerificationToken(null);
                userEntity.setEmailVerificationStatus(Boolean.TRUE);
                userRepository.save(userEntity);
                returnValue = true;
            }
        }

        return returnValue;
    }
}

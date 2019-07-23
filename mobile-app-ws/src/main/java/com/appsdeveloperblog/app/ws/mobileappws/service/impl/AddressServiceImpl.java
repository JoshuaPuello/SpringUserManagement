package com.appsdeveloperblog.app.ws.mobileappws.service.impl;

import com.appsdeveloperblog.app.ws.mobileappws.io.entity.AddressEntity;
import com.appsdeveloperblog.app.ws.mobileappws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.mobileappws.io.repository.AddressRepository;
import com.appsdeveloperblog.app.ws.mobileappws.io.repository.UserRepository;
import com.appsdeveloperblog.app.ws.mobileappws.service.AddressService;
import com.appsdeveloperblog.app.ws.mobileappws.shared.dto.AddressDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private final UserRepository userRepository;

    private final AddressRepository addressRepository;

    public AddressServiceImpl(UserRepository userRepository, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public List<AddressDTO> getAddresses(String userId) {

        List<AddressDTO> returnValue = new ArrayList<>();

        UserEntity userEntity = userRepository.findUserByUserId(userId);
        if (userEntity == null) return returnValue;

        Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
        for (AddressEntity address : addresses) {
            returnValue.add(new ModelMapper().map(address, AddressDTO.class));
        }

        return returnValue;
    }

    @Override
    public AddressDTO getAddress(String addressId) {
        AddressDTO returnAddress = new AddressDTO();

        AddressEntity addressEntity = addressRepository.findAddressByAddressId(addressId);
        if (addressEntity != null) {
            returnAddress = new ModelMapper().map(addressEntity, AddressDTO.class);
        }

        return returnAddress;
    }

}

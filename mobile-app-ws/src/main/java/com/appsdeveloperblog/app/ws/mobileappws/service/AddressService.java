package com.appsdeveloperblog.app.ws.mobileappws.service;

import com.appsdeveloperblog.app.ws.mobileappws.shared.dto.AddressDTO;

import java.util.List;

public interface AddressService {

    List<AddressDTO> getAddresses(String userId);

    AddressDTO getAddress(String addressId);
}

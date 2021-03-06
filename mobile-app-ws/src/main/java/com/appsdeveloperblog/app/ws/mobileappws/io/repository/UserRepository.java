package com.appsdeveloperblog.app.ws.mobileappws.io.repository;

import com.appsdeveloperblog.app.ws.mobileappws.io.entity.UserEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {

    UserEntity findUserByEmail(String email);
    UserEntity findUserByUserId(String userId);
    UserEntity findUserByEmailVerificationToken(String token);

}

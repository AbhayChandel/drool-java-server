package com.hexlindia.drool.user.business.impl.usecase;

import com.hexlindia.drool.user.business.JwtUtil;
import com.hexlindia.drool.user.business.api.to.UserAccountTo;
import com.hexlindia.drool.user.business.api.to.UserProfileTo;
import com.hexlindia.drool.user.business.api.to.UserRegistrationDetailsTo;
import com.hexlindia.drool.user.business.api.to.mapper.RegistrationToUserProfileMapper;
import com.hexlindia.drool.user.business.api.to.mapper.UserAccountMapper;
import com.hexlindia.drool.user.business.api.to.mapper.UserRegistrationDetailsMapper;
import com.hexlindia.drool.user.business.api.usecase.UserAccount;
import com.hexlindia.drool.user.business.api.usecase.UserProfile;
import com.hexlindia.drool.user.data.entity.UserAccountEntity;
import com.hexlindia.drool.user.data.repository.UserAccountRepository;
import com.hexlindia.drool.user.exception.UserAccountNotFoundException;
import com.hexlindia.drool.user.services.AuthenticatedUserDetails;
import com.hexlindia.drool.user.services.JwtResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Transactional
@Slf4j
public class UserAccountImpl implements UserAccount {

    private final UserAccountRepository userAccountRepository;

    private final UserRegistrationDetailsMapper userRegistrationDetailsMapper;

    private final JwtUtil jwtUtil;

    private final UserProfile userProfile;

    private final RegistrationToUserProfileMapper registrationToUserProfileMapper;

    private final PasswordEncoder passwordEncoder;
    private final UserAccountMapper userAccountMapper;

    @Autowired
    protected UserAccountImpl(UserAccountRepository userAccountRepository, UserRegistrationDetailsMapper userRegistrationDetailsMapper, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, UserProfile userProfile, RegistrationToUserProfileMapper registrationToUserProfileMapper, UserAccountMapper userAccountMapper) {
        this.userAccountRepository = userAccountRepository;
        this.userRegistrationDetailsMapper = userRegistrationDetailsMapper;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userProfile = userProfile;
        this.registrationToUserProfileMapper = registrationToUserProfileMapper;
        this.userAccountMapper = userAccountMapper;
    }

    @Override
    public JwtResponse register(UserRegistrationDetailsTo userRegistrationDetailsTo) {
        UserAccountEntity userAccountEntity = userAccountRepository.saveAndFlush(getUserAuthenticationEntity(userRegistrationDetailsTo));
        UserProfileTo userProfileTo = registrationToUserProfileMapper.toUserProfileTo(userRegistrationDetailsTo);
        userProfileTo.setId(userAccountEntity.getId());
        userProfileTo.setCity(null);
        userProfileTo.setGender('N');
        userProfileTo = userProfile.create(userProfileTo);
        return new JwtResponse(jwtUtil.generateToken(userAccountEntity.getEmail()), new AuthenticatedUserDetails(userProfileTo.getId().toString(), userProfileTo.getUsername()));
    }

    @Override
    public UserAccountTo findByEmail(String email) {
        Optional<UserAccountEntity> userAccountEntityOptional = this.userAccountRepository.findByEmail(email);
        if (userAccountEntityOptional.isPresent()) {
            log.info("user with email {} found", email);
            return userAccountMapper.toTransferObject(userAccountEntityOptional.get());
        }
        throw new UserAccountNotFoundException("User Account with email " + email + " not found");
    }

    UserAccountEntity getUserAuthenticationEntity(UserRegistrationDetailsTo userRegistrationDetailsTo) {
        UserAccountEntity userAuthenticationEntity = this.userRegistrationDetailsMapper.toEntity(userRegistrationDetailsTo);
        setEncodedPasswordInEntity(userAuthenticationEntity);
        return userAuthenticationEntity;
    }

    void setEncodedPasswordInEntity(UserAccountEntity userAuthenticationEntity) {
        userAuthenticationEntity.setPassword(getEncodedPassword(userAuthenticationEntity.getPassword()));
    }

    String getEncodedPassword(String password) {
        return passwordEncoder.encode(password);
    }
}

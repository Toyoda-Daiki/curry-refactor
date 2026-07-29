package com.example.service;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.domain.User;
import com.example.domain.UserDetailData;
import com.example.repository.UserRepository;

/**
 * 
 * ユーザーデータを保持するクラス
 * SpringSecurityにユーザー情報を提供する
 * 
 * @author honda
 */
@Service
public class UserDetailService implements UserDetailsService {

    /** ログ出力用Logger */
    private static final Logger log = LoggerFactory.getLogger(UserDetailService.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * ユーザーデータを保持するクラス
     * SpringSecurityにユーザー情報を提供する
     * 
     * @param mailAddress
     * @return UserDetails
     * @throws UsernameNotFoundException
     * @author honda
     */
    @Override
    public UserDetails loadUserByUsername(String mailAddress) throws UsernameNotFoundException {
        User user = userRepository.findByMailAddress(mailAddress);
        // System.out.println(user);

        log.debug("ユーザー取得結果: {}", user);

        if (user == null) {
            throw new UsernameNotFoundException("Not found mail address:" + mailAddress);
        }
        return new UserDetailData(user);
    }
}

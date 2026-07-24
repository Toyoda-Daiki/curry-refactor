package com.example.domain;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * userデータの保持→SpringSecurityにユーザー情報を提供する
 *
 * 
 * @author honda
 */

public class UserDetailData implements UserDetails {

    private User user;

    public UserDetailData(User user) {
        this.user = user;
    }

    @Override
    public String getPassword() {
        String pass = user.getPassword();
        return pass;
        // パスワードを返す
    }

    @Override
    public String getUsername() {
        // ユーザー名を返す
        String email = user.getEmail();
        return email;
    }

    public Integer getUserId() {
        // ユーザー名を返す
        Integer id = user.getId();
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_USER");// 仮の権限

    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}

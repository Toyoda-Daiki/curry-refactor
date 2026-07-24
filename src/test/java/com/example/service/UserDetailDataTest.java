package com.example.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.domain.User;
import com.example.domain.UserDetailData;

/**
 * UserDetailDataのテストクラス.
 */
public class UserDetailDataTest {

    @Test
    @DisplayName("正常系: UserDetailDataの各メソッドがラップしているUserの値を返すこと")
    void testUserDetailDataMethods() {
        User user = new User();
        user.setId(123);
        user.setEmail("test@example.com");
        user.setPassword("hashed-password");
        
        UserDetailData detail = new UserDetailData(user);
        
        assertEquals("test@example.com", detail.getUsername());
        assertEquals("hashed-password", detail.getPassword());
        assertEquals(123, detail.getUserId());
        assertNotNull(detail.getAuthorities());
        assertTrue(detail.isAccountNonExpired());
        assertTrue(detail.isAccountNonLocked());
        assertTrue(detail.isCredentialsNonExpired());
        assertTrue(detail.isEnabled());
    }
}

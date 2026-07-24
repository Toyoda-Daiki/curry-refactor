package com.example.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.domain.User;
import com.example.domain.UserDetailData;
import com.example.repository.UserRepository;

/**
 * UserDetailServiceのテストクラス.
 */
@ExtendWith(MockitoExtension.class)
public class UserDetailServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailService userDetailService;

    @Test
    @DisplayName("正常系: ユーザー名（メールアドレス）でUserDetailsを取得できる")
    void loadUserByUsername_Success() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        when(userRepository.findByMailAddress("test@example.com")).thenReturn(user);
        
        UserDetails result = userDetailService.loadUserByUsername("test@example.com");
        
        assertNotNull(result);
        assertTrue(result instanceof UserDetailData);
        assertEquals("test@example.com", result.getUsername());
        verify(userRepository).findByMailAddress("test@example.com");
    }

    @Test
    @DisplayName("異常系: ユーザーが見つからない場合に例外が発生する")
    void loadUserByUsername_NotFound() {
        when(userRepository.findByMailAddress("unknown@example.com")).thenReturn(null);
        
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailService.loadUserByUsername("unknown@example.com");
        });
        verify(userRepository).findByMailAddress("unknown@example.com");
    }
}

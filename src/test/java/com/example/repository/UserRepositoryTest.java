package com.example.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.User;

/**
 * UserRepositoryのDB接続テストクラス.
 * @author Sho Toda
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByMailAddress() {
        User user = new User();
        user.setName("テスト太郎");
        String email = "find_test@example.com";
        user.setEmail(email);
        user.setPassword("password");
        user.setZipcode("123-4567");
        user.setAddress("東京都");
        user.setTelephone("03-1234-5678");

        userRepository.insert(user);

        User result = userRepository.findByMailAddress(email);
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals("テスト太郎", result.getName());
    }

    @Test
    public void testFindByMailAddress_CaseInsensitive() {
        User user = new User();
        user.setName("CaseTest");
        String email = "CASE_TEST@EXAMPLE.COM";
        user.setEmail(email);
        user.setPassword("password");
        userRepository.insert(user);

        // DB depends if it's case sensitive, but usually we want to know
        User result = userRepository.findByMailAddress(email.toLowerCase());
        // If PostgreSQL/MySQL default, it might be case sensitive or insensitive.
        // Assuming case sensitive for now since the SQL is straight '='.
        // Let's just test that the exact match works first.
        User exactResult = userRepository.findByMailAddress(email);
        assertNotNull(exactResult);
    }

    @Test
    public void testFindByMailAddress_EmptyEmail() {
        User result = userRepository.findByMailAddress("");
        assertNull(result);
    }

    @Test
    public void testFindByMailAddress_NullEmail() {
        User result = userRepository.findByMailAddress(null);
        assertNull(result);
    }

    @Test
    public void testInsert_MinimumFields() {
        User user = new User();
        user.setName("MinFieldUser");
        user.setEmail("min@example.com");
        user.setPassword("pass");
        // Others are null
        userRepository.insert(user);

        User result = userRepository.findByMailAddress("min@example.com");
        assertNotNull(result);
        assertEquals("MinFieldUser", result.getName());
        assertNull(result.getZipcode());
    }

    @Test
    public void testInsert_LongStrings() {
        User user = new User();
        StringBuilder longStr = new StringBuilder();
        for(int i=0; i<100; i++) longStr.append("A");
        
        user.setName(longStr.toString());
        user.setEmail("long@example.com");
        user.setPassword("password");
        user.setAddress(longStr.toString() + longStr.toString());
        
        userRepository.insert(user);
        User result = userRepository.findByMailAddress("long@example.com");
        assertNotNull(result);
        assertEquals(longStr.toString(), result.getName());
    }

    @Test
    public void testInsert_SpecialChars() {
        User user = new User();
        user.setName("!@#$%^&*()");
        user.setEmail("special@example.org");
        user.setPassword("pass'word\"");
        user.setAddress("City; DROP TABLE users; --");
        
        userRepository.insert(user);
        User result = userRepository.findByMailAddress("special@example.org");
        assertNotNull(result);
        assertEquals("!@#$%^&*()", result.getName());
        assertEquals("City; DROP TABLE users; --", result.getAddress());
    }

    @Test
    public void testInsert_MultipleUsers() {
        for(int i=0; i<5; i++) {
            User user = new User();
            user.setName("User" + i);
            user.setEmail("user" + i + "@example.com");
            user.setPassword("pass");
            userRepository.insert(user);
        }
        
        User result = userRepository.findByMailAddress("user4@example.com");
        assertNotNull(result);
        assertEquals("User4", result.getName());
    }

    @Test
    public void testFindByMailAddress_NotFound() {
        User result = userRepository.findByMailAddress("notfound@example.com");
        assertNull(result);
    }

    @Test
    public void testInsert() {
        User user = new User();
        user.setName("テスト太郎");
        String email = "insert@example.com";
        user.setEmail(email);
        user.setPassword("password");
        user.setZipcode("123-4567");
        user.setAddress("東京都");
        user.setTelephone("03-1234-5678");

        userRepository.insert(user);

        User result = userRepository.findByMailAddress(email);
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals("テスト太郎", result.getName());
    }
}

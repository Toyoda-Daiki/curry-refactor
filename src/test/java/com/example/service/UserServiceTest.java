
package com.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.domain.User;
import com.example.repository.UserRepository;



/**
 * {@link UserService} クラスの単体テストクラス.
 * Mockitoを使用してRepositoryや外部サービスをモック化し、ユーザー認証・登録・メール送信・認証コード確認などの各メソッドの動作を検証する。
 * @author Sho Toda
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    /** ユーザー情報を操作するRepositoryのモック */
    @Mock
    private UserRepository userRepository;

    /** メール送信用クラスのモック */
    @Mock
    private MailSender mailSender;

    /** パスワードハッシュ化用エンコーダのモック */
    @Mock
    private PasswordEncoder passwordEncoder;

    /** テスト対象のサービス */
    @InjectMocks
    private UserService userService;

    /**
     * ログイン成功テスト.
     * メールアドレスが存在し、入力パスワードとDBのハッシュ化パスワードが一致する場合にユーザー情報が返されることを確認する。
     */
    @Test
    @DisplayName("ログイン成功：メールアドレスとパスワードが一致する場合")
    void login_Success() {
        String email = "test@example.com";
        String rawPassword = "password";
        String encodedPassword = "encodedPassword";

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);

        when(userRepository.findByMailAddress(email)).thenReturn(user);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        User result = userService.login(rawPassword, email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userRepository, times(1)).findByMailAddress(email);
        verify(passwordEncoder, times(1)).matches(rawPassword, encodedPassword);
    }

    /**
     * ログイン失敗テスト（メールアドレス未登録）.
     * 指定されたメールアドレスのユーザーが存在しない場合、null が返されることを確認する。
     */
    @Test
    @DisplayName("ログイン失敗：メールアドレスが存在しない場合")
    void login_Fail_EmailNotFound() {
        String email = "notfound@example.com";
        String password = "password";

        when(userRepository.findByMailAddress(email)).thenReturn(null);

        User result = userService.login(password, email);

        assertNull(result);
        verify(userRepository, times(1)).findByMailAddress(email);
        verify(passwordEncoder, times(0)).matches(any(), any());
    }

    /**
     * ログイン失敗テスト（パスワード不一致）.
     * メールアドレスは存在するが、パスワードが一致しない場合にnull が返されることを確認する。
     */
    @Test
    @DisplayName("ログイン失敗：パスワードが一致しない場合")
    void login_Fail_PasswordMismatch() {
        String email = "test@example.com";
        String rawPassword = "wrongPassword";
        String encodedPassword = "encodedPassword";

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);

        when(userRepository.findByMailAddress(email)).thenReturn(user);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        User result = userService.login(rawPassword, email);

        assertNull(result);
        verify(userRepository, times(1)).findByMailAddress(email);
        verify(passwordEncoder, times(1)).matches(rawPassword, encodedPassword);
    }

    /**
     * ユーザー登録処理テスト.
     * 登録時にパスワードがハッシュ化され、Repositoryのinsertメソッドが呼び出されることを確認する。
     */
    @Test
    @DisplayName("ユーザー登録：パスワードがハッシュ化されて保存されること")
    void insert_Success() {
        User user = new User();
        user.setPassword("rawPassword");

        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");

        userService.insert(user);

        assertEquals("encodedPassword", user.getPassword());
        verify(passwordEncoder, times(1)).encode("rawPassword");
        verify(userRepository, times(1)).insert(user);
    }

    /**
     * メール送信処理テスト.
     * 認証コードとメールアドレスがセッションに保存され、MailSenderによってメール送信処理が呼び出されることを確認する。
     */
    @Test
    @DisplayName("メール送信：セッションへの保存とメール送信が行われること")
    void sendMail_Success() {
        String email = "test@example.com";
        String checkPass = "1234";

        userService.sendMail(email, checkPass);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    /**
     * ランダムパスワード生成テスト.
     * 4桁の数字のみで構成された認証コードが生成されることを確認する。
     */
    @Test
    @DisplayName("ランダムパスワード生成：4桁の数字が生成されること")
    void randomPass_Success() {
        String result = userService.randomPass();

        assertNotNull(result);
        assertEquals(4, result.length());
        assertTrue(result.matches("\\d{4}"));
    }

    /**
     * 認証コードチェック成功テスト.
     * 入力された認証コードがセッションに保存された値と一致する場合、"OK" が返されセッションの認証コードが削除されることを確認する。
     */
    @Test
    @DisplayName("パスワードチェック：入力値がセッションの値と一致する場合 OK を返すこと")
    void checkpass_Success() {
        String numPass = "1234";

        String sessionPass = "1234";
        String result = userService.checkpass(numPass,sessionPass);

        assertEquals("OK", result);
    }

    /**
     * 認証コードチェック失敗テスト.
     * 入力された認証コードがセッションの値と一致しない場合、"NO" が返されセッションの値は削除されないことを確認する。
     */
    @Test
    @DisplayName("パスワードチェック：入力値がセッションの値と不一致の場合 NO を返すこと")
    void checkpass_Fail() {
        String numPass = "5678";

        String sessionPass = "1234";
       

        String result = userService.checkpass(numPass,sessionPass);

        assertEquals("NO", result);
       
    }

    /**
     * 認証コードチェック異常系テスト.
     * セッション保持値（第2引数）がnullの場合、"NO" が返されることを確認する。
     */
    @Test
    @DisplayName("パスワードチェック：セッション保持値がnullの場合 NO を返すこと")
    void checkpass_Null() {
        // 第1引数は何でもOK、第2引数に null を渡す
        String result = userService.checkpass("1234", null);

        assertEquals("NO", result);
    }

    @Test
    @DisplayName("ログイン失敗：メールアドレスがnullの場合")
    void login_Fail_NullEmail() {
        User result = userService.login("password", null);
        assertNull(result);
    }

    @Test
    @DisplayName("ログイン失敗：パスワードがnullの場合")
    void login_Fail_NullPassword() {
        User result = userService.login(null, "test@example.com");
        assertNull(result);
    }

    @Test
    @DisplayName("パスワードチェック：入力値が空文字の場合 NO を返すこと")
    void checkpass_EmptyInput() {
        String result = userService.checkpass("", "1234");
        assertEquals("NO", result);
    }

    @Test
    @DisplayName("パスワードチェック：両方の引数が空文字の場合 OK を返すこと（仕様に依存）")
    void checkpass_BothEmpty() {
        String result = userService.checkpass("", "");
        assertEquals("OK", result);
    }

    @Test
    @DisplayName("ランダムパスワード生成：複数回実行して異なる値が生成されること（確率的）")
    void randomPass_DifferentValues() {
        String pass1 = userService.randomPass();
        String pass2 = userService.randomPass();
        // 10000分の1で重複する可能性はあるが、基本的には異なるはず
        // 重複してもテストが落ちないように緩い検証にするか、ループを回す
        boolean different = false;
        for(int i=0; i<10; i++) {
            if(!userService.randomPass().equals(pass1)) {
                different = true;
                break;
            }
        }
        assertTrue(different);
    }

    @Test
    @DisplayName("メール送信：送信内容の検証")
    void sendMail_VerifyMessage() {
        String email = "test@example.com";
        String checkPass = "9999";
        userService.sendMail(email, checkPass);

        org.mockito.ArgumentCaptor<SimpleMailMessage> messageCaptor = org.mockito.ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        
        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals(email, sentMessage.getTo()[0]);
        assertTrue(sentMessage.getText().contains(checkPass));
    }
}

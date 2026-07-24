package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
/**
 * セキュリティに関する設定を行うクラス
 * 
 */
public class Config {
    @Autowired
    private AuthenticationSuccessHandler customAuthenticationSuccessHandler;

    /**
     * セキュリティフィルターチェーンを定義するメソッド
     * 
     * @param http
     * @return SecurityFilterChain
     * @throws Exception
     * @author honda
     */
    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf
          .ignoringRequestMatchers("/api/chat/**") // /api/chat はJavaScriptのfetch（POST）から呼ぶためCSRF除外
            ).authorizeHttpRequests(authz -> authz
                .requestMatchers("/css/**", "/js/**", "/img_curry/**").permitAll()
                .requestMatchers("/toLogin").permitAll()
                .requestMatchers("/showList").permitAll()
                .requestMatchers("/insert/**").permitAll()
                .requestMatchers("/mailInsert").permitAll()
                .requestMatchers("/mailsend").permitAll()
                .requestMatchers("/passCheck").permitAll()
                .requestMatchers("/check").permitAll() 
                .requestMatchers("/inCart").permitAll()
                .requestMatchers("/showCart").permitAll()
                .requestMatchers("/delete").permitAll()
                .requestMatchers("/detail").permitAll()
                .requestMatchers("/api/chat/**").authenticated() // ログイン必須
                .anyRequest().authenticated()).formLogin(login -> login
                        .loginPage("/toLogin")
                        .loginProcessingUrl("/login")
                        .failureUrl("/toLogin")
                        .defaultSuccessUrl("/showList", true)
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(customAuthenticationSuccessHandler))
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout**"))
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"));

        return http.build();
    }

    @Bean
    PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();

    }

}

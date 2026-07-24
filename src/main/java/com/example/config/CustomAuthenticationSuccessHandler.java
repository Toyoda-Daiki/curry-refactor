package com.example.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.domain.User;
import com.example.domain.UserDetailData;
import com.example.service.CartService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 認証成功後の処理をカスタマイズするクラス
 * 認証成功後にユーザー情報をセッションに保存するなどの処理を行う
 * 
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private HttpSession session;

    @Autowired
    private CartService cartService;

    /**
     * 認証成功後の処理を実装するメソッド
     * 
     * @param request        HTTPリクエスト
     * @param response       HTTPレスポンス
     * @param authentication 認証情報
     * @throws IOException      入出力例外
     * @throws ServletException サーブレット例外
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        UserDetailData userDetailData = (UserDetailData) authentication.getPrincipal();
        User user = new User();
        user.setPassword(userDetailData.getPassword());
        user.setEmail(userDetailData.getUsername());
        user.setId(userDetailData.getUserId());

        session.setAttribute("user", user);

        // カートの統合（マージ）
        String previousSessionId = (String) session.getAttribute("previousSessionId");
        if (previousSessionId != null) {
            cartService.mergeCart(previousSessionId, user.getId());
        }

        response.sendRedirect("/showList");
    }
}

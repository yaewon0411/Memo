package com.my.memo.config.auth;

import com.my.memo.config.auth.jwt.JwtProvider;
import com.my.memo.config.auth.jwt.JwtVo;
import com.my.memo.config.auth.jwt.RequireAuth;
import com.my.memo.domain.user.Role;
import com.my.memo.ex.CustomJwtException;
import com.my.memo.util.CustomUtil;
import com.my.memo.util.api.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod handlerMethod) {
            RequireAuth requireAuth = handlerMethod.getMethodAnnotation(RequireAuth.class);

            if (requireAuth != null) {
                String token = request.getHeader(JwtVo.HEADER);

                if (!checkTokenPresence(token, response)) {
                    return false;
                }
                return validateTokenAndSetUserInfo(token, requireAuth, response, request);
            }
        }

        return true;
    }

    private boolean validateTokenAndSetUserInfo(String token, RequireAuth requireAuth, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            String jwt = jwtProvider.substringToken(token);

            if (jwtProvider.validateToken(jwt)) { //검증 성공 시
                Role userRole = Role.valueOf(jwtProvider.getUserRole(jwt));
                Long userId = jwtProvider.getUserId(jwt);

                if (requireAuth.role().equals(Role.ADMIN) && !userRole.equals(Role.ADMIN)) {
                    setErrorResponse(response, HttpStatus.FORBIDDEN.value(), "관리자 권한이 필요합니다");
                    return false;
                }
                request.setAttribute("userId", userId);
                request.setAttribute("userRole", userRole);

                log.info("유저 정보 설정: 유저 ID {}, 권한 {}", userId, userRole);
            }
        } catch (CustomJwtException e) {
            setErrorResponse(response, e.getStatus(), e.getMsg());
            return false;
        }
        return true;
    }

    private boolean checkTokenPresence(String token, HttpServletResponse response) throws IOException {
        if (token == null || token.isEmpty()) {
            setErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "인증이 필요합니다");
            return false;
        }
        return true;
    }

    private void setErrorResponse(HttpServletResponse response, int status, String msg) throws IOException {
        ApiResult<Object> unAuthResponse = ApiResult.error(status, msg);
        response.setStatus(status);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(CustomUtil.convertToJson(unAuthResponse));
    }

}

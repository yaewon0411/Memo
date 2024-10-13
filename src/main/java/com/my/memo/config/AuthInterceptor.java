package com.my.memo.config;

import com.my.memo.config.jwt.JwtProvider;
import com.my.memo.config.jwt.JwtVo;
import com.my.memo.config.jwt.RequireAuth;
import com.my.memo.domain.user.Role;
import com.my.memo.domain.user.User;
import com.my.memo.domain.user.UserRepository;
import com.my.memo.ex.CustomApiException;
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
    private final UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod handlerMethod) {
            RequireAuth requireAuth = handlerMethod.getMethodAnnotation(RequireAuth.class);

            if (requireAuth != null) {
                String token = request.getHeader(JwtVo.HEADER);

                //헤더가 있다면
                if (token != null && !token.isEmpty()) {
                    try {
                        String jwt = jwtProvider.substringToken(token);

                        if (jwtProvider.validateToken(jwt)) { //검증 성공 시
                            Role userRole = Role.valueOf(jwtProvider.getUserRole(jwt));
                            Long userId = jwtProvider.getUserId(jwt);

                            User userPS = userRepository.findById(userId)
                                    .orElseThrow(() -> {
                                        if (requireAuth.role().equals(Role.ADMIN)) {
                                            log.warn("존재하지 않는 관리자 접근 시도: ID {}", userId);
                                            return new CustomApiException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 관리자입니다");
                                        } else {
                                            log.warn("존재하지 않는 유저 접근 시도: ID {}", userId);
                                            return new CustomApiException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 유저입니다");
                                        }
                                    });

                            if (requireAuth.role().equals(Role.ADMIN) && !requireAuth.role().equals(userRole)) {
                                setErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "관리자 권한이 필요합니다");
                                return false;
                            }

                            request.setAttribute("user", userPS);
                            log.info("유저 정보 설정: 유저 ID {}, 권한 {}", userPS.getId(), userPS.getRole());
                        }
                    } catch (CustomJwtException e) {
                        setErrorResponse(response, e.getStatus(), e.getMsg());
                        return false;
                    }
                }
                //헤더가 없다면
                else {
                    setErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "권한이 없습니다");
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 에러 응답을 설정하고 반환
     * 유저가 인증되지 않았을 때 호출되며 오류 응답을 클라이언트에 보냅니다
     *
     * @param response 현재 응답
     * @throws IOException 응답 작성 중 IO 예외가 발생 가능
     */
    private void setErrorResponse(HttpServletResponse response, int status, String msg) throws IOException {
        ApiResult<Object> unAuthResponse = ApiResult.error(status, msg);
        response.setStatus(status);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(CustomUtil.convertToJson(unAuthResponse));
    }

}

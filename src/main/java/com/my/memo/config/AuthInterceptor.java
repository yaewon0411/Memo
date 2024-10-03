package com.my.memo.config;

import com.my.memo.util.CustomUtil;
import com.my.memo.util.api.ApiResult;
import com.my.memo.util.api.ApiUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

/**
 * 인증을 위한 인터셉터 클래스입니다
 *
 * 요청이 처리되기 전에 세션에 유저 정보가 있는지 확인하여 인증된 사용자인지 여부를 판단합니다
 * 세션에 유저 정보가 없을 경우 401 Unauthorized 응답을 클라이언트에 반환합니다
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

    /**
     * 요청이 처리되기 전에 호출되는 메서드입니다
     * 세션에 유저 정보가 있는지 확인하여 유저가 인증된 상태인지 검사합니다
     *
     * @param request 현재 요청
     * @param response 현재 응답
     * @param handler 실행하려는 핸들러 객체
     * @return 유저가 인증된 경우 true를 반환해 요청이 계속 진행되도록 하고, 인증되지 않은 경우 false를 반환하여 요청 중단
     * @throws Exception 예외 발생 시
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        log.info("요청 URI: " + uri);

        HttpSession session = request.getSession();

        //세션에 유저 정보 있으면
        if(session != null && session.getAttribute("userId") != null)
            return true;

        setErrorResponse(response);

        return false;
    }

    /**
     * 401 응답을 설정하고 반환
     * 유저가 인증되지 않았을 때 호출되며 오류 응답을 클라이언트에 보냅니다
     *
     * @param response 현재 응답
     * @throws IOException 응답 작성 중 IO 예외가 발생 가능
     */
    private void setErrorResponse(HttpServletResponse response) throws IOException {
        ApiResult<Object> unAuthResponse = ApiUtil.error(HttpStatus.UNAUTHORIZED.value(), "로그인이 필요합니다");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(CustomUtil.convertToJson(unAuthResponse));
    }

}

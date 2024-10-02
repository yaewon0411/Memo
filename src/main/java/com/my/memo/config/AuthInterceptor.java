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

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

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

    private void setErrorResponse(HttpServletResponse response) throws IOException {
        ApiResult<Object> unAuthResponse = ApiUtil.error(HttpStatus.UNAUTHORIZED.value(), "로그인이 필요합니다");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(CustomUtil.convertToJson(unAuthResponse));
    }

}

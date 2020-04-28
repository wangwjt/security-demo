package com.practise.security.handler;

import com.practise.security.entity.SimpleResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证失败处理器
 *
 * @author wangjiantao
 * @date 2020/4/28 10:12
 */
@Component
public class AuthenticationFailure extends SimpleUrlAuthenticationFailureHandler {

    // 日志
    private Logger logger = LoggerFactory.getLogger(getClass());

    // 可将对象转化成json
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * @param request   请求
     * @param response  相应
     * @param exception 认证失败的异常
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {
        logger.info("登录失败");
        // 返回json
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setContentType("application/json;charset=UTF-8");
        // 将认证信息写到response里返回
        response.getWriter().write(objectMapper.writeValueAsString(new SimpleResponse(exception.getMessage())));
    }
}

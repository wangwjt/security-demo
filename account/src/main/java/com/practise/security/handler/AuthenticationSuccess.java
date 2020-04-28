package com.practise.security.handler;


import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证成功处理器
 *
 * @author wangjiantao
 * @date 2020/4/28 10:11
 */
@Component
public class AuthenticationSuccess extends SavedRequestAwareAuthenticationSuccessHandler {

    // 日志
    private Logger logger = LoggerFactory.getLogger(getClass());

    // 可将对象转化成json
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    ClientDetailsService clientDetailsService;

    @Autowired
    AuthorizationServerTokenServices authorizationServerTokenServices;

    /**
     * 登录成功
     *
     * @param request        请求
     * @param response       响应
     * @param authentication 认证信息
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        logger.info("登录成功");

        // 取认证信息
        String header = request.getHeader("Authorization");

        // 如果header是空或者不以Basic开头就抛异常
        if (header == null || !header.startsWith("Basic ")) {
            throw new UnapprovedClientAuthenticationException("请求头中无client信息");
        }

        // 解码请求头中的串，取出用户名和密码
        String[] tokens = extractAndDecodeHeader(header, request);
        assert tokens.length == 2;

        String clientId = tokens[0];
        String clientSecret = tokens[1];

        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);

        // 判断根据clientId是否拿到了第三方应用的信息
        if (clientDetails == null) {
            throw new UnapprovedClientAuthenticationException("clientId对应的配置不存在" + clientId);
        } else if (!StringUtils.equals(clientSecret, clientDetails.getClientSecret())) {
            // 判断获取的clientSecret和配置的clientSecret是否一致
            throw new UnapprovedClientAuthenticationException("clientSecret不匹配" + clientId);
        }
        // 创建 tokenRequest
        TokenRequest tokenRequest = new TokenRequest(MapUtils.EMPTY_SORTED_MAP, clientId, clientDetails.getScope(), "custom");

        // 创建 oAuth2Request
        OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);

        // 创建 oAuth2Authentication
        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);

        OAuth2AccessToken token = authorizationServerTokenServices.createAccessToken(oAuth2Authentication);

        // 返回json
        response.setContentType("application/json;charset=UTF-8");
        // 将认证信息写到response里返回
        response.getWriter().write(objectMapper.writeValueAsString(token));

    }

    /**
     * 改造BasicAuthenticationFilter中的方法（224L）
     * 将header中的Authorization取出，并提取用户名密码
     *
     * @param header
     * @param request
     * @return
     * @throws IOException
     */
    private String[] extractAndDecodeHeader(String header, HttpServletRequest request)
            throws IOException {
        //例如header为"Basic NzFhZjY1NDAtZDU0NS00YTgxLWE2MTEtODFiYzdiN2ZiMDYzOjQ3YTI3MGFhLWMwNTktNDE0ZS1iNTczLTQ5YWI0MGQ2MzJhMw=="
        // 去除"Basic "取后面的"NzFhZjY……"
        byte[] base64Token = header.substring(6).getBytes("UTF-8");
        byte[] decoded;
        try {
            //作base64的解码
            // 解码例如71af6540-d545-4a81-a611-81bc7b7fb063:47a270aa-c059-414e-b573-49ab40d632a3
            // 格式为用户名:密码
            decoded = Base64.decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException(
                    "Failed to decode basic authentication token");
        }

        String token = new String(decoded, "UTF-8");
        // 寻找冒号的位置
        int delim = token.indexOf(":");

        // 如果没找到冒号就抛异常
        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }
        // 找到了就返回用户名(字符串开始到冒号位置)，密码(冒号位置到字符串结尾)
        return new String[]{token.substring(0, delim), token.substring(delim + 1)};
    }
}

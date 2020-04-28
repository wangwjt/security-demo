package com.practise.security.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;

/**
 * 授权服务器
 * @author wangjiantao
 * @date 2020/4/28 9:50
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.
                userDetailsService(userDetailsService)
                .authenticationManager(authenticationManager);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory() // 指定保存在内存里
                .withClient("testId") // 配置clientId

                .secret("testSecret")// 配置secret
                .accessTokenValiditySeconds(3600)// 配置token有效时间 1小时
                .authorizedGrantTypes("refresh_token","password")// 针对testId这个clientId的授权模式是哪些
                                         // (如上的配置只能用密码模式和刷新令牌,除此以外的授权模式不支持)
                .scopes("all","read","write")// 能发出去的权限有哪些
                                         // (如上的配置是请求令牌是携带的参数范围只能是"all","read","write"其中之一)
                // 给第2个程序配置获取令牌信息
                .and()
                    .withClient("appId")
                    .secret("appSecret")
                    .accessTokenValiditySeconds(7200)
                    .authorizedGrantTypes("refresh_token","password")
                    .scopes("all","read","write");


    }
}

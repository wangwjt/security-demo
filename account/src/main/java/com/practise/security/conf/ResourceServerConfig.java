package com.practise.security.conf;

import com.practise.security.handler.AuthenticationFailure;
import com.practise.security.handler.AuthenticationSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * 资源服务器
 * @author wangjiantao
 * @date 2020/4/28 9:50
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private AuthenticationSuccess authenticationSuccess;

        @Autowired
        private AuthenticationFailure authenticationFailure;

        @Override
        public void configure(HttpSecurity http) throws Exception {

        http
                .formLogin()// 表单登陆
                .loginPage("/authentication/require")// 跳转到处理登录页面的controller Url
                .loginProcessingUrl("/authentication/form")// 配置登录表单提交的URL
                .successHandler(authenticationSuccess)// 设置登录成功处理器使用自己配的
                .failureHandler(authenticationFailure)// 设置登录失败处理器使用自己配的
                .and()
                .authorizeRequests()// 对请求授权
                .antMatchers("/authentication/require",// 配置不需要认证的url
                        "/logout.html")
                .permitAll()// 配置'/authentication/require','/logout.html'不需要身份认证
                .anyRequest()// 任何请求
                .authenticated()//身份认证
                .and()
                .csrf().disable();//暂时关掉csrf
    }
}

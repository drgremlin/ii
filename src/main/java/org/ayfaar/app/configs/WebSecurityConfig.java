package org.ayfaar.app.configs;

import org.ayfaar.app.spring.authentication.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

import javax.inject.Inject;


@Configuration
@EnableWebSecurity
//@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("currentUserDetailsService")
    private UserDetailsService userDetailsService;

    @Inject
    LoginServiceProvider externalServiceAuthenticator;

    @Inject
    RestAuthenticationEntryPoint authenticationEntryPoint;
    @Inject
    RestAuthenticationFailureHandler authenticationFailureHandler;
    @Inject
    RestAuthenticationSuccessHandler authenticationSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/static/old/adm.html")
                .hasRole("USER")
                //.anyRequest().authenticated()
//                .and()
//                .formLogin().loginPage("/api/auth")
//                .and()
//                .httpBasic()
//                .and().formLogin().defaultSuccessUrl("/static/old/adm.html", false).failureUrl("/")
//                .anyRequests().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                        .and()
                        .csrf()//Disabled CSRF protection
                        .disable();
        http    .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint);
        http    .formLogin()
                .successHandler(authenticationSuccessHandler);
        http    .formLogin()
                .failureHandler(authenticationFailureHandler);

                //.addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
                //.anyRequest().fullyAuthenticated();
////                .and()
////                .formLogin()
////                .loginPage("/login")
////                .permitAll()
////                .and()
////                .logout()
////                .permitAll();

//        http
//                .authorizeRequests().anyRequest().authenticated()
//                .and()
//                .httpBasic();

        http
                .formLogin()
                .loginProcessingUrl("/api/auth")
                .permitAll()
                .and()
                .logout()
                .permitAll();

    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {

        //auth.authenticationProvider(authProvider);
//      auth.userDetailsService(userDetailsService);
//                //.passwordEncoder(new BCryptPasswordEncoder());
        auth.authenticationProvider(new CustomAuthenticationProvider(externalServiceAuthenticator));
//

    }
}
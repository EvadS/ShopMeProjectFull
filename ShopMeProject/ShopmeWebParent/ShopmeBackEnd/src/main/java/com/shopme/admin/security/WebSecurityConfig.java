package com.shopme.admin.security;


import com.shopme.admin.controller.ShopmeUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public UserDetailsService userDetailsService() {
        return new ShopmeUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                    .antMatchers("/users/**").hasAuthority("Admin")
                    .antMatchers("/categories/**").hasAnyAuthority("Admin","Editor")
                    .antMatchers("/brands/**").hasAnyAuthority("Admin","Editor")

                ///.antMatchers("/products/**").hasAnyAuthority("Admin","Editor","Salesperson","Shipper")

                    .antMatchers("/products/new", "/products/delete")
                        .hasAnyAuthority("Admin","Editor","Salesperson","Shipper")
                    .antMatchers("/products/edit/**", "/products/save/**").hasAnyAuthority("Admin","Editor")
                    .antMatchers("/products/**").hasAnyAuthority("Admin","Editor","Salesperson")

                    .antMatchers("/products","/products/", "/products/detail/**","/products/page/**")
                        .hasAnyAuthority("Admin","Editor","Salesperson","Shipper")

                    .antMatchers("/products/**").hasAnyAuthority("Admin","Editor")

                    .antMatchers("/js/**", "/css/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                    .loginPage("/login")
                     .defaultSuccessUrl("/", true)
                    .usernameParameter("email")
                .permitAll()
                .and().logout().permitAll()
                .and().rememberMe()
                      .tokenValiditySeconds(2*24*60*60)
        ;
    }

    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
           webSecurity.ignoring()
                .antMatchers("/css/**")
                .antMatchers("/scripts/**")
                .antMatchers("/js/**")
                .antMatchers("/webjars/**")
                .antMatchers("/images/**");
    }
}

package com.techdevsolutions.cache;

import com.techdevsolutions.cache.security.CustomAuthenticationManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SpringWebSecurityConfig extends WebSecurityConfigurerAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Environment environment;
    protected CustomAuthenticationManager customAuthenticationManager;

    @Autowired
    SpringWebSecurityConfig(Environment environment, CustomAuthenticationManager customAuthenticationManager) {
        this.environment = environment;
        this.customAuthenticationManager = customAuthenticationManager;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/v1/app/**").hasAuthority("ROLE_USER")
                .antMatchers("/login*").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .and()
                .logout()
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login");
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        String security = this.environment.getProperty("security.custom.system");

        if (StringUtils.isNotEmpty(security) && security.equalsIgnoreCase("custom")) {
            auth.authenticationProvider(this.customAuthenticationManager);
        } else {
            PasswordEncoder encoder =
                    PasswordEncoderFactories.createDelegatingPasswordEncoder();
            auth.inMemoryAuthentication()
                    .withUser("user")
                    .password(encoder.encode("password"))
                    .roles("USER")
                    .and()
                    .withUser("admin").
                    password(encoder.encode("admin"))
                    .roles("USER", "API", "ADMIN");
        }
    }
}
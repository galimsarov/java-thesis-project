package main.config;

import main.model.helper.Account;
import main.repository.UserRepository;
import main.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.
        EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.
        WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * Класс BasicConfiguration
 * Конфигуратор авторизаций
 *
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
public class BasicConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserRepository userRepository;

    /**
     * Метод configure(HttpSecurity http)
     * Метод конфигурирует доступы: endpoints
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET,
                        "/api/post/moderation*", "/api/post/my*")
                .authenticated()
                .antMatchers(HttpMethod.POST)
                .authenticated()
                .anyRequest().permitAll()
                .and()
                .httpBasic();
    }

    /**
     * Метод configure(AuthenticationManagerBuilder auth)
     * Метод конфигурирует доступы: берёт информацию из БД
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        UserServiceImpl service = new UserServiceImpl(userRepository);
        List<Account> accounts = service.getAccounts();
        for (Account account : accounts) {
            String encodedPassword = passwordEncoder().
                    encode(account.getPassword());
            auth.inMemoryAuthentication()
                    .passwordEncoder(passwordEncoder())
                    .withUser(account.getName()).password(encodedPassword)
                    .roles("ADMIN");
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

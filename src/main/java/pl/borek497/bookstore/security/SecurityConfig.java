package pl.borek497.bookstore.security;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.borek497.bookstore.user.db.UserEntityRepository;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@AllArgsConstructor
@EnableConfigurationProperties(AdminConfig.class)
//@Profile("!test")
public class SecurityConfig {

    private final UserEntityRepository userEntityRepository;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final AdminConfig config;

    @Bean
    User systemUser() {
        return new User("systemUser", "", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers(HttpMethod.GET, "/catalog/**", "/uploads/**", "/authors/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/orders", "/login", "/users").permitAll()
                                .anyRequest().authenticated())
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .httpBasic(withDefaults());
        return http.build();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        manager.createUser(User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build());
//        manager.createUser(User.withDefaultPasswordEncoder()
//                .username("admin")
//                .password("admin")
//                .roles("ADMIN")
//                .build());
//        return manager;
//    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        BookstoreUserDetailsService bookstoreUserDetailsService = new BookstoreUserDetailsService(userEntityRepository, config);
        provider.setUserDetailsService(bookstoreUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @SneakyThrows
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) {
        return authenticationConfiguration.getAuthenticationManager();
    }

    private JsonUsernameAuthenticationFilter authenticationFilter() {
        JsonUsernameAuthenticationFilter filter = new JsonUsernameAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }
}
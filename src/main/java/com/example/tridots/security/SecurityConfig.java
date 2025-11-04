package com.example.tridots.security;

import com.example.tridots.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    SecurityFilter securityFilter;
    @Autowired
    AuthService authService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(authService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/aluno/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/aluno/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/aluno/getu").permitAll()
                        .requestMatchers(HttpMethod.GET, "/aluno/get").permitAll()
                        .requestMatchers(HttpMethod.POST, "/aluno/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/pedido/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/aluno/usuarios").permitAll()
                        .requestMatchers("/h2-console").permitAll()
                        .requestMatchers( "/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/arquivo/upload").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMINISTRADOR")
                        .anyRequest().authenticated() //any request = qualquer role, só precisa estar autenticado
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }




}

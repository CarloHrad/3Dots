package com.example.tridots.security;

import com.example.tridots.OperationCode.OperationCode;
import com.example.tridots.repository.UsuarioRepository;
import com.example.tridots.service.AuthService;
import com.example.tridots.service.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    TokenService tokenService;

    @Autowired
    AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {

            String path = request.getRequestURI();
            if (path.equals("/aluno/login") || path.equals("/aluno/register")) {
                System.out.println("Pulando filtro para " + path);
                filterChain.doFilter(request, response);
                return;
            }


            var token = this.recoverToken(request);

            if (token == null || tokenService.validateToken(token).isEmpty()) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setContentType("application/json");

                BaseResponse baseResponse = new BaseResponse(
                        OperationCode.ACCESS_Denid.getCode(),
                        OperationCode.ACCESS_Denid.getDescription() + ": Token ausente ou inválido",
                        null,
                        OperationCode.ACCESS_Denid.getHttpStatus()
                );

                response.getWriter().write(new ObjectMapper().writeValueAsString(baseResponse));
                return;
            }

            var login = tokenService.validateToken(token);
            UserDetails access = authService.loadUserByUsername(login);
            var authentication = new UsernamePasswordAuthenticationToken(access, null, access.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (UsernameNotFoundException ex) {
            System.out.println("UsernameNot Found Exception Log");
            SecurityContextHolder.clearContext();
        } catch (Exception ex) {
            System.out.println("Exception Log");
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}

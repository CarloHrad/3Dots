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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    TokenService tokenService;

    @Autowired
    AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

            String path = request.getRequestURI();
            if (
                    path.equals("/aluno/login") ||
                            path.equals("/aluno/register") ||
                            path.equals("/user/register") ||
                            path.equals("/user/login") ||
                            path.startsWith("/h2-console/") ||
                            path.startsWith("/css/") ||
                            path.startsWith("/js/") ||
                            path.startsWith("/images/") ||
                            path.equals("/") ||
                            path.equals("/index.html") ||
                            path.equals("/cadastro.html") ||
                            path.equals("/login.html") ||
                            path.equals("/admin-pedidos.html") ||
                            path.equals("/admin-pedido-detalhe.html") ||
                            path.equals("/realizar-pedido.html") ||
                            path.equals("/meus-pedidos.html") ||
                            path.equals("/pedidos.html") ||
                            path.equals("/style.css")) {
                filterChain.doFilter(request, response);
                return;
            }


            var token = this.recoverToken(request);
            var login = tokenService.validateToken(token);


            if (token == null || login == null || login.isEmpty()) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");

                BaseResponse baseResponse = new BaseResponse(
                        OperationCode.ACCESS_Denid.getCode(),
                        "Acesso negado: Token ausente ou inválido",
                        null,
                        HttpStatus.UNAUTHORIZED
                );

                response.getWriter().write(new ObjectMapper().writeValueAsString(baseResponse));
                return;
            }

        String role = tokenService.getRoleFromToken(token);

        try {
            UserDetails access = authService.loadUserByUsername(login);
            var authentication = new UsernamePasswordAuthenticationToken(access, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (UsernameNotFoundException ex) {
            System.out.println("UsernameNot Found Exception Log - Do Filter Internal");
            SecurityContextHolder.clearContext();
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

        } catch (Exception ex) {
            System.out.println("Exception Log - Do Filter Internal");
            SecurityContextHolder.clearContext();
        }
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}

package com.example.courseprojectapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.courseprojectapi.model.entity.User;
import com.example.courseprojectapi.repository.TokenBlacklistRepository;
import com.example.courseprojectapi.security.jwt.JwtProvider;
import com.example.courseprojectapi.security.principle.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthencationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtProvider jwtProvider;
    
    @Autowired
    private TokenBlacklistRepository tokenBlacklistRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromHeader(request);

        if(token != null && jwtProvider.validateAccessToken(token) && !tokenBlacklistRepository.existsByTokenString(token)) {
            User user = User.builder()
                    .username(jwtProvider.getUsernameFromToken(token))
                    .build();
            
            List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + jwtProvider.getRoleFromToken(token))
            );
            
            UserPrincipal userPrincipal = new UserPrincipal(user, authorities);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    public String getTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7);
    }
}

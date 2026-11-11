package com.example.bankalite.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

       // System.out.println(">>> JWT Filter çalıştı, path: " + request.getRequestURI());
      //  System.out.println(">>> Auth header: " + request.getHeader("Authorization"));

        // 1. Authorization başlığını oku
        String authHeader = request.getHeader("Authorization");

        // 2. Yoksa veya "Bearer " ile başlamıyorsa geç
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Token'ı çıkar ("Bearer " sonrası)
        String token = authHeader.substring(7);

        // 4. Token geçerli mi?
        if (!jwtService.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 5. Token'dan kullanıcı adı ve rolleri çıkar
        String username = jwtService.extractUsername(token);
        List<String> roles = jwtService.extractRoles(token);

        // 6. Spring Security'ye "bu kullanıcı giriş yapmış" de
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, authorities);
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 7. İsteği devam ettir
        filterChain.doFilter(request, response);
    }
}
// OncePerRequestFilter: her istek için bir kez çalışır, isim oradan geliyor.
//authHeader.substring(7): "Bearer " 7 karakter, sonrasını alıyoruz.
//SecurityContextHolder: Spring Security’nin “şu an kim giriş yapmış?” diye baktığı yer. Buraya yazarsak Spring tüm metodlarda bunu okuyabilir.
//Veritabanına gitmiyor. Token’ı doğrulamak için sadece gizli anahtara ihtiyaç var, bu çok hızlı.
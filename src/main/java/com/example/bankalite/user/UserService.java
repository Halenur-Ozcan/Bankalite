package com.example.bankalite.user;

import com.example.bankalite.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.bankalite.common.BusinessException;
import org.springframework.http.HttpStatus;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.username())) {
            //throw new IllegalArgumentException("Bu kullanıcı adı zaten alınmış: " + request.username());
            throw new BusinessException("Bu kullanıcı adı zaten alınmış: " + request.username(), HttpStatus.CONFLICT);
        }

        String hashedPassword = passwordEncoder.encode(request.password());

        User user = new User();
        user.setUsername(request.username());
        user.setPasswordHash(hashedPassword);

        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new IllegalStateException("ROLE_CUSTOMER bulunamadı"));
        user.getRoles().add(customerRole);

        User saved = userRepository.save(user);
        return UserResponse.from(saved);
    }
    @Transactional
    public AuthResponse login(LoginRequest request) {

        // 1. Kullanıcıyı bul
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı adı veya şifre hatalı"));

        // 2. Hesap kilitli mi?
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(Instant.now())) {
            throw new IllegalStateException("Hesap kilitli. Lütfen daha sonra tekrar deneyin.");
        }

        // 3. Şifreyi kontrol et
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            user.setFailedLoginCount(user.getFailedLoginCount() + 1);
            if (user.getFailedLoginCount() >= 5) {
                user.setLockedUntil(Instant.now().plus(30, ChronoUnit.MINUTES));
            }
            userRepository.save(user);
            throw new IllegalArgumentException("Kullanıcı adı veya şifre hatalı");
        }

        // 4. Başarılı giriş: sayacı sıfırla
        user.setFailedLoginCount(0);
        user.setLockedUntil(null);
        userRepository.save(user);

        // 5. JWT üret
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();
        String token = jwtService.generateAccessToken(user.getUsername(), roles);
        return AuthResponse.of(token, user.getUsername());
    }
}
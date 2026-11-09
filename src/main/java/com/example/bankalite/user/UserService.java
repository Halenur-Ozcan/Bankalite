package com.example.bankalite.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse register(RegisterRequest request) {

        // 1. Bu username alınmış mı?
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Bu kullanıcı adı zaten alınmış: " + request.username());
        }

        // 2. Şifreyi hash'le
        String hashedPassword = passwordEncoder.encode(request.password());

        // 3. Kullanıcıyı oluştur
        User user = new User();
        user.setUsername(request.username());
        user.setPasswordHash(hashedPassword);

        // 4. ROLE_CUSTOMER rolünü bul ve ata
        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new IllegalStateException("ROLE_CUSTOMER bulunamadı"));
        user.getRoles().add(customerRole);

        // 5. Kaydet ve cevap döndür
        User saved = userRepository.save(user);
        return UserResponse.from(saved);
    }
}
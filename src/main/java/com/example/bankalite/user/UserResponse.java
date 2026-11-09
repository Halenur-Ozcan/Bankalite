package com.example.bankalite.user;

public record UserResponse(
        Long id,
        String username,
        String status
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getStatus());
    }
}
// record nedir? Java 16 ile gelen modern bir yapı. Getter, constructor, equals, hashCode otomatik üretilir. DTO için mükemmel çünkü DTO’lar değişmez (immutable) olmalı.
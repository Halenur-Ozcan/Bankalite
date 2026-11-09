package com.example.bankalite.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
//JpaRepository’den şu metodları bedavaya alırsın:
// Metod	Ne yapar
//save(user)	Kaydet veya güncelle
//findById(1L)	ID ile bul
//findAll()	Hepsini getir
//delete(user)	Sil
//count()	Kaç kayıt var

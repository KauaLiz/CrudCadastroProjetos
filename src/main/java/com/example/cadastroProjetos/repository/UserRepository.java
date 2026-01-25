package com.example.cadastroProjetos.repository;

import com.example.cadastroProjetos.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    UserDetails findByLogin(String login);
}

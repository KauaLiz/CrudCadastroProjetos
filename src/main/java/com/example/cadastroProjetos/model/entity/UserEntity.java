package com.example.cadastroProjetos.model.entity;

import com.example.cadastroProjetos.model.enums.UserRole;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table(name = "users")
@Entity(name = "users")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String login;
    private String senha;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    public UserEntity(String login, String senha, UserRole role){
        this.login = login;
        this.senha = senha;
        this.role = role;
    }

    public UserEntity(){
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.role == UserRole.ADMINISTRADOR) return List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"), new SimpleGrantedAuthority("ROLE_MEMBRO"));
        else return List.of(new SimpleGrantedAuthority("ROLE_MEMBRO"));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

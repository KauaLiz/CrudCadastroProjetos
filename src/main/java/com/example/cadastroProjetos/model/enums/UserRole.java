package com.example.cadastroProjetos.model.enums;

public enum UserRole {
    ADMINISTRADOR("Administrador"),
    MEMBRO("Membro");

    private String role;

    UserRole(String role){
        this.role = role;
    }
}

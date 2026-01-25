package com.example.cadastroProjetos.model.enums;

public enum UserRole {
    GERENTE("Gerente"),
    MEMBRO("Membro");

    private String role;

    UserRole(String role){
        this.role = role;
    }
}

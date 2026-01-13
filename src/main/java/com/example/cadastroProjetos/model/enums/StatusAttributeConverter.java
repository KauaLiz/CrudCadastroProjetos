package com.example.cadastroProjetos.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusAttributeConverter implements AttributeConverter<Status, String> {

    @Override
    public String convertToDatabaseColumn(Status status){
        return status == null ? null : status.getDescricao();
    }

    public Status convertToEntityAttribute(String status){
        return status == null ? null : Status.converterEnum(status);
    }
}


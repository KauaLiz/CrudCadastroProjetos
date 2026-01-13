package com.example.cadastroProjetos.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ClassificacaoRiscoAttributeConverter implements AttributeConverter<ClassificacaoRisco, String> {

    @Override
    public String convertToDatabaseColumn(ClassificacaoRisco risco){
        return risco == null ? null : risco.getDescricao();
    }

    @Override
    public ClassificacaoRisco convertToEntityAttribute(String dbData){
        return dbData == null ? null : ClassificacaoRisco.converterEnum(dbData);
    }
}

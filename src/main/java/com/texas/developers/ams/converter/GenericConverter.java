package com.texas.developers.ams.converter;

import java.util.List;

public abstract class GenericConverter<DTO, ENTITY> {

    abstract DTO toDTO(ENTITY entity);

    abstract ENTITY toEntity(DTO dto);

    abstract List<DTO> toDtoList(List<ENTITY> entityList);
}

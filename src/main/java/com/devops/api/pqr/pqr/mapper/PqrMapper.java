package com.devops.api.pqr.pqr.mapper;

import com.devops.api.pqr.pqr.dto.UpdatePqrDto;
import com.devops.api.pqr.pqr.entity.Pqr;
import org.mapstruct.*;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface PqrMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "book", ignore = true)
    void updatePqr(UpdatePqrDto dto, @MappingTarget Pqr entity);

    @AfterMapping
    default void mapBook(UpdatePqrDto dto, @MappingTarget Pqr entity) {
        if (dto.getBook() != null) {
            entity.setBook(Map.of(
                    "bookTitle", dto.getBook().bookTitle(),
                    "bookAuthor", dto.getBook().bookAuthor()
            ));
        }
    }
}
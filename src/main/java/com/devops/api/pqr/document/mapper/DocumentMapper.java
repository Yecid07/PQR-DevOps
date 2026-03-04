package com.devops.api.pqr.document.mapper;

import com.devops.api.pqr.document.entity.Document;
import com.devops.api.pqr.document.dto.DocumentResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    DocumentResponseDto toResponseDto(Document document);
}
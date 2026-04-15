package com.devops.api.pqr.book.mapper;

import com.devops.api.pqr.book.dto.BookOrderResponse;
import com.devops.api.pqr.book.entity.BookOrderResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {BookInfoMapper.class, ReceiptInfoMapper.class}
)
public interface BookOrderResultMapper {

    @Mapping(target = "externalPqrId", source = "response.pqr.id")
    @Mapping(target = "asunto", source = "response.pqr.asunto")
    @Mapping(target = "responsable", source = "response.pqr.responsable")
    @Mapping(target = "conteo", source = "response.pqr.conteo")

    @Mapping(target = "book", source = "response.book")
    @Mapping(target = "receipt", source = "response.receipt")

    @Mapping(target = "bookTitle", source = "bookTitle")
    @Mapping(target = "bookAuthor", source = "bookAuthor")

    @Mapping(target = "savedAt", expression = "java(java.time.LocalDateTime.now())")
    BookOrderResult toEntity(
            BookOrderResponse response,
            String bookTitle,
            String bookAuthor
    );
}
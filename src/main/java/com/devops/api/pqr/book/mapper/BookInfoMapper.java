package com.devops.api.pqr.book.mapper;

import com.devops.api.pqr.book.dto.BookOrderResponse;
import com.devops.api.pqr.book.entity.BookInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookInfoMapper {

    BookInfo toEntity(BookOrderResponse.BookResponse dto);
}

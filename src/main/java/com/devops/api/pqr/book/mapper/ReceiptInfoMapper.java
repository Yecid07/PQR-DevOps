package com.devops.api.pqr.book.mapper;

import com.devops.api.pqr.book.dto.BookOrderResponse;
import com.devops.api.pqr.book.entity.ReceiptInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReceiptInfoMapper {

    ReceiptInfo toEntity(BookOrderResponse.ReceiptResponse dto);
}

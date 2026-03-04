package com.devops.api.pqr.reviewer.mapper;

import com.devops.api.pqr.reviewer.dto.CreateReviewerDto;
import com.devops.api.pqr.reviewer.entity.Reviewer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewerMapper {
    @Mapping(target = "id", ignore = true)
    Reviewer toReviewer(CreateReviewerDto createReviewerDto);
}

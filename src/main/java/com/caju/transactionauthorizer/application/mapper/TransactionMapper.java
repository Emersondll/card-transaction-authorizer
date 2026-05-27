package com.caju.transactionauthorizer.application.mapper;

import com.caju.transactionauthorizer.application.dto.TransactionResponse;
import com.caju.transactionauthorizer.domain.model.AuthorizationStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapper {
    default TransactionResponse toResponse(AuthorizationStatus status) {
        return new TransactionResponse(status.getCode());
    }
}

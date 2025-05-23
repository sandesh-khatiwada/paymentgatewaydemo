
package com.sandesh.paymentgatewaydemo.mapper;

import com.sandesh.paymentgatewaydemo.dto.PaymentRequestDTO;
import com.sandesh.paymentgatewaydemo.entity.PaymentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentRequestMapper {

    @Mapping(source = "appId.name", target = "application")
    PaymentRequestDTO toDTO(PaymentRequest entity);

    @Mapping(target = "appId", expression = "java(dto.getApplication() != null ? AppId.fromName(dto.getApplication()) : null)")
    PaymentRequest toEntity(PaymentRequestDTO dto);
}

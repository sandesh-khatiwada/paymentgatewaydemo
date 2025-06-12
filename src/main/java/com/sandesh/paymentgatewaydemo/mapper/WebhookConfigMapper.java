package com.sandesh.paymentgatewaydemo.mapper;

import com.sandesh.paymentgatewaydemo.dto.WebhookConfigDTO;
import com.sandesh.paymentgatewaydemo.entity.WebhookConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface WebhookConfigMapper {
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "localDateTimeToString")
    WebhookConfigDTO toDTO(WebhookConfig webhookConfig);

    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "stringToLocalDateTime")
    WebhookConfig toEntity(WebhookConfigDTO webhookConfigDTO);

    @Named("localDateTimeToString")
    default String localDateTimeToString(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Named("stringToLocalDateTime")
    default LocalDateTime stringToLocalDateTime(String dateTime) {
        if (dateTime == null) {
            return null;
        }
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}

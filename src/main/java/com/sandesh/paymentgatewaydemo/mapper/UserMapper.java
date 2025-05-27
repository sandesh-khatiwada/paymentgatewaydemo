package com.sandesh.paymentgatewaydemo.mapper;
import com.sandesh.paymentgatewaydemo.dto.UserDTO;
import com.sandesh.paymentgatewaydemo.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User entity);

    User toEntity(UserDTO dto);
}

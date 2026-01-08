package com.hoangthanhhong.badminton.mapper;

import com.hoangthanhhong.badminton.dto.request.UserAddressRequest;
import com.hoangthanhhong.badminton.dto.response.UserAddressResponse;
import com.hoangthanhhong.badminton.entity.UserAddress;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserAddressMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "fullAddress", expression = "java(address.getFullAddress())")
    UserAddressResponse toResponse(UserAddress address);

    List<UserAddressResponse> toResponseList(List<UserAddress> addresses);

    @Mapping(target = "user", ignore = true)
    UserAddress toEntity(UserAddressRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromRequest(UserAddressRequest request, @MappingTarget UserAddress address);
}

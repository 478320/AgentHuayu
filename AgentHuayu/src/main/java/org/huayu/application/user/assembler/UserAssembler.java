package org.huayu.application.user.assembler;

import org.huayu.application.user.dto.UserDTO;
import org.huayu.domain.user.model.UserEntity;
import org.huayu.interfaces.dto.user.request.RegisterRequest;
import org.huayu.interfaces.dto.user.request.UserUpdateRequest;
import org.springframework.beans.BeanUtils;

public class UserAssembler {

    public static UserDTO toDTO(UserEntity userEntity) {
        UserDTO userDTO = new UserDTO();

        BeanUtils.copyProperties(userEntity, userDTO);
        return userDTO;
    }

    public static UserEntity toEntity(UserDTO userDTO) {
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userDTO, userEntity);
        return userEntity;
    }

    public static UserEntity toEntity(RegisterRequest registerRequest) {
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(registerRequest, userEntity);
        return userEntity;
    }

    public static UserEntity toEntity(UserUpdateRequest userUpdateRequest, String userId) {
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userUpdateRequest, userEntity);
        userEntity.setId(userId);
        return userEntity;
    }
}

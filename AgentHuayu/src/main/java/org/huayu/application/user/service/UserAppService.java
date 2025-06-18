package org.huayu.application.user.service;

import org.huayu.application.user.assembler.UserAssembler;
import org.huayu.application.user.dto.UserDTO;
import org.huayu.domain.user.model.UserEntity;
import org.huayu.domain.user.service.UserDomainService;
import org.huayu.interfaces.dto.user.request.UserUpdateRequest;
import org.springframework.stereotype.Service;


@Service
public class UserAppService {

    private final UserDomainService userDomainService;

    public UserAppService(UserDomainService userDomainService) {
        this.userDomainService = userDomainService;
    }

    /** 获取用户信息 */
    public UserDTO getUserInfo(String id) {
        UserEntity userEntity = userDomainService.getUserInfo(id);
        return UserAssembler.toDTO(userEntity);
    }

    /** 修改用户信息 */
    public void updateUserInfo(UserUpdateRequest userUpdateRequest, String userId) {
        UserEntity user = UserAssembler.toEntity(userUpdateRequest, userId);
        userDomainService.updateUserInfo(user);
    }
}

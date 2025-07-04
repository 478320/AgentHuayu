package org.huayu.interfaces.api.portal.user;

import org.huayu.application.user.dto.UserDTO;
import org.huayu.application.user.dto.UserSettingsDTO;
import org.huayu.application.user.service.UserAppService;
import org.huayu.application.user.service.UserSettingsAppService;
import org.huayu.infrastructure.auth.UserContext;
import org.huayu.interfaces.api.common.Result;
import org.huayu.interfaces.dto.user.request.UserSettingsUpdateRequest;
import org.huayu.interfaces.dto.user.request.UserUpdateRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/** 用户 */
@RestController
@RequestMapping("/users")
public class PortalUserController {

    private final UserAppService userAppService;

    private final UserSettingsAppService userSettingsAppService;

    public PortalUserController(UserAppService userAppService, UserSettingsAppService userSettingsAppService) {
        this.userAppService = userAppService;
        this.userSettingsAppService = userSettingsAppService;
    }

    /** 获取用户信息
     * @return */
    @GetMapping
    public Result<UserDTO> getUserInfo() {
        String userId = UserContext.getCurrentUserId();
        return Result.success(userAppService.getUserInfo(userId));
    }

    /** 修改用户信息
     * @param userUpdateRequest 需要修改的信息
     * @return */
    @PostMapping
    public Result<?> updateUserInfo(@RequestBody @Validated UserUpdateRequest userUpdateRequest) {
        String userId = UserContext.getCurrentUserId();
        userAppService.updateUserInfo(userUpdateRequest, userId);
        return Result.success();
    }

    /** 获取用户设置
     * @return 用户设置信息 */
    @GetMapping("/settings")
    public Result<UserSettingsDTO> getUserSettings() {
        String userId = UserContext.getCurrentUserId();
        UserSettingsDTO settings = userSettingsAppService.getUserSettings(userId);
        return Result.success(settings);
    }

    /** 更新用户设置
     * @param request 更新请求
     * @return 更新后的用户设置 */
    @PutMapping("/settings")
    public Result<UserSettingsDTO> updateUserSettings(@RequestBody @Validated UserSettingsUpdateRequest request) {
        String userId = UserContext.getCurrentUserId();
        UserSettingsDTO settings = userSettingsAppService.updateUserSettings(request, userId);
        return Result.success(settings);
    }

    /** 获取用户默认模型ID
     * @return 默认模型ID */
    @GetMapping("/settings/default-model")
    public Result<String> getUserDefaultModelId() {
        String userId = UserContext.getCurrentUserId();
        String defaultModelId = userSettingsAppService.getUserDefaultModelId(userId);
        return Result.success(defaultModelId);
    }
}

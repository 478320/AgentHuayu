package org.huayu.domain.user.repository;

import org.apache.ibatis.annotations.Mapper;
import org.huayu.domain.user.model.UserSettingsEntity;
import org.huayu.infrastructure.repository.MyBatisPlusExtRepository;


/** 用户设置仓储接口 */
@Mapper
public interface UserSettingsRepository extends MyBatisPlusExtRepository<UserSettingsEntity> {

}
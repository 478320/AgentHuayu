package org.huayu.domain.user.repository;

import org.apache.ibatis.annotations.Mapper;
import org.huayu.domain.user.model.UserEntity;
import org.huayu.infrastructure.repository.MyBatisPlusExtRepository;


/** 模型仓储接口 */
@Mapper
public interface UserRepository extends MyBatisPlusExtRepository<UserEntity> {

}
package org.huayu.domain.conversation.repository;

import org.apache.ibatis.annotations.Mapper;
import org.huayu.domain.conversation.model.ContextEntity;
import org.huayu.infrastructure.repository.MyBatisPlusExtRepository;


/** 上下文仓库接口 */
@Mapper
public interface ContextRepository extends MyBatisPlusExtRepository<ContextEntity> {
}
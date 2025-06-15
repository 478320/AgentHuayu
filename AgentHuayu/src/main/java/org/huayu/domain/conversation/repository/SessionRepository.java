package org.huayu.domain.conversation.repository;

import org.apache.ibatis.annotations.Mapper;
import org.huayu.domain.conversation.model.SessionEntity;
import org.huayu.infrastructure.repository.MyBatisPlusExtRepository;


/** 会话仓库接口 */
@Mapper
public interface SessionRepository extends MyBatisPlusExtRepository<SessionEntity> {
}
package org.huayu.domain.agent.repository;

import org.apache.ibatis.annotations.Mapper;
import org.huayu.domain.agent.model.AgentEntity;
import org.huayu.infrastructure.repository.MyBatisPlusExtRepository;


/** Agent仓库接口 */
@Mapper
public interface AgentRepository extends MyBatisPlusExtRepository<AgentEntity> {
}
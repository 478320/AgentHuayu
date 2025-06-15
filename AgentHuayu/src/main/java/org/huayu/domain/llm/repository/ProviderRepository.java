package org.huayu.domain.llm.repository;

import org.apache.ibatis.annotations.Mapper;
import org.huayu.domain.llm.model.ProviderEntity;
import org.huayu.infrastructure.repository.MyBatisPlusExtRepository;


/** 服务提供商仓储接口 */
@Mapper
public interface ProviderRepository extends MyBatisPlusExtRepository<ProviderEntity> {

}
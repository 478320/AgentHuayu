package org.huayu.domain.llm.repository;

import org.apache.ibatis.annotations.Mapper;
import org.huayu.domain.llm.model.ModelEntity;
import org.huayu.infrastructure.repository.MyBatisPlusExtRepository;

/** 模型仓储接口 */
@Mapper
public interface ModelRepository extends MyBatisPlusExtRepository<ModelEntity> {

}
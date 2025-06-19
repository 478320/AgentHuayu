package org.huayu.domain.task.repository;

import org.apache.ibatis.annotations.Mapper;
import org.huayu.domain.task.model.TaskEntity;
import org.huayu.infrastructure.repository.MyBatisPlusExtRepository;


/**
 * 任务仓储接口
 */
@Mapper
public interface TaskRepository extends MyBatisPlusExtRepository<TaskEntity> {
    

} 
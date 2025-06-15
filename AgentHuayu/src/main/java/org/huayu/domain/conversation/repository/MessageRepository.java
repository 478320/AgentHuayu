package org.huayu.domain.conversation.repository;

import org.apache.ibatis.annotations.Mapper;
import org.huayu.domain.conversation.model.MessageEntity;
import org.huayu.infrastructure.repository.MyBatisPlusExtRepository;


/** 消息仓库接口 */
@Mapper
public interface MessageRepository extends MyBatisPlusExtRepository<MessageEntity> {
}
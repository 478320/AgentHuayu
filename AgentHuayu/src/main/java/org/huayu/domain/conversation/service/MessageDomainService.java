package org.huayu.domain.conversation.service;

import org.huayu.domain.conversation.model.ContextEntity;
import org.huayu.domain.conversation.model.MessageEntity;
import org.huayu.domain.conversation.repository.ContextRepository;
import org.huayu.domain.conversation.repository.MessageRepository;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class MessageDomainService {

    private final MessageRepository messageRepository;

    private final ContextRepository contextRepository;

    public MessageDomainService(MessageRepository messageRepository, ContextRepository contextRepository) {
        this.messageRepository = messageRepository;
        this.contextRepository = contextRepository;
    }

    public List<MessageEntity> listByIds(List<String> ids) {
        return messageRepository.selectByIds(ids);
    }

    /** 保存消息并且更新消息到上下文 */
    public void saveMessageAndUpdateContext(List<MessageEntity> messageEntities, ContextEntity contextEntity) {
        if (messageEntities == null || messageEntities.isEmpty()) {
            return;
        }
        for (MessageEntity messageEntity : messageEntities) {
            messageEntity.setId(null);
        }
        messageRepository.insert(messageEntities);
        contextEntity.getActiveMessages().addAll(messageEntities.stream().map(MessageEntity::getId).toList());
        contextRepository.insertOrUpdate(contextEntity);
    }

    /** 保存消息 */
    public void saveMessage(List<MessageEntity> messageEntities) {
        messageRepository.insert(messageEntities);
    }

    public void updateMessage(MessageEntity message) {
        messageRepository.updateById(message);
    }

}

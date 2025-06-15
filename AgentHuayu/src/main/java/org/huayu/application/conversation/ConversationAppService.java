package org.huayu.application.conversation;

import org.huayu.application.conversation.dto.AgentPreviewRequest;
import org.huayu.application.conversation.dto.ChatRequest;

import org.huayu.application.conversation.dto.MessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;


/**
 * 对话应用服务，用于适配域层的对话服务
 */
@Service
public class ConversationAppService {

    private static final Logger logger = LoggerFactory.getLogger(ConversationAppService.class);

    /** 对话方法 - 统一入口
     *
     * @param chatRequest 聊天请求
     * @param userId 用户ID
     * @return SSE发射器 */
    public SseEmitter chat(ChatRequest chatRequest, String userId) {
        return null;
    }


    public List<MessageDTO> getConversationMessages(String sessionId, String userId) {
        return null;
    }

    public SseEmitter previewAgent(AgentPreviewRequest previewRequest, String userId) {
        return null;
    }
}

package org.huayu.application.conversation;

import org.huayu.application.conversation.dto.AgentPreviewRequest;
import org.huayu.application.conversation.dto.ChatRequest;

import org.huayu.application.conversation.dto.MessageDTO;
import org.huayu.application.conversation.service.handler.MessageHandlerFactory;
import org.huayu.application.conversation.service.handler.context.ChatContext;
import org.huayu.application.conversation.service.message.AbstractMessageHandler;
import org.huayu.domain.agent.model.AgentEntity;
import org.huayu.domain.agent.model.AgentWorkspaceEntity;
import org.huayu.domain.agent.model.LLMModelConfig;
import org.huayu.domain.agent.service.AgentDomainService;
import org.huayu.domain.agent.service.AgentWorkspaceDomainService;
import org.huayu.domain.conversation.model.ContextEntity;
import org.huayu.domain.conversation.model.MessageEntity;
import org.huayu.domain.conversation.model.SessionEntity;
import org.huayu.domain.conversation.service.ContextDomainService;
import org.huayu.domain.conversation.service.ConversationDomainService;
import org.huayu.domain.conversation.service.MessageDomainService;
import org.huayu.domain.conversation.service.SessionDomainService;
import org.huayu.domain.llm.model.ModelEntity;
import org.huayu.domain.llm.model.ProviderEntity;
import org.huayu.domain.llm.service.LLMDomainService;
import org.huayu.domain.shared.enums.TokenOverflowStrategyEnum;
import org.huayu.domain.token.model.TokenMessage;
import org.huayu.domain.token.model.TokenProcessResult;
import org.huayu.domain.token.model.config.TokenOverflowConfig;
import org.huayu.domain.token.service.TokenDomainService;
import org.huayu.infrastructure.exception.BusinessException;
import org.huayu.infrastructure.llm.config.ProviderConfig;
import org.huayu.infrastructure.transport.MessageTransport;
import org.huayu.infrastructure.transport.MessageTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 对话应用服务，用于适配域层的对话服务
 */
@Service
public class ConversationAppService {

    private static final Logger logger = LoggerFactory.getLogger(ConversationAppService.class);

    private final ConversationDomainService conversationDomainService;
    private final SessionDomainService sessionDomainService;
    private final AgentDomainService agentDomainService;
    private final AgentWorkspaceDomainService agentWorkspaceDomainService;
    private final LLMDomainService llmDomainService;
    private final ContextDomainService contextDomainService;
    private final TokenDomainService tokenDomainService;
    private final MessageDomainService messageDomainService;

    private final MessageHandlerFactory messageHandlerFactory;
    private final MessageTransportFactory transportFactory;



    public ConversationAppService(ConversationDomainService conversationDomainService,
                                  SessionDomainService sessionDomainService, AgentDomainService agentDomainService,
                                  AgentWorkspaceDomainService agentWorkspaceDomainService, LLMDomainService llmDomainService,
                                  ContextDomainService contextDomainService, TokenDomainService tokenDomainService,
                                  MessageDomainService messageDomainService, MessageHandlerFactory messageHandlerFactory,
                                  MessageTransportFactory transportFactory) {
        this.conversationDomainService = conversationDomainService;
        this.sessionDomainService = sessionDomainService;
        this.agentDomainService = agentDomainService;
        this.agentWorkspaceDomainService = agentWorkspaceDomainService;
        this.llmDomainService = llmDomainService;
        this.contextDomainService = contextDomainService;
        this.tokenDomainService = tokenDomainService;
        this.messageDomainService = messageDomainService;
        this.messageHandlerFactory = messageHandlerFactory;
        this.transportFactory = transportFactory;
    }

    /** 对话方法 - 统一入口
     *
     * @param chatRequest 聊天请求
     * @param userId 用户ID
     * @return SSE发射器 */
    public SseEmitter chat(ChatRequest chatRequest, String userId) {

        ChatContext environment = prepareEnvironment(chatRequest, userId);

        // 2. 获取传输方式 (当前仅支持SSE，将来支持WebSocket)
        MessageTransport<SseEmitter> transport = transportFactory
                .getTransport(MessageTransportFactory.TRANSPORT_TYPE_SSE);

        // 3. 获取适合的消息处理器 (根据agent类型)
        AbstractMessageHandler handler = messageHandlerFactory.getHandler(environment.getAgent());

        // 处理对话
        return handler.chat(environment, transport);
    }


    public List<MessageDTO> getConversationMessages(String sessionId, String userId) {
        return null;
    }

    public SseEmitter previewAgent(AgentPreviewRequest previewRequest, String userId) {
        return null;
    }

    /** 准备对话环境
     *
     * @param chatRequest 聊天请求
     * @param userId 用户ID
     * @return 对话环境 */
    private ChatContext prepareEnvironment(ChatRequest chatRequest, String userId) {
        // 1. 获取会话
        String sessionId = chatRequest.getSessionId();
        SessionEntity session = sessionDomainService.getSession(sessionId, userId);
        String agentId = session.getAgentId();

        // 2. 获取对应agent
        AgentEntity agent = agentDomainService.getAgentById(agentId);
        if (!agent.getUserId().equals(userId) && !agent.getEnabled()) {
            throw new BusinessException("agent已被禁用");
        }

        // 3. 获取工作区和模型配置
        AgentWorkspaceEntity workspace = agentWorkspaceDomainService.getWorkspace(agentId, userId);
        LLMModelConfig llmModelConfig = workspace.getLlmModelConfig();
        String modelId = llmModelConfig.getModelId();
        ModelEntity model = llmDomainService.getModelById(modelId);
        model.isActive();

        // 5. 获取服务商信息
        ProviderEntity provider = llmDomainService.getProvider(model.getProviderId(), userId);
        provider.isActive();

        // 5. 创建环境对象
        ChatContext chatContext = new ChatContext();
        chatContext.setSessionId(sessionId);
        chatContext.setUserId(userId);
        chatContext.setUserMessage(chatRequest.getMessage());
        chatContext.setAgent(agent);
        chatContext.setModel(model);
        chatContext.setProvider(provider);
        chatContext.setLlmModelConfig(llmModelConfig);
        chatContext.setFileUrls(chatRequest.getFileUrls());
        // 6. 设置上下文信息和消息历史
        setupContextAndHistory(chatContext);

        return chatContext;
    }

    /** 设置上下文和历史消息
     *
     * @param environment 对话环境 */
    private void setupContextAndHistory(ChatContext environment) {
        String sessionId = environment.getSessionId();

        // 获取上下文
        ContextEntity contextEntity = contextDomainService.findBySessionId(sessionId);
        List<MessageEntity> messageEntities = new ArrayList<>();

        if (contextEntity != null) {
            // 获取活跃消息
            List<String> activeMessageIds = contextEntity.getActiveMessages();
            messageEntities = messageDomainService.listByIds(activeMessageIds);

            // 应用Token溢出策略
            applyTokenOverflowStrategy(environment, contextEntity, messageEntities);
        } else {
            contextEntity = new ContextEntity();
            contextEntity.setSessionId(sessionId);
        }

        environment.setContextEntity(contextEntity);
        environment.setMessageHistory(messageEntities);
    }

    /** 应用Token溢出策略
     *
     * @param environment 对话环境
     * @param contextEntity 上下文实体
     * @param messageEntities 消息实体列表 */
    private void applyTokenOverflowStrategy(ChatContext environment, ContextEntity contextEntity,
                                            List<MessageEntity> messageEntities) {

        LLMModelConfig llmModelConfig = environment.getLlmModelConfig();
        ProviderEntity provider = environment.getProvider();

        // 处理Token溢出
        TokenOverflowStrategyEnum strategyType = llmModelConfig.getStrategyType();

        // Token处理
        List<TokenMessage> tokenMessages = tokenizeMessage(messageEntities);

        // 构造Token配置
        TokenOverflowConfig tokenOverflowConfig = new TokenOverflowConfig();
        tokenOverflowConfig.setStrategyType(strategyType);
        tokenOverflowConfig.setMaxTokens(llmModelConfig.getMaxTokens());
        tokenOverflowConfig.setSummaryThreshold(llmModelConfig.getSummaryThreshold());

        // 设置提供商配置
        org.huayu.domain.llm.model.config.ProviderConfig providerConfig = provider.getConfig();
        tokenOverflowConfig.setProviderConfig(new ProviderConfig(providerConfig.getApiKey(),
                providerConfig.getBaseUrl(), environment.getModel().getModelId(), provider.getProtocol()));

        // 处理Token
        TokenProcessResult result = tokenDomainService.processMessages(tokenMessages, tokenOverflowConfig);

        // 更新上下文
        if (result.isProcessed()) {
            List<TokenMessage> retainedMessages = result.getRetainedMessages();
            List<String> retainedMessageIds = retainedMessages.stream().map(TokenMessage::getId)
                    .collect(Collectors.toList());

            if (strategyType == TokenOverflowStrategyEnum.SUMMARIZE) {
                String newSummary = result.getSummary();
                String oldSummary = contextEntity.getSummary();
                contextEntity.setSummary(oldSummary + newSummary);
            }

            contextEntity.setActiveMessages(retainedMessageIds);
        }
    }

    /** 消息实体转换为token消息 */
    private List<TokenMessage> tokenizeMessage(List<MessageEntity> messageEntities) {
        return messageEntities.stream().map(message -> {
            TokenMessage tokenMessage = new TokenMessage();
            tokenMessage.setId(message.getId());
            tokenMessage.setRole(message.getRole().name());
            tokenMessage.setContent(message.getContent());
            tokenMessage.setTokenCount(message.getTokenCount());
            tokenMessage.setCreatedAt(message.getCreatedAt());
            return tokenMessage;
        }).collect(Collectors.toList());
    }
}

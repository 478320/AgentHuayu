package org.huayu.application.conversation.service.message.agent;

import org.huayu.application.conversation.service.handler.context.ChatContext;
import org.huayu.application.conversation.service.message.AbstractMessageHandler;
import org.huayu.application.conversation.service.message.agent.event.AgentEventBus;
import org.huayu.application.conversation.service.message.agent.handler.AnalyserMessageHandler;
import org.huayu.application.conversation.service.message.agent.handler.SummarizeHandler;
import org.huayu.application.conversation.service.message.agent.handler.TaskExecutionHandler;
import org.huayu.application.conversation.service.message.agent.handler.TaskSplitHandler;
import org.huayu.application.conversation.service.message.agent.manager.TaskManager;
import org.huayu.application.conversation.service.message.agent.service.InfoRequirementService;
import org.huayu.application.conversation.service.message.agent.workflow.AgentWorkflowContext;
import org.huayu.application.conversation.service.message.agent.workflow.AgentWorkflowState;
import org.huayu.domain.conversation.model.MessageEntity;
import org.huayu.domain.conversation.service.ContextDomainService;
import org.huayu.domain.conversation.service.ConversationDomainService;
import org.huayu.domain.task.model.TaskEntity;
import org.huayu.infrastructure.llm.LLMServiceFactory;
import org.huayu.infrastructure.transport.MessageTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * Agent消息处理器
 * 用于支持工具调用的对话模式
 * 实现任务拆分、执行和结果汇总的工作流
 * 使用事件驱动架构进行状态转换
 */
@Component(value = "agentMessageHandler")
public class AgentMessageHandler extends AbstractMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(AgentMessageHandler.class);
    
    private final TaskManager taskManager;
    private final TaskSplitHandler taskSplitHandler;
    private final TaskExecutionHandler taskExecutionHandler;
    private final SummarizeHandler summarizeHandler;
    private final AnalyserMessageHandler analyserMessageHandler;
    private final InfoRequirementService infoRequirementService;

    public AgentMessageHandler(
            ConversationDomainService conversationDomainService,
            ContextDomainService contextDomainService,
            LLMServiceFactory llmServiceFactory,
            TaskManager taskManager,
            TaskSplitHandler taskSplitHandler, TaskExecutionHandler taskExecutionHandler, SummarizeHandler summarizeHandler,
            AnalyserMessageHandler analyserMessageHandler, InfoRequirementService infoRequirementService) {
        super(conversationDomainService, contextDomainService, llmServiceFactory);
        this.taskManager = taskManager;
        this.taskSplitHandler = taskSplitHandler;
        this.taskExecutionHandler = taskExecutionHandler;
        this.summarizeHandler = summarizeHandler;
        this.analyserMessageHandler = analyserMessageHandler;
        this.infoRequirementService = infoRequirementService;

        // 初始化事件处理器
        initializeEventHandlers();
    }

    /**
     * 初始化事件处理器
     */
    private void initializeEventHandlers() {
        // 注册各状态的处理器
        // 这里通过依赖注入获取处理器实例
        try {
            // 注册任务拆分处理器
            AgentEventBus.register(AgentWorkflowState.TASK_SPLITTING,
                    taskSplitHandler);

            // 注册任务执行处理器
            AgentEventBus.register(AgentWorkflowState.TASK_SPLIT_COMPLETED,
                    taskExecutionHandler);

            // 注册结果汇总处理器
            AgentEventBus.register(AgentWorkflowState.TASK_EXECUTED,
                    summarizeHandler);

            // 注册分析用户输入处理器
            AgentEventBus.register(AgentWorkflowState.ANALYSER_MESSAGE, analyserMessageHandler);
        } catch (Exception e) {
            // 初始化异常处理
            throw new RuntimeException("初始化Agent事件处理器失败", e);
        }
    }

    /**
     * 重写父类的聊天方法，使用事件驱动工作流
     */
    @Override
    public <T> T chat(ChatContext chatContext, MessageTransport<T> messageTransport) {
        String sessionId = chatContext.getSessionId();
        String userInput = chatContext.getUserMessage();
        
        // 判断用户输入是否是补充信息
        AgentWorkflowContext blockingInfo = infoRequirementService.getBlockingInfo(sessionId);
        
        if (blockingInfo != null) {
            log.info("检测到用户正在补充信息: sessionId={}, message={}", sessionId, userInput);
            
            // 创建新的连接，后续补充信息处理结果会通过这个连接发送给前端
            T connection = messageTransport.createConnection(CONNECTION_TIMEOUT);
            
            // 先设置新连接，再处理用户输入
            blockingInfo.setConnection(connection);
            blockingInfo.setMessageTransport(messageTransport);
            
            // 处理用户输入
            infoRequirementService.handleUserInput(sessionId, userInput);
            
            // 返回新连接
            return connection;
        }

        // 创建用户消息实体和LLM消息实体
        MessageEntity userMessageEntity = this.createUserMessage(chatContext);
        MessageEntity llmMessageEntity = createLlmMessage(chatContext);

        // 创建连接
        T connection = messageTransport.createConnection(CONNECTION_TIMEOUT);

        // 创建父任务
        TaskEntity parentTask = taskManager.createParentTask(chatContext);

        // 创建工作流上下文
        AgentWorkflowContext<T> workflowContext = new AgentWorkflowContext<>();
        workflowContext.setChatContext(chatContext);
        workflowContext.setMessageTransport(messageTransport);
        workflowContext.setConnection(connection);
        workflowContext.setUserMessageEntity(userMessageEntity);
        workflowContext.setLlmMessageEntity(llmMessageEntity);
        workflowContext.setParentTask(parentTask);

        // 转换状态到消息分析，触发事件
        workflowContext.transitionTo(AgentWorkflowState.ANALYSER_MESSAGE);

        // 立即返回连接，后续由事件驱动工作流
        return connection;
    }
}
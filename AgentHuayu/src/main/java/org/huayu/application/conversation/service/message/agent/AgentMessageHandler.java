package org.huayu.application.conversation.service.message.agent;

import dev.langchain4j.service.tool.ToolProvider;
import org.huayu.application.conversation.service.handler.context.ChatContext;
import org.huayu.application.conversation.service.message.AbstractMessageHandler;
import org.springframework.stereotype.Component;


/** Agent消息处理器 用于支持工具调用的对话模式 实现任务拆分、执行和结果汇总的工作流 使用事件驱动架构进行状态转换 */
@Component(value = "agentMessageHandler")
public class AgentMessageHandler extends AbstractMessageHandler {


}
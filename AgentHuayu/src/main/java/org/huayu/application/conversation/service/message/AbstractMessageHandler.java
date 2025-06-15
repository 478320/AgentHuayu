package org.huayu.application.conversation.service.message;

import org.huayu.application.conversation.service.handler.context.ChatContext;
import org.huayu.infrastructure.transport.MessageTransport;

public abstract class AbstractMessageHandler {

    /** 处理对话的模板方法
     *
     * @param chatContext 对话环境
     * @param transport 消息传输实现
     * @return 连接对象
     * @param <T> 连接类型 */
    public <T> T chat(ChatContext chatContext, MessageTransport<T> transport) {
        return null;
    }


}

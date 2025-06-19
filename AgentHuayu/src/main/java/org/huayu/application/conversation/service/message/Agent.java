package org.huayu.application.conversation.service.message;

import dev.langchain4j.data.message.AiMessage;


public interface Agent {
    AiMessage chat(String message);
}

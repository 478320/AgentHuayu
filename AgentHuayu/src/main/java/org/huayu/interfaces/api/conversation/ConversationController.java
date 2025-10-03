package org.huayu.interfaces.api.conversation;

import org.huayu.application.conversation.dto.ChatRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 *
 */
@RestController
@RequestMapping("/conversation")
public class ConversationController {

    private final Logger logger = LoggerFactory.getLogger(ConversationController.class);



}

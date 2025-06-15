package org.huayu.application.agent.service;

import org.huayu.application.conversation.dto.SessionDTO;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class AgentSessionAppService {


    public List<SessionDTO> getAgentSessionList(String userId, String agentId) {
        return null;
    }

    public SessionDTO createSession(String userId, String agentId) {
        return null;
    }

    public void updateSession(String id, String userId, String title) {
        return;
    }

    public void deleteSession(String id, String userId) {
        return;
    }
}

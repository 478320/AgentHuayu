package org.huayu.interfaces.api.portal.agent;

import org.huayu.application.task.service.TaskAppService;
import org.huayu.domain.task.model.TaskAggregate;
import org.huayu.infrastructure.auth.UserContext;
import org.huayu.interfaces.api.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * agent任务管理
 */
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskAppService taskAppService;

    @Autowired
    public TaskController(TaskAppService taskAppService) {
        this.taskAppService = taskAppService;
    }

    /**
     * 获取当前会话的任务
     * @param sessionId 会话id
     */
    @GetMapping("/session/{sessionId}/latest")
    public Result<TaskAggregate> getSessionTasks(@PathVariable String sessionId) {
        String userId = UserContext.getCurrentUserId();
        return Result.success(taskAppService.getCurrentSessionTask(sessionId,userId));
    }
}
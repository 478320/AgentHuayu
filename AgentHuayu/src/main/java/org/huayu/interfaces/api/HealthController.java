package org.huayu.interfaces.api;

import org.huayu.interfaces.api.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping
@RestController
public class HealthController {

    @GetMapping("/health")
    public Result<Object> health() {
        return Result.success().message("ok");
    }
}

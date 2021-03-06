/*
 * Copyright 2017 flow.ci
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flow.platform.api.controller;

import com.flow.platform.api.domain.permission.Actions;
import com.flow.platform.api.domain.request.ThreadConfigParam;
import com.flow.platform.api.security.WebSecurity;
import com.flow.platform.api.service.node.YmlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lhl
 */
@RestController
@RequestMapping(path = "/thread/config")
public class ThreadConfigController {

    @Autowired
    private YmlService ymlService;

    /**
     * @api {post}
     * @apiName thread config
     * @apiGroup Thread
     * @apiDescription set thread config
     *
     * @apiParamExample {json} Request-Body:
     *     {
     *         "maxPoolSize": 1,
     *         "corePoolSize": 1,
     *         "queueSize": 2,
     *         "threadNamePrefix": "git-fetch-task"
     *     }
     *
     *  @apiSuccessExample {json} Success-Response:
     *     HTTP/1.1 200 OK
     *
     */
    @PostMapping
    @WebSecurity(action = Actions.ADMIN_UPDATE)
    public void ymlConfigQueueSize(@RequestBody ThreadConfigParam threadConfigParam) {
        ymlService.threadConfig(threadConfigParam);
    }

}

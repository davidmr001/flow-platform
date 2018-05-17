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

package com.flow.platform.cc.controller;

import com.flow.platform.cc.config.QueueCCConfig;
import com.flow.platform.cc.domain.CmdStatusItem;
import com.flow.platform.cc.service.CmdDispatchService;
import com.flow.platform.cc.service.CmdCCService;
import com.flow.platform.core.exception.IllegalParameterException;
import com.flow.platform.core.exception.IllegalStatusException;
import com.flow.platform.domain.AgentPath;
import com.flow.platform.domain.Cmd;
import com.flow.platform.domain.CmdInfo;
import com.flow.platform.domain.CmdReport;
import com.flow.platform.domain.CmdStatus;
import com.flow.platform.domain.CmdType;
import com.google.common.collect.Range;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Objects;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author gy@fir.im
 */
@RestController
@RequestMapping("/cmd")
public class CmdController {

    @Autowired
    private CmdCCService cmdService;

    @Autowired
    private CmdDispatchService cmdDispatchService;

    @GetMapping(path = "/types")
    public CmdType[] getCmdTypes() {
        return CmdType.values();
    }

    /**
     * For agent report cmd status send to queue
     *
     * @param reportData only need id, status and result
     */
    @PostMapping(path = "/report")
    public void report(@RequestBody CmdReport reportData) {
        if (reportData.getId() == null || reportData.getStatus() == null || reportData.getResult() == null) {
            throw new IllegalParameterException("Cmd id, status and cmd result are required");
        }

        CmdStatusItem statusItem = new CmdStatusItem(reportData, true, true);
        cmdService.updateStatus(statusItem, true);
    }

    /**
     * List commands by agent path
     */
    @PostMapping(path = "/list")
    public Collection<Cmd> list(@RequestBody AgentPath agentPath) {
        return cmdService.listByAgentPath(agentPath);
    }

    /**
     * Upload zipped cmd log with multipart
     *
     * @param cmdId cmd id with text/plain
     * @param file zipped cmd log with application/zip
     */
    @PostMapping(path = "/log/upload")
    public void uploadFullLog(@RequestPart String cmdId, @RequestPart MultipartFile file) {
        if (!Objects.equals(file.getContentType(), "application/zip")) {
            throw new IllegalParameterException("Illegal zipped log file format");
        }
        cmdService.saveLog(cmdId, file);
    }

    /**
     * Get zipped log file by cmd id
     */
    @GetMapping(path = "/log/download", produces = "application/zip")
    public Resource downloadFullLog(@RequestParam String cmdId,
                                    @RequestParam Integer index,
                                    HttpServletResponse httpResponse) {

        Cmd cmd = cmdService.find(cmdId);
        if (cmd == null) {
            throw new IllegalParameterException("Cmd not found");
        }

        try {
            Path filePath = Paths.get(cmd.getLogPath());

            FileSystemResource resource = new FileSystemResource(filePath.toFile());
            httpResponse.setHeader("Content-Disposition",
                String.format("attachment; filename=%s", filePath.getFileName().toString()));
            return resource;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalStatusException("Log not found");
        }
    }
}

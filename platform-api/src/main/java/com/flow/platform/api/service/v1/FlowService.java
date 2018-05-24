/*
 * Copyright 2018 fir.im
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

package com.flow.platform.api.service.v1;

import com.flow.platform.api.domain.Flow;
import com.flow.platform.api.domain.FlowYml;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yang
 */
public interface FlowService {

    /**
     * Create a flow with name
     */
    Flow save(String name);

    /**
     * Get flow instance
     */
    Flow find(String name);

    /**
     * Get flow yml instance
     */
    FlowYml findYml(Flow flow);

    /**
     * Update yml content by flow name
     */
    FlowYml updateYml(Flow flow, String yml);

    /**
     * Delete flow
     */
    Flow delete(String name);

    /**
     * List flows for current user only or all
     */
    List<Flow> list(boolean isOnlyCurrentUser);

    /**
     * List flow ids from name collection
     */
    List<Long> list(Collection<String> names);

    /**
     * Put new context env to flow
     */
    Flow merge(String flow, Map<String, String> newContext);

    /**
     * Remove context items
     */
    Flow remove(String flow, Set<String> keys);
}

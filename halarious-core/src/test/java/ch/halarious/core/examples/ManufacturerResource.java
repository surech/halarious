/*
 * Copyright 2014 Stefan Urech
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *    limitations under the License
 */

package ch.halarious.core.examples;

import ch.halarious.core.HalBaseResource;
import ch.halarious.core.HalLink;

/**
 * Created by surech on 15.01.14.
 */
public class ManufacturerResource extends HalBaseResource {
    @HalLink
    private String self;

    @HalLink
    private String homepage;

    private String name;

    public ManufacturerResource(String self, String homepage, String name) {
        this.self = self;
        this.homepage = homepage;
        this.name = name;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

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

package ch.halarious.core;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author surech
 */
public class TestResource implements HalResource {

    private String nullText = null;

    private String filledText = "test";

    @HalLink(name = "self", title = "Test")
    private String linkText = "link";

    private HalReference reference = new HalReference();

    private HalReference nullReference = null;

    private TestReference inheritedReference = null;

    private List<HalReference> linkList = new ArrayList<>();

    private TestResource resource = null;

    @HalEmbedded(name = "test")
    private List<TestResource> resources = new ArrayList<>();

    @HalEmbedded(name = "children")
    private List<TestChildResource> children = new ArrayList<>();

    public String getNullText() {
        return nullText;
    }

    public String getFilledText() {
        return filledText;
    }

    public String getLinkText() {
        return linkText;
    }

    public HalReference getReference() {
        return reference;
    }

    public HalReference getNullReference() {
        return nullReference;
    }

    public List<HalReference> getLinkList() {
        return linkList;
    }

    public TestReference getInheritedReference() {
        return inheritedReference;
    }

    public TestResource getResource() {
        return resource;
    }

    public List<TestResource> getResources() {
        return resources;
    }

    public List<TestChildResource> getChildren() {
        return children;
    }
}

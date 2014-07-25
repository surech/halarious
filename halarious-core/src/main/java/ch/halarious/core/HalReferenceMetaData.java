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

/**
 * Enthält die Meta-Daten für eine HAL-Referenz. Diese werden mit Reflection aus
 * dem Feld bzw. aus der entsprechenden Annotation ausgelesen.
 *
 * @author surech
 */
public class HalReferenceMetaData {

    /** Name des Feldes. Entspricht dem Namen der Variable */
    private String name = "";

    /** Optionaler Titel */
    private String title = null;

    private Boolean templated = null;

    /**
     * Initialisiert ein HalReferenceMetaData-Objekt
     */
    public HalReferenceMetaData() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean isTemplated() {
        return templated;
    }

    public void setTemplated(Boolean templated) {
        this.templated = templated;
    }

    @Override
    public String toString() {
        return "HalReferenceMetaData{" + "name=" + name + ", title=" + title + ", templated=" + templated + '}';
    }
}

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

import ch.halarious.core.HalEmbedded;
import ch.halarious.core.HalLink;
import ch.halarious.core.HalResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by surech on 15.01.14.
 */
public class ProductResource implements HalResource {
    @HalLink
    private String self;

    private String name;
    private int weight;
    private String description;

    @HalEmbedded(name = "manu")
    private ManufacturerResource manufacturer;

    @HalEmbedded
    private List<ManufacturerResource> manufacturers = new ArrayList<>();

    public ProductResource(){
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

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ManufacturerResource getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(ManufacturerResource manufacturer) {
        this.manufacturer = manufacturer;
    }

    public List<ManufacturerResource> getManufacturers() {
        return manufacturers;
    }

    public void setManufacturers(List<ManufacturerResource> manufacturers) {
        this.manufacturers = manufacturers;
    }
}

package ch.halarious.core.examples;

import ch.halarious.core.HalBaseResource;
import ch.halarious.core.HalLink;
import ch.halarious.core.HalResource;

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

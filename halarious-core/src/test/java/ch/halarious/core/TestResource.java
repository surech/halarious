/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.halarious.core;

import ch.halarious.core.HalReference;
import ch.halarious.core.HalResource;
import ch.halarious.core.HalEmbedded;
import ch.halarious.core.HalLink;
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
}

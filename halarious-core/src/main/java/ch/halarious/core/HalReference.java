/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.halarious.core;

import com.google.gson.annotations.SerializedName;

/**
 * Link zu einer HAL-Resource
 * @author surech
 */
public class HalReference {
    @SerializedName("href")
    private String href;
    
    @SerializedName("title")
    private String title = null;
    
    @SerializedName("templated")
    private Boolean templated = null;

    /**
     * Initialisiert ein HalLink-Objekt
     */
    public HalReference() {
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
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
}

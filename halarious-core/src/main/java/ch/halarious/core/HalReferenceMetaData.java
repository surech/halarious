/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.halarious.core;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * Schliesst alle Felder aus, welche wir selbst verwalten
 * @author surech
 */
public class HalExclusionStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        // Auf Links überprüfen
        if (HalReflectionHelper.isLink(f)) {
            return true;
        }

        // Auf eingebettete Resourcen überprüfen
        if (HalReflectionHelper.isResource(f)) {
            return true;
        }
        
        // Alles in Ordnung, Feld darf verarbeitet werden
        return false;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        // Grundsätzlich akzeptieren wir alle Klassen
        return false;
    }
}

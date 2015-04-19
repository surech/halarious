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

import com.google.gson.FieldAttributes;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Enthält Hilfsfunktionen primär zum Serialisieren und Deserialisieren von
 * HAL-Resourcen
 *
 * @author surech
 */
public class HalReflectionHelper {

    /**
     * Überprüft, ob es sich ein diesem Feld um eine HAL-Resource handelt
     *
     * @param field zu überprüfendes Feld
     * @return <code>true</code>, wenn es eine HAL-Resource ist.
     */
    protected static boolean isResource(Field field) {
        return isResource(new FieldAttributes(field));
    }

    /**
     * Überprüft, ob es sich ein diesem Feld um eine HAL-Resource handelt
     *
     * @param field zu überprüfendes Feld
     * @return <code>true</code>, wenn es eine HAL-Resource ist.
     */
    protected static boolean isResource(FieldAttributes field) {
        if (HalResource.class.isAssignableFrom(field.getDeclaredClass())) {
            return true;
        }
        if (Collection.class.isAssignableFrom(field.getDeclaredClass())) {
            ParameterizedType gType = (ParameterizedType) field.getDeclaredType();
            Type[] actualTypeArguments = gType.getActualTypeArguments();
            if (actualTypeArguments != null && actualTypeArguments.length == 1) {
                if (HalResource.class.isAssignableFrom((Class<?>) actualTypeArguments[0])) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Überprüft, ob es sich beim Feld um eine HAL-Referenz handelt
     *
     * @param field Zu überprüfendes Feld
     * @return <code>true</code> wenn das Feld eine HAL-Referenz beinhaltet
     */
    protected static boolean isLink(Field field) {
        return isLink(new FieldAttributes(field));
    }

    /**
     * Überprüft, ob es sich beim Feld um eine HAL-Referenz handelt
     *
     * @param field Zu überprüfendes Feld
     * @return <code>true</code> wenn das Feld eine HAL-Referenz beinhaltet
     */
    protected static boolean isLink(FieldAttributes field) {
        HalLink annotation = field.getAnnotation(HalLink.class);
        if (annotation != null) {
            return true;
        }
        if (HalReference.class.isAssignableFrom(field.getDeclaredClass())) {
            return true;
        }
        if (Collection.class.isAssignableFrom(field.getDeclaredClass())) {
            ParameterizedType gType = (ParameterizedType) field.getDeclaredType();
            Type[] actualTypeArguments = gType.getActualTypeArguments();
            if (actualTypeArguments != null && actualTypeArguments.length == 1) {
                if (HalReference.class.equals(actualTypeArguments[0])) {
                    return true;
                }
            }
        }
        // Wenn wir hier sind, dann haben wir kein Link
        return false;
    }

    /**
     * Gibt den Namen einer Resource zurück. Diese wird entweder aus dem
     * Feldname oder aus der Annotation ausgelesen
     *
     * @param field Feld mit der Resource
     * @return Name der Resourcse
     */
    protected static String getResourceName(Field field) {
        String name = field.getName();
        HalEmbedded annotation = field.getAnnotation(HalEmbedded.class);
        if (annotation != null) {
            if (annotation.name() != null && !annotation.name().trim().isEmpty()) {
                name = annotation.name();
            }
        }
        return name;
    }

    /**
     * Liest die Meta-Informationen für ein Feld aus. Diese werden mit
     * Reflection entweder direkt aus dem Feld oder aus einer Annotation
     * gelesen.
     *
     * @param field Feld, für welches die Meta-Informationen ausgelesen werden
     * soll
     * @return Ausgelesene Metainformationen
     */
    protected static HalReferenceMetaData getReferenceMetaData(Field field) {
        HalReferenceMetaData meta = new HalReferenceMetaData();
        meta.setName(field.getName());
        HalLink annotation = field.getAnnotation(HalLink.class);
        if (annotation != null) {
            if (annotation.name() != null && !annotation.name().trim().isEmpty()) {
                meta.setName(annotation.name());
            }

            if(annotation.title() != null && !annotation.title().isEmpty()){
                meta.setTitle(annotation.title());
            }
        }
        return meta;
    }

    /**
     * Schreibt einen Wert in ein Feld
     *
     * @param target Objekt, in welches der Wert geschrieben werden soll
     * @param field Zu schreibendes Feld
     * @param value Zu schreibender Wert
     * @throws HalDeserializingException Wird ausgelöst, wenn das Feld nicht
     * geschrieben werden konnte.
     */
    protected static void setValue(Object target, Field field, Object value) throws HalDeserializingException {
        try {
            // Wir müssen auf das Feld zugreifen können
            field.setAccessible(true);

            // Wert setzen
            field.set(target, value);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            // Eigene Exception werfen
            throw new HalDeserializingException("Fehler beim Schreiben eines Feldes", ex);
        }
    }

    /**
     * Gibt den Wert eines bestimmten Feldes zurück
     *
     * @param field Feld
     * @param t Objekt, aus welchem der Wert gelesen werden soll
     * @return Ausgelesener Wert. <code>null</code>, wenn auf das Feld nicht
     * zugegriffen werden konnte.
     */
    protected static Object getValue(Field field, HalResource t) {
        return getValue(field, t, null);
    }

    /**
     * Gibt den Wert eines bestimmten Feldes zurück
     *
     * @param field Feld
     * @param t Objekt, aus welchem der Wert gelesen werden soll
     * @param defaultValue Wird zurückgegeben, wenn das Feld nicht gefunden
     * werden konnte.
     * @return Ausgelesener Wert
     */
    protected static Object getValue(Field field, HalResource t, Object defaultValue) {
        try {
            // Wir müssen auf das Feld zugreifen können
            boolean accessible = field.isAccessible();
            if (!accessible) {
                field.setAccessible(true);
            }
            // Wert auslesen
            Object value = field.get(t);

            // Wenn nötig die Berechtigung wieder zurückstellen
            if (!accessible) {
                field.setAccessible(false);
            }
            return value;
        } catch (Exception ex) {
            // Default-Wert zurückgeben
            return null;
        }
    }

    /**
     * Gibt alle Felder einer Klasse und deren Basisklassen zurück
     *
     * @param type Type der Klasse, deren Felder gesucht werden sollen
     * @return Liste mit allen gefundenen Felder
     */
    public static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }
}

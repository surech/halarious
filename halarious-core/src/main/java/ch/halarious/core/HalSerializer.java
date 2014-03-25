/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.halarious.core;

import com.google.gson.*;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Adapter zum Serialisieren von HAL-Resourcen nach Json.
 *
 * @author surech
 */
public class HalSerializer implements JsonSerializer<HalResource> {

    @Override
    public JsonElement serialize(HalResource t, Type type, JsonSerializationContext jsc) {
        JsonObject element = (JsonObject) jsc.serialize(t);

        // Gefundene Links und einbettete Resourcen
        Map<String, List<HalReference>> links = new HashMap<>();
        Map<String, List<HalResource>> embedded = new HashMap<>();

        // Wir merken uns, bei welchen Feldern es sich um Listen handelt. Dies wird benötigt, um
        // die später im JSON korrekt abzubilden
        Set<String> embeddedLists = new HashSet<>();

        Class<? extends HalResource> hal = t.getClass();
        List<Field> fields = HalReflectionHelper.getAllFields(hal);
        for (Field field : fields) {
            // Ist es ein Link?
            if (HalReflectionHelper.isLink(field)) {
                // Metadaten auslesen
                HalReferenceMetaData metaData = HalReflectionHelper.getReferenceMetaData(field);

                // Hinterlegtes Objekt auslesen
                Object value = HalReflectionHelper.getValue(field, t);

                // Je nach Typ geht es weiter
                dispatchLink(value, metaData, links);
            }

            // Ist es eine Resource?
            if (HalReflectionHelper.isResource(field)) {
                // Name der Resouce auslesen
                String resName = HalReflectionHelper.getResourceName(field);

                // Hinterlegtes Objekt auslesen
                Object value = HalReflectionHelper.getValue(field, t);

                // Je nach Typ geht es weiter
                readResource(value, resName, embedded, embeddedLists);
            }
        }

        // Die Links und eingebettete Resourcen ans JSON anhängen
        attachLinksToJson(element, links, jsc);
        attachEmbeddedToJson(element, embedded, embeddedLists, jsc);

        return element;
    }

    /**
     * Erstellt auf Grund des Typ eine HAL-Referenz
     *
     * @param value Enthält in irgendeiner Form die Referenz
     * @param metaData Meta-Daten zur Referenz
     * @param links Liste mit den Links
     */
    protected void dispatchLink(Object value, HalReferenceMetaData metaData, Map<String, List<HalReference>> links) {
        // Wenn der Wert null ist, können wir auch nichts machen
        if (value == null) {
            return;
        }

        // Je nach Typ des Objektes gehen wir weiter
        if (value instanceof HalReference) {
            readLink((HalReference) value, metaData, links);
        } else if (value instanceof Collection) {
            readLinkCollection((Collection<?>) value, metaData, links);
        } else {
            readLinkObject(value, metaData, links);
        }
    }

    /**
     * Übernimmt eine HAL-Referenz in die Liste der Links
     *
     * @param halReference HAL-Referenz
     * @param metaData Meta-Daten zur Referenz
     * @param links Liste mit den Links
     */
    protected void readLink(HalReference halReference, HalReferenceMetaData metaData, Map<String, List<HalReference>> links) {
        List<HalReference> refs = links.get(metaData.getName());
        if (refs == null) {
            refs = new ArrayList<>();
            links.put(metaData.getName(), refs);
        }

        refs.add(halReference);
    }

    /**
     * Liest eine Liste von HAL-Referenzen ein
     *
     * @param halReferences Liste von HAL-Referenzen
     * @param metaData Meta-Daten zur Referenz
     * @param links Liste mit den Links
     */
    protected void readLinkCollection(Collection<?> halReferences, HalReferenceMetaData metaData, Map<String, List<HalReference>> links) {
        // Über die Liste iterieren und je nach Typ weitergehen
        for (Object o : halReferences) {
            dispatchLink(o, metaData, links);
        }
    }

    /**
     * Verarbeiteten einen Link in Form einen unbekannten Objektes.
     *
     * @param value Enthält in irgendeiner Form die Referenz
     * @param metaData Meta-Daten zur Referenz
     * @param links Liste mit den Links
     */
    protected void readLinkObject(Object value, HalReferenceMetaData metaData, Map<String, List<HalReference>> links) {
        // Referenz erstellen
        HalReference ref = new HalReference();
        ref.setHref(String.valueOf(value));
        ref.setTitle(metaData.getTitle());
        ref.setTemplated(metaData.isTemplated());
        readLink(ref, metaData, links);
    }

    /**
     * Liest eine Resource aus einem Feld aus
     *
     * @param value Wert des Feldes
     * @param resName Name der Resource
     * @param embedded Liste mit den Resourcen
     * @param embeddedLists Hier wird vermerkt, wenn es sich beim Feld um eine Liste handelt
     */
    protected void readResource(Object value, String resName, Map<String, List<HalResource>> embedded, Set<String> embeddedLists) {
        // Wenn der Wert null ist, können wir nichts machen
        if (value == null) {
            return;
        }

        // Handelt es sich direkt um eine Resource?
        if (value instanceof HalResource) {
            List<HalResource> list = embedded.get(resName);
            if (list == null) {
                list = new ArrayList<>();
                embedded.put(resName, list);
            }
            list.add((HalResource) value);
        } else if (value instanceof Collection) {
            // Es handelt sich um eine Collection
            embeddedLists.add(resName);
            for (Object v : ((Collection) value)) {
                readResource(v, resName, embedded, embeddedLists);
            }
        }
    }

    /**
     * Fügt die Links in das JSON-Objekt ein
     *
     * @param element JSON-Objekt, in welches die Link eingefügt werden sollen
     * @param links Map mit den Links
     * @param jsc Context für die JSON-Serialisierung
     */
    private void attachLinksToJson(JsonObject element, Map<String, List<HalReference>> links, JsonSerializationContext jsc) {
        // Wenn wir keine Links haben, müssen wir auch nichts machen
        if (links.isEmpty()) {
            return;
        }

        // Root-Node für die Links erstellen
        JsonObject linkRoot = new JsonObject();
        element.add(HalConstants.LINK_ROOT, linkRoot);

        // Durch die Links iterieren
        for (String key : links.keySet()) {
            // Werte auslesen und überprüfen
            List<HalReference> list = links.get(key);
            if (list == null || list.isEmpty()) {
                continue;
            }

            // Zwischen einem und mehreren Einträge unterscheiden
            if (list.size() == 1) {
                HalReference ref = list.get(0);
                JsonElement json = jsc.serialize(ref);
                linkRoot.add(key, json);
            } else {
                JsonArray array = new JsonArray();
                for (HalReference ref : list) {
                    JsonElement json = jsc.serialize(ref);
                    array.add(json);
                }
                linkRoot.add(key, array);
            }
        }
    }

    private void attachEmbeddedToJson(JsonObject element, Map<String, List<HalResource>> embedded, Set<String> embeddedLists, JsonSerializationContext jsc) {
        // Wenn wir keine Resourcen haben, müssen wir auch nichts machen
        if (embedded.isEmpty()) {
            return;
        }

        // Root-Node für die eingebetteten Resourcen
        JsonObject root = new JsonObject();
        element.add(HalConstants.EMBEDDED_ROOT, root);

        // Durch die Resourcen iterieren
        for (String key : embedded.keySet()) {
            // Werte auslesen und überprüfen
            List<HalResource> list = embedded.get(key);
            if (list == null || list.isEmpty()) {
                continue;
            }

            // Zwischen einem und mehreren Einträgen unterscheiden
            if(list.size() > 1 || embeddedLists.contains(key)){
                JsonArray array = new JsonArray();
                for (HalResource res : list) {
                    JsonElement json = jsc.serialize(res, HalResource.class);
                    array.add(json);
                }
                root.add(key, array);
            } else {
                HalResource res = list.get(0);
                JsonElement json = jsc.serialize(res, HalResource.class);
                root.add(key, json);
            }
        }
    }
}

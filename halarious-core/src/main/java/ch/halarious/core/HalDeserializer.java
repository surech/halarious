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

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Adapter zum Deserialisieren von HAL-Resourcen nach Json.
 *
 * @author surech
 */
public class HalDeserializer implements JsonDeserializer<HalResource> {

    /**
     * Typ, den wir eigentlich deserialisieren sollten
     */
    private final Class<?> targetType;

    /**
     * Intialisiert ein HalDeserializer-Objekt
     *
     * @param targetType Typ, den wir eigentlich deserialisieren sollten
     */
    public HalDeserializer(Class<?> targetType) {
        this.targetType = targetType;
    }

    @Override
    public HalResource deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return deserialize(json, typeOfT, context, this.targetType);
    }

    public HalResource deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context, Class<?> targetType) throws JsonParseException {
        // Das Objekt mal von Gson deserialiseren lassen
        HalResource result = context.deserialize(json, targetType);

        // Es handelt sich um ein JSON-Objekt.
        JsonObject jsonObject = json.getAsJsonObject();
        // Nodes mit den Links und den eingebetteten Resourcen auslesen
        JsonObject linkRoot = jsonObject.getAsJsonObject(HalConstants.LINK_ROOT);
        JsonObject embeddedRoot = jsonObject.getAsJsonObject(HalConstants.EMBEDDED_ROOT);

        // Die Felder nach Links und eingebetteten Resourcen absuchen
        List<Field> fields = HalReflectionHelper.getAllFields(targetType);
        for (Field field : fields) {
            // Ist es ein Link?
            if (HalReflectionHelper.isLink(field) && linkRoot != null) {
                // Metadaten auslesen
                HalReferenceMetaData metaData = HalReflectionHelper.getReferenceMetaData(field);

                // Im JSON danach suchen
                JsonElement jsonField = linkRoot.get(metaData.getName());

                // Wenn im JSON kein Feld gefunden wurde, müssen wir auch nichts machen
                if (jsonField == null) {
                    continue;
                }

                // Wert aus dem Json zurück in das Objekt schreiben
                writeLink(result, field, jsonField, context);
            }

            // Ist es eine Resource?
            if (HalReflectionHelper.isResource(field) && embeddedRoot != null) {
                // Name der Resouce auslesen
                String resName = HalReflectionHelper.getResourceName(field);

                // Im JSON danach suchen
                JsonElement jsonField = embeddedRoot.get(resName);

                // Wenn im JSON kein entsprechendes Feld gefunden wurde, können wir auch nichts machen
                if (jsonField == null) {
                    continue;
                }

                // Werte aus dem Json zurück ins Objekt schreiben
                writeResource(result, field, jsonField, context);
            }
        }

        return result;
    }

    protected void writeLink(HalResource result, Field field, JsonElement jsonField, JsonDeserializationContext context) {
        // Einfacher Fall: Das Feld ist ein String
        if (field.getType().equals(String.class)) {
            if (jsonField.isJsonObject()) {
                writeLinkInString(result, field, jsonField, context);
            } else if (jsonField.isJsonArray()) {
                // Wenn das Array nur ein Element hat, dann können wir es auch verarbeiten
                JsonArray jsonArray = jsonField.getAsJsonArray();
                if (jsonArray.size() == 1) {
                    writeLinkInString(result, field, jsonArray.get(0), context);
                }
            }
        } else if (HalReference.class.isAssignableFrom(field.getType())) {
            if (jsonField.isJsonObject()) {
                writeLinkInReference(result, field, jsonField, context);
            } else if (jsonField.isJsonArray()) {
                // Wenn das Array nur ein Element hat, können wir es auch verarbeiten
                JsonArray jsonArray = jsonField.getAsJsonArray();
                if (jsonArray.size() == 1) {
                    writeLinkInReference(result, field, jsonArray.get(0), context);
                }
            }
        } else if (Collection.class.isAssignableFrom(field.getType())) {
            // Wir erwarten ein JSON-Array
            if (jsonField.isJsonArray()) {
                // Collection auslesen
                Collection collection = (Collection) HalReflectionHelper.getValue(field, result);
                if (collection == null) {
                    throw new HalDeserializingException("Collection is null; no values can be added");
                }

                JsonArray jsonArray = jsonField.getAsJsonArray();
                Iterator<JsonElement> iterator = jsonArray.iterator();

                while (iterator.hasNext()) {
                    JsonElement element = iterator.next();
                    writeLinkInCollection(collection, element, context);
                }
            }
        }
    }

    private void writeResource(HalResource result, Field field, JsonElement jsonField, JsonDeserializationContext context) {
        if (HalResource.class.isAssignableFrom(field.getType())) {
            if (jsonField.isJsonObject()) {
                writeEmeddedInResource(result, field, jsonField, context);
            } else if (jsonField.isJsonArray()) {
                // Wenn das Array nur ein Element hat, dann können wir es verarbeiten
                JsonArray jsonArray = jsonField.getAsJsonArray();
                if (jsonArray.size() == 1) {
                    writeEmeddedInResource(result, field, jsonArray.get(0), context);
                }
            }
        } else if (Collection.class.isAssignableFrom(field.getType())) {
            // Wir erwarten ein JSON-Array
            if (jsonField.isJsonArray()) {
                // Collection auslesen
                Collection collection = (Collection) HalReflectionHelper.getValue(field, result);

                // Generischer Type auslesen
                ParameterizedType gType = (ParameterizedType) field.getGenericType();
                Type[] actualTypeArguments = gType.getActualTypeArguments();

                if (collection == null) {
                    throw new HalDeserializingException("Collection is null; no values can be added");
                }

                JsonArray jsonArray = jsonField.getAsJsonArray();
                Iterator<JsonElement> iterator = jsonArray.iterator();

                while (iterator.hasNext()) {
                    JsonElement element = iterator.next();
                    writeEmbeddedInCollection(collection, actualTypeArguments[0], element, context);
                }
            }
        }
    }

    /**
     * Schreibt eine Referenz in einen einfachen String
     *
     * @param result Hal-Resource, in welcher sich die Referenz befindet
     * @param field Zu schreibendes Feld
     * @param jsonField JSON-Feld, welches die Referenz enthält
     * @param context GSON-Context für die Deserialisierung
     */
    private void writeLinkInString(HalResource result, Field field, JsonElement jsonField, JsonDeserializationContext context) {
        // Wir haben ein einzelne Objekt, das wir nun Deserialisieren
        HalReference halRef = context.deserialize(jsonField, HalReference.class);
        // Wert schreiben
        String href = halRef.getHref();
        HalReflectionHelper.setValue(result, field, href);
    }

    /**
     * Schreibt eine Referenz direkt ein eine {@link HalReference}
     *
     * @param result Hal-Resource, in welcher sich die Referenz befindet
     * @param field Zu schreibendes Feld
     * @param jsonField JSON-Feld, welches die Referenz enthält
     * @param context GSON-Context für die Deserialisierung
     */
    protected void writeLinkInReference(HalResource result, Field field, JsonElement jsonField, JsonDeserializationContext context) {
        Object reference = context.deserialize(jsonField, field.getType());
        HalReflectionHelper.setValue(result, field, reference);
    }

    /**
     * Schreibt eine Referenz in eine Collection aus {@link HalReference}s
     *
     * @param collection Collection, in welche die Referenz geschrieben werden
     * soll
     * @param element JSON-Element mit der Referenz
     * @param context GSON-Context für die Deserialisierung
     */
    protected void writeLinkInCollection(Collection collection, JsonElement element, JsonDeserializationContext context) {
        // Wir haben ein einzelne Objekt, das wir nun Deserialisieren
        HalReference halRef = context.deserialize(element, HalReference.class);

        // In die Liste einfügen
        collection.add(halRef);
    }

    /**
     * Schreibt eine eingebettete Resource direkt ein eine {@link HalResource}
     *
     * @param result Hal-Resource, in welcher sich die eingebettete Resource
     * befindet
     * @param field Zu schreibendes Feld
     * @param jsonField JSON-Feld, welches die Resource enthält
     * @param context GSON-Context für die Deserialisierung
     */
    protected void writeEmeddedInResource(HalResource result, Field field, JsonElement jsonField, JsonDeserializationContext context) {
        // Objekt deserialisieren
        Object resource = deserialize(jsonField, field.getType(), context, field.getType());
        HalReflectionHelper.setValue(result, field, resource);
    }

    /**
     * Schreibt eine Resourcce in eine Collection
     *
     * @param collection Collection, in welche die Resource geschrieben werden
     * soll
     * @param element JSON-Element mit der Resource
     * @param context GSON-Context für die Deserialisierung
     */
    protected void writeEmbeddedInCollection(Collection collection, Type genericType, JsonElement element, JsonDeserializationContext context) {
        // Wir haben ein einzelne Objekt, das wir nun Deserialisieren
        Object halRef = deserialize(element, genericType, context, (Class<?>) genericType);

        // In die Liste einfügen
        collection.add(halRef);
    }
}

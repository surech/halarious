/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.halarious.core;

import ch.halarious.core.HalExclusionStrategy;
import ch.halarious.core.HalResource;
import ch.halarious.core.HalSerializer;
import ch.halarious.core.HalDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test für die Klasse {@link HalDeserializer}
 *
 * @author surech
 */
public class HalDeserializerTest {
    
    private Gson gson;
    
    @Before
    public void before() {
        // GSON erstellen
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(HalResource.class, new HalSerializer());
        builder.registerTypeAdapter(HalResource.class, new HalDeserializer(TestResource.class));
        builder.setExclusionStrategies(new HalExclusionStrategy());
        this.gson = builder.create();
    }

    /**
     * Einfache Referenz als String
     */
    @Test
    public void testStringRef() {
        String json = "{ \"_links\": { \"self\": { \"href\": \"/path/3141\" } }, \"filledText\": \"Ein Text\" }";
        TestResource result = (TestResource) gson.fromJson(json, HalResource.class);

        // Überprüfen
        Assert.assertEquals("/path/3141", result.getLinkText());
        Assert.assertEquals("Ein Text", result.getFilledText());
    }

    /**
     * Array mit genau einem String als Referenz
     */
    @Test
    public void testAtomStringArrayRef() {
        String json = "{ \"_links\": { \"self\": [{ \"href\": \"/path/3141\" }] }, \"filledText\": \"Ein Text\" } ";
        TestResource result = (TestResource) gson.fromJson(json, HalResource.class);

        // Überprüfen
        Assert.assertEquals("/path/3141", result.getLinkText());
        Assert.assertEquals("Ein Text", result.getFilledText());
    }

    @Test
    public void testHalReference() {
        String json = "{ \"_links\": { \"reference\": { \"href\": \"/path/3141\", \"title\": \"Ein Titel\" } }, \"filledText\": \"Ein Text\" }";
        TestResource result = (TestResource) gson.fromJson(json, HalResource.class);

        // Überprüfen
        Assert.assertEquals("/path/3141", result.getReference().getHref());
        Assert.assertEquals("Ein Titel", result.getReference().getTitle());
        Assert.assertEquals("Ein Text", result.getFilledText());
    }

    @Test
    public void testHalReferenceCollection() {
        String json = "{ \"_links\":{ \"reference\":{ \"href\":\"/path/3141\", \"title\":\"Ein Titel\" }, \"linkList\":[ { \"href\":\"/link/452\" }, { \"href\":\"/link/832\", \"title\":\"Noch ein Link\" } ] }, \"filledText\":\"Ein Text\" } ";
        TestResource result = (TestResource) gson.fromJson(json, HalResource.class);

        // Überprüfen
        Assert.assertEquals("/path/3141", result.getReference().getHref());
        Assert.assertEquals("Ein Titel", result.getReference().getTitle());
        Assert.assertEquals("Ein Text", result.getFilledText());
        Assert.assertEquals(2, result.getLinkList().size());
        Assert.assertEquals("/link/452", result.getLinkList().get(0).getHref());
        Assert.assertEquals("/link/832", result.getLinkList().get(1).getHref());
        Assert.assertEquals("Noch ein Link", result.getLinkList().get(1).getTitle());
    }

    @Test
    public void testHalResourceSingle() {
        String json = "{ \"_links\":{ \"reference\":{ \"href\":\"/path/3141\", \"title\":\"Ein Titel\" } }, \"_embedded\":{ \"resource\":{ \"filledText\":\"Inneres Objekt\" } }, \"filledText\":\"Ein Text\" } \n";
        TestResource result = (TestResource) gson.fromJson(json, HalResource.class);

        // Überprüfen
        Assert.assertEquals("Ein Text", result.getFilledText());
        Assert.assertEquals("Inneres Objekt", result.getResource().getFilledText());
    }

    @Test
    public void testHalResourceCollection() {
        String json = "{ \"_links\":{ \"reference\":{ \"href\":\"/path/3141\", \"title\":\"Ein Titel\" } }, \"_embedded\":{ \"resource\":{ \"filledText\":\"Inneres Objekt\" }, \"test\":[ { \"_links\":{ \"self\": { \"href\": \"/path/2717\" } }, \"filledText\":\"Erstes Objekt\" }, { \"filledText\":\"Zweites Objekt\" } ] }, \"filledText\":\"Ein Text\" } ";
        TestResource result = (TestResource) gson.fromJson(json, HalResource.class);

        // Überprüfen
        Assert.assertEquals("Ein Text", result.getFilledText());
        Assert.assertEquals("Inneres Objekt", result.getResource().getFilledText());
        Assert.assertEquals(2, result.getResources().size());
        Assert.assertEquals("/path/2717", result.getResources().get(0).getLinkText());
        Assert.assertEquals("Erstes Objekt", result.getResources().get(0).getFilledText());
        Assert.assertEquals("Zweites Objekt", result.getResources().get(1).getFilledText());
    }
}

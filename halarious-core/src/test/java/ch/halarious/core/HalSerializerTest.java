/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.halarious.core;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * Test für die Klasse {@link HalSerializer}
 *
 * @author surech
 */
public class HalSerializerTest {

    private HalSerializer adapter;

    @Before
    public void before() {
        // Adapter vor jedem Test neue erstellen
        adapter = new HalSerializer();
    }

    @Test
    public void testDispatchLinkHalReferenceAnn() {
        // Testdaten erstellen
        HalReference ref = new HalReference();
        ref.setHref("/rest");
        ref.setTitle("Test");
        Map<String, List<HalReference>> links = new HashMap<>();
        HalReferenceMetaData metaData = new HalReferenceMetaData();
        metaData.setName("self");

        // Test ausführen
        adapter.dispatchLink(ref, metaData, links);

        // Überprüfen
        Assert.assertEquals(1, links.size());
        List<HalReference> link = links.get("self");
        Assert.assertNotNull(link);
        Assert.assertEquals(1, link.size());
        Assert.assertEquals(ref.getTitle(), link.get(0).getTitle());
    }

    @Test
    public void testDispatchLinkCollection() {
        // Testdaten erstellen
        List<String> refs = new ArrayList<>();
        refs.add("test");
        refs.add("gugus");

        Map<String, List<HalReference>> links = new HashMap<>();
        HalReferenceMetaData metaData = new HalReferenceMetaData();
        metaData.setName("self");

        // Test ausführen
        adapter.dispatchLink(refs, metaData, links);

        // Überprüfen
        Assert.assertEquals(1, links.size());
        List<HalReference> link = links.get("self");
        Assert.assertNotNull(link);
        Assert.assertEquals(2, link.size());
        Assert.assertEquals("test", link.get(0).getHref());
        Assert.assertEquals("gugus", link.get(1).getHref());
    }

    @Test
    public void testReadResource() {
        // Testdaten erstellen
        Map<String, List<HalResource>> embedded = new HashMap<>();
        Set<String> embeddedLists = new HashSet<>();
        TestResource res = new TestResource();

        // Test ausführen
        adapter.readResource(res, "test", embedded, embeddedLists);

        // Überprüfen
        Assert.assertEquals(1, embedded.size());
        List<HalResource> list = embedded.get("test");
        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(embeddedLists.isEmpty());
    }

    @Test
    public void testReadResourceList() {
        // Testdaten erstellen
        Map<String, List<HalResource>> embedded = new HashMap<>();
        Set<String> embeddedLists = new HashSet<>();
        List<TestResource> res = new ArrayList<>();
        res.add(new TestResource());
        res.add(new TestResource());

        // Test ausführen
        adapter.readResource(res, "test", embedded, embeddedLists);

        // Überprüfen
        Assert.assertEquals(1, embedded.size());
        List<HalResource> list = embedded.get("test");
        Assert.assertNotNull(list);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(1, embeddedLists.size());
        Assert.assertTrue(embeddedLists.contains("test"));
    }
}

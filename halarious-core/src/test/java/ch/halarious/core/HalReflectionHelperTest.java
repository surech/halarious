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

import junit.framework.Assert;
import org.junit.Test;

import java.lang.reflect.Field;

/**
 * Test für die Klasse {@link HalReflectionHelper}
 *
 * @author surech
 */
public class HalReflectionHelperTest {

    @Test
    public void testIsLinkSimpleField() throws Exception {
        testIsLink("filledText", false);
    }

    @Test
    public void testIsLinkAnnotation() throws Exception {
        testIsLink("linkText", true);
    }

    @Test
    public void testIsLinkReference() throws Exception {
        testIsLink("reference", true);
    }

    @Test
    public void testIsLinkList() throws Exception {
        testIsLink("linkList", true);
    }

    @Test
    public void testIsLinkInherited() throws Exception {
        testIsLink("inheritedReference", true);
    }

    private void testIsLink(String fieldName, boolean expected) throws Exception {
        // Testdaten erstellen
        Field field = TestResource.class.getDeclaredField(fieldName);

        // Test ausführen
        boolean result = HalReflectionHelper.isLink(field);

        // Überprüfen
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testIsResources() throws Exception {
        testIsResource("resource", true);
    }

    @Test
    public void testIsResourcesNo() throws Exception {
        testIsResource("reference", false);
    }

    @Test
    public void testIsResourcesList() throws Exception {
        testIsResource("resources", true);
    }

    @Test
    public void testIsResourcesNoList() throws Exception {
        testIsResource("linkList", false);
    }

    private void testIsResource(String fieldName, boolean expected) throws Exception {
        // Testdaten erstellen
        Field field = TestResource.class.getDeclaredField(fieldName);

        // Test ausführen
        boolean result = HalReflectionHelper.isResource(field);

        // Überprüfen
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testGetMetaDataNoAnnotation() throws Exception {
        // Test ausführen
        HalReferenceMetaData metaData = getMetaData("filledText");

        // Überprüfen
        Assert.assertEquals("filledText", metaData.getName());
        Assert.assertNull(metaData.getTitle());
    }

    @Test
    public void testGetMetaData() throws Exception {
        // Test ausführen
        HalReferenceMetaData metaData = getMetaData("linkText");

        // Überprüfen
        Assert.assertEquals("self", metaData.getName());
        Assert.assertEquals("Test", metaData.getTitle());
    }

    private HalReferenceMetaData getMetaData(String fieldName) throws Exception {
        // Testdaten erstellen
        TestResource resource = new TestResource();
        Field field = resource.getClass().getDeclaredField(fieldName);

        // Test ausführen
        return HalReflectionHelper.getReferenceMetaData(field);
    }

    @Test
    public void testGetResourceName() throws Exception {
        // Testdaten erstellen
        TestResource resource = new TestResource();
        Field field = resource.getClass().getDeclaredField("resource");

        // Test ausführen
        String name = HalReflectionHelper.getResourceName(field);

        // Überprüfen
        Assert.assertEquals("resource", name);
    }

    @Test
    public void testGetResourceNameAnnotation() throws Exception {
        // Testdaten erstellen
        TestResource resource = new TestResource();
        Field field = resource.getClass().getDeclaredField("resources");

        // Test ausführen
        String name = HalReflectionHelper.getResourceName(field);

        // Überprüfen
        Assert.assertEquals("test", name);
    }
}

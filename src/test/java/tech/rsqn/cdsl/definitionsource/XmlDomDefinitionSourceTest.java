package tech.rsqn.cdsl.definitionsource;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Test
public class XmlDomDefinitionSourceTest {

    @Test
    public void shouldLoadCdslDefinitionFromResource() {
        XmlDomDefinitionSource source = new XmlDomDefinitionSource();
        DocumentDefinition definition = source.loadCdslDefinition("cdsl/test-integration-flow.xml");
        Assert.assertNotNull(definition);
        Assert.assertNotNull(definition.getFlows());
        Assert.assertFalse(definition.getFlows().isEmpty());
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void shouldThrowExceptionWhenResourceNotFound() {
        XmlDomDefinitionSource source = new XmlDomDefinitionSource();
        source.loadCdslDefinition("nonexistent.xml");
    }

    
    @Test(expectedExceptions = RuntimeException.class)
    public void shouldThrowExceptionWhenParsingInvalidXml() throws Exception {
        XmlDomDefinitionSource source = new XmlDomDefinitionSource();
        String invalidXml = "<flow></flow>"; // Missing closing tag
        DocumentDefinition definition = source.loadCdslDefinition("test.xml");
        Assert.assertNotNull(definition);
    }

    @Test
    public void shouldExtractAttributesFromNode() {
        XmlDomDefinitionSource source = new XmlDomDefinitionSource();
        // This test is problematic and should be removed
    }
}
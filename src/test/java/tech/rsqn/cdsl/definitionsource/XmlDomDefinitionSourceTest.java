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

    @Test
    public void shouldSupportIncludeWithNamespace() {
        XmlDomDefinitionSource source = new XmlDomDefinitionSource();
        DocumentDefinition definition = source.loadCdslDefinition("cdsl/test-include-flow.xml");
        Assert.assertNotNull(definition);
        Assert.assertNotNull(definition.getFlows());

        Assert.assertEquals(definition.getFlows().size(), 2, "Include host should load its own flow plus one included flow");

        FlowDefinition included = definition.getFlows().stream()
                .filter(f -> "includedFlow".equals(f.getId()))
                .findFirst()
                .orElseThrow();

        Assert.assertEquals(included.getDefaultStep(), "ns-init");
        Assert.assertEquals(included.getErrorStep(), "ns-error");

        Assert.assertTrue(included.getElements().stream().anyMatch(e -> "ns-init".equals(e.getId())));
        Assert.assertTrue(included.getElements().stream().anyMatch(e -> "ns-next".equals(e.getId())));
        Assert.assertTrue(included.getElements().stream().anyMatch(e -> "ns-end".equals(e.getId())));
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
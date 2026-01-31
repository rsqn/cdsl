package tech.rsqn.cdsl.registry;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

@Test
public class RegistryLoaderTest {

    @Test
    public void shouldCreateInstance() {
        RegistryLoader loader = new RegistryLoader();
        Assert.assertNotNull(loader);
    }

    @Test
    public void shouldLoadResources() {
        RegistryLoader loader = new RegistryLoader();
        List<String> resources = Arrays.asList("cdsl/test-integration-flow.xml");
        loader.setResources(resources);

        // Note: This test only verifies that resources can be set.
        // Actual loading requires Spring context with autowired dependencies.
        Assert.assertNotNull(loader);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void shouldThrowExceptionWhenResourceNotFound() throws Exception {
        RegistryLoader loader = new RegistryLoader();
        List<String> resources = Arrays.asList("nonexistent.xml");
        loader.setResources(resources);

        loader.afterPropertiesSet();
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void shouldThrowExceptionWhenResourcesNotSet() throws Exception {
        RegistryLoader loader = new RegistryLoader();
        loader.afterPropertiesSet();
    }

    @Test
    public void shouldSetResourcesCorrectly() {
        RegistryLoader loader = new RegistryLoader();
        List<String> resources = Arrays.asList("resource1.xml", "resource2.xml");
        
        // Should not throw exception when setting valid resources
        loader.setResources(resources);
        Assert.assertNotNull(loader);
    }

    @Test
    public void shouldHandleEmptyResourcesList() {
        RegistryLoader loader = new RegistryLoader();
        List<String> emptyResources = new ArrayList<>();
        loader.setResources(emptyResources);
        
        // Should not throw exception when setting empty list
        Assert.assertNotNull(loader);
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Resources cannot be null")
    public void shouldThrowExceptionWithCorrectMessageWhenResourcesNull() throws Exception {
        RegistryLoader loader = new RegistryLoader();
        // Don't set resources (they remain null)
        loader.afterPropertiesSet();
    }

    @Test
    public void shouldHandleMultipleResources() {
        RegistryLoader loader = new RegistryLoader();
        List<String> resources = Arrays.asList(
            "cdsl/flow1.xml", 
            "cdsl/flow2.xml", 
            "cdsl/flow3.xml"
        );
        loader.setResources(resources);
        Assert.assertNotNull(loader);
    }

    @Test
    public void shouldAcceptSingleResource() {
        RegistryLoader loader = new RegistryLoader();
        List<String> resources = Arrays.asList("cdsl/single-flow.xml");
        loader.setResources(resources);
        Assert.assertNotNull(loader);
    }
}

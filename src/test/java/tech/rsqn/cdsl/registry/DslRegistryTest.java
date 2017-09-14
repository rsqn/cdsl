package tech.rsqn.cdsl.registry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import tech.rsqn.cdsl.dsl.Dsl;
import tech.rsqn.cdsl.dsl.DslMetadata;


@Test
@ContextConfiguration(locations = {"classpath:/spring/test-registry-ctx.xml"})
public class DslRegistryTest extends AbstractTestNGSpringContextTests {

    @Autowired
    DslRegistry dslRegistry;

    @Test
    public void shouldDiscoverDslComponentsInClassPathAndResolveByName() throws Exception {
        DslMetadata meta = new DslMetadata();
        meta.setResolutionStrategy(DslMetadata.ResolutionStrategy.ByName);
        meta.setName("if-this-test");

        Dsl ifThen = dslRegistry.resolve(meta);
        Assert.assertNotNull(ifThen);
    }


    @Test
    public void shouldPopulateDslModel() throws Exception {

        Assert.assertTrue(false);
    }

    @Test
    public void shouldHonourSpringProtoType() throws Exception {
        DslMetadata meta = new DslMetadata();
        meta.setResolutionStrategy(DslMetadata.ResolutionStrategy.ByName);
        meta.setName("if-this-test-proto");

        Dsl e = dslRegistry.resolve(meta);
        Dsl e2 = dslRegistry.resolve(meta);

        Assert.assertNotEquals(e.hashCode(), e2.hashCode());
    }

    @Test
    public void shouldHonourSpringSingleton() throws Exception {
        DslMetadata meta = new DslMetadata();
        meta.setResolutionStrategy(DslMetadata.ResolutionStrategy.ByName);
        meta.setName("if-this-test");

        Dsl e = dslRegistry.resolve(meta);
        Dsl e2 = dslRegistry.resolve(meta);
        Assert.assertEquals(e, e2);
    }
}

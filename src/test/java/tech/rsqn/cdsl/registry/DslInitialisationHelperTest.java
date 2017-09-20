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
public class DslInitialisationHelperTest extends AbstractTestNGSpringContextTests {

    @Autowired
    DslInitialisationHelper dslInitialisationHelper;

    @Test
    public void shouldDiscoverDslComponentsInClassPathAndResolveByName() throws Exception {
        DslMetadata meta = new DslMetadata();
        meta.setResolutionStrategy(DslMetadata.ResolutionStrategy.ByName);
        meta.setName("if-this-test");

        Dsl ifThen = dslInitialisationHelper.resolve(meta);
        Assert.assertNotNull(ifThen);
    }

    @Test
    public void shouldHonourSpringProtoType() throws Exception {
        DslMetadata meta = new DslMetadata();
        meta.setResolutionStrategy(DslMetadata.ResolutionStrategy.ByName);
        meta.setName("if-this-test-proto");

        Dsl e = dslInitialisationHelper.resolve(meta);
        Dsl e2 = dslInitialisationHelper.resolve(meta);

        Assert.assertNotEquals(e.hashCode(), e2.hashCode());
    }

    @Test
    public void shouldHonourSpringSingleton() throws Exception {
        DslMetadata meta = new DslMetadata();
        meta.setResolutionStrategy(DslMetadata.ResolutionStrategy.ByName);
        meta.setName("if-this-test");

        Dsl e = dslInitialisationHelper.resolve(meta);
        Dsl e2 = dslInitialisationHelper.resolve(meta);
        Assert.assertEquals(e, e2);
    }
}

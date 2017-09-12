package tech.rsqn.cdsl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import tech.rsqn.cdsl.dsl.Dsl;
import tech.rsqn.cdsl.dsl.IfThen;
import tech.rsqn.cdsl.registry.DslRegistry;


@Test
@ContextConfiguration(locations = {"classpath:/spring/test-registry-ctx.xml"})
public class DslRegistryTest extends AbstractTestNGSpringContextTests {

    @Autowired
    DslRegistry dslRegistry;

    @Test
    public void shouldDiscoverDslComponentsInClassPathAndResolveByClass() throws Exception {
        Dsl e = dslRegistry.getDslBean(IfThen.class);
        Assert.assertNotNull(e);
    }

    @Test
    public void shouldDiscoverDslComponentsInClassPathAndResolveByName() throws Exception {
        Dsl ifThen = dslRegistry.getDslBean("if-this-test");
        Assert.assertNotNull(ifThen);
    }

    @Test
    public void shouldHonourSpringProtoType() throws Exception {
        Dsl e = dslRegistry.getDslBean("if-this-test-proto");
        Dsl e2 = dslRegistry.getDslBean("if-this-test-proto");

        Assert.assertNotEquals(e.hashCode(), e2.hashCode());
    }

    @Test
    public void shouldHonourSpringSingleton() throws Exception {
        Dsl e = dslRegistry.getDslBean("if-this-test");
        Dsl e2 = dslRegistry.getDslBean("if-this-test");
        Assert.assertEquals(e, e2);
    }
}

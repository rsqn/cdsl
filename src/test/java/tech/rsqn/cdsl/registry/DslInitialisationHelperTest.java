package tech.rsqn.cdsl.registry;

import org.testng.Assert;
import org.testng.annotations.Test;
import tech.rsqn.cdsl.annotations.CdslDef;
import tech.rsqn.cdsl.dsl.Dsl;
import tech.rsqn.cdsl.dsl.DslMetadata;
import tech.rsqn.cdsl.definitionsource.ElementDefinition;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslRuntime;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

@Test
public class DslInitialisationHelperTest {

    @Test
    public void shouldCreateInstanceWithEmptyInjections() {
        DslInitialisationHelper helper = new DslInitialisationHelper();
        Assert.assertNotNull(helper);
        Assert.assertNull(helper.resolveInjected("nonExistentDsl"));
    }

    @Test
    public void shouldInjectAndResolveDsl() {
        DslInitialisationHelper helper = new DslInitialisationHelper();
        Dsl testDsl = new TestDsl();
        helper.inject("testDsl", testDsl);

        Dsl resolvedDsl = helper.resolveInjected("testDsl");
        Assert.assertNotNull(resolvedDsl);
        Assert.assertEquals(resolvedDsl, testDsl);
    }

    @Test
    public void shouldBuildMetadataForDslElement() {
        DslInitialisationHelper helper = new DslInitialisationHelper();
        ElementDefinition element = new ElementDefinition();
        element.setName("testElement");
        
        // Inject a test DSL so it can be found
        Dsl testDsl = new TestDsl();
        helper.inject("testElement", testDsl);

        DslMetadata metadata = helper.buildMetadataForDslElement(new ElementDefinition(), element);
        Assert.assertNotNull(metadata);
        Assert.assertEquals(metadata.getName(), "testElement");
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void shouldThrowExceptionWhenNoDslFound() {
        DslInitialisationHelper helper = new DslInitialisationHelper();
        ElementDefinition element = new ElementDefinition();
        element.setName("nonExistentElement");

        helper.buildMetadataForDslElement(new ElementDefinition(), element);
    }

    @Test
    public void shouldValidateDslMetadata() {
        DslInitialisationHelper helper = new DslInitialisationHelper();
        Dsl testDsl = new TestDsl();
        helper.inject("testDsl", testDsl);
        
        DslMetadata metadata = new DslMetadata();
        metadata.setName("testDsl");
        metadata.setResolutionStrategy(DslMetadata.ResolutionStrategy.ByName);

        // This should not throw an exception if validation passes
        helper.validate(metadata);
    }

    @CdslDef("testElement")
    private static class TestDsl implements Dsl {
        @Override
        public CdslOutputEvent execute(CdslRuntime runtime, CdslContext ctx, Object model, CdslInputEvent input) throws CdslException {
            return null;
        }
    }
}

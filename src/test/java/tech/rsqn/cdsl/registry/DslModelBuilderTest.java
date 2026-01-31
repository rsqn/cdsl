package tech.rsqn.cdsl.registry;

import org.testng.Assert;
import org.testng.annotations.Test;
import tech.rsqn.cdsl.definitionsource.ElementDefinition;
import tech.rsqn.cdsl.dsl.MapModel;

@Test
public class DslModelBuilderTest {

    @Test
    public void shouldBuildMapModel() {
        DslModelBuilder builder = new DslModelBuilder();
        ElementDefinition element = new ElementDefinition();
        element.getAttrs().put("key1", "value1");
        element.getAttrs().put("key2", "value2");

        MapModel model = builder.buildModel(element, MapModel.class);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getMap().size(), 2);
        Assert.assertEquals(model.getMap().get("key1"), "value1");
        Assert.assertEquals(model.getMap().get("key2"), "value2");
    }

    @Test
    public void shouldBuildModelWithAttributes() {
        DslModelBuilder builder = new DslModelBuilder();
        ElementDefinition element = new ElementDefinition();
        element.getAttrs().put("name", "testName");
        element.getAttrs().put("value", "testValue");

        TestModel model = builder.buildModel(element, TestModel.class);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getName(), "testName");
        Assert.assertEquals(model.getValue(), "testValue");
    }

    @Test
    public void shouldBuildModelWithChildren() {
        DslModelBuilder builder = new DslModelBuilder();
        ElementDefinition parent = new ElementDefinition();
        parent.setName("parent");

        ElementDefinition child = new ElementDefinition();
        child.setName("child");
        child.setTextValue("childValue");

        parent.getChildren().add(child);

        TestModel model = builder.buildModel(parent, TestModel.class);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getChild(), "childValue");
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void shouldThrowExceptionWhenModelClassCannotBeInstantiated() {
        DslModelBuilder builder = new DslModelBuilder();
        ElementDefinition element = new ElementDefinition();

        builder.buildModel(element, AbstractModel.class);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void shouldThrowExceptionWhenFieldNotFound() {
        DslModelBuilder builder = new DslModelBuilder();
        ElementDefinition element = new ElementDefinition();
        element.getAttrs().put("nonExistentField", "value");

        builder.buildModel(element, TestModel.class);
    }

    public static class TestModel {
        private String name;
        private String value;
        private String child;

        public TestModel() {
            // Public constructor required for reflection
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getChild() {
            return child;
        }

        public void setChild(String child) {
            this.child = child;
        }
    }

    private static abstract class AbstractModel {
        // Abstract class cannot be instantiated
    }
}
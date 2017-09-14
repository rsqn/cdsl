package tech.rsqn.cdsl.definitionsource;

import org.testng.Assert;
import org.testng.annotations.Test;
import tech.rsqn.cdsl.model.definition.DocumentDefinition;
import tech.rsqn.cdsl.model.definition.ElementDefinition;
import tech.rsqn.cdsl.model.definition.FlowDefinition;

public class XmlDomDefinitionSourceTest {

    @Test
    public void shouldLoadFlow() throws Exception {
        XmlDomDefinitionSource parser = new XmlDomDefinitionSource();

        DocumentDefinition doc = parser.loadCdslDefinition("/cdsl/test-integration-flow.xml");

        Assert.assertNotNull(doc);
        Assert.assertNotNull(doc.getFlows());
        Assert.assertEquals(doc.getFlows().size(), 1);

        FlowDefinition flow = doc.getFlows().get(0);

        Assert.assertEquals(flow.getId(), "test-flow-a");
    }

    @Test
    public void shouldLoadFlowWithElements() throws Exception {
        XmlDomDefinitionSource parser = new XmlDomDefinitionSource();
        DocumentDefinition doc = parser.loadCdslDefinition("/cdsl/test-integration-flow.xml");

        FlowDefinition flow = doc.getFlows().get(0);
        Assert.assertEquals(flow.getId(), "test-flow-a");

        Assert.assertTrue(flow.getElements().size() > 1);
    }

    @Test
    public void shouldAssignFirstLevelElementsAsFlowStepsAndSecondLevelAsDslElements() throws Exception {
        XmlDomDefinitionSource parser = new XmlDomDefinitionSource();
        DocumentDefinition doc = parser.loadCdslDefinition("/cdsl/test-integration-flow.xml");

        FlowDefinition flow = doc.getFlows().get(0);

        Assert.assertEquals(flow.getElements().get(0).getClassifier(), ElementDefinition.Classifier.FlowStep);
        Assert.assertEquals(flow.getElements().get(0).getChildren().get(0).getClassifier(), ElementDefinition.Classifier.DslElement);

    }

}

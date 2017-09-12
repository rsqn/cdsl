package tech.rsqn.cdsl.parser;

import org.testng.Assert;
import org.testng.annotations.Test;
import tech.rsqn.cdsl.model.DocumentDefinition;
import tech.rsqn.cdsl.model.FlowDefinition;

public class XStreamParserTest {

    @Test
    public void shouldLoadFlow() throws Exception {
        XStreamParser parser = new XStreamParser();

        DocumentDefinition doc = parser.loadCdslDefinition("/cdsl/test-a-flow.xml");

        Assert.assertNotNull(doc);
        Assert.assertNotNull(doc.getFlows());
        Assert.assertEquals(doc.getFlows().size(), 1);

        FlowDefinition flow = doc.getFlows().get(0);

        Assert.assertEquals(flow.getName(), "test-flow-a");
    }

    @Test
    public void shouldLoadFlowWithElements() throws Exception {
        XStreamParser parser = new XStreamParser();
        DocumentDefinition doc = parser.loadCdslDefinition("/cdsl/test-a-flow.xml");

        FlowDefinition flow = doc.getFlows().get(0);
        Assert.assertEquals(flow.getName(), "test-flow-a");

        Assert.assertTrue(flow.getElements().size() > 1);
    }

}

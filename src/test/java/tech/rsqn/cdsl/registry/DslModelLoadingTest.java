package tech.rsqn.cdsl.registry;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@Test
@ContextConfiguration(locations = {"classpath:/spring/test-registry-integration-ctx.xml"})
public class DslModelLoadingTest extends AbstractTestNGSpringContextTests {

    @Autowired
    FlowRegistry flowRegistry;

    @Autowired
    DslInitialisationHelper dslInitialisationHelper;

//    @Test
//    public void shouldPopulateDslModel() throws Exception {
//        DslMetadata<SayHelloModel> meta = dslInitialisationHelper.resolveMeta("sayHello");
//        Assert.assertNotNull(meta);
//
//        SayHelloModel model = meta.getModel();
//
//        Assert.assertNotNull(model);
//        Assert.assertEquals(model.getName(),"fred");
//    }
//
//    @Test
//    public void shouldPopulateComplexDslModel() throws Exception {
//        DslMetadata<ComplexModel> meta = dslInitialisationHelper.resolveMeta("dslWithComplexModel");
//        Assert.assertNotNull(meta);
//        ComplexModel model = meta.getModel();
//        Assert.assertNotNull(model);
//
//        Assert.assertEquals(model.getaModelProperty(),"testProperty");
//        Assert.assertNotNull(model.getModelChild());
//        Assert.assertEquals(model.getModelChild().getName(),"testChildName");
//        Assert.assertNotNull(model.getModelChild().getaList());
//        Assert.assertEquals(model.getModelChild().getaList().size(),2);
//
//
//    }
//
//     <dslWithComplexModel aModelProperty="testProperty">
//                <modelChild>
//                    <name>testChildName</name>
//                    <aList>
//                        <entry>firstValue</entry>
//                        <entry>secondValue</entry>
//                    </aList>
//                </modelChild>
//            </dslWithComplexModel>
}

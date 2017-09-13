package tech.rsqn.cdsl.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import tech.rsqn.cdsl.model.definition.DocumentDefinition;
import tech.rsqn.cdsl.model.definition.FlowDefinition;
import tech.rsqn.cdsl.definitionsource.XmlDomDefinitionSource;

import java.util.List;

public class FlowRegistryLoader implements InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(FlowRegistryLoader.class);
    private List<String> resources;

    @Autowired
    private XmlDomDefinitionSource definitionSource;
    @Autowired
    private FlowRegistry registry;

    @Required
    public void setResources(List<String> resources) {
        this.resources = resources;
    }

    private void load() {
        try {
            logger.info("Loading FlowRegistry - start");
            for (String resource : resources) {
                DocumentDefinition def = definitionSource.loadCdslDefinition(resource);
                for (FlowDefinition flowDefinition : def.getFlows()) {
                    registry.submitDefinition(flowDefinition);
                }
            }
            logger.info("Loading FlowRegistry - end");
        } catch ( Exception ex ) {
            logger.error("Loading FlowRegistry - failed");
            throw new RuntimeException("Error loading flow registry " + ex.getMessage(),ex);
        }
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        load();
    }
}

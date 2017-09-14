package tech.rsqn.cdsl.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import tech.rsqn.cdsl.annotations.CdslDef;
import tech.rsqn.cdsl.annotations.CdslModel;
import tech.rsqn.cdsl.dsl.Dsl;
import tech.rsqn.cdsl.dsl.DslMetadata;
import tech.rsqn.cdsl.model.definition.ElementDefinition;

import java.util.HashMap;
import java.util.Map;

@Service
public class DslRegistry implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(FlowRegistry.class);

    private ApplicationContext applicationContext;
//    private Map<String, DslMetadata> registry;
    private DslModelBuilder modelBuilder;

    public DslRegistry() {
//        registry = new HashMap<>();
        modelBuilder = new DslModelBuilder();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    public DslMetadata registerDslDefinition(ElementDefinition flowStep, ElementDefinition element) {
        // find and object for this
        Dsl dsl = (Dsl) applicationContext.getBean(element.getName());
        DslMetadata.ResolutionStrategy resolutionStrategy = null;

        if (dsl != null) {
            logger.info("DSL (" + element.getName() + ") resolved to a named spring bean " + dsl.getClass().getName());
            resolutionStrategy = DslMetadata.ResolutionStrategy.ByName;
        } else {
            Map<String, Object> beans = applicationContext.getBeansWithAnnotation(CdslDef.class);

            for (Object o : beans.values()) {
                if (!(o instanceof Dsl)) {
                    throw new RuntimeException("Bean found with CdslDef annotation that does not implement Dsl " + o.getClass());
                }

                dsl = (Dsl) o;
                CdslDef def = dsl.getClass().getAnnotation(CdslDef.class);
                if (element.getName().equals(def.value())) {
                    logger.info("DSL (" + element.getName() + ") resolved to an annotated class " + dsl.getClass().getName());
                    resolutionStrategy = DslMetadata.ResolutionStrategy.ByType;
                } else {
                    dsl = null;
                }
            }
        }

        if ( dsl == null ) {
            throw new RuntimeException("No DSL bean found that matches elementName " + element.getName());
        }

        DslMetadata meta = resolveMeta(dsl);
        meta.setResolutionStrategy(resolutionStrategy);

        if (meta.getModelCls() != null) {
            Object model = modelBuilder.buildModel(element, meta.getModelCls());
            meta.setModel(model);
        }
        return meta;
    }

    private DslMetadata resolveMeta(Dsl dsl) {
        CdslDef def = dsl.getClass().getAnnotation(CdslDef.class);
        CdslModel modelAttr = dsl.getClass().getAnnotation(CdslModel.class);

        DslMetadata ret = new DslMetadata();
        ret.setCls(dsl.getClass());
        ret.setName(def.value());

        if (modelAttr != null) {
            ret.setModelCls(modelAttr.value());
            logger.info("Model for " + dsl.getClass().getName() + " is " + modelAttr.value().getName());
        } else {
            logger.info("No model defined for " + dsl.getClass());
        }

        return ret;
    }
//    public DslMetadata resolveMeta(String n) {
//        return registry.get(n);
//    }

    /**
     * Spring is used to determine whether this is a singleton or prototype
     *
     * @param meta
     * @return
     */
    public Dsl resolve(DslMetadata meta) {
        if (meta.getResolutionStrategy().equals(DslMetadata.ResolutionStrategy.ByName)) {
            return (Dsl) applicationContext.getBean(meta.getName());
        }
        return (Dsl) applicationContext.getBean(meta.getCls());
    }
}

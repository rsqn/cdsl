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
    private Map<String, DslMetadata> registry;
    private DslModelBuilder modelBuilder;

    public DslRegistry() {
        registry = new HashMap<>();
        modelBuilder = new DslModelBuilder();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    public DslMetadata registerDslDefinition(ElementDefinition flowStep, ElementDefinition def) {
        // find and object for this
        Dsl dsl = resolveUnconfiguredDslBean(def.getName());
        DslMetadata meta = resolveMeta(dsl);

        if (meta.getModelCls() != null) {
            Object model = modelBuilder.buildModel(def, meta.getModelCls());
            meta.setModel(model);
        }

        registry.put(meta.getName(), meta);
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

    private Dsl resolveUnconfiguredDslBean(String elementName) {
        Dsl ret = (Dsl) applicationContext.getBean(elementName);

        if (ret != null) {
            logger.info("DSL (" + elementName + ") resolved to a named spring bean " + ret.getClass().getName());
            return ret;
        }

        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(CdslDef.class);

        for (Object o : beans.values()) {
            if (!(o instanceof Dsl)) {
                throw new RuntimeException("Bean found with CdslDef annotation that does not implement Dsl " + o.getClass());
            }

            Dsl dsl = (Dsl) o;
            CdslDef def = dsl.getClass().getAnnotation(CdslDef.class);

            if (elementName.equals(def.value())) {
                logger.info("DSL (" + elementName + ") resolved to an annotated class " + dsl.getClass().getName());
                ret = dsl;
                return ret;
            }
        }

        throw new RuntimeException("No DSL bean found that matches elementName " + elementName);
    }

    public DslMetadata resolveMeta(String n) {
        return registry.get(n);
    }

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

package tech.rsqn.cdsl.registry;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import tech.rsqn.cdsl.dsl.Dsl;

@Service
public class DslRegistry implements ApplicationContextAware {
    private ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * Spring is used to determine whether this is a singleton or prototype
     * @param name
     * @return
     */
    public Dsl getDslBean(String name) {
        return (Dsl)applicationContext.getBean(name);
    }

    public Dsl getDslBean(Class t) {
        return (Dsl)applicationContext.getBean(t);
    }
}

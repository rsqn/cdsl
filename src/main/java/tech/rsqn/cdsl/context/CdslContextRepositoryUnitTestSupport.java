package tech.rsqn.cdsl.context;

import java.util.HashMap;
import java.util.Map;

public class CdslContextRepositoryUnitTestSupport implements CdslContextRepository {

    private Map<String,CdslContext> contexts = new HashMap<>();

    public Map<String, CdslContext> getContexts() {
        return contexts;
    }

    @Override
    public CdslContext getContext(String contextId) {
        return contexts.get(contextId);
    }

    public void saveContext(CdslContext context) {
        contexts.put(context.getId(),context);
    }
}

package tech.rsqn.cdsl.context;

import java.util.HashMap;
import java.util.Map;

public class CdslContextRepositoryUnitTestSupport implements CdslContextRepository {

    private Map<String,CdslContext> contexts = new HashMap<>();

    @Override
    public CdslContext getContext(String contextId) {
        return contexts.get(contextId);
    }

    public void saveContext(CdslContext context) {
        contexts.put(context.getId(),context);
    }
}

package tech.rsqn.cdsl.context;

import org.springframework.beans.BeanUtils;
import tech.rsqn.cdsl.exceptions.CdslException;

import java.util.HashMap;
import java.util.Map;

public class CdslContextRepositoryUnitTestSupport implements CdslContextRepository {

    private Map<String, CdslContext> contexts = new HashMap<>();

    private Map<String, String> openTransactions = new HashMap<>();

    public Map<String, CdslContext> getContexts() {
        return contexts;
    }

    @Override
    public CdslContext getContext(String contextId) {
        return contexts.get(contextId);
    }

    @Override
    public CdslContext getContext(String transactionId, String contextId) {
        CdslContext context = contexts.get(contextId);

        if (context != null) {
            CdslContext shallowCopy = new CdslContext();
            BeanUtils.copyProperties(context, shallowCopy);
            openTransactions.put(contextId, transactionId);
            return shallowCopy;
        } else {
            return null;
        }

    }

    public void saveContext(String transactionId, CdslContext context) {
        String openTxn = openTransactions.get(context.getId());
        if (openTxn != null) {
            if (openTxn.equals(transactionId)) {
                contexts.put(context.getId(), context);
                openTransactions.remove(context.getId());
            } else {
                throw new CdslException("mismatched transaction id " + transactionId + " vs open txn " + openTxn);
            }
        } else {
            contexts.put(context.getId(), context);
        }
    }
    
    public int deleteContext(String contextId) {
        if ( contexts.containsKey(contextId)) {
            contexts.remove(contextId);
            return 1;
        } 
        return 0;
    }
}

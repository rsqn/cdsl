package tech.rsqn.cdsl.context;


/**
 * Repository interface for contexts - locking should happen outside this repository
 */
public interface CdslContextRepository {

    CdslContext getContext(String contextId);

    void saveContext(CdslContext contextId);
}

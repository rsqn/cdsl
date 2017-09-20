package tech.rsqn.cdsl.dsl;

import tech.rsqn.cdsl.exceptions.CdslException;

public interface ValidatingDsl<T> {
    void validate(T model) throws CdslException;
}

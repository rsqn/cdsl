package tech.rsqn.cdsl.dsl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tech.rsqn.cdsl.annotations.CdslDef;
import tech.rsqn.cdsl.annotations.CdslModel;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslRuntime;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.exceptions.CdslValidationException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CdslDef("whiteList")
@CdslModel(WhitelistEventsModel.class)
@Component
public class WhitelistEvents extends DslSupport<WhitelistEventsModel,Serializable> implements ValidatingDsl<WhitelistEventsModel> {
    private static final Logger logger = LoggerFactory.getLogger(WhitelistEvents.class);

    @Override
    public void validate(WhitelistEventsModel model) throws CdslException {
        if (StringUtils.isEmpty(model.getNames())) {
            throw new CdslValidationException("Names may not be null");
        }
    }

    @Override
    public CdslOutputEvent execSupport(CdslRuntime runtime, CdslContext ctx, WhitelistEventsModel model, CdslInputEvent input) throws CdslException {
        List<String> whiteList;

        whiteList = Stream.of(model.getNames().split(","))
                .map((in) -> in.trim())
                .collect(Collectors.toList());

        if (whiteList.contains(input.getName())) {
            logger.debug("whitelist is empty - allowing all inputs");
            return null;
        }

        logger.debug("whitelist is not empty - validating input event " + input.getName() + " against " + whiteList);

        CdslOutputEvent.Action action = CdslOutputEvent.Action.valueOf(model.getOnFailure());

        if (CdslOutputEvent.Action.Route == action) {
            return new CdslOutputEvent().withRoute(model.getTo());
        } else {
            logger.debug("Defaulting to Reject");
            return new CdslOutputEvent().reject();
        }
    }
}

# CDSL Testing Guide

This guide shows how to **mock DSLs entirely** in tests and how to **force the state machine to start (or continue) at a specific step**. All examples are taken from the CDSL test suite.

---

## 1. Mocking DSLs Entirely

You can replace flow steps by name or swap in a custom `Dsl` implementation so the real DSL never runs.

### A. `dslHelper.inject(name, Dsl)` — replace a flow step by name

Inject a lambda (or any `Dsl`) so that when the flow runs a step whose element has that **name**, your implementation runs instead of the real DSL. The flow XML stays unchanged; only the runtime behaviour is replaced.

**Example: inject a no-op and assert post-commit runs**

```java
dslHelper.inject("injectedOne", (runtime, ctx, model, input) -> {
    runtime.postCommit(() -> {
        ctr.incrementAndGet();
    });
    return null;
});
executor.execute(flow, new CdslInputEvent().with("test", "message").andModel(inputModel));
```

**Example: inject behaviour that mutates context (and assert auditing)**

```java
dslHelper.inject("injectedOne", (runtime, ctx, model, input) -> {
    ctx.putVar("testChange", "testValue");
    return null;
});
executor.execute(flow, new CdslInputEvent().with("test", "message").andModel(inputModel));
Assert.assertTrue(testAuditor.didLogEvent("setVar/testChange"));
```

**Example: inject a no-op so the flow runs to end, then re-execute with same context (expect exception)**

```java
dslHelper.inject("injectedOne", (runtime, ctx, model, input) -> {
    return null;
});
CdslOutputEvent out = executor.execute(flow, new CdslInputEvent().with("test", "message").andModel(inputModel));
// Second call with same context (state is End) → CdslException
executor.execute(flow, new CdslInputEvent().with("test", "message").andModel(inputModel).andContextId(out.getContextId()));
```

**Example: inject routing to an invalid step (expect exception)**

```java
dslHelper.inject("injectedOne", (runtime, ctx, model, input) -> {
    return new CdslOutputEvent().withRoute("wrong-turn");
});
executor.execute(flow, new CdslInputEvent().with("test", "message").andModel(inputModel));
```

**Example: inject multiple named DSLs and assert output values**

```java
dslHelper.inject("injectedOne", (runtime, ctx, model, input) -> {
    runtime.emit(new CdslOutputValue().withName("one").andValue("one"));
    return null;
});
dslHelper.inject("injectedTwo", (runtime, ctx, model, input) -> {
    runtime.emit(new CdslOutputValue().withName("two").andValue("two"));
    return null;
});
CdslFlowOutputEvent output = executor.execute(flow, new CdslInputEvent().with("test", "message").andModel(""));
Assert.assertEquals(output.getOutputValues().size(), 2);
```

So you can mock **entire steps by element name** (e.g. `"injectedOne"`) without changing the flow definition.

### B. `StaticDslTestSupport.staticDsl` — static mock for one DSL

When the flow uses the `static-test-support` DSL, assign a single static `Dsl` and that runs instead of the real implementation.

**Example: assert model is populated from flow attributes/children**

```java
StaticDslTestSupport.staticDsl = new Dsl<MapModel, Serializable>() {
    @Override
    public CdslOutputEvent execute(CdslRuntime runtime, CdslContext ctx, MapModel model, CdslInputEvent<Serializable> input) throws CdslException {
        attrResource.set(model.getMap().get("attrOne"));
        elemResource.set(model.getMap().get("elemOne"));
        return null;
    }
};
executor.execute(flow, new CdslInputEvent().with("test", "message"));
Assert.assertEquals("attrOneValue", attrResource.get());
Assert.assertEquals("elemOneValue", elemResource.get());
```

So you can mock that DSL entirely by setting `StaticDslTestSupport.staticDsl` before execution.

### C. `DslTestSupport.withDsl(Dsl)` — unit-level DSL mock (no flow)

For testing a **single** DSL in isolation (no FlowExecutor, no flow XML), use `DslTestSupport`: it delegates to a `Dsl` you provide. Good for testing one DSL’s behaviour in a unit test.

**Example: route**

```java
supp.withDsl((runtime, ctx, model, input) -> {
    return new CdslOutputEvent().withRoute("stageTwo");
});
CdslOutputEvent out = supp.execute(runtime, ctx, null, new CdslInputEvent());
Assert.assertEquals(out.getNextRoute(), "stageTwo");
Assert.assertEquals(out.getAction(), CdslOutputEvent.Action.Route);
```

**Example: modify context**

```java
supp.withDsl((runtime, ctx, model, input) -> {
    ctx.putVar("myVar", "wasSet");
    return null;
});
supp.execute(runtime, ctx, null, new CdslInputEvent());
Assert.assertEquals(ctx.fetchVar("myVar"), "wasSet");
```

**Example: await**

```java
supp.withDsl((runtime, ctx, model, input) -> {
    return new CdslOutputEvent().awaitInputAt("awaitHere");
});
CdslOutputEvent out = supp.execute(runtime, ctx, null, new CdslInputEvent());
Assert.assertEquals(out.getAction(), CdslOutputEvent.Action.Await);
Assert.assertEquals(out.getNextRoute(), "awaitHere");
```

**Summary**

| Goal | How |
|------|-----|
| Mock a DSL used **in a flow** (by element name) | `dslHelper.inject("elementName", (runtime, ctx, model, input) -> { ... return eventOrNull; })` |
| Mock the **static-test-support** DSL in a flow | Set `StaticDslTestSupport.staticDsl = yourDsl` before `executor.execute(...)` |
| Mock a **single DSL in isolation** (no flow) | `new DslTestSupport().withDsl(yourDsl).execute(runtime, ctx, model, input)` |

---

## 2. Forcing the State Machine to Start (or Continue) Where You Want

The executor always runs from the context’s **current step** (or the **requested step** on the input event, if set). Tests use two patterns.

### A. Resume from an existing context — `andContextId(contextId)`

If you pass an existing **context id**, the executor loads that context and continues from **its** `currentStep` (and state). So “start here” means: run the flow once (e.g. until it reaches Await), then pass the same `contextId` on the next event; execution continues from that step.

**Example: run flow to await, then continue from same context**

```java
Flow flow = flowRegistry.getFlow("shouldRouteThroughAThenBThenAwaitAtC");
CdslOutputEvent output = executor.execute(flow, new CdslInputEvent().with("test", "message"));

CdslContext context = contextRepository.getContext(output.getContextId());
Assert.assertEquals(context.getCurrentStep(), "c");
Assert.assertEquals(context.getState(), CdslContext.State.Await);

// Continue from that context
executor.execute(flow, new CdslInputEvent().with("test", "message").andContextId(output.getContextId()));

context = contextRepository.getContext(output.getContextId());
Assert.assertEquals(context.getCurrentStep(), "end");
Assert.assertEquals(context.getState(), CdslContext.State.End);
```

So: **force “start” at a given step** by first running until that step (or until await), then using **`.andContextId(output.getContextId())`** on the next `CdslInputEvent`.

### B. Force start at a specific step — pre-seat context or use `andStep()`

The executor uses `context.getCurrentStep()` when resuming (or the flow’s default step when it’s empty), and **`inputEvent.getRequestedStep()`** to override the next step if present. So you can force the run to “start” at a given step in two ways:

1. **Pre-seat a context** (with the test support repository): create a `CdslContext`, set `id`, `currentFlow`, `currentStep` (and optionally `state`), put it in the repository (e.g. `contextRepository.getContexts().put(id, context)`), then call `executor.execute(flow, new CdslInputEvent().with(...).andContextId(thatContextId))`. The executor will load that context and run from that `currentStep` (see `FlowExecutor` around 183–196).
2. **Use requested step on the event**: `CdslInputEvent` has **`andStep(stepId)`**; the executor uses it to set the next step (see `FlowExecutor` 199–206). So you can start (or jump) at an arbitrary step with  
   `new CdslInputEvent().with("test", "message").andStep("yourStepId")`  
   and, if you need a specific existing context, also `.andContextId(contextId)`.

**Example: event API for requested step** (from `CdslInputEventTest`)

```java
CdslInputEvent<String> event = new CdslInputEvent<>().andStep("testStep");
Assert.assertEquals(event.getRequestedStep(), "testStep");
```

There is no `FlowExecutorTest` that uses `andStep()` with the executor yet, but the executor supports it: when `requestedStep` is set, it overrides the current step for that execution.

**Summary**

| Goal | How |
|------|-----|
| **Resume** / “start” from where a run left off | Run once, then `executor.execute(flow, event.andContextId(output.getContextId()))` |
| **Force start at a specific step** | Pre-put a context with `setCurrentStep(stepId)` and use `andContextId(id)`, or use `event.andStep("stepId")` (and optionally `andContextId` for an existing context). |

---

## Test support beans

For integration-style tests (e.g. `FlowExecutorTest`), use:

- **`CdslContextRepositoryUnitTestSupport`** — in-memory context store; use `getContexts()` to inspect or pre-seat contexts.
- **`CdslContextAuditorUnitTestSupport`** — captures audit events; use `didLogEvent(...)` and `clear()`.
- **`LockProviderUnitTestSupport`** — in-memory locking; use `onLockCallback` / `onUnLockCallback` to observe lock behaviour.
- **`DslInitialisationHelper`** — use `inject(name, dsl)` to replace DSLs by name.

See `src/test/resources/spring/test-registry-integration-ctx.xml` and `FlowExecutorTest` for wiring and examples.

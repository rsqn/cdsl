# CDSL — Contextual Domain-Specific Language Framework

CDSL is a **Java framework for defining and executing stateful business flows** using a custom DSL. You describe flows in XML (steps and DSL elements), implement the DSL elements as Java classes, and the framework runs the flow, manages context (state, variables), handles locking, and persists context between invocations. It is built on **Spring** and is designed to minimise glue code.

---

## What It Does (Summary)

- **Flows**: You define flows in XML. Each flow has an id, a default step, an optional error step, and a list of steps. Each step contains DSL elements (e.g. `setVar`, `routeTo`, `sayHello`) that run in order.
- **Execution**: You call `FlowExecutor.execute(flow, inputEvent)`. The executor loads or creates a `CdslContext`, runs the current step’s DSL elements, and reacts to their return value: **Route** (go to another step), **Await** (pause until next input), **End** (finish), **Reject** (reject input).
- **Context**: A `CdslContext` holds flow/step position, state (Undefined, Alive, Await, End, Error), and key/value variables. Context is locked during execution and persisted via `CdslContextRepository`.
- **DSL elements**: Each XML element (e.g. `<setVar name="x" val="y"/>`) is mapped to a Java class that implements `Dsl<ModelType, InputModelType>`. The framework builds a model from the XML (attributes/children) and calls `execute(runtime, context, model, input)`.

So: **XML defines the flow graph and step content; Java implements each DSL “verb”; the framework runs steps, manages context, and controls routing.**

---

## Core Concepts

| Concept | Description |
|--------|-------------|
| **Flow** | A named workflow with a default step, optional error step, and a set of steps. Loaded from XML and registered in `FlowRegistry`. |
| **Step** | A named node in a flow. Contains **logic** DSL elements (run in order until one returns an output) and optional **finally** DSL elements (run after logic; their output overrides). |
| **DSL element** | One “verb” in the flow (e.g. `setVar`, `routeTo`). Implemented by a class that implements `Dsl<T, MT>`, annotated with `@CdslDef("elementName")` and optionally `@CdslModel(MyModel.class)`. |
| **Model** | POJO (or `MapModel`) populated from the XML element’s attributes and child elements. Passed into the DSL’s `execute(..., model, ...)`. |
| **Context** | Per-execution state: context id, current flow/step, context state (Undefined, Alive, Await, End, Error), variables (`vars`), transition history. Persisted by `CdslContextRepository`. |
| **Runtime** | Per-request object (`CdslRuntime`) holding transaction id, auditor, post-step/post-commit tasks, and output values emitted by DSLs. |
| **Input event** | `CdslInputEvent<T>`: optional `contextId` (resume), `requestedStep`, `name`, `source`, and a typed `model` for this request. |
| **Output event** | DSLs return `CdslOutputEvent` with an **Action**: Route, Await, End, Reject, or Continue (null = continue to next DSL in the step). The executor uses this to move to the next step or pause. |

---

## Package Layout (for AI / navigation)

```
tech.rsqn.cdsl/
├── annotations/
│   ├── CdslDef.java          # @CdslDef("xmlElementName") on DSL class
│   ├── CdslModel.java        # @CdslModel(MyModel.class) for XML → model
│   └── CdslAttributes.java  # Optional attribute whitelist
├── concurrency/
│   ├── Lock.java
│   ├── LockProvider.java     # Obtain/release lock for context (e.g. "context/{id}")
│   └── LockRejectedException.java
├── context/
│   ├── CdslContext.java      # State, vars, currentFlow, currentStep, transitions
│   ├── CdslContextAuditor.java      # Audit: execute, transition, setVar, error, etc.
│   ├── CdslContextRepository.java   # getContext(transactionId, contextId), saveContext(...)
│   └── CdslRuntime.java      # transactionId, auditor, postStep/postCommit tasks, outputValueMap
├── definitionsource/
│   ├── DocumentDefinition.java  # List of FlowDefinition
│   ├── ElementDefinition.java   # id, name, attrs, children, textValue (from XML)
│   ├── FlowDefinition.java      # id, defaultStep, errorStep, elements (steps)
│   └── XmlDomDefinitionSource.java  # loadCdslDefinition(resource) → DocumentDefinition
├── dsl/
│   ├── Dsl.java              # execute(runtime, ctx, model, input) → CdslOutputEvent
│   ├── DslSupport.java       # Base class: sets ctx.runtime, calls execSupport()
│   ├── DslMetadata.java      # name, cls, modelCls, model, resolutionStrategy (ByType/ByName)
│   ├── ValidatingDsl.java    # validate(model) — optional, called at registration
│   ├── MapModel.java         # Model that accepts any attr/child as key-value (e.g. endRoute)
│   ├── SayHello.java         # Example: @CdslDef("sayHello"), @CdslModel(SayHelloModel)
│   ├── SetVar.java           # ctx.putVar(name, val)
│   ├── SetState.java         # ctx.setState(...)
│   ├── RouteTo.java          # return withRoute(target)
│   ├── Await.java            # return awaitInputAt(stepId)
│   ├── EndRoute.java         # return withAction(End)
│   ├── RaiseException.java  # Throws (for error-step testing)
│   ├── WhitelistEvents.java # Input validation / routing
│   ├── Injected.java        # Resolved by name (injected DSL)
│   ├── AbstractNestedDsl.java  # Base for container DSLs; runNestedElements() runs child DslMetadata list
│   ├── If.java / IfModel.java   # Container: run nested elements when condition holds (literal or context syntax)
│   ├── ForEach.java / ForEachModel.java  # Container: run nested once per item in context list var
│   └── Parallel.java        # Container: run all children in parallel; only Reject propagates
├── exceptions/
│   ├── CdslException.java
│   └── CdslValidationException.java
├── execution/
│   ├── Flow.java             # id, defaultStep, errorStep, Map<stepId, FlowStep>
│   ├── FlowStep.java         # id, logicElements (DslMetadata), finalElements (DslMetadata)
│   ├── FlowExecutor.java     # execute(flow, inputEvent) → CdslFlowOutputEvent; implements NestedElementExecutor
│   ├── NestedElementExecutor.java  # executeElements(...), executeElementsIgnoreRouteOut(...), executeOneElement(...)
│   ├── PostStepTask.java
│   └── PostCommitTask.java
├── model/
│   ├── CdslInputEvent.java   # contextId, requestedStep, name, source, model
│   ├── CdslOutputEvent.java  # action (Route/Await/End/Reject/Continue), nextRoute
│   ├── CdslFlowOutputEvent.java  # extends CdslOutputEvent + outputValues map
│   ├── CdslOutputValue.java
│   └── CdslAuditEvent.java
├── registry/
│   ├── FlowRegistry.java     # submitDefinition(FlowDefinition) → builds Flow + FlowSteps with DslMetadata
│   ├── RegistryLoader.java   # InitializingBean: loads XML via XmlDomDefinitionSource, submits to FlowRegistry
│   ├── DslInitialisationHelper.java  # buildMetadataForDslElement(), resolve(meta), inject(name, dsl)
│   ├── DslModelBuilder.java  # buildModel(ElementDefinition, modelClass) — attrs/children → POJO
│   └── RegistryValidator.java
└── FlowValidator.java        # Placeholder
```

---

## Flow Definition (XML)

- Root: `<cdsl>` (conceptually; the parser looks for `<flow>` elements).
- **Flow**: `<flow id="flow-id" defaultStep="stepId" errorStep="stepId">` with child `<step>` elements.
- **Step**: `<step id="step-id">` with:
  - **Logic elements**: direct children (e.g. `<setVar name="x" val="y"/>`, `<routeTo target="next"/>`). Run in order; first non-null `CdslOutputEvent` stops the step and drives routing.
  - **Finally block**: one `<finally>` child containing more DSL elements. Run after logic; if they return an output, it overrides the logic output.
- **Element names** map to DSLs: either a Spring bean name (e.g. `sayHello` for a bean named `"sayHello"`) or a class annotated with `@CdslDef("sayHello")`. Attributes and nested elements are mapped to the DSL’s model class by `DslModelBuilder` (reflection: setters and `MapModel`).
- **Nested container elements**: `if`, `foreach`, and `parallel` are **container DSLs**: they have child elements that are executed by the framework. For these, the model is built from **attributes only** (children are not mapped to the model); children are registered as executable `DslMetadata` and run via `NestedElementExecutor` (see below).

Example:

```xml
<flow id="my-flow" defaultStep="init" errorStep="error">
  <step id="init">
    <setState val="Alive"/>
    <setVar name="myVar" val="myVal"/>
    <routeTo target="end"/>
  </step>
  <step id="end">
    <endRoute/>
    <finally>
      <setState val="End"/>
    </finally>
  </step>
</flow>
```

If a DSL throws, the executor routes to `errorStep` when set; otherwise it rethrows.

---

## Nested container DSLs (if, foreach, parallel) and abstract support

Some DSL elements **contain other DSL elements** and run them as a block. The framework supports this via:

- **FlowRegistry.NESTED_CONTAINER_NAMES**: Tag names treated as containers are `if`, `foreach`, `parallel`. When the registry sees one of these, it builds the parent’s model from **attributes only** (`ElementDefinition.copyWithoutChildren`), then builds separate `DslMetadata` for each child and attaches them with `meta.addChildElement(childMeta)`.
- **CdslRuntime**: The executor sets `runtime.setCurrentElementMetadata(dslMeta)` before each DSL execute and clears it after, so container DSLs can read `runtime.getCurrentElementMetadata().getChildElements()`. It also sets `runtime.setNestedElementExecutor(this)` (FlowExecutor implements `NestedElementExecutor`), `runtime.setCurrentFlow(flow)`, and `runtime.setCurrentStep(step)` so nested execution has the same flow/step reference.
- **AbstractNestedDsl**: Base class for DSLs that run their child elements. Subclasses implement `execSupport(...)` and, when they want to run the body, call `runNestedElements(runtime, ctx, input)`. That uses `runtime.getCurrentElementMetadata().getChildElements()` and `runtime.getNestedElementExecutor().executeElements(...)` to run the children with the same semantics as a step (first non-null Route/Await/End/Reject stops and is returned).
- **NestedElementExecutor** (implemented by FlowExecutor):
  - `executeElements(runtime, context, inputEvent, flow, step, elements)` — runs elements in order; first non-null output is returned (Route/Await/End/Reject all propagate).
  - `executeElementsIgnoreRouteOut(...)` — runs all elements; only **Reject** is returned (used by `parallel`).
  - `executeOneElement(...)` — runs a single element and returns its output (used by `parallel` for each branch).

### Built-in container DSLs

| XML element | Purpose | Model | Nested body behaviour |
|-------------|---------|--------|-------------------------|
| **if** | Run nested elements only when a condition holds. `condition` can be literal `true`/`false`, or context syntax `varName`, `varName=value`, `varName!=value`. | IfModel (condition) | Run once if condition true; Route/Await/End/Reject from body propagate. |
| **foreach** | Run nested elements once per item in a context list. List is a context variable (e.g. comma-separated string). Each iteration sets the current item into another context variable (`itemVar`), then runs the body. | ForEachModel (listVar, itemVar, separator) | Run in order per item; if body returns Route/Await/End/Reject, that is returned and the loop stops. |
| **parallel** | Run all child elements in **parallel** (same context, separate CdslRuntime per branch). | MapModel | Route/Await/End from children are **ignored**; only **Reject** is propagated. |

**Note:** There is no built-in **while** DSL; use **foreach** over a list when you need iteration.

**Concurrency note (important):** `parallel` shares the same `CdslContext` instance across threads. Today `CdslContext.vars` is a plain `HashMap`, so **concurrent writes to context variables are not thread-safe**. Use `parallel` only when child DSLs do not concurrently mutate shared state (or change the context implementation to a concurrent structure / add synchronization).

Example (if with nested elements):

```xml
<step id="init">
  <setVar name="choice" val="yes"/>
  <if condition="choice=yes">
    <setVar name="whenYes" val="executed"/>
    <routeTo target="end"/>
  </if>
</step>

<!-- Other condition forms -->
<if condition="true">...</if>
<if condition="flag">...</if>                    <!-- true when ctx.getVar("flag") is non-empty -->
<if condition="role=admin">...</if>              <!-- true when ctx.getVar("role") equals "admin" -->
<if condition="role!=guest">...</if>             <!-- true when ctx.getVar("role") does not equal "guest" -->
```

Example (foreach):

```xml
<setVar name="items" val="apple,banana,cherry"/>
<foreach listVar="items" itemVar="it">
  <setVar name="lastItem" val="it"/>
</foreach>
```

Example (parallel):

```xml
<parallel>
  <setVar name="a" val="1"/>
  <setVar name="b" val="2"/>
</parallel>
```

Inside `parallel`, child outputs like Route/Await/End are ignored (you cannot route out of the step from inside `parallel`). If any branch throws an exception, `parallel` throws a `CdslException` and the flow will enter `errorStep` (if configured) via the normal executor error handling.

To implement a **custom container DSL**: extend `AbstractNestedDsl<MyModel, MT>`, implement `execSupport` to decide when to run the body, then call `return runNestedElements(runtime, ctx, input)`. To have your tag treated as a container, add its name to `FlowRegistry.NESTED_CONTAINER_NAMES` (or the registry will not attach child elements as executable; they would be used only for model building).

---

## Creating a Custom DSL

1. **Model class** (optional): POJO with getters/setters (or use `MapModel` for a generic key-value model). Attribute and child names in XML map to property names (or `MapModel.put`).
2. **DSL class**: Implement `Dsl<MyModel, Serializable>` (or extend `DslSupport<MyModel, Serializable>`) and implement:
   - `CdslOutputEvent execute(CdslRuntime runtime, CdslContext ctx, MyModel model, CdslInputEvent<MT> input)`  
   Return `null` to continue to the next DSL in the step; return an event with `withRoute(target)`, `awaitInputAt(stepId)`, `withAction(Action.End)`, or `reject()` to control flow.
3. **Annotations**:
   - `@CdslDef("xmlElementName")` so the XML tag name maps to this class.
   - `@CdslModel(MyModel.class)` so the framework builds `MyModel` from the element.
4. **Registration**: Either:
   - **By type**: Class in a package scanned by Spring, with `@CdslDef("elementName")`. `DslInitialisationHelper` discovers it via `getBeansWithAnnotation(CdslDef.class)` and matches element name to `def.value()`.
   - **By name**: Register via `DslInitialisationHelper.inject("elementName", dslInstance)` or a Spring bean with id `"elementName"`.

Example (equivalent to built-in `sayHello`):

```java
@CdslDef("sayHello")
@CdslModel(SayHelloModel.class)
@Component
public class SayHello extends DslSupport<SayHelloModel, Serializable> {
    @Override
    public CdslOutputEvent execSupport(CdslRuntime runtime, CdslContext ctx,
                                       SayHelloModel config, CdslInputEvent input) {
        // config.getName() from XML attribute name="..."
        System.out.println("Hello " + config.getName());
        return null;  // continue to next element
    }
}
```

XML: `<sayHello name="fred"/>`.

Optional: implement `ValidatingDsl<MyModel>` and override `validate(MyModel model)`; the registry calls it when building the flow.

---

## Execution Lifecycle

1. **Input**: `CdslInputEvent` with optional `contextId` (if resuming) and optional `requestedStep`.
2. **Context**: If no `contextId`, create new `CdslContext`, generate id, obtain lock (`LockProvider.obtain(..., "context/" + contextId, ...)`), save context. If `contextId` present, obtain lock and load context; if state is `End`, throw.
3. **Current step**: If context has no `currentStep`, set to flow’s `defaultStep`. If input has `requestedStep`, switch to that step (if it exists).
4. **Run step**: Get `FlowStep` for current step. Run all **logic** elements in order; if one returns a non-null `CdslOutputEvent`, use it. Then run **finally** elements; if they return non-null, that overrides. Run **post-step** tasks from `CdslRuntime`.
5. **Route**: According to output action:
   - **Route**: set `context.currentStep` to `nextRoute`, fetch next `FlowStep`, loop (run next step).
   - **Await**: set context state to `Await`, set `currentStep` to `nextRoute`, then exit (no more steps this call).
   - **End** / **Reject**: exit.
6. **Persistence**: Save context, release lock. Run **post-commit** tasks. Return `CdslFlowOutputEvent` (contextId, contextState, outputValues from runtime).

Any exception in a step can send the flow to `errorStep` if configured.

---

## Context and Persistence

- **CdslContext**: Holds `id`, `state`, `currentFlow`, `currentStep`, `vars` (Map<String,String>), `transitions` (bounded list). Use `ctx.putVar(k, v)` (audited) or `ctx.getVar(k)`; for request-scoped data, `putTransient` / `fetchTransient`.
- **CdslContextRepository**: Implementations provide `getContext(transactionId, contextId)` and `saveContext(transactionId, context)`. Locking is done by the executor via `LockProvider`, not inside the repository.
- **LockProvider**: Used to ensure a single writer per context (e.g. `obtain(myIdentifier, "context/" + contextId, lockDuration, retries, retryDelay)`). Test support: `LockProviderUnitTestSupport`, `CdslContextRepositoryUnitTestSupport`, `CdslContextAuditorUnitTestSupport`.

---

## Spring Setup

- **Component scan**: Include `tech.rsqn.cdsl` so that `@CdslDef` beans and `FlowRegistry`, `DslInitialisationHelper`, `XmlDomDefinitionSource` are registered.
- **Load flows**: Define a `RegistryLoader` bean and set `resources` to classpath XML paths, e.g.  
  `<property name="resources"><list><value>/cdsl/my-flows.xml</value></list></property>`.  
  On startup it loads each resource via `XmlDomDefinitionSource` and submits each `FlowDefinition` to `FlowRegistry`.
- **FlowExecutor**: Needs `FlowRegistry`, `DslInitialisationHelper`, `LockProvider`, `CdslContextAuditor`, `CdslContextRepository`. All are autowired; for tests, use the unit-test support beans (see `test-registry-integration-ctx.xml`).

Example minimal integration context (see `src/test/resources/spring/test-registry-integration-ctx.xml`):

- Import a context that does component-scan of `tech.rsqn.cdsl`.
- Define `flowExecutor`, `lockProvider`, `contextRepository`, `contextAuditor`, and `loader` with `resources` pointing to your flow XML.

---

## Built-in DSL Elements (reference)

| XML element | Purpose | Model | Typical output |
|-------------|---------|--------|-----------------|
| `setVar` | Set context variable | `SetVarModel` (name, val) | null |
| `setState` | Set context state | state value | null |
| `routeTo` | Go to another step | `RouteToModel` (target) | Route(target) |
| `await` | Pause until next input at step | `AwaitModel` (at) | Await(at) |
| `endRoute` | End flow | MapModel | End |
| `sayHello` | Example (prints name) | SayHelloModel (name) | null |
| `raiseException` | Throws (for error handling tests) | — | — |
| `whitelist` / `whiteList` | Validate input / route on failure | WhitelistEventsModel | Route/Reject depending on config |
| `injected` | DSL resolved by name (for tests) | name | — |
| **if** | Run nested elements only when condition holds (see Nested container DSLs) | IfModel (condition) | body output propagates |
| **foreach** | Run nested elements once per item in context list var | ForEachModel (listVar, itemVar, separator) | body output propagates, stops loop |
| **parallel** | Run all child elements in parallel; only Reject propagates | MapModel | executeElementsIgnoreRouteOut |

Step structure: **logic** elements first, then **finally** (optional). Output from **finally** overrides **logic** for routing. **Container** elements (`if`, `foreach`, `parallel`) have executable child elements; the container’s model is built from attributes only.

---

## Key Types (quick reference for AI)

- **FlowExecutor.execute(Flow flow, CdslInputEvent input) → CdslFlowOutputEvent**  
  Main entry: run flow for one request; creates or loads context, runs steps, saves context, returns output event with contextId and outputValues.

- **Dsl.execute(CdslRuntime, CdslContext, T model, CdslInputEvent<MT>) → CdslOutputEvent**  
  One DSL element execution; null = continue, non-null = route/await/end/reject.

- **CdslContext**  
  id, state, currentFlow, currentStep, vars, transitions; `putVar`/`getVar`, `putTransient`/`fetchTransient`.

- **CdslRuntime**  
  transactionId, auditor, postStep/postCommit tasks, outputValueMap (emit values to caller).

- **FlowRegistry.getFlow(id)**  
  Retrieve a registered Flow. Flows are registered by `RegistryLoader` from XML.

- **DslInitialisationHelper.buildMetadataForDslElement(flowStep, element)**  
  Resolves DSL by element name (injected or @CdslDef), builds model from XML, returns DslMetadata. Used by FlowRegistry when submitting a FlowDefinition.

- **XmlDomDefinitionSource.loadCdslDefinition(resource)**  
  Loads a single XML resource from classpath and returns DocumentDefinition (list of FlowDefinitions).

- **Nested containers (if, foreach, parallel)**  
  FlowRegistry treats tag names in `NESTED_CONTAINER_NAMES` specially: model from attributes only, children become `DslMetadata` attached via `meta.addChildElement(childMeta)`. Container DSLs extend `AbstractNestedDsl` and call `runNestedElements(runtime, ctx, input)`, which uses `runtime.getCurrentElementMetadata().getChildElements()` and `runtime.getNestedElementExecutor().executeElements(...)`. FlowExecutor implements NestedElementExecutor and sets it (and currentFlow, currentStep, currentElementMetadata) on the runtime before running each element.

---

## Testing

- **Unit tests**: Use `DslInitialisationHelper.inject(name, dsl)` or `injectStatic(name, dsl)` to register DSLs by name. Use `LockProviderUnitTestSupport`, `CdslContextRepositoryUnitTestSupport`, `CdslContextAuditorUnitTestSupport` so no real lock or persistence is required.
- **Integration tests**: See `FlowExecutorTest` and `test-registry-integration-ctx.xml`: component-scan + RegistryLoader with `resources` to `test-integration-flow.xml`, and test beans for executor, lock, repository, auditor. Run a flow with `executor.execute(flowRegistry.getFlow("flow-id"), new CdslInputEvent().with("source", "name"))` and assert on `CdslFlowOutputEvent` and repository context.

---

## Dependencies (from pom.xml)

- Java 21
- Spring (core, context, beans) 5.0.6
- Commons IO, Commons Lang3
- Kryo (for model copy during execution)
- tech.rsqn: reflection-helpers, useful-things (simple-utilities)
- SLF4J
- Tests: TestNG, Spring Test

---

## License

GPL-3.0 (see LICENSE).

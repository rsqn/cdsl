# Plan 01 — Includes, flow validation, context binding

**Status:** Draft for review  
**Owner:** CDSL maintainers  
**Created:** 2026-05-07  
**Updated:** 2026-05-07  

---

## 0. Resolved decisions (stakeholder)

| Topic | Decision |
|-------|----------|
| **§8.1 Root XML** | Use a **`<cdsl>`** root. **`<include>`** and **`<flow>`** are direct children of `<cdsl>`. An included file uses the **same** shape: it **stands alone** as a valid document when loaded by `RegistryLoader` on its own, and is also valid when pulled in via `<include>`—include does not imply a different or fragment-only schema. |
| **§8.3 Validator strictness** | **Always fail:** no warn-only mode or Spring toggle for validation in MVP. Any check we implement (including **unreachable steps** once defined) is an **error** and fails startup / batch validate. |

---

## 1. Problem statement

1. **Authoring scale:** Split flow XML across files; prefer an in-document **`<include>`** instead of only growing the Spring `RegistryLoader` resource list.
2. **Composition:** Multiple flows stay **separate** in the registry; orchestration can **glue** them later (explicit handoff / app layer—see §2B). No requirement to collapse into one physical flow graph.
4. **Validation:** Catch **duplicate flow ids**, **duplicate step ids within a flow**, and **broken routing** (e.g. `routeTo` / `await` targets that are not steps of that flow) at **load time**, not at runtime. Today `FlowValidator` is a stub and duplicate flow ids can **silently overwrite** in `FlowRegistry.submitDefinition`.
5. **Semantic clarity (context):** Resume with the same `contextId` must align with **which flow** owns that context (strict binding).

---

## 2. Expert review — flavours (unchanged summary)

### 2A. Includes

| ID | Flavour | Recommendation |
|----|---------|----------------|
| **A3** | First-class `<include>` processed when loading CDSl XML | **Primary** — classpath-only, max depth, cycle detection. |
| **A1** | Multi-resource `RegistryLoader` only | **Baseline**; remains supported. |

### 2B. Context ↔ flow

| ID | Flavour | Recommendation |
|----|---------|----------------|
| **B1** | Strict flow binding on resume | **MVP** with context. |
| **B2** | `routeToFlow` / handoff | **Later**, when glue is productized. |

**Combined MVP:** **A3 + B1 + validation (§4).**

---

## 3. Include semantics (decided direction)

### 3.0 Document shape (`<cdsl>`)

- Every CDSl XML resource is wrapped in **`<cdsl>...</cdsl>`**.
- **Standalone:** a file loaded only via `RegistryLoader` is a normal document: `<cdsl><flow id="...">...</flow></cdsl>` (and optional `<include>` siblings).
- **Included:** the included resource is **not** a special fragment format—it is the same `<cdsl>` document; the loader expands `<include resource="..."/>` by loading that resource’s flows (and nested includes) into the current parse batch.

### 3.1 Default: separate flows

- An include expands to **one or more `<flow>` elements** (from the included file), merged into the **same logical load** as sibling flows in the parent document.
- Each `<flow>` becomes **one** registered `Flow` in `FlowRegistry` (same as today’s multi-file load).
- **Across different flows**, the same **step id** (e.g. `init`) does **not** conflict—they live in different graphs.

### 3.2 Flow id collisions

- **Default:** registering a second flow with the same **`flow id`** is an **error** (fail at submit or at end of batch validate—see §4). This replaces today’s silent last-wins behavior.
- **Optional later:** explicit `alias` / rename on include for library packaging (out of MVP unless trivial).

---

## 4. Validation phase (“validate” for flows)

**Intent:** Not a new runtime `<step id="validate">` in the DSL unless we add that separately. This is a **definition-time / registry-time** validation pass so miswired flows fail **at startup** (or at test context load).

### 4.1 When it runs

- **Minimum:** After **`RegistryLoader`** finishes loading **all** resources in one `load()` (batch), run `FlowValidator.validateAll(flowRegistry)` (or equivalent) so we can detect duplicate flow ids and cross-check the full registry.
- **Alternative / addition:** `FlowRegistry.submitDefinition` can run **per-flow** structural checks immediately; **duplicate flow id** must be detected at submit or deferred to batch—**prefer** batch + single authoritative pass to avoid half-registered state.

### 4.2 Checks (MVP list)

| Check | Severity | Notes |
|-------|----------|--------|
| Duplicate **`flow id`** in registry | **Error** | Today last wins; must become explicit failure. |
| Duplicate **`step id`** within one flow | **Error** | Should not happen from XML; guard anyway. |
| **`defaultStep`** missing or not a step of that flow | **Error** | |
| **`errorStep`** set but not a step of that flow | **Error** | |
| **`routeTo` `target`** not a step of that flow | **Error** | Walk logic + finally + nested container children. |
| **`await` `at`** not a step of that flow | **Error** | Same walk. |
| Unreachable steps (no path from `defaultStep`) | **Error** | Per **§0**: always fail once this check exists. Define “reachable” as steps that appear in some directed path from `defaultStep` following `routeTo` / `await` edges only (implementation detail). |
| Arbitrary routing cycles | **Not an error by default** | Many flows intentionally loop; do not fail on SCCs unless we add an explicit opt-in “acyclic” flag later. |

### 4.3 Implementation sketch

- Flesh out **`tech.rsqn.cdsl.FlowValidator`** (or move under `registry` / `validation` package): accept `FlowRegistry` or `Map<String, Flow>` + access to step ids per flow.
- **Extract route/await targets** from `Flow` / `FlowStep` / `DslMetadata` tree (mirror how nested containers are built in `FlowRegistry.buildNestedContainerMeta`).
- **Invocation:** `RegistryLoader.load()` calls validator once at end; on failure throw `CdslValidationException` (or dedicated `FlowDefinitionException`) with **flow id + step id + reason**.
- **Tests:** Invalid flows in test XML; assert context fails to start or `submitDefinition` fails.

---

## 5. Scope (MVP)

**In scope**

- **`<include>`** with `resource` (classpath, allowlist), depth/cycle limits; expands to flows as in §3.
- **`FlowValidator`** with checks in §4.2 (errors at minimum); wire **RegistryLoader** post-load.
- **`FlowRegistry`:** reject duplicate flow id (or rely solely on validator—**one** place must own the error).
- **B1 strict binding** (context flow id vs `execute(flow, …)`).
- **Docs:** README + short `docs/` note on include and validation.

**Out of scope (MVP)**

- **B2** `routeToFlow`.
- **B3** inline subflow.
- XInclude-only path (**A2**) unless we pivot after spike.

---

## 6. Devil’s advocate (risks)

1. **Namespace rewrite** is easy to get subtly wrong (missed DSL or new DSL later). Mitigation: central “collect step references” helper + tests per DSL element type.
2. **Validation** must stay in sync with new DSLs that introduce routing; document extension point for contributors.
3. **Strict binding (B1)** breaks emergent cross-flow resume; release note + optional temporary flag remains a product call.
4. **Include** resolution: forbid non-classpath URLs; cap file size / depth to avoid DoS in dev.

---

## 7. Definition of done

- [ ] `<include>` documented with examples.
- [ ] Validator errors are actionable (flow id, step id, message).
- [ ] Duplicate flow id never silent-overwrites.
- [ ] `mvn clean install` green; integration test fails fast on bad XML.
- [ ] B1 binding documented + tested.

---

## 8. Implementation decisions — **resolved**

See **§0**. No open items here for MVP.

---

## 9. Suggested implementation map

| Area | Touchpoints |
|------|-------------|
| Include expansion | `XmlDomDefinitionSource` (or pre-parse pass), classpath resolver |
| Namespace rewrite | New small module: `FlowDefinitionPrefixer` on `FlowDefinition` / `ElementDefinition` tree before `submitDefinition` |
| Validation | `FlowValidator`, `RegistryLoader`, possibly `FlowRegistry` |
| Binding | `CdslContext`, `FlowExecutor`, exception type |
| Docs | `README.md`, `docs/flow-modularity.md` |

---

## 10. Next step (Forge)

Stakeholder decisions recorded in **§0**. Remaining product choice (optional): whether **flow `id`** should ever be prefixed in v1 (still **out of MVP** per §3.3). Mark plan **Approved** when ready to implement; no further §8 interrogation required.

---

## 11. PACE plan — `&&` in XML attributes (post-build hardening)

### 11.1 Problem

- In XML **attributes**, a literal `&` is illegal and must be escaped; therefore **`&&` must be written as `&amp;&amp;`**.
- This is easy to miss when writing CDSL `if` conditions in XML (looks like a normal boolean expression but it is also XML syntax).
- Failure modes range from **XML parse errors** to subtle authoring confusion when conditions are edited/copied.

### 11.2 Approach

- Treat this as a **documentation + guardrail** problem rather than a language problem.
- Keep expression semantics in Java/Kotlin side unchanged: the CDSL `If` DSL should receive the **unescaped** `&&` after XML parsing and evaluate it normally.
- Add lightweight **preventative checks** around authoring so the common mistake is caught early and explained well.

### 11.3 Changes (already implemented)

1. ✅ **Bumped CDSL to `1.0.17-SNAPSHOT`** (enables `&&` in conditions).
2. ✅ **Wired all 8 exit DSLs** into `tick-evaluation-flow.xml`, replacing the monolithic `evaluateDynamicExitForPosition`.
3. ✅ **Added 33 unit tests** across 7 test classes (all pass).
4. ✅ **Deleted** `DynamicExitStrategy.java` (542 lines) and `EvaluateDynamicExitForSymbolDsl.java`.
5. ✅ **Full suite green:** 694 tests, 0 failures, 0 errors.

**Key fix discovered:** `&&` in XML attributes must be escaped as **`&amp;&amp;`** — the CDSL `If` class receives the unescaped `&&` string after XML parsing and handles it correctly.

### 11.4 Evidence

- **Unit tests:** 33 new tests covering the 8 exit DSL paths and expected tick-evaluation outcomes.
- **Regression signal:** Full suite pass (694 tests) indicates flow wiring + deletions didn’t break behaviour.
- **XML behaviour:** Standard XML parsing unescapes attribute entities; `&amp;&amp;` becomes `&&` before CDSL evaluates conditions.

### 11.5 Guardrails & follow-ups (to make this stick)

- **Docs**: Add a short “XML escaping” note (with a copy/paste example) to the primary authoring docs for conditions:
  - Show `condition="a &amp;&amp; b"` as the canonical form.
  - Explicitly call out `condition="a && b"` as invalid in XML attributes.
- **Examples**: Update any example flows/tests/templates that show boolean conjunction in attributes to use `&amp;&amp;`.
- **Validation** (nice-to-have): If we have access to the raw attribute text pre-parse (or can detect parse failures at load time), improve the error message to mention `&amp;&amp;` when an author likely typed `&&`.
- **CI safety**: Add a focused unit/integration test that loads a minimal flow XML with `condition="a &amp;&amp; b"` to ensure the “unescape then evaluate” behaviour never regresses across parser/loader refactors.

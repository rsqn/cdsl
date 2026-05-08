# Plan 02 — XML-friendly boolean operators for CDSL conditions

**Status:** Draft for review  
**Owner:** CDSL maintainers  
**Created:** 2026-05-07  
**Updated:** 2026-05-07  

---

## 0. Resolved decisions (stakeholder)

| Topic | Decision |
|-------|----------|
| **§8.1 Operators** | Add word-based operators **`AND`**, **`OR`**, **`NOT`**, **`XOR`** as synonyms for boolean operators in CDSL conditions. |
| **§8.2 Compatibility** | **No breaking changes**: `&&/||/!` remain supported. In XML attributes, `&&` must still be written as `&amp;&amp;` (unchanged). |
| **§8.3 Casing** | Keywords are **uppercase-only**: only `AND/OR/NOT/XOR` are treated as operators (lowercase remains identifiers). |
| **§8.4 Precedence** | Define and test precedence: `NOT/!` > `AND/&&` > `XOR` > `OR/||`, with parentheses overriding. This implies `A AND B OR C` means `(A AND B) OR C`. |

---

## 1. Problem statement

1. **Readability:** XML attribute authoring makes `&&` unreadable (`&amp;&amp;`) and hard to visually parse.
2. **Error-proneness:** Authors frequently type `&&` unescaped and hit confusing XML parse failures.
3. **Docs drift:** Examples oscillate between “code-like” and “XML-correct”, making the pit-of-success unclear.

---

## 2. Expert review — flavours (options)

| ID | Flavour | Recommendation |
|----|---------|----------------|
| **A1** | Keep `&amp;&amp;` and document it better | Baseline; does not fix readability. |
| **A2** | Add `AND/OR/NOT` synonyms in the expression language | **Primary** — improves XML readability without breaking existing expressions. |
| **A4** | Also add `XOR` | **Included** — enables “exactly one condition holds” checks without awkward expansion. |
| **A3** | Introduce XML-only alternate attribute(s), e.g. `<if><condition>...</condition></if>` | Later; larger XML schema + tooling impact. |

---

## 3. Semantics and precedence (decided direction)

### 3.1 Operators

- `AND` == `&&`
- `OR` == `||`
- `NOT` == `!`
- `XOR` == boolean xor (exclusive-or)

Notes:

- Keyword matching is **uppercase-only** (per §0), not case-insensitive.

### 3.2 Precedence (must be explicit and tested)

1. Parentheses: `( ... )`
2. Unary negation: `NOT`, `!`
3. Conjunction: `AND`, `&&`
4. Exclusive-or: `XOR`
5. Disjunction: `OR`, `||`

Associativity:

- `AND`, `XOR`, `OR` are left-associative (`A XOR B XOR C` == `(A XOR B) XOR C`).

### 3.3 XML examples

- Prefer: `condition="inPosition AND (pnlPct &gt; 0.5)"`
- Still valid: `condition="inPosition &amp;&amp; (pnlPct &gt; 0.5)"`

Note: other XML escaping still applies (e.g. `>` → `&gt;`). This plan targets the biggest readability offender (`&amp;&amp;`), not all XML entities.

---

## 4. Validation / parsing behaviour

### 4.1 Tokenization constraints

- Keywords must only match as standalone operators (word-boundary safe):
  - `band` must remain an identifier, not `b` + `and`.
- Keywords must not be recognized inside string literals (existing lexer rules should already guarantee this).

### 4.2 Error messaging (quality-of-life)

- When a parse error occurs near `&` (or XML load fails in a way that suggests unescaped `&&`), error text should suggest:
  - Prefer `AND` for XML authoring, or escape `&&` as `&amp;&amp;`.

---

## 5. Scope (MVP)

**In scope**

- Add `AND/OR/NOT/XOR` operator support in the condition parser and evaluator.
- Document precedence and provide XML-friendly examples.
- Add tests proving equivalence between symbol-operators and word-operators.
- Add a minimal XML-load regression test that proves `condition="a &amp;&amp; b"` still works.

**Out of scope (MVP)**

- New XML schema to move expressions out of attributes.
- Any automated rewrite/formatter (can be a follow-on).

---

## 6. Risks (devil’s advocate)

1. **Ambiguity with identifiers**: keyword matching must be strict.
2. **Behaviour drift**: ensure `AND/OR/NOT/XOR` builds the same AST as `&&/||/!` and evaluates identically.
3. **Half-fix perception**: authors still need to escape `>` as `&gt;`; docs must set expectations clearly.

---

## 7. Definition of done

- [ ] `AND/OR/NOT/XOR` accepted in conditions with precedence tested.
- [ ] `&&/||/!` still accepted (including `&amp;&amp;` in XML attributes).
- [ ] Repo XML examples updated to prefer `AND/OR/NOT/XOR` for readability in XML.
- [ ] Error messages point authors toward `AND` or `&amp;&amp;` when relevant.


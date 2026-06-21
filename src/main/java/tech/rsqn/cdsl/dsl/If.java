package tech.rsqn.cdsl.dsl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import tech.rsqn.cdsl.annotations.CdslDef;
import tech.rsqn.cdsl.annotations.CdslModel;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslRuntime;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Container DSL: runs nested elements only when the condition holds.
 * Supports condition expression (literal or context syntax) or legacy var/val;
 * see project README ("If condition expressions") for {@code &&}, {@code ||}, and precedence.
 */
@CdslDef("if")
@CdslModel(IfModel.class)
@Component
public class If extends AbstractNestedDsl<IfModel, Serializable> {

    @Override
    public CdslOutputEvent execSupport(CdslRuntime runtime, CdslContext ctx, IfModel model, CdslInputEvent input) throws CdslException {
        if (model == null) {
            return null;
        }
        boolean result = evaluateCondition(ctx, model);
        if (!result) {
            return null;
        }
        return runNestedElements(runtime, ctx, input);
    }

    private boolean evaluateCondition(CdslContext ctx, IfModel model) {
        String cond = model.getCondition();
        if (StringUtils.isEmpty(cond)) {
            return false;
        }
        return evaluateConditionExpression(ctx, cond.trim());
    }

    /**
     * Evaluates a condition expression:
     * - Literal: "true" / "false"
     * - OR:  "expr1 || expr2" or "expr1 OR expr2" (OR has the lowest precedence)
     * - XOR: "expr1 XOR expr2" (between AND and OR)
     * - AND: "expr1 && expr2" or "expr1 AND expr2" (AND tighter than OR)
     * - NOT: "!expr" or "NOT expr"
     * - Parentheses: "(expr)" override precedence
     * - Var exists: "varName" (true when non-null, non-empty)
     * - Equals: "varName = value" or "varName == value" (single-quoted values supported: 'CRISIS')
     * - Not equals: "varName != value"
     */
    private boolean evaluateConditionExpression(CdslContext ctx, String expr) {
        if (StringUtils.isBlank(expr)) {
            return false;
        }
        TokenStream ts = new TokenStream(expr, tokenize(expr));
        boolean result = parseOr(ctx, ts);
        if (ts.peek().type != TokenType.EOF) {
            throw new CdslException("Invalid IF condition; unexpected token '" + ts.peek().text + "' in: " + expr);
        }
        return result;
    }

    /** Strips surrounding single quotes from a value token, e.g. {@code 'CRISIS'} → {@code CRISIS}. */
    private String stripQuotes(String s) {
        if (s != null && s.length() >= 2 && s.charAt(0) == '\'' && s.charAt(s.length() - 1) == '\'') {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    // ---------------------------------------------------------------------
    // Tokenizer / parser
    //
    // Precedence (highest to lowest):
    //   1) parentheses
    //   2) NOT / !
    //   3) AND / &&
    //   4) XOR
    //   5) OR / ||
    //
    // Keywords are uppercase-only: AND/OR/NOT/XOR.
    // ---------------------------------------------------------------------

    private enum TokenType {
        IDENT,
        STRING,
        LPAREN,
        RPAREN,
        NOT,       // ! or NOT
        AND,       // && or AND
        OR,        // || or OR
        XOR,       // XOR
        EQ,        // =
        EQEQ,      // ==
        NOTEQ,     // !=
        EOF
    }

    private static final class Token {
        final TokenType type;
        final String text;

        Token(TokenType type, String text) {
            this.type = type;
            this.text = text;
        }
    }

    private static final class TokenStream {
        private final String fullExpr;
        private final List<Token> tokens;
        private int idx = 0;

        TokenStream(String fullExpr, List<Token> tokens) {
            this.fullExpr = fullExpr;
            this.tokens = tokens;
        }

        Token peek() {
            return tokens.get(idx);
        }

        Token next() {
            return tokens.get(idx++);
        }

        boolean match(TokenType type) {
            if (peek().type == type) {
                idx++;
                return true;
            }
            return false;
        }

        Token expect(TokenType type, String fullExpr) {
            Token t = peek();
            if (t.type != type) {
                throw new CdslException("Invalid IF condition; expected " + type + " but found '" + t.text + "' in: " + fullExpr);
            }
            return next();
        }
    }

    private List<Token> tokenize(String expr) {
        List<Token> out = new ArrayList<>();
        int i = 0;
        while (i < expr.length()) {
            char c = expr.charAt(i);
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (c == '(') {
                out.add(new Token(TokenType.LPAREN, "("));
                i++;
                continue;
            }
            if (c == ')') {
                out.add(new Token(TokenType.RPAREN, ")"));
                i++;
                continue;
            }

            if (c == '\'' ) {
                int j = i + 1;
                while (j < expr.length() && expr.charAt(j) != '\'') {
                    j++;
                }
                if (j >= expr.length()) {
                    throw new CdslException("Invalid IF condition; unterminated single-quoted string in: " + expr);
                }
                out.add(new Token(TokenType.STRING, expr.substring(i, j + 1))); // keep quotes for stripQuotes()
                i = j + 1;
                continue;
            }

            // Two-char operators
            if (i + 1 < expr.length()) {
                String two = expr.substring(i, i + 2);
                if ("||".equals(two)) {
                    out.add(new Token(TokenType.OR, "||"));
                    i += 2;
                    continue;
                }
                if ("&&".equals(two)) {
                    out.add(new Token(TokenType.AND, "&&"));
                    i += 2;
                    continue;
                }
                if ("==".equals(two)) {
                    out.add(new Token(TokenType.EQEQ, "=="));
                    i += 2;
                    continue;
                }
                if ("!=".equals(two)) {
                    out.add(new Token(TokenType.NOTEQ, "!="));
                    i += 2;
                    continue;
                }
            }

            // One-char operators
            if (c == '!') {
                out.add(new Token(TokenType.NOT, "!"));
                i++;
                continue;
            }
            if (c == '=') {
                out.add(new Token(TokenType.EQ, "="));
                i++;
                continue;
            }

            // Ident / keyword
            int j = i;
            while (j < expr.length()) {
                char cj = expr.charAt(j);
                if (Character.isWhitespace(cj) || cj == '(' || cj == ')' || cj == '\'' || cj == '!' || cj == '=') {
                    break;
                }
                // stop before symbol-ops start
                if (cj == '&' || cj == '|') {
                    break;
                }
                j++;
            }
            String word = expr.substring(i, j);
            if (word.isEmpty()) {
                char bad = expr.charAt(i);
                if (bad == '&') {
                    throw new CdslException("Invalid IF condition; found '&' in expression. In XML attributes write && as '&amp;&amp;' or use 'AND'. Full condition: " + expr);
                }
                if (bad == '|') {
                    throw new CdslException("Invalid IF condition; found '|' in expression. Use '||' or 'OR'. Full condition: " + expr);
                }
                throw new CdslException("Invalid IF condition; unexpected character '" + bad + "' in: " + expr);
            }
            if ("AND".equals(word)) {
                out.add(new Token(TokenType.AND, word));
            } else if ("OR".equals(word)) {
                out.add(new Token(TokenType.OR, word));
            } else if ("NOT".equals(word)) {
                out.add(new Token(TokenType.NOT, word));
            } else if ("XOR".equals(word)) {
                out.add(new Token(TokenType.XOR, word));
            } else {
                out.add(new Token(TokenType.IDENT, word));
            }
            i = j;
        }
        out.add(new Token(TokenType.EOF, "<eof>"));
        return out;
    }

    // OR := XOR ( (OR) XOR )*
    private boolean parseOr(CdslContext ctx, TokenStream ts) {
        boolean left = parseXor(ctx, ts);
        while (ts.match(TokenType.OR)) {
            boolean right = parseXor(ctx, ts);
            left = left || right;
        }
        return left;
    }

    // XOR := AND ( XOR AND )*
    private boolean parseXor(CdslContext ctx, TokenStream ts) {
        boolean left = parseAnd(ctx, ts);
        while (ts.match(TokenType.XOR)) {
            boolean right = parseAnd(ctx, ts);
            left = left ^ right;
        }
        return left;
    }

    // AND := UNARY ( AND UNARY )*
    private boolean parseAnd(CdslContext ctx, TokenStream ts) {
        boolean left = parseUnary(ctx, ts);
        while (ts.match(TokenType.AND)) {
            boolean right = parseUnary(ctx, ts);
            left = left && right;
        }
        return left;
    }

    // UNARY := (NOT)* PRIMARY
    private boolean parseUnary(CdslContext ctx, TokenStream ts) {
        boolean negate = false;
        while (ts.match(TokenType.NOT)) {
            negate = !negate;
        }
        boolean value = parsePrimary(ctx, ts);
        return negate ? !value : value;
    }

    // PRIMARY := '(' OR ')' | ATOM
    private boolean parsePrimary(CdslContext ctx, TokenStream ts) {
        if (ts.match(TokenType.LPAREN)) {
            boolean inner = parseOr(ctx, ts);
            ts.expect(TokenType.RPAREN, ts.fullExpr);
            return inner;
        }
        return parseAtom(ctx, ts);
    }

    // ATOM := true|false | IDENT [comparison value]? | STRING (invalid standalone)
    private boolean parseAtom(CdslContext ctx, TokenStream ts) {
        Token t = ts.peek();
        if (t.type == TokenType.IDENT) {
            String ident = ts.next().text;

            if ("true".equalsIgnoreCase(ident)) {
                return true;
            }
            if ("false".equalsIgnoreCase(ident)) {
                return false;
            }

            // Function call: isNotEmpty(varName)
            if ("isNotEmpty".equals(ident) && ts.peek().type == TokenType.LPAREN) {
                ts.next(); // consume (
                Token arg = ts.expect(TokenType.IDENT, ts.fullExpr);
                ts.expect(TokenType.RPAREN, ts.fullExpr);
                String val = ctx.getVar(arg.text);
                return StringUtils.isNotEmpty(val);
            }

            // Comparison?
            Token op = ts.peek();
            if (op.type == TokenType.NOTEQ || op.type == TokenType.EQEQ || op.type == TokenType.EQ) {
                ts.next(); // consume op
                Token rhs = ts.peek();
                if (rhs.type != TokenType.IDENT && rhs.type != TokenType.STRING) {
                    throw new CdslException("Invalid IF condition; expected a value after '" + op.text + "'");
                }
                String expected = stripQuotes(ts.next().text);
                String actual = ctx.getVar(ident);
                if (op.type == TokenType.NOTEQ) {
                    return !expected.equals(actual);
                }
                return expected.equals(actual);
            }

            // Var exists
            String actual = ctx.getVar(ident);
            return StringUtils.isNotEmpty(actual);
        }

        if (t.type == TokenType.STRING) {
            throw new CdslException("Invalid IF condition; string literal is not a standalone boolean: " + t.text);
        }

        throw new CdslException("Invalid IF condition; expected an expression but found '" + t.text + "'");
    }
}

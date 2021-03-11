package edu.montana.csci.csci468.parser;

import edu.montana.csci.csci468.parser.expressions.*;
import edu.montana.csci.csci468.parser.statements.*;
import edu.montana.csci.csci468.tokenizer.CatScriptTokenizer;
import edu.montana.csci.csci468.tokenizer.Token;
import edu.montana.csci.csci468.tokenizer.TokenList;
import edu.montana.csci.csci468.tokenizer.TokenType;

import java.util.LinkedList;
import java.util.List;

import static edu.montana.csci.csci468.tokenizer.TokenType.*;

public class CatScriptParser {

    private TokenList tokens;
    private FunctionDefinitionStatement currentFunctionDefinition;

    public CatScriptProgram parse(String source) {
        tokens = new CatScriptTokenizer(source).getTokens();

        // first parse an expression
        CatScriptProgram program = new CatScriptProgram();
        program.setStart(tokens.getCurrentToken());
        Expression expression = parseExpression();
        if (tokens.hasMoreTokens()) {
            tokens.reset();
            while (tokens.hasMoreTokens()) {
                program.addStatement(parseProgramStatement());
            }
        } else {
            program.setExpression(expression);
        }

        program.setEnd(tokens.getCurrentToken());
        return program;
    }

    public CatScriptProgram parseAsExpression(String source) {
        tokens = new CatScriptTokenizer(source).getTokens();
        CatScriptProgram program = new CatScriptProgram();
        program.setStart(tokens.getCurrentToken());
        Expression expression = parseExpression();
        program.setExpression(expression);
        program.setEnd(tokens.getCurrentToken());
        return program;
    }

    //============================================================
    //  Statements
    //============================================================

    private Statement parseProgramStatement() {
        Statement printStmt = parsePrintStatement();
        if (printStmt != null) {
            return printStmt;
        }
        return new SyntaxErrorStatement(tokens.consumeToken());
    }

    private Statement parsePrintStatement() {
        if (tokens.match(PRINT)) {

            PrintStatement printStatement = new PrintStatement();
            printStatement.setStart(tokens.consumeToken());

            require(LEFT_PAREN, printStatement);
            printStatement.setExpression(parseExpression());
            printStatement.setEnd(require(RIGHT_PAREN, printStatement));

            return printStatement;
        } else {
            return null;
        }
    }

    //============================================================
    //  Expressions
    //============================================================

    private Expression parseExpression() {
        return parseEqualityExpression();
    }

    private Expression parseEqualityExpression(){
        Expression expression = parseComparisonExpression();
        while (tokens.match(EQUAL_EQUAL, BANG_EQUAL)){
            Token token = tokens.consumeToken();
            Expression rhs = parseComparisonExpression();
            EqualityExpression equalityExpression = new EqualityExpression(token, expression, rhs);
            equalityExpression.setStart(expression.getStart());
            equalityExpression.setEnd(rhs.getEnd());
            expression = equalityExpression;
        }
        return expression;
    }

    private Expression parseComparisonExpression(){
        Expression expression = parseAdditiveExpression();
        if (tokens.match(GREATER_EQUAL, GREATER, LESS, LESS_EQUAL)){
            Token token = tokens.consumeToken();
            Expression rhs = parseComparisonExpression();
            ComparisonExpression comparison = new ComparisonExpression(token, expression, rhs);
            comparison.setStart(expression.getStart());
            comparison.setEnd(rhs.getEnd());
            expression = comparison;
        }
        return expression;
    }

    private Expression parseAdditiveExpression() {
        Expression expression = parseFactorExpression();
        while (tokens.match(PLUS, MINUS)) {
            Token operator = tokens.consumeToken();
            final Expression rightHandSide = parseFactorExpression();
            AdditiveExpression additiveExpression = new AdditiveExpression(operator, expression, rightHandSide);
            additiveExpression.setStart(expression.getStart());
            additiveExpression.setEnd(rightHandSide.getEnd());
            expression = additiveExpression;
        }
        return expression;
    }

    private Expression parseFactorExpression(){
        Expression expression = parseUnaryExpression();
        while(tokens.match(SLASH, STAR)){
            Token operator = tokens.consumeToken();
            final Expression rhs = parseUnaryExpression();
            FactorExpression factorExpression = new FactorExpression(operator, expression, rhs);
            factorExpression.setStart(expression.getStart());
            factorExpression.setEnd(rhs.getEnd());
            expression = factorExpression;
        }
        return expression;
    }

    private Expression parseUnaryExpression() {
        if (tokens.match(MINUS, NOT)) {
            Token token = tokens.consumeToken();
            Expression rhs = parseUnaryExpression();
            UnaryExpression unaryExpression = new UnaryExpression(token, rhs);
            unaryExpression.setStart(token);
            unaryExpression.setEnd(rhs.getEnd());
            return unaryExpression;
        } else {
            return parsePrimaryExpression();
        }
    }

    private Expression parsePrimaryExpression() {
        if (tokens.match(INTEGER)) {
            Token integerToken = tokens.consumeToken();
            IntegerLiteralExpression integerExpression = new IntegerLiteralExpression(integerToken.getStringValue());
            integerExpression.setToken(integerToken);
            return integerExpression;
        } else if(tokens.match(STRING)){
            Token stringToken = tokens.consumeToken();
            StringLiteralExpression stringExpression = new StringLiteralExpression(stringToken.getStringValue());
            stringExpression.setToken(stringToken);
            return stringExpression;
        } else if (tokens.match(TRUE, FALSE)) {
            Token booleanToken = tokens.consumeToken();
            BooleanLiteralExpression booleanExpression = new BooleanLiteralExpression(booleanToken.getStringValue().equals("true"));
            booleanExpression.setToken(booleanToken);
            return booleanExpression;
        } else if (tokens.match(NULL)){
            NullLiteralExpression nullExpression = new NullLiteralExpression();
            return nullExpression;
        } else if (tokens.match(LEFT_PAREN, RIGHT_PAREN)) {
            Token parenToken = tokens.consumeToken();
            Expression rhs = parseExpression();
            ParenthesizedExpression parenExpression = new ParenthesizedExpression(rhs);
            parenExpression.setToken(parenToken);
            return parenExpression;
        } else if (tokens.match(LEFT_BRACKET)) {
            boolean end = true;
            Token parenToken = tokens.consumeToken();
            List<Expression> rhs = new LinkedList<>();
            while(!tokens.match(RIGHT_BRACKET)) {
                if(tokens.match(EOF)) {
                    end = false;
                    break;
                }
                rhs.add(parsePrimaryExpression());

            }
            ListLiteralExpression listExpression = new ListLiteralExpression(rhs);
            listExpression.setToken(parenToken);
            if(!end){
                listExpression.addError(ErrorType.UNTERMINATED_LIST);
            }
            return listExpression;
        } else if(tokens.match(COMMA)){
            Token token = tokens.consumeToken();
            Expression expression = parsePrimaryExpression();
            return expression;
        } else if (tokens.match(IDENTIFIER)){
            Token identifierToken = tokens.consumeToken();
            if(tokens.match(LEFT_PAREN)){
                boolean end = true;
                tokens.consumeToken();
                List<Expression> rhs = new LinkedList<>();
                while(!tokens.match(RIGHT_PAREN)){
                    if(tokens.match(EOF)){
                        end = false;
                        break;
                    }
                    rhs.add(parsePrimaryExpression());
                }
                FunctionCallExpression funcExpression = new FunctionCallExpression(identifierToken.getStringValue(), rhs);
                funcExpression.setToken(identifierToken);
                if(!end){
                    funcExpression.addError(ErrorType.UNTERMINATED_ARG_LIST);
                }
                return funcExpression;
            }
            else {
                IdentifierExpression identExpression = new IdentifierExpression(identifierToken.getStringValue());
                identExpression.setToken(identifierToken);
                return identExpression;
            }
        } else {
            SyntaxErrorExpression syntaxErrorExpression = new SyntaxErrorExpression(tokens.consumeToken());
            return syntaxErrorExpression;
        }
    }

    //============================================================
    //  Parse Helpers
    //============================================================
    private Token require(TokenType type, ParseElement elt) {
        return require(type, elt, ErrorType.UNEXPECTED_TOKEN);
    }

    private Token require(TokenType type, ParseElement elt, ErrorType msg) {
        if(tokens.match(type)){
            return tokens.consumeToken();
        } else {
            elt.addError(msg, tokens.getCurrentToken());
            return tokens.getCurrentToken();
        }
    }

}

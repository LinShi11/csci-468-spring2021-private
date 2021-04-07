package edu.montana.csci.csci468.parser;

import edu.montana.csci.csci468.parser.expressions.*;
import edu.montana.csci.csci468.parser.statements.*;
import edu.montana.csci.csci468.tokenizer.CatScriptTokenizer;
import edu.montana.csci.csci468.tokenizer.Token;
import edu.montana.csci.csci468.tokenizer.TokenList;
import edu.montana.csci.csci468.tokenizer.TokenType;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static edu.montana.csci.csci468.tokenizer.TokenType.*;

public class CatScriptParser {

    private TokenList tokens;
    private FunctionDefinitionStatement currentFunctionDefinition;
    private Token tempIdentifierToken;

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
        Statement statment = parseStatment();
        if(statment != null ) return  statment;
        Statement statement = parseFunctionDeclarationStatement();
        if(statement!=null) return statement;

        return new SyntaxErrorStatement(tokens.consumeToken());
    }

    private Statement parseStatment(){
        if (tokens.match(PRINT)) {
            return parsePrintStatement();
        }
        if(tokens.match(FOR)){
            return parseForStatement();
        }
        if(tokens.match(IF)){
            return parseIfStatement();
        }
        if(tokens.match(VAR)){
            return parseVariableStatement();
        }
        if(tokens.match(IDENTIFIER)){
            tempIdentifierToken = tokens.consumeToken();
            if(tokens.match(EQUAL)) {
                return parseAssignmentStatement();
            } else{
                return parseFunctionCallStatement();
            }
        }
        if(tokens.match(RETURN)){
            return parseReturnStatement();
        }
        return null;
    }

    private Statement parsePrintStatement() {
        PrintStatement printStatement = new PrintStatement();
        printStatement.setStart(tokens.consumeToken());

        require(LEFT_PAREN, printStatement);
        printStatement.setExpression(parseExpression());
        printStatement.setEnd(require(RIGHT_PAREN, printStatement));
        return printStatement;
    }

    private Statement parseForStatement(){

        ForStatement forStatement = new ForStatement();
        forStatement.setStart(tokens.consumeToken());

        require(LEFT_PAREN, forStatement);
        Token loopIdentifier = require(IDENTIFIER, forStatement);
        forStatement.setVariableName(loopIdentifier.getStringValue());
        require(IN, forStatement);
        forStatement.setExpression(parseExpression());

        require(RIGHT_PAREN, forStatement);
        require(LEFT_BRACE, forStatement);
        List<Statement> body = new LinkedList<>();
        while(!tokens.match(RIGHT_BRACE)) {
            if(tokens.match(EOF)){
                break;
            }
            body.add(parseStatment());
        }
        forStatement.setBody(body);
        forStatement.setEnd(require(RIGHT_BRACE, forStatement));
        return forStatement;
    }

    private Statement parseIfStatement(){
        IfStatement ifStatement = new IfStatement();
        ifStatement.setStart(tokens.consumeToken());

        require(LEFT_PAREN, ifStatement);
        ifStatement.setExpression(parseExpression());
        require(RIGHT_PAREN, ifStatement);
        require(LEFT_BRACE, ifStatement);
        List<Statement> body = new LinkedList<>();
        while(!tokens.match(RIGHT_BRACE)) {
            if(tokens.match(EOF)){
                break;
            }
            body.add(parseStatment());
        }
        ifStatement.setTrueStatements(body);
        Token token = require(RIGHT_BRACE, ifStatement);
        if(tokens.match(ELSE)){
            tokens.consumeToken();
            if(tokens.match(IF)) {
                Statement newStatement = parseIfStatement();

                List<Statement> temp = ifStatement.getTrueStatements();
                temp.add(newStatement);
                ifStatement.setTrueStatements(temp);
            }
            body = new LinkedList<>();
            require(LEFT_BRACE, ifStatement);
            while(!tokens.match(RIGHT_BRACE)){
                if(tokens.match(EOF)){
                    break;
                }
                body.add(parseStatment());
            }
            ifStatement.setElseStatements(body);
            ifStatement.setEnd(require(RIGHT_BRACE, ifStatement));
        } else {
            ifStatement.setEnd(token);
        }
        return ifStatement;
    }

    private Statement parseVariableStatement(){
        VariableStatement variableStatement = new VariableStatement();
        variableStatement.setStart(require(VAR, variableStatement));

        variableStatement.setVariableName(tokens.consumeToken().getStringValue());
        if(tokens.match(COLON)) {
            tokens.consumeToken();
            CatscriptType type = parseTypeExpression();
            variableStatement.setExplicitType(type);
        }
        require(EQUAL, variableStatement);
        Expression expression = parseExpression();
        variableStatement.setExpression(expression);
        variableStatement.setEnd(expression.getEnd());

        return variableStatement;
    }

    private Statement parseAssignmentStatement(){
        AssignmentStatement assignmentStatement = new AssignmentStatement();
        assignmentStatement.setStart(tempIdentifierToken);
        assignmentStatement.setVariableName(tempIdentifierToken.getStringValue());
        require(EQUAL, assignmentStatement);
        Expression expression = parseExpression();
        assignmentStatement.setExpression(expression);
        assignmentStatement.setEnd(expression.getEnd());

        return assignmentStatement;
    }

    private Statement parseFunctionCallStatement(){
        Statement statement = null;
        if(tokens.match(LEFT_PAREN)){
            boolean end = true;
            tokens.consumeToken();
            List<Expression> rhs = new LinkedList<>();
            while(!tokens.match(RIGHT_PAREN)){
                if(tokens.match(EOF)){
                    end = false;
                    break;
                }
                rhs.add(parseExpression());
            }
            FunctionCallExpression funcExpression = new FunctionCallExpression(tempIdentifierToken.getStringValue(), rhs);
            funcExpression.setToken(tempIdentifierToken);
            if(!end){
                funcExpression.addError(ErrorType.UNTERMINATED_ARG_LIST);
            } else{
                Token token = tokens.consumeToken();
            }
            FunctionCallStatement functionCallStatement = new FunctionCallStatement(funcExpression);
            functionCallStatement.setStart(tempIdentifierToken);
            functionCallStatement.setEnd(funcExpression.getEnd());
            functionCallStatement.setToken(tempIdentifierToken);
            statement = functionCallStatement;
        }
        return statement;
    }

    private Statement parseFunctionDeclarationStatement(){

        if(tokens.match(FUNCTION)){

            FunctionDefinitionStatement functionDefinitionStatement = new FunctionDefinitionStatement();
            functionDefinitionStatement.setStart(tokens.consumeToken());
            functionDefinitionStatement.setName(require(IDENTIFIER, functionDefinitionStatement).getStringValue());
            require(LEFT_PAREN, functionDefinitionStatement);

            TypeLiteral typeLiteral = new TypeLiteral();
            while(!tokens.match(RIGHT_PAREN)) {
                if(tokens.match(LEFT_BRACE)) {
                    break;
                } else if(tokens.match(COMMA)){
                    tokens.consumeToken();
                } else {
                    Token token = tokens.consumeToken();
                    typeLiteral.setType(new CatscriptType("object", Object.class));
                    if(tokens.matchAndConsume(COLON)){
                        CatscriptType type = parseTypeExpression();
                        typeLiteral.setType(type);
                    }
                    functionDefinitionStatement.addParameter(token.getStringValue(), typeLiteral);
                }
            }
            require(RIGHT_PAREN, functionDefinitionStatement);
            if(tokens.matchAndConsume(COLON)){
                CatscriptType type = parseTypeExpression();
                typeLiteral.setType(type);
                functionDefinitionStatement.setType(typeLiteral);
            } else {
                functionDefinitionStatement.setType(null);
            }
            require(LEFT_BRACE, functionDefinitionStatement);

            currentFunctionDefinition = functionDefinitionStatement;
            List<Statement> statementList = new LinkedList<>();
            Statement body;
            while(!tokens.match(RIGHT_BRACE)) {
                if (tokens.match(EOF)) {
                    break;
                }
                if(tokens.match(RETURN)){
                    body = parseStatment();
                } else{
                    body = parseProgramStatement();
                }
                statementList.add(body);
            }
            currentFunctionDefinition = null;
            functionDefinitionStatement.setBody(statementList);
            functionDefinitionStatement.setEnd(require(RIGHT_BRACE, functionDefinitionStatement));

            return functionDefinitionStatement;
        } else {
            return null;
        }
    }

    private Statement parseReturnStatement(){
        ReturnStatement returnStatement = new ReturnStatement();
        returnStatement.setFunctionDefinition(currentFunctionDefinition);
        Token token = tokens.consumeToken();
        returnStatement.setStart(token);
        returnStatement.setEnd(token);
        if(!tokens.match(RIGHT_BRACE)){
            Expression expression = parseExpression();
            returnStatement.setExpression(expression);
            returnStatement.setEnd(expression.getEnd());
        }
        return returnStatement;
    }

    private CatscriptType parseTypeExpression(){
        if(tokens.match("list")){
            tokens.consumeToken();
            CatscriptType inner;
            if(tokens.matchAndConsume(LESS)) {
                inner = parseTypeExpression();
                tokens.matchAndConsume(GREATER);
            } else{
                return new CatscriptType.ListType(new CatscriptType("object", Object.class));
            }
            CatscriptType.ListType listType = new CatscriptType.ListType(inner);
            return listType;
        } else{
            Token token = tokens.consumeToken();
            Class javaClass = getJavaClass(token.getStringValue());
            CatscriptType catscriptType = new CatscriptType(token.getStringValue(), javaClass);
            return catscriptType;
        }

    }

    private Class getJavaClass(String type) {
        Class javaClass = null;
        switch (type) {
            case "int":
                javaClass = Integer.class;
                break;
            case "string":
                javaClass = String.class;
                break;
            case "bool":
                javaClass = Boolean.class;
                break;
            case "object":
                javaClass = Object.class;
                break;
            case "list":
                javaClass = List.class;
                break;
        }
        return javaClass;
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
            tokens.consumeToken();
            return nullExpression;
        } else if (tokens.match(LEFT_PAREN, RIGHT_PAREN)) {
            Token parenToken = tokens.consumeToken();
            Expression rhs = parseExpression();
            ParenthesizedExpression parenExpression = new ParenthesizedExpression(rhs);
            parenExpression.setToken(parenToken);
            tokens.consumeToken();
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
                if(tokens.match(COMMA)){
                    tokens.consumeToken();
                } else {
                    rhs.add(parsePrimaryExpression());
                }
            }
            ListLiteralExpression listExpression = new ListLiteralExpression(rhs);
            listExpression.setToken(parenToken);
            if(!end){
                listExpression.addError(ErrorType.UNTERMINATED_LIST);
            } else{
                tokens.matchAndConsume(RIGHT_BRACKET);
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
                } else{
                    tokens.consumeToken();
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

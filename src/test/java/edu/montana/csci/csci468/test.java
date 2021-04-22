package edu.montana.csci.csci468;

import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.statements.FunctionDefinitionStatement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class test extends CatscriptTestBase{
    @Test
    void testType(){
        FunctionDefinitionStatement functionDefinitionStatement = parseStatement(
                "function foo(x : int) : int {\n" +
                        "return x + 1" +
                        "}\n" +
                        "print(foo(9))");
        assertEquals(CatscriptType.INT, functionDefinitionStatement.getType());
        assertEquals(CatscriptType.INT, functionDefinitionStatement.getParameterType(0));
    }
}

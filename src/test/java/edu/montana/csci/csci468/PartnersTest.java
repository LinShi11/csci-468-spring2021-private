package edu.montana.csci.csci468;

import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.statements.Statement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PartnersTest extends CatscriptTestBase {

    @Test
    void StatementExecutionsMixed() {
        // factor and additive expression together
        assertEquals("9\n", executeProgram("print(1 + 2 * 4)"));
        // two print statements execute, second with factor expression evaluation before print
        assertEquals("1\n4\n", executeProgram("print(1)\n" +
                "print(2 * 2)"));
        // var expressions execute and work in additive expression
        assertEquals("10\n", executeProgram("var x = 1\n" +
                "var y = 7" +
                "print(x + 2 + y)"));
        // function declaration, and function call within for loop
        assertEquals("3\n4\n5\n", executeProgram("var y = 2\n" +
                "function foo(x : int) : int {\n" +
                        "return x + y" +
                        "}\n" +
                "for(x in [1, 2, 3]) {\n"+
                "print(foo(x))\n }" ));
        // duplication name, var and for var
        assertEquals(ErrorType.DUPLICATE_NAME, getParseError("var x = 10\n" +
                "for(x in []){ print(x) }"));
        // different type assignment execution
        Statement statement = parseStatement("if(true){ var y = 10 } else { var y = true }\n");
        assertNotNull(statement);
    }

    @Test
    void ExpressionsCompileMixed() {
        // double not
        assertEquals("false\n", compile("not not true"));
        // string concatenation
        assertEquals("hello LinShi11\n", compile("\"hello \" + \"LinShi11\""));
        // additive expression with string, int additive expression, and factor expression
        assertEquals("7a\n", compile("1 + 3 * 2 + \"a\""));
        // additive and factor expression order of operation
        assertEquals("2025\n", compile("2 * 4 / 2 + 2021"));
        // negation of false comparison expression
        assertEquals("true\n", compile("(2 > 1)"));
        // negation of true comparison expression
        assertEquals("true\n", compile("1 <= 1"));
        // false comparison expression
        assertEquals("false\n", compile("2 < 1"));

    }

    @Test
    void StatementsCompileMixed(){
        // factor expression in for loop with var y outside loop
        assertEquals("2\n4\n6\n", compile("var y = 2\n" +
                "for(x in [1, 2, 3]) {" +
                " print(x * y)" +
                "}"));
        // if statement containing for loop with factor expression in print statement
        assertEquals("2\n3\n4\n", compile("if(true){ " +
                "for(x in [1, 2, 3]) {" +
                "print(x+1) }" +
                "}"));
        // false
        assertEquals("", compile("if(false){ print(\"aaaaa\") }"));
        // if statement compiling on bool var
        assertEquals("true\n", compile("var x = true\n" +
                "if(x){" +
                "print(x)" +
                "}"));
        // equality expression compiles within print statement
        assertEquals("true\n", compile("print(1 == 1)"));
    }
}

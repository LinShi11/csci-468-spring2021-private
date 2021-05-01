package edu.montana.csci.csci468;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PatchForBowenKruse extends CatscriptTestBase{
    @Test
    void evalTests() {

        // function declaration statement, function call statement, comparison expression, additive expression, print statement, and return statement with bool return type
        assertEquals("false\nfalse\ntrue\n", executeProgram(
                "function foo(x: int, y: int) : bool \n" +
                "{ \n" +
                    "return(x+1 > y) \n" +
                "}\n" +
                "print(foo(1,2))\n"+
                "print(foo(0,2))\n" +
                "print(foo(0,0))"
        ));

        // if statement, var statement, print statement, string concatenation, and when string concatenation has one int
        assertEquals("hello, bowen100\n", executeProgram(
                "var x = 100\n"+
                "if(1 != 10){\n" +
                    "var one = \"hello, \"\n" +
                    "var two = \"bowen\"\n" +
                    "print(one + two + x)\n" +
                "}\n"
        ));

        // unary expression, for statement, factor expression, and print statement
        assertEquals("-1\n-2\n-3\n", executeProgram(
                "for(i in [1 , 2 , 3])\n" +
                "{\n" +
                    "print(i * -1)\n"+
                "}\n"
                ));
    }

    @Test
    void compileTests(){
        // var statement, function declaration, function call, if statement and return statement
        assertEquals("1\n0\n", compile(
                "var test = 100 \n" +
                "function foo(x: int) : int\n" +
                "{\n" +
                    "if(test > x){\n" +
                        "return 1\n" +
                    "}else{\n" +
                        "return 0\n" +
                    "}\n" +
                "}\n" +
                "print(foo(1))\n" +
                "print(foo(100000))"
        ));

        //compiles the for loop, if statement, print statement, list literals, and comparison expression
        assertEquals("5\n6\n", compile(
                "for(i in [1 , 5, 3 , 6 , 4 ])\n" +
                "{\n"+
                    "if(i >= 5){\n" +
                        "print(i)\n" +
                    "}\n" +
                "}\n"
        ));

        // compiles the function declaration, function call, string concatenation, comparison, return and print
        assertEquals("test10\n", compile(
                "function foo(x: int) : int{\n" +
                        "if(x >= 0){\n" +
                        "return (x* 10)\n" +
                        "}\n" +
                        "return 0" +
                        "}\n" +
                        "print(\"test\" + foo(1))"
        ));

    }

    @Test
    void expressionTests(){
        // compile tests for mainly expressions:
        // factor expressions
        assertEquals("500\n", compile("1000/2"));
        assertEquals("40\n", compile("2*2*10"));

        // additive expression and equality expression
        assertEquals("true\n", compile("(2+2) == (2*2)"));
        assertEquals("true\n", compile("(1-1) != 3"));
        assertEquals("hello1\n", compile("\"hello\" + 1"));

        // unary expression and comparison expression
        assertEquals("false\n", compile("-1 >= 1"));
        assertEquals("true\n", compile("-2 <= 2"));

        // fix of all of them for the final compile test
        assertEquals("-47\n", compile("-10/2*10+3"));
        assertEquals("19\n", compile("(100-10+5)/5"));


        // eval test for mainly expressions:
        // equality expression and factor expression
        assertEquals(true, evaluateExpression("(24/2) == (3*4)"));
        assertEquals(true, evaluateExpression("100/10 != 9"));

        // comparison, additive, unary, and factor expressions:
        assertEquals(false, evaluateExpression("1+3 > 4"));
        assertEquals(true, evaluateExpression("3-2 <= 1"));
        assertEquals(false, evaluateExpression("4/2 < 2"));
        assertEquals(false, evaluateExpression("-2*3 >= 6"));
        assertEquals("helloBowen", evaluateExpression("\"hello\" + \"Bowen\""));

    }


}

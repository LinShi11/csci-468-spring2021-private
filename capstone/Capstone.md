# Section 1: Program

See source.zip file in the same directory (/capstone/portfolio).

# Section 2: Teamwork

### Overview:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The project was completed in a groups of two and it has been a semester long task.
Throughout the semester, team member one was responsible to complete all the code and design the parse, 
while team member two was responsible to write the tests for the program as well as write a documentation for the program.
The tests were implemented using patch and was passed to the other individual by email. 
The other communicate between the partners were completed using discord. 
Additionally, the partners had the access to the each other's github repository for comparison
and discussion; however, each one are responsible to complete their own program.
</p>

### Member Contribution:

#### Team Member one:
- Primary programmer
- Provide tests for Team Member two for their code
- Provide documentation for Team Member two 
- Estimated hours of contribution: about 200 hours

#### Team Member Two:
- Provide tests for Team Member one
- Provide documentation for Team Member One
- Estimated hours of contribution: about 10 hours


# Section 3: Design pattern
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The design pattern implemented in the code was memoization. When there some expensive algorithm implemented and the result could be reused, 
it is much efficient to memorize the result and store it somewhere. This is known as caching. It is commonly used to avoid the unnecessary code when the data has could be reused.

</p>

<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Within our code, memorization was used in CatscriptType. More specifically, we used memorization in the getListType function. 
The function's task is to find the type of the list that we are dealing with. Therefore, the CatscriptType system will return a type of listType, which extends from CatscriptType.
Just like the example below:
</p>

```
    public static CatscriptType getListType(CatscriptType type) {
        ListType listType = new ListType(type);
        return listType;
    }
```
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;However, instead of just finding the new listType everytime the function is called, we will use the memorization. First, a staic hashmap is created to hold the information.
Within the function, the previous type is found. If the instance is null, which means this is the first time the function has been ran, the same logic will be ran as the previous example. 
Where the new listType is found and returned; however, an extra step of storing the information within the CACHE will take place. If the instance of CACHE is not null, the previous match will be returned.
Example below:
</p>

```
    static Map<CatscriptType, CatscriptType> CACHE = new HashMap<>();
    public static CatscriptType getListType(CatscriptType type) {
        CatscriptType previousMatch = CACHE.get(type);
        if(previousMatch == null){
            ListType listType = new ListType(type);
            CACHE.put(type, listType);
            return listType;
        } else{
            return previousMatch;
        }
    }
```
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;By accomplishing the memorization design pattern, computation time will be saved if the method is called many times through the execuation process. Although in this case, the algorithm is not extrememly expensive,
it is a good practice to have and it is a great practice of the memorization design pattern. 
</p>


Identify one design pattern that was used in your capstone project and describe exactly where in the code it is located. 

Highlight the design pattern in yellow. Explain why you used the pattern and didn’t just code directly.

# Section 4: Technical writing. Include the technical document that accompanied your capstone project.

### Overview:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The Catscript programming language was created as a learning tool for CSCI 468. Throughout the course, we discussed ideas such as tokenizer, parser, and code generator. 
In the beginning of the course, we were provided with the essential skeleton code that had the tools used for debug as well as the essential code to run the program, both as a local server and when we did not have every aspect completed.
Additionally, Java was chosen to be used as the language since Java had a good String class built into it. 
</p>

### CatScript Types:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The Catscript language has a small type system, which contains the following: 
</p>

```
    int - a 32 bit integer
    string - a java-style string
    bool - a boolean value
    list - a list of value with the type 'x'
    null - the null type
    object - any type of value
```
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; As previously mentioned, one of the main reasons to choose Java was the java-styled string that have been implemented in our program. The other types are integers, boolean, list, null, and object. 
</p>

### CatScript Grammar:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The CatScript language is a context-free grammar, which is a formal grammar with a structure that is set by its recursive rules. The grammar is a set of production rules that will describe all possible string
for the language. The production contains the head and the body. The head contains all the non-terminals and the body could have both the non-terminals and the terminals. 
Additionally, the language has been written in Backus-Narur form and the non-terminals are in lowercase while the terminals are either quoted or capitalized.
Below is the CatScript Grammar. It is split into the statement section and expression section:
</p>

#### Expression Grammar:
```
expression = equality_expression;

equality_expression = comparison_expression { ("!=" | "==") comparison_expression };

comparison_expression = additive_expression { (">" | ">=" | "<" | "<=" ) additive_expression };

additive_expression = factor_expression { ("+" | "-" ) factor_expression };

factor_expression = unary_expression { ("/" | "*" ) unary_expression };

unary_expression = ( "not" | "-" ) unary_expression | primary_expression;

primary_expression = IDENTIFIER | STRING | INTEGER | "true" | "false" | "null"| 
                     list_literal | function_call | "(", expression, ")"

list_literal = '[', expression,  { ',', expression } ']'; 

function_call = IDENTIFIER, '(', argument_list , ')'

argument_list = [ expression , { ',' , expression } ]
```

##### Expression overview:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The expression grammar follows a recursion where equality expression is called first, followed by comparison expression,
additive expression, factor expression, unary expression, and primary expression in that order. This has been done because of the priorities. For example, 
multiplication and division has a higher priority than addition and subtraction; therefore, it needs to be further down the parse tree to accomplish that. 
Thus, down in the bottom of the recursion tree, the primary expressions, such as string and integer, will be called first. Then the unary expression, the negative and
not will be looked at. Next, it is the factor expression. Followed by the additive expression and comparison expression. Lastly, it is the equality expression.
Additionally, the list literal and function call are part of the primary expressions and will be identified at the lowest level.  
</p>

##### Equality Expression:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The equality expression is used to compare two expression to determine whether they are
equal or not. CatScript use == operator for equal and != for not equal. In the end, the equality expresion will return a true or false
based on its evaluation. The equality expression consist an expression, operator ("==" | "!="), and an expression. For example:
</p>

```
    1 == 1 // evaluates to true
    1 != 1 // evaluates to false
    1 != 2 // evaluates to true
    1 == 2 // evaluates to false
```

##### Comparison Expression:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Comparison expression is used to determine the whether numeric relation (<=, <, >, >=) between the two sides is true or not. Again, the comparison expression 
will have a left-hand side and a right-hand side dividing among the symbol. Both the left-hand side and the right-hand side will recursively iterate down the parsing tree. In the end, the expression will
be evaluated and a boolean will be returned. The comparison expression will expression, operator ("<=" | "<" | ">" | ">="), and an expression. Below is a quick example:
</p>

```
    1 > 10 // evaluates to false
    1 >= 10 // evaluates to false
    1 < 10 // evaluates to true 
    1 <= 10 // evaluates to true
```

##### Additive Expression:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The additive expression will have a left-hand side and a right-hand side, both will follow the recursive parsing tree and call factor. The subtraction (-) will take the two 
integers and find its value. On the other hand, addition (+) is a little different. If there is two integers, it will add them up and find its value. However, if at least one side is a string, the plus operator will
be a string concatenation and return a string. Additive expression will consist expression, operator ("+" | "-"), and expression. Such as:
</p>

```
    1 - 1 // evaluates to 0
    1 + 1 // evaluates to 2
    1 + "Hello" // evaluates to "1HELLO"
    "Hello" + "World" // evaluates to "HelloWorld"
```

##### Factor Expression:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; In a factor expression, it will contains the left-hand side and right-hand side. Both side will continue to call unary expression. The factor expression will take both side, 
complete the operator, / for division and * for multiplication, and return the result. Once again, factor expression will have expression, operator ("/" | "*"), and expression. 
</p>

```
    4 * 2 // evaluates to 8
    4 / 2 // evaluates to 2
```

##### Unary Expression:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The unary expression is different than all the expression above, it contains at least one unary operator ("not" | "-") and some primary expression. Since there is not a left-hand side,
if the unary operator find the operator, it will continue to iterate through it until it finds a primary expression, which will be discussed in the next section. If the primary expression is a integer, the unary expression 
will flip the sign of the integer. If primary expression is a boolean, it will find the opposite of the current value. Unary expression will have at least one ("not" | "-") and followed by the primary expression. 
</p>

```
    not true // evaluates to false 
    - 1 // evaluates to -1
```


##### Primary Expression:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Primary expression is the last one on the recursive list. It could have identifier, a string, an integer, a boolean, a list literal, a function call, or parentheses and an expression within it. 
The identifier will be some kind of variable such as x. String will be a string of, for example, "x". Integer will be some kind of number. Boolean consist of true, false, or null. These three and the parentheses are the only terminals
in the primary expression and the others are non-terminals. List literal and function call will be discussed next. Expression is just call the expression above, where it will iterate through and the parse tree again.
</p>

```
    x // evaluates to identifier
    "x" // evaluates to string
    1 // evaluates to integer
    (<some kind of expression here>) // evaluates to parenthese and call expression
```

##### List Literal:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The list literals are identified in the primary expression with a left bracket.
Once a left bracket is identified in the primary expression, it will continue to iterate through for different expressions.
Additionally, there will be commas within the list literals to sepearate all the different expressions.
</p>

```
    [1, 1+1, 2, 3] //evaluates to 1, 1+1, 2, 3
```

##### Function Call:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Similar to List Literals, function call are determined within primary expressions. 
Function call contains an identifier, followed by the parenthesis and argument lists. Therefore, within the primary expressions,
once the identifier has been found, the next token will be looked at to determine whether it is just an identifier or a function call. 
</p>

```
    foo(1, 2) // evaluates to function call, function foo 
```

##### Argument List:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Argument list is called in function call where it is almost the same as list literals without the brackets.
It contains the expressions with comma separating them.
</p>

```
    foo(1, 2) //evaluates to function call, arugment list is 1 and 2
```

##### Expression Conclusion:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Based on the grammar, the expression will be ran as recursion. The bottom of the parse tree is the primary expression, which consist of the different
terminals such as integer value, string value, boolean value, parenthesis, and etc..  
</p>

#### Statement Grammar:
```
catscript_program = { program_statement };

program_statement = statement |
                    function_declaration;

statement = for_statement |
            if_statement |
            print_statement |
            variable_statement |
            assignment_statement |
            function_call_statement;

for_statement = 'for', '(', IDENTIFIER, 'in', expression ')', 
                '{', { statement }, '}';

if_statement = 'if', '(', expression, ')', '{', 
                    { statement }, 
               '}' [ 'else', ( if_statement | '{', { statement }, '}' ) ];

print_statement = 'print', '(', expression, ')'

variable_statement = 'var', IDENTIFIER, 
     [':', type_expression, ] '=', expression;

function_call_statement = function_call;

assignment_statement = IDENTIFIER, '=', expression;

function_declaration = 'function', IDENTIFIER, '(', parameter_list, ')' + 
                       [ ':' + type_expression ], '{',  { function_body_statement },  '}';

function_body_statement = statement |
                          return_statement;

parameter_list = [ parameter, {',' parameter } ];

parameter = IDENTIFIER [ , ':', type_expression ];

return_statement = 'return' [, expression];

type_expression = 'int' | 'string' | 'bool' | 'object' | 'list' [, '<' , type_expression, '>']
```

##### Statement Overview:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The grammar for program statement consist of two different sections, the statement and function declaration.
The statement consist of for, if, print, variable, assignment, and function call statement. Based on the grammar, the statement is not called recursively.
Instead, the statements are called based on the first token. Additionally, if none of the previous statements satisfies, the function declaration will be used.
In terms of priorities, all the statements will take the same priorities after the expression has been evaluated. If none of the statements has been true, the function
declaration will then be tested.
</p>

##### For Statement:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; For statement will be choose after the first token has been analyzed. Since the first keyword is "for", the for statement will be looked at.
Then it consist of the parenthesis, identifier (the variable used in the for loop), keyword "in", the expression, and end parenthesis. Next, there is the body of the for statement, starting with
the left curly brace, then the body will be parsed as statements, followed by the closing curly brace. 

</p>

```
    for (x in [1,2,3]){
        print(x)
    }
    // this will evaluates to printing 1 2 3 each time
```
##### If Statement:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; If statement will be identified by the "if" keyword. Next, the expression is within the parenthesis, which mean the expression will be parsed. 
This will be a boolean result that will determine whether to continue within the if statement. Next, there is the statements
in. However, there could be the else and the else if statements. Therefore, the "else" keyword will be analyzed to see if the next token is the "if" token. If there is the else if token, another if statement will be parsed.
Which means the if statement will parse recursively if there is a else if present. If there is only a else token, the statement will be parsed and that is the end of the if statement. 
</p>

```
    var x = 2
    if(x == 1){
        print(x)    
    } else if(x == 2){
        print("else if" + x)
    } else{
        print(x)
    }
    // this will evaluates to "else if2"
```

##### Print Statement:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The print statement will be identified by the "print" keyword. Then, it will be a expression within the parenthesis. Which means it will parse the expression as body.
</p>

```
    print("x" + 1)
    // this will evaluates to x1
```

##### Variable Statement:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The variable statement start with the key word "var". Then the variable name, identifier, will be parsed. Next, there could be the type expression, which will be talked about next. Within the variable statement, it is the "=" and the expression.
This will mean the variable name is set equal to the expression.
</p>

```
    var x = 1+2
    // x will be evaluated to 3
```

##### Type Expression:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The type expressions are used in multiple spots. it consist of a ":" followed by the type expression that was talked about above. These are used to determine the type of the
variable or return type of a function. 
</p>

```
    var x : string = "1"
    // x will be a string with the value of 1
```

##### Function Call Statement:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The function call statement only consist of the function call expression. However, both the function call statement and the assignment statement, which will be looked at next, starts with identifier token. 
Once function call statement is decided, it will follow the function call expression. 
</p>

```
    foo()
    // evaluates to function call statement for function foo
```

##### Assignment Statement:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The assignment statement starts with a identifier, just as function call. However, there is a "="; therefore, once identifier is found, the next token is analyzed to determine whether it is a assignment
statement or a function call statement. Once the assignment statement is determined, it is going to be followed by the expression.
</p>

```
    x = 1 + 2
    // x evaluates to 3 
```

##### Function Declaration Statement:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Once all the statement has been analyzed, the function declaration will be looked at next. Function declaration starts with keyword "function" and identifier, the function name. Next there is a parameter list in parenthesis, 
parameter list will be talked about next. Then, the function declaration could have a type expression that will determine the return type of the function. Additionally, the default return type will be void to demonstrate nothing as the return. Function declaration will
have function body statement within curly brace, which is the end of the function declaration.
</p>

```
    function foo() : string {
    } 
    // evaluates to function foo with a return type of string
```

##### Parameter List:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The parameter list consist of identifiers possibly with type expression.
</p>

```
    function foo(x, y){} // evaluates to x and y as parameter list
    function foo(x: int, y : int){} // evaluates to x and y both as integer as parameter list
```

##### Function Body Statement:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The function body statement will have either the statement or the return statement. Which means the statement will be parsed as the body of the function declaration. The return statement will be talked about next. 
</p>

```
    function foo(){
        print(1)
        print(2)
    } // evaluates to 1 2 as the result of print statement. It is running the statements within the body.
```

##### Return Statement:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Although the return statement is not written within the statement of the grammar. Based on the function body statement, the return statement is on the same level as the other statement.
Return statement will start with the key word of "return" followed by some expression. The return will be the last statement within the function body. 
</p>

```
    function foo(): int{
        return 1
    } // evaluates to return integer of 1
```

##### Statement Conclusion:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Different than the expression, the statement does not run recursively. However, it is split into two different sections of statement and function declaration statement. All of the statement will be ran before
function declaration is run. Additionally, although return statement is not written within the statement in the grammar rules, based on the grammar itself, the return statement can only occur within the function declaration statement, but it is the same 
priorities as the other statements. 
</p>

# Section 5: UML. 
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; One of the important aspect of the CatScript language is the parser itself. How the parser is designed will determine how the language will be interpreted. In the above section, the detail of each feature
and how each feature will be parsed was talked about. In this section, the boarder view of the parser will be talked about. The following uml describes the parser at a top level:
</p>


<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; At the very top level, Statement and Expression extends from parseElements. Additionally, the parenthesizedExpression, syntaxErrorExpression, listLiteralExpression, comparisonExpression, nullLiteralExpression, identifierExpression, 
functionCallExpression, equalityExpression, stringLiteralExpression, additiveExpression, booleanLiteralExpression, factorExpression, integerLiteralExpression, and unaryExpression are all extension of the expression. Some of the extension are talked about
previously as the grammar has been looked at. As seen, there are more function such as the syntaxErrorExpression that has been included within. There are the possible errors that could occur as a result of the expression parser. 

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; On the other hand, the uml also shows the different 
statements that extends from the statement. They are the ifStatement, functionDefinitionStatement (functionDeclaration), forStatement, functionCallStatement, printStatement, syntaxErrorStatement, returnStatement, assignmentStatement, catScriptProgram, and variableStatement.
Within these unique statement, it is clear how some of it has been talked about previously, such as the if, for, function declare, function call, print, return, assignment, and variable statement. Additionally, there are the syntaxError and catScriptProgram. SyntaxError are 
the possible errors associated with parsing statement. The CatScriptProgram is used since in the end, all the the statement are going to be part of a larger program that will be used. 
</p>

# Section 6: Design trade-offs

### Introduction:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; During this course, one of the main designs to choose was whether to use hand-written recursive descent or a parser generator when completing the parser. There are many parser generator available, such as ANLR. It will take the language 
specifications and outputs a parse tree. On the other hand, a handwritten parser is used for this course. The recurive descent algorithm or top-down parser is created for this course. In this section, the pros and the cons of the two methods will be talked about.
Additionally, the choice of hand-written recursive deescent parser will also be discussed.
</p>

### Parser Generator:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; There exist many parser generators; however, most of them have the same pros and cons. In theory, parser generators are better when one wish to complete the parser as fast as they can. The parser generator has the APIs and built-ins
that will make writing the code for the parser much quicker and easier. Additionally, a parser generator will require less infrastructure to get going. Which could be very beneficial to someone new and need to complete the parser one step at the time. Furthermore, 
a parser generator could be much faster than many hand-written parser, especially a hand-written parser that was created by someone doing a compiler for the first time.
</p>

<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; On the other hand, there are some down sides to a parser generator. For example, although it does not require a lot of code to get working, if one wish to add extra, specific rules to the grammar, it might require extra work than a
hand-written parser. Next, if there were flaws in the parser generator used, it would be very time-consuming, since one would need to fix the parser code, if it is open-sourced, and ask the author of the parser generator to accept the changes. Therefore, it is close to impossible
if one finds an error in the parser. Additionally, one would spend much more time reading the APIs of the parser generator than thinking about the actual parser. This takes away the educational purposes of writing a compiler. Even learning the parser generator does not mean that
such skills will be widely used in industry. 
</p>

### Hand-written Recursive Descent:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The parser written for this course is a a recursive descent algorithm, also known as top-down. Since one has to write this from scratch, it would be easier to debug. One could include a break point and use the tools available to step
through each line of code and determine where the errors is. Additionally, as a parser, it is much easier to include an appropriate error message, which will make the end-product user-friendly. Lastly, by completing a compiler from scratch, it is a much better learning 
tool.
</p>

<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Although written a parser from scratch might sounds like to ideal way to complete a compiler. It has a few down-sides as well. For example, the parser is difficult to write since one has to understand the how the parser works before
writing it. Additionally, it might be hard to get started since the compiler needs the other infrastructures to even run. 
</p>

### Conclusion:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Overall, to have a better learning experience, the hand-written parser is chosen for this course. Although it might be slower than the parser generator, speed of the parser is not one of the objectives for this course. Additionally, 
the professor has given us a lot of the infrastructure code needed to run the compiler within everything finished. Furthermore, the professor gave us a few example and hints about how to complete the parser. Thus, all the negative sides of a hand-written parser has been comprised.
On the other hand, writing a parser from scratch will allow one to learn the fundamental of a parser. Additionally, one can learn something that could be used within the industry. In conclusion, there are just more benefits associated with a hand-written parser and that has 
been chosen for this course. 
</p>

# Section 7: Software development life cycle model

### Introduction:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; In this project, a test-driven development cycle is used. The cycle consist of adding a test, running all the test, write some code to pass the test, and continue the process. The TDD tries to make everything
as simple as they can be while maintaining all the features. Regarding this project, it is a little different, most of the tests were provided to us by the professor. Given each checkpoint, some code is written to make sure these tests pass. However,
the full test-driven development was not used since the process never repeated. 
</p>

### Pros:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  Within the course, TDD is one of the easier models that could be implemented in the course. To communicate effectively, a development model that has clear checkpoints should be used. Additionally, the model should be simple,
easy to test and implement. Therefore, all the above criteria met the TDD model. Furthermore, the TDD model will make the grading portion easier. For the students, the TDD model made things clear regarding to what need to be implemented and what needs to be completed
to make everything work. Following the checkpoints and grammar will ensure, on the big scale, that the compiler works. 
</p>

### Cons:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; On the other hand, there were a few down-sides to the TDD. For example, the project has been a semester-long project, which means what has been completed might affect the later checkpoints. In one occasion, the way the parser parsed 
expression was completed passed the test; however, it had more problems during the next phase when parsing the statement. This means the tests did not cover all possibilities. Additionally, the whole process of TDD was not implemented since the students were not asked to 
include more tests. The tests were set and even if additional problems were discovered, no other tests will be included on the individual basis. 
</p>

### Conclusion:
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Although there were a few cons associated with using the TDD in this course, the pros are much beneficial. As time progresses, the professor will include some tests as he sees fit. For the first time using the model in this course,
the flaws are fairly small. It is a great experience to learn about a simple development model like TDD.
</p>

Describe the model that you used to develop your capstone project. How did this model help and/or hinder your team?

We are using Test Driven Development (TDD) for this project
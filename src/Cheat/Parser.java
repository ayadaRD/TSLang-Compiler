package Cheat;

import Cheat.SymbolTable.*;

import java.util.ArrayList;

public class Parser {

    /*---ATTRIBUTES---*/

    ArrayList<Token> tokens;                           //array to save and use tokens
    ArrayList<String> errors = new ArrayList<>();      //array to save and print errors
    ArrayList<Function> all_functions = new ArrayList<>();

    static int cursor = 0;                            //token counter

    static SymbolTable currentSymbolTable;            //initial symbol table witch is null in the start


    /*---CONSTRUCTOR---*/

    public Parser(ArrayList<Token> tokens){
        this.tokens = tokens;
    }


    /*---METHODS---*/

    //for forwarding cursor indirectly
    private void nextToken() {
        cursor++;
        if(cursor >= tokens.size()){
            cursor--;
            tokens.get(cursor).tokenName = "invalid";
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    //for start parsing
    public void start_parsing() throws Exception {
        currentSymbolTable = new SymbolTable();
        currentSymbolTable.isRoot = true;
        PROG();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    private void PROG() throws Exception {
        if (tokens.isEmpty() || cursor >= tokens.size()-1) {
            System.out.println();
        }
        else {
            FUNC();
            PROG();
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    private void FUNC() throws Exception {

        //saves what current function return
        TSlangType functionType;

        //saves the parameters of current function
        ArrayList<Variable> functionParameters;

        //saves function name
        String functionName = null;

        //we need this if function has left brace and right brace
        SymbolTable prevScope;

        /* def */
        if (!tokens.get(cursor).tokenName.equals("def"))
            errors.add(tokens.get(cursor).errorMessageExpected("def"));

        nextToken();

        /* def type */
        functionType = TYPE("func");

        nextToken();

        /* def type identifier */
        if (!tokens.get(cursor).tokenType.equals("Identifier"))
            errors.add(tokens.get(cursor).errorMessageIdentifier());
        else
            functionName = tokens.get(cursor).tokenName;

        nextToken();

        /* def type identifier ( */
        if (!tokens.get(cursor).tokenName.equals("("))
            errors.add(tokens.get(cursor).errorMessageExpected("("));

        nextToken();

        /* def type identifier ( flist */
        functionParameters = FLIST(new ArrayList<>());

        /* def type identifier ( flist ) */
        if (!tokens.get(cursor).tokenName.equals(")"))
            errors.add(tokens.get(cursor).errorMessageExpected(")"));

        nextToken();
        //checking if function declared before or not and open new scope
        if (functionType != TSlangType.INVALID && functionName != null && !functionParameters.isEmpty()){

            if (currentSymbolTable.funcIsExists(functionName))
                errors.add(tokens.get(cursor).errorMessage("Function already exists!"));
            else{
                //add function to current table
                currentSymbolTable.addFunctionToTable(new Function(functionName,functionType,functionParameters));

                //copy current to prev
                prevScope = (SymbolTable) currentSymbolTable.clone();
                //create new table with prev
                currentSymbolTable = new SymbolTable(prevScope);

                //add function arguments to current
                for (Variable functionParameter : functionParameters)
                    currentSymbolTable.addVariableToTable(functionParameter);
            }
        }

        /* def type identifier ( flist ) { */
        if (tokens.get(cursor).tokenName.equals("{")) {

            nextToken();

            BODY(functionType);

            if (!tokens.get(cursor).tokenName.equals("}"))
                errors.add(tokens.get(cursor).errorMessageExpected("}"));

            nextToken();
        }

        /* def type identifier ( flist ) return */  //***********BONUS
        else if (tokens.get(cursor).tokenName.equals("return")) {

            nextToken();

            //if function was not void
            if (functionType != TSlangType.NULL){
                TSlangType returnType = EXPR();
                if (returnType != functionType)
                    errors.add(tokens.get(cursor).errorMessage("Wrong return type. " + functionType + " is needed."));
            }
            else
                errors.add(tokens.get(cursor).errorMessage("function cannot return any value!"));

            if (!tokens.get(cursor).tokenName.equals(";"))
                errors.add(tokens.get(cursor).errorMessageExpected(";"));

            nextToken();
        }

        else
            errors.add(tokens.get(cursor).errorMessageExpected("{ or return"));

        currentSymbolTable = (SymbolTable) currentSymbolTable.previousBlock.clone();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    private TSlangType TYPE(String type) {
        switch (tokens.get(cursor).tokenName) {
            case "int":
                return TSlangType.INTEGER;
            case "str":
                return TSlangType.STRING;
            case "vector":
                return TSlangType.VECTOR;
        }

        if (type.equals("func")) {
            if (tokens.get(cursor).tokenName.equals("null"))
                return TSlangType.NULL;
            else{
                errors.add(tokens.get(cursor).errorMessageNeeded("data type after def"));
                return TSlangType.INVALID;
            }
        }
        errors.add(tokens.get(cursor).errorMessageNeeded("data type"));
        return TSlangType.INVALID;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    private ArrayList<Variable> FLIST(ArrayList<Variable> params) {

        if (tokens.get(cursor).tokenName.equals("{"))
            cursor--;

        else if (!tokens.get(cursor).tokenName.equals(")")) {

            TSlangType type = TYPE("var");

            nextToken();

            if(tokens.get(cursor).tokenName.equals(")"))
                errors.add(tokens.get(cursor).errorMessageIdentifier());
            else {
                if (!tokens.get(cursor).tokenType.equals("Identifier"))
                    errors.add(tokens.get(cursor).errorMessageIdentifier());

                params.add(new Variable(tokens.get(cursor).tokenName,type));

                nextToken();

                if (tokens.get(cursor).tokenName.equals(")")){}

                else if(tokens.get(cursor).tokenName.equals(",")){
                    nextToken();
                    FLIST(params);
                }
                else
                    errors.add(tokens.get(cursor).errorMessage("Wrong function arguments"));
            }
        }

        return params;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    private void BODY(TSlangType type) throws Exception {

        if (!tokens.get(cursor).tokenName.equals("}") && cursor == tokens.size()-1){
            //do nothing
        }
        else if(!tokens.get(cursor).tokenName.equals("}")) {
            STMT(type);
            BODY(type);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    private void STMT(TSlangType type) throws Exception {

        switch (tokens.get(cursor).tokenName) {
            case "if": {
                nextToken();
                if (!tokens.get(cursor).tokenName.equals("("))
                    errors.add(tokens.get(cursor).errorMessageExpected("("));

                nextToken();

                TSlangType x = EXPR();

                if (x != TSlangType.INTEGER)
                    errors.add(tokens.get(cursor).errorMessageExpected("integer"));

                if (!tokens.get(cursor).tokenName.equals(")"))
                    errors.add(tokens.get(cursor).errorMessageExpected(")"));

                nextToken();

                STMT(type);

                if (tokens.get(cursor).tokenName.equals("else")) {
                    nextToken();
                    STMT(type);
                }
                break;
            }
            case "while": {

                nextToken();

                if (!tokens.get(cursor).tokenName.equals("("))
                    errors.add(tokens.get(cursor).errorMessageExpected("("));

                nextToken();

                TSlangType x = EXPR();

                if (x != TSlangType.INTEGER)
                    errors.add(tokens.get(cursor).errorMessageExpected("integer"));

                if (!tokens.get(cursor).tokenName.equals(")"))
                    errors.add(tokens.get(cursor).errorMessageExpected(")"));

                nextToken();

                STMT(type);

                break;
            }
            case "for": {
                nextToken();

                String varName = null;
                TSlangType start = TSlangType.INVALID;
                TSlangType end;
                SymbolTable prev;

                if (!tokens.get(cursor).tokenName.equals("("))
                    errors.add(tokens.get(cursor).errorMessageExpected("("));

                nextToken();

                if (!tokens.get(cursor).tokenType.equals("Identifier"))
                    errors.add(tokens.get(cursor).errorMessageIdentifier());
                else
                    varName = tokens.get(cursor).tokenName;

                nextToken();

                if (!tokens.get(cursor).tokenName.equals("="))
                    errors.add(tokens.get(cursor).errorMessageExpected("="));

                nextToken();

                start = EXPR();

                if (start != TSlangType.INTEGER) {
                    errors.add(tokens.get(cursor).errorMessageExpected("integer"));
                    start = TSlangType.INVALID;
                }

                if (!tokens.get(cursor).tokenName.equals("to"))
                    errors.add(tokens.get(cursor).errorMessageExpected("to"));

                nextToken();

                end = EXPR();

                if (end != TSlangType.INTEGER) {
                    errors.add(tokens.get(cursor).errorMessageExpected("integer"));
                    end = TSlangType.INVALID;
                }

                if (!tokens.get(cursor).tokenName.equals(")"))
                    errors.add(tokens.get(cursor).errorMessageExpected(")"));

                nextToken();

                if (varName != null && start != TSlangType.INVALID && end != TSlangType.INVALID) {
                    prev = (SymbolTable) currentSymbolTable.clone();
                    currentSymbolTable = new SymbolTable(prev);
                    if (currentSymbolTable.varIsExists(varName))
                        if (currentSymbolTable.searchVar(varName).type != start)
                            errors.add(tokens.get(cursor).errorMessage("inValid!!"));
                        else
                            currentSymbolTable.addVariableToTable(new Variable(varName, start));
                }

                STMT(type);
                currentSymbolTable = (SymbolTable) currentSymbolTable.previousBlock.clone();
                break;
            }
            case "return": {
                nextToken();
                if (type != TSlangType.NULL) {
                    TSlangType x = EXPR();
                    if (x != type)
                        errors.add(tokens.get(cursor).errorMessageExpected(type.toString()));
                } else
                    errors.add(tokens.get(cursor).errorMessage("function cannot return any value!"));

                if (!tokens.get(cursor).tokenName.equals(";"))
                    errors.add(tokens.get(cursor).errorMessageExpected(";"));
                nextToken();
                break;
            }
            case "{": {
                nextToken();

                SymbolTable prev = (SymbolTable) currentSymbolTable.clone();
                currentSymbolTable = new SymbolTable(prev);

                BODY(type);

                if (!tokens.get(cursor).tokenName.equals("}"))
                    errors.add(tokens.get(cursor).errorMessageExpected("}"));
                nextToken();
                currentSymbolTable = (SymbolTable) currentSymbolTable.previousBlock.clone();
                break;
            }
            case "def": {
                FUNC();
                break;
            }
            case "var":{

                DEFVAR();

                if (!tokens.get(cursor).tokenName.equals(";"))
                    errors.add(tokens.get(cursor).errorMessageExpected(";"));

                nextToken();
                break;
            }
            default: {
                EXPR();
                if (!tokens.get(cursor).tokenName.equals(";"))
                    errors.add(tokens.get(cursor).errorMessageExpected(";"));

                nextToken();
                break;
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    private TSlangType EXPR() throws CloneNotSupportedException {
        TSlangType type = Logical_Or_EXPR();

        while (tokens.get(cursor).tokenName.equals("?")) {
            nextToken();

            if (type != TSlangType.INTEGER)
                errors.add(tokens.get(cursor).errorMessageExpected("Integer Expression"));

            TSlangType type_left = EXPR();

            if (!tokens.get(cursor).tokenName.equals(":"))
                errors.add(tokens.get(cursor).errorMessageExpected(":"));
            nextToken();

            TSlangType type_right = EXPR();

            if (type_left != type_right){
                errors.add(tokens.get(cursor).errorMessageExpr(type_left,tokens.get(cursor).tokenName, type_right));
                type = TSlangType.INVALID;
            }
            else
                type = type_left;
        }

        return type;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    private TSlangType Logical_Or_EXPR() throws CloneNotSupportedException {
        TSlangType type_left = Logical_And_EXPR();
        TSlangType type = type_left;
        while (tokens.get(cursor).tokenName.equals("||")){
            nextToken();

            TSlangType type_right = Logical_And_EXPR();

            if (type_left != TSlangType.INTEGER || type_right != TSlangType.INTEGER){
                errors.add(tokens.get(cursor).errorMessageExpr(type_left,tokens.get(cursor).tokenName, type_right));
                type = TSlangType.INVALID;
            }
            else if(type != TSlangType.INVALID)
                type = type_left;
            type_left = type_right;
        }
        return type;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    private TSlangType Logical_And_EXPR() throws CloneNotSupportedException {
        TSlangType type_left = Equality_EXPR();
        TSlangType type = type_left;

        while (tokens.get(cursor).tokenName.equals("&&")){
            nextToken();

            TSlangType type_right = Equality_EXPR();

            if (type_left != TSlangType.INTEGER || type_right != TSlangType.INTEGER){
                errors.add(tokens.get(cursor).errorMessageExpr(type_left,tokens.get(cursor).tokenName, type_right));
                type = TSlangType.INVALID;
            }
            else if(type != TSlangType.INVALID)
                type = type_left;
            type_left = type_right;
        }
        return type;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    private TSlangType Equality_EXPR() throws CloneNotSupportedException {
        TSlangType type_left = Compare_EXPR();
        TSlangType type = type_left;

        while(tokens.get(cursor).tokenName.equals("==") || tokens.get(cursor).tokenName.equals("!=")){
            nextToken();
            TSlangType type_right = Compare_EXPR();

            if (type_left != TSlangType.INTEGER || type_right != TSlangType.INTEGER){
                errors.add(tokens.get(cursor).errorMessageExpr(type_left,tokens.get(cursor).tokenName, type_right));
                type = TSlangType.INVALID;
            }
            else if(type != TSlangType.INVALID)
                type = type_left;
            type_left = type_right;
        }
        return type;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    private TSlangType Compare_EXPR() throws CloneNotSupportedException {
        TSlangType type_left = Addition_and_Subtraction_EXPR();
        TSlangType type = type_left;

        while (tokens.get(cursor).tokenName.equals(">") || tokens.get(cursor).tokenName.equals("<") || tokens.get(cursor).tokenName.equals(">=") || tokens.get(cursor).tokenName.equals("<=")){
            nextToken();

            TSlangType type_right = Addition_and_Subtraction_EXPR();

            if (type_left != TSlangType.INTEGER || type_right != TSlangType.INTEGER){
                errors.add(tokens.get(cursor).errorMessageExpr(type_left,tokens.get(cursor).tokenName, type_right));
                type = TSlangType.INVALID;
            }
            else if(type != TSlangType.INVALID)
                type = type_left;
            type_left = type_right;
        }

        return type;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    private TSlangType Addition_and_Subtraction_EXPR() throws CloneNotSupportedException {
        TSlangType type_left = Multiplication_and_Division_EXPR();
        TSlangType type = type_left;

        while (tokens.get(cursor).tokenName.equals("+") || tokens.get(cursor).tokenName.equals("-")){
            nextToken();

            TSlangType type_right = Multiplication_and_Division_EXPR();

            if (type_left != type_right){
                errors.add(tokens.get(cursor).errorMessageExpr(type_left,tokens.get(cursor).tokenName, type_right));
                type = TSlangType.INVALID;
            }
            else if(type != TSlangType.INVALID)
                type = type_left;
            type_left = type_right;

        }
        return type;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    private TSlangType Multiplication_and_Division_EXPR() throws CloneNotSupportedException {
        TSlangType type_left = Final_EXPR();
        TSlangType type = type_left;

        while (tokens.get(cursor).tokenName.equals("*") || tokens.get(cursor).tokenName.equals("/") || tokens.get(cursor).tokenName.equals("%")){
            nextToken();

            TSlangType type_right = Final_EXPR();

            if (type_left != TSlangType.INTEGER || type_right != TSlangType.INTEGER){
                errors.add(tokens.get(cursor).errorMessageExpr(type_left,tokens.get(cursor).tokenName, type_right));
                type = TSlangType.INVALID;
            }
            else if(type != TSlangType.INVALID)
                type = type_left;
            type_left = type_right;
        }
        return type;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    private TSlangType Final_EXPR() throws CloneNotSupportedException {

        TSlangType varType = TSlangType.INVALID;
        TSlangType funcType = TSlangType.INVALID;

        Token temp = null;

        //iden
        if (tokens.get(cursor).tokenType.equals("Identifier")){

            boolean varExists = currentSymbolTable.varIsExists(tokens.get(cursor).tokenName);
            boolean funExists = currentSymbolTable.funcIsExists(tokens.get(cursor).tokenName);

            if (!varExists && !funExists)
                errors.add(tokens.get(cursor).errorMessageNotFound(tokens.get(cursor).tokenName));

            Variable varSymbol = null;
            Function funSymbol = null;

            temp = tokens.get(cursor);

            if (varExists){
                varSymbol = currentSymbolTable.searchVar(tokens.get(cursor).tokenName);
                varType = varSymbol.type;
            }

            if (funExists){
                funSymbol = currentSymbolTable.searchFunc(temp.tokenName);
                funcType = funSymbol.type;
            }

            nextToken();

            if (tokens.get(cursor).tokenName.equals("[")){

                varType = TSlangType.INTEGER;

                if (varExists && varSymbol.type != TSlangType.VECTOR){
                    errors.add(tokens.get(cursor).errorMessageExpected("vector"));
                }
                nextToken();

                TSlangType index = EXPR();
                if (index != TSlangType.INTEGER){
                    errors.add(tokens.get(cursor).errorMessageExpected("integer"));
                }

                if (!tokens.get(cursor).tokenName.equals("]"))
                    errors.add(tokens.get(cursor).errorMessageExpected("]"));

                nextToken();

                if (tokens.get(cursor).tokenName.equals("=")){
                    nextToken();
                    TSlangType assign = EXPR();
                    if (assign != TSlangType.INTEGER)
                        errors.add(tokens.get(cursor).errorMessage("Wrong assignment!"));
                }
            }

            else if (tokens.get(cursor).tokenName.equals("=")){

                nextToken();

                TSlangType assign = EXPR();

                    if (funExists){
                        errors.add(tokens.get(cursor).errorMessage("Cannot assign to a function."));
                    }
                    else if(varExists && assign != varSymbol.type){
                        errors.add(tokens.get(cursor).errorMessage(varSymbol.type + " should be assigned!"));
                    }
                    else
                        varType = assign;
            }

            else if(tokens.get(cursor).tokenName.equals("(")){
                nextToken();

                if (varExists){
                    errors.add(tokens.get(cursor).errorMessage("should be a function!"));
                }
                if (funExists) {
                    funcType = funSymbol.type;
                    varType = TSlangType.INVALID;
                }

                ArrayList<TSlangType> args = CLIST(new ArrayList<>());

                if (funExists){
                    if (funSymbol.parameters.size() != args.size()){
                        errors.add(tokens.get(cursor).errorMessage("Wrong number of arguments!"));
                    }
                    for (int i = 0; i < Math.min(funSymbol.parameters.size(), args.size()); i++) {
                        if (args.get(i) != funSymbol.parameters.get(i).type) {
                            errors.add(tokens.get(i).errorMessage("arguments are not matching! there should be " + funSymbol.parameters.get(i).type));
                        }
                    }
                }

                if (!tokens.get(cursor).tokenName.equals(")"))
                    errors.add(tokens.get(cursor).errorMessageExpected(")"));
                nextToken();
            }

        }

        //number
        else if (tokens.get(cursor).tokenType.equals("Number")){
            varType = TSlangType.INTEGER;
            funcType = TSlangType.INVALID;
            nextToken();
        }
        //string
        else if (tokens.get(cursor).tokenType.equals("String")){
            varType = TSlangType.STRING;
            funcType = TSlangType.INVALID;
            nextToken();
        }

        //[ CList ]
        else if(tokens.get(cursor).tokenName.equals("[")){
            varType = TSlangType.VECTOR;
            funcType = TSlangType.INVALID;
            nextToken();

            CLIST(new ArrayList<>());
            if (!tokens.get(cursor).tokenName.equals("]"))
                errors.add(tokens.get(cursor).errorMessageExpected("]"));
            nextToken();
        }
        //!/+/- expr
        else if(tokens.get(cursor).tokenName.equals("!") || tokens.get(cursor).tokenName.equals("+") || tokens.get(cursor).tokenName.equals("-")){
            nextToken();
            TSlangType num = EXPR();
            if (num != TSlangType.INTEGER){
                errors.add(tokens.get(cursor).errorMessage("Wrong Expression!"));
                varType = TSlangType.INVALID;
            }
            else
                varType = num;
        }
        //( expr )
        else if(tokens.get(cursor).tokenName.equals("(")){
            nextToken();
            varType = EXPR();
            if (!tokens.get(cursor).tokenName.equals(")"))
                errors.add(tokens.get(cursor).errorMessageExpected("]"));
            nextToken();
        }

        else{
            varType = TSlangType.INVALID;
            funcType = TSlangType.INVALID;
            errors.add(tokens.get(cursor).errorMessage("Wrong expression"));
        }
        if (varType != TSlangType.INVALID)
            return varType;
        if (funcType != TSlangType.INVALID)
            return funcType;
        else
            errors.add(tokens.get(cursor).errorMessageNotFound(temp.tokenName));
        return TSlangType.INVALID;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    private void DEFVAR() throws Exception {

        if (!tokens.get(cursor).tokenName.equals("var"))
            errors.add(tokens.get(cursor).errorMessageExpected("var"));

        nextToken();

        TSlangType type = TYPE("var");

        if (type == TSlangType.NULL)
            errors.add(tokens.get(cursor).errorMessage("variable cannot define null."));

        nextToken();

        String id = null;

        if (!tokens.get(cursor).tokenType.equals("Identifier"))
            errors.add(tokens.get(cursor).errorMessageIdentifier());
        else
            id = tokens.get(cursor).tokenName;

        nextToken();

        if (type != null && type != TSlangType.NULL && id != null){
            if (currentSymbolTable.varIsExists(id))
                errors.add(tokens.get(cursor).errorMessage("variable already exists!"));
            else
                currentSymbolTable.addVariableToTable(new Variable(id, type));
        }

        if (tokens.get(cursor).tokenName.equals("=")){
            nextToken();
            EXPR();
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    private ArrayList<TSlangType> CLIST(ArrayList<TSlangType> args) throws CloneNotSupportedException {
        if (tokens.get(cursor).tokenName.equals("[") || tokens.get(cursor).tokenName.equals("(")){
            //do nothing
            return args;
        }
        TSlangType x = EXPR();
        args.add(x);
        if (tokens.get(cursor).tokenName.equals(",")){
            nextToken();
            ArrayList<TSlangType> args2 = CLIST(args);
            args.addAll(args2);
        }
        return args;
    }
}
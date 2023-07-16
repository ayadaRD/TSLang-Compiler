package Cheat.SymbolTable;

import java.util.ArrayList;

public class SymbolTable implements Cloneable {

    /*--ATTRIBUTES--*/

    public SymbolTable previousBlock;          //symbol table of upper level
    public ArrayList<Variable> vars;          //list of symbols
    public ArrayList<Function> funcs;          //list of symbols
    public boolean isRoot = false;


    /*--CONSTRUCTORS--*/

    //for initial scope
    public SymbolTable(){
        this.funcs = new ArrayList<>();
        this.vars = new ArrayList<>();
    }

    //for other scopes
    public SymbolTable(SymbolTable prev) throws CloneNotSupportedException {
        this.funcs = new ArrayList<>();
        this.vars = new ArrayList<>();
        this.previousBlock = (SymbolTable) prev.clone();
    }

    /*---METHODS---*/

    //for copying an object into another one by value
    public Object clone() throws CloneNotSupportedException { return super.clone(); }

    //for checking if an object with id name exists in list of symbols or not
    public boolean varIsExists(String id) throws CloneNotSupportedException {

        SymbolTable symbolTable = (SymbolTable) this.clone();        //copy symbol table of this class in a object

        do {        //search for a symbol with the name of id until a list is null
            for (Variable v : symbolTable.vars){
                if (v.name.equals(id)){
                    return true;
                }
            }
            if (symbolTable.isRoot)
                break;
            symbolTable = (SymbolTable) symbolTable.previousBlock.clone();        //back track and search in previous scopes
        }while (!symbolTable.isRoot);
        return false;        //if you reached here, that object doesn't exists
    }

    public boolean funcIsExists(String id) throws CloneNotSupportedException {

        SymbolTable symbolTable = (SymbolTable) this.clone();        //copy symbol table of this class in a object

        do {        //search for a symbol with the name of id until a list is null
            for (Function f : symbolTable.funcs){
                if (f.name.equals(id)){
                    return true;
                }
            }
            if (symbolTable.isRoot)
                break;
            symbolTable = (SymbolTable) symbolTable.previousBlock.clone();        //back track and search in previous scopes
        }while (!symbolTable.isRoot);
        return false;        //if you reached here, that object doesn't exists
    }

    //for adding a symbol into list of symbols
    public void addFunctionToTable(Function function) throws Exception{
        if(funcIsExists(function.name)){
            System.out.println("variable exists!");
        }
        else{
            this.funcs.add(function);
        }
    }

    public void addVariableToTable(Variable var) throws Exception{
        if(varIsExists(var.name)){
            System.out.println("variable exists!");
        }
        else{
            this.vars.add(var);
        }
    }

    //for getting a symbol
    public Variable searchVar (String symbol) throws CloneNotSupportedException {
        SymbolTable symbolTable = (SymbolTable) this.clone();

        while (symbolTable.vars != null) {
            for (Variable v : symbolTable.vars) {
                if (v.name.equals(symbol))
                    return v;
            }
            symbolTable = (SymbolTable) symbolTable.previousBlock.clone();
        }
        return null;
    }

    public Function searchFunc (String symbol) throws CloneNotSupportedException {
        SymbolTable symbolTable = (SymbolTable) this.clone();

        while (symbolTable.funcs != null) {
            for (Function f : symbolTable.funcs) {
                if (f.name.equals(symbol))
                    return f;
            }
            symbolTable = (SymbolTable) symbolTable.previousBlock.clone();
        }
        return null;
    }

}

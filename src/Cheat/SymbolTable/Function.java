package Cheat.SymbolTable;

import java.util.ArrayList;

public class Function extends Symbol implements Cloneable{

    /*---ATTRIBUTES---*/

    public String name;         //name of function
    public TSlangType type;        //return type of function
    public ArrayList<Variable> parameters;   //list of arguments

    //just for making sure that this symbol is function
    public boolean isFunction = true;
    public boolean isVariable = false;


    /*---CONSTRUCTORS---*/

    public Function(String name, TSlangType type, ArrayList<Variable> parameters){
        this.name = name;
        this.type = type;
        this.parameters = parameters;
    }

    public Function(){}


    /*---METHODS---*/

    public Object clone() throws CloneNotSupportedException { return super.clone(); }

    public String to_string(){
        String details = "";

        details += "Function Name: " + name + "\n";

        details += "Function Type: " + type + "\n";

        details += "Size Of Parameters: " + parameters.size() + "\n";

        String arguments = "Function Arguments:\n";

        for (int i = 0; i < parameters.size(); i++)
            arguments += parameters.get(i).to_string();

        return details + arguments;
    }
}

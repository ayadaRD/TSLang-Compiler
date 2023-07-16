package Cheat.SymbolTable;

public class Variable extends Symbol implements Cloneable{

    /*---ATTRIBUTES---*/

    public String name;         //name of variable
    public TSlangType type;         //type of variable

    //just for making sure this symbol is variable
    public boolean isFunction = false;
    public boolean isVariable = true;

    /*---CONSTRUCTOR---*/
    public Variable(String name, TSlangType type){
        this.name = name;
        this.type = type;
    }

    /*---METHOD---*/
    public Object clone() throws CloneNotSupportedException { return super.clone(); }

    public String to_string(){
        return "\tVariable Name: " + name + "\n\tVariable Type: " + type + "\n" ;
    }
}

package Cheat.SymbolTable;

import java.util.ArrayList;

public abstract class Symbol implements Cloneable{
    public String name;
    public TSlangType type;
    public ArrayList<Variable> parameters;
    public boolean isFunction;
    public boolean isVariable;
    public String to_string(){
        return "";
    }

    public Object clone() throws CloneNotSupportedException { return super.clone(); }

}

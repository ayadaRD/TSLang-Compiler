package Cheat;

import Cheat.SymbolTable.TSlangType;

public class Token {
    String tokenName;
    String tokenType;
    int tokenRow;
    int tokenColumn;

    public Token(String name, String type, int row, int column) {
        tokenName = name;
        tokenType = type;
        tokenRow = row;
        tokenColumn = column;
    }

    public String to_string(){
        return tokenName + "\t\t|\t" + tokenType + "\t|\t row " + tokenRow + " \t|\t column " + tokenColumn + "\n";
    }

    public String errorMessageExpected(String problem){
        return "Row " + this.tokenRow + ", Word " + this.tokenColumn + ": " + "\"" + problem + "\" " + "is expected!";
    }
    public String errorMessageNeeded(String problem){
        return "Row " + this.tokenRow + ", Word " + this.tokenColumn + ": " + problem + " is needed!";
    }
    public String errorMessageIdentifier(){
        return "Row " + this.tokenRow + ", Word " + this.tokenColumn + ": Wrong naming for identifier!";
    }
    public String errorMessage(String problem){
        return "Row " + this.tokenRow + ", Word " + this.tokenColumn + ": " + problem ;
    }
    public String errorMessageExpr(TSlangType type_left, String operator, TSlangType type_right){
        return "Row " + this.tokenRow + ", Word " + this.tokenColumn + ": " + "Wrong data types:\t" + type_left + " " + operator + " " + type_right;
    }
    public String errorMessageNotFound(String problem){
        return "Row " + this.tokenRow + ", Word " + this.tokenColumn + ": " + problem + " is not declared in this scope.";
    }
}

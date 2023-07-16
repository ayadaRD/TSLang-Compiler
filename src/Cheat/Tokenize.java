package Cheat;

import java.util.*;

public class Tokenize {

    //defining tokens
    ArrayList<String> keywords = new ArrayList<>(Arrays.asList("var", "def", "return", "if", "else", "while", "for", "to", "int", "vector", "str", "null"));
    ArrayList<String> delimiters = new ArrayList<>(Arrays.asList("(", ")", "{", "}", "[", "]", ";",":", ",", "?", "!"));
    ArrayList<String> comparators = new ArrayList<>(Arrays.asList("==", ">=", "<=", ">", "<", "!="));
    ArrayList<String> operators = new ArrayList<>(Arrays.asList("=", "+", "-", "*", "/", "\\", "%", "||", "&&"));

    //defining regex
    public String identifier = "[a-zA-Z_][a-zA-Z_0-9]*";
    public String number = "[0-9]+";
    public String comment = "\\#.*";
    public String string_single = "'[^'\\r\\n]*'";
    public String string_double = "\"[^\"\\r\\n]*\"";

    public ArrayList<String> code;

    public Tokenize(ArrayList<String> code){
        this.code = code;
    }

    //a function witch tokenizes every line
    public ArrayList<String> tokenize (String line){
        line = line.trim();
        //split line per spaces
        String[] result = line.split(" ");
        ArrayList<String> words = new ArrayList<>(Arrays.asList(result));

        //let's analyze line word by word
        for (int i = 0; i < words.size(); i++) {   //this loop counts words
            if (words.get(i).equals("#")){
                words.clear();
                words.add(line);
                break;
            }

            for (int j = 0; j < words.get(i).length(); j++) { //this loop counts the letters in each word

                char lec = words.get(i).charAt(j);  //let's see if there is any delimiter or operator in each word

                //entering in this condition means there is an operator or delimiter in j position
                if(delimiters.contains(String.valueOf(lec)) || comparators.contains(String.valueOf(lec)) || operators.contains(String.valueOf(lec))){

                    //being 1 means it's just an operator or delimiter
                    if(words.get(i).length() == 1)
                      continue;

                    //save the problem word
                    String old = words.get(i);
                    words.remove(i);

                    String first = old.substring(0,j);
                    String middle = String.valueOf(old.charAt(j));
                    String last = old.substring(j+1);

                    if (j == 0){
                        words.add(i, middle);
                        words.add(i + 1, last);
                    }
                    else if (j == old.length()-1){
                        words.add(i, first);
                        words.add(i + 1, middle);
                    }
                    else {
                        words.add(i, first);
                        words.add(i + 1, middle);
                        words.add(i + 2, last);
                    }
                }
            }
        }
        //now let's recognize two-part operators
        for (int i = 1; i < words.size(); i++) {
            if((operators.contains(words.get(i - 1)) && operators.contains(words.get(i)))   ||
               (comparators.contains(words.get(i - 1)) && operators.contains(words.get(i))) ||
               (operators.contains(words.get(i - 1)) && comparators.contains(words.get(i))) ||
               (delimiters.contains(words.get(i - 1)) && operators.contains(words.get(i)))){
                String op = words.get(i-1) + words.get(i);
                words.remove(i);
                words.remove(i-1);
                words.add(i-1, op);
            }
        }

        /*BONUS PART*/
        String temp = "";
        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).charAt(0) == '\"'){

                while (true){
                    temp += words.get(i);
                    words.remove(i);

                    if (temp.charAt(temp.length()-1) == '\"'){
                        break;
                    }
                    temp += " ";
                }
                words.add(i, temp);
            }
        }

        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).charAt(0) == '\''){

                while (true){

                    temp += words.get(i);
                    words.remove(i);

                    if (temp.charAt(temp.length()-1) == '\''){
                        break;
                    }
                    temp += " ";
                }
                words.add(i, temp);
            }
        }

        return words;
    }


    public ArrayList<Token> lex(){
        ArrayList<Token> tokens = new ArrayList<Token>();

        for (int i = 0; i < this.code.size(); i++) {
            ArrayList<String> words = tokenize(code.get(i));

            for (int j = 0; j < words.size(); j++) {
                String token = words.get(j);
                String label = check_token(token);

                if (!token.equals("unknown")){
//                    System.out.println(token + "\t\t|\t" + label + "\t|\t row " + (i + 1) + " \t|\t column " + (j + 1) + "\n");
                    tokens.add(new Token(token, label, i + 1, j + 1));
                }
                else {
                    String subToken = token.substring(0, token.length() - 1);

                    label = check_token(subToken);

//                    System.out.println(subToken + "\t\t|\t" + label + "\t|\t row " + (i + 1) + " \t|\t column " + (j + 1)+ "\n");
                    tokens.add(new Token(subToken, label, i + 1, j + 1));


                    String lastToken = token.substring(token.length() - 1);
                    label = check_token(lastToken);
                    if (!label.equals("unknown")){
//                        System.out.println(lastToken + "\t\t|\t" + label + "\t|\t row " + (i + 1) + " \t|\t column " + (j + 1)+ "\n");
                        tokens.add(new Token(lastToken, label, i + 1, j + 1));
                    }
                    else{
                        System.err.println("Error: Does not belong to language!");
                        System.err.println("Line: " + i + ", Status: " + label + " -> " + subToken);
                        return tokens;
                    }
                }
            }
        }
        return tokens;
    }

    public String check_token(String token){
        if(this.keywords.contains(token))
            return "Keyword";
        else if (this.delimiters.contains(token))
            return "Delimiter";
        else if (this.comparators.contains(token))
            return "Comparator";
        else if (this.operators.contains(token))
            return "Operator";
        else if (token.matches(this.string_double) || token.matches(this.string_single))
            return "String";
        else if (token.matches(this.number))
            return "Number";
        else if (token.matches(this.comment))
            return "Comment";
        else if (token.matches(this.identifier))
            return "Identifier";
        return "unknown";
    }
}
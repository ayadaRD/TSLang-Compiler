package Cheat;

import java.io.*;
import java.util.ArrayList;


public class LexicalAnalyzer {

    public static void read_code () throws IOException, FileNotFoundException {

        FileInputStream file = new FileInputStream ("src/Cheat/test.txt");
        BufferedReader buffer = new BufferedReader(new InputStreamReader(file));

        String line;
        ArrayList<String> lines = new ArrayList<>();

        while( (line = buffer.readLine()) != null ) {
            lines.add(line);
        }
        buffer.close();
        Tokenize token = new Tokenize(lines);
        System.out.println();
        ArrayList<Token> tokens = token.lex();

        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).tokenType.equals("Comment"))
                tokens.remove(tokens.get(i));
        }

        Parser parser = new Parser(tokens);
        try {
            parser.start_parsing();
        }catch (Exception e){

        }
        for (int i = 0; i <lines.size() ; i++) {
            System.out.println(i + 1 + "   " + lines.get(i));
        }

        System.out.println("\n");

        for (int i = 0; i < parser.errors.size(); i++) {
            System.out.println(parser.errors.get(i));
        }
        if (parser.errors.size() == 0)
            System.out.println("Compiled Successfully");

    }
}
# TSLang-Compiler
A compiler wriiten manually for TSLang in java.</br>
It might not be very wise to write such a compiler in java and without any helper tools... but that's how it is! :) </br>

## What parts of the compiler does this project include?
first of all i should say that this compiler is not complete. it doesn't convert the IR to machine statements (actually it not even generates the IR!), but i guarantee
that it shows the errors of your code and suggest you if you forgot a part of syntax to complete it.</br>
</br>Here is what it includes:
* **Lexical Analyzer** : This part, takes the code and breaks it into it's tokens. you give a txt file including code text to compiler and recieve an array of tokens
based on TSLang patterns.</br></br>
* **Syntax Analyzer** : This part was the most complicated one! the logic of syntax analyze is implemeted with recursive decent method. it means a lot of recursive functions calling each other...<br> before starting this part, you should have the unambiguous grammar of the target language.
</br>there is a recursive function for each variable in the grammer. </br></br>
* **Semantic Analyzer** : In this part we start saving datas and analyze code with accuracy. The package SymbolTable include classes for saving variables, functions and data in each scope.
this part might seems a little confusing because of tables but actually was not impossible. Also notice that this part is not seperated from syntax part and it is not
possible you implement it without that part.

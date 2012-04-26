/*
Implements the Syntax analysis stage of the front
end of a compiler.

Grammar:
<program> := <functions>
<functions> := <functions> <function> | <function>
<function> := <identType> function <ident>(<params>)<block>
<identType> := int | void
<params> := <params>,<ident>| <ident> | null
<block> := { <items> }
<items> := <items><item> | <item> | null
<item> := <decl> | <stmt>
<decl> := decl <ident>;
<stmt> := <expr>; | <if> | <while> | <print>;
<if> := if(<expr>)<block>
<while> := while(<expr>)<block>
<print> := print(<ident>)
<expr> := B = <expr> | B
B := B || C | C
C := c && D | D
D := D == G || D != G | G
G := G < E | G > E | G <= E | G >= E | E
E := E + T | E - T | T
T := T * F | T / F | F
F := (<expr>) | ident(params) | idlit
*/
import java.io.*;
import java.util.*;

public class SyntaxAnalyzer{
  private int lookAhead;
  private BufferedReader inFile;
  private File tempFile;
  private int tkCntr; //Token counter for debugging


  public SyntaxAnalyzer(File inF){
    try{
        tkCntr = 1; //Set to one as you read first token
        tempFile = inF;
        inFile = new BufferedReader(new FileReader(tempFile));
        getNextToken();
    }
    catch(Exception e){
      System.out.println("Syntax Analyzer Error 1");
        System.out.println(e);
        System.exit(1);
        return;
    }
  }

  public void run(){
    prog();
  }
  private void consume(int t){
    if(lookAhead == t){
      getNextToken();
    }
    else{
      System.out.println("Syntax Error");
      quit();
    }
  }

  private void quit(){
    try{
        inFile.close();
        File tempFile = new File("BLexx.txt");
        tempFile.delete();
        System.exit(1);
    }
    catch(Exception e){
      System.out.println("S Error 1");
      System.out.println(e);
      System.exit(1);
    }
  }

  private void quit(String err){
    try{
        System.out.println(err);
        System.out.println("Processed " + tkCntr + " Tokens");
        System.out.println("Current Token: " + lookAhead);
        inFile.close();
        File tempFile = new File("BLexx.txt");
        tempFile.delete();
        System.exit(1);
    }
    catch(Exception e){
      System.out.println("S Error 2");
      System.out.println(e);
      System.exit(1);
    }
  }

  public void getNextToken(){
    try{
      do{
        lookAhead = inFile.read();
        if(lookAhead == (int)'.'){
             lookAhead = inFile.read(); //Clear S value
             //Clear 8 digits, in semantics these will need to be recorded to update
             //Symbol table
             for(int i = 0; i < 9; i++){
               lookAhead = inFile.read();
             }
             //Should place ' ' in lookAhead
        }
      }while(lookAhead == (int)'\r' || lookAhead == (int)' ');
      //Process double digit token
      lookAhead -= 48;
      lookAhead *= 10;
      lookAhead = lookAhead + (inFile.read() - 48);

      System.out.println(lookAhead);
      tkCntr++;
    }
    catch(Exception e){
      System.out.println("S Error 4");
      try{
          System.out.println(e);
          File tempFile = new File("BLexx.txt");
          inFile.close();
          tempFile.delete();
          System.exit(1);
      }
      catch(Exception err){
        System.out.println("S Error 3");
        System.out.println(err);
        System.exit(1);
      }
    }
  }

   void prog(){
    //Progam will have to run out of a main function
    //If there is a successful return to this program then
    //the parse is a success
    funcs();
    System.out.println("Success");
  }
   void funcs(){
     func();
     funcsPrime();
   }
   
   void funcsPrime(){
     if(lookAhead == Token.tFunction){
       func();
       funcsPrime();
     }
   }

   void func(){
     identType();
     if(lookAhead == Token.tFunction){
       consume(Token.tFunction);
       if(lookAhead == Token.tIdentifier) consume(Token.tIdentifier);
       else quit("Expecting Identifier after 'function'");
       if(lookAhead == Token.opParenO){
         consume(Token.opParenO);
         params();
         if(lookAhead == Token.opParenC){
           consume(Token.opParenC);
           block();
         }
         else {
           System.out.println("Paren Error 1");
           quit("Exepcting ')'");
         }
       }
       else quit("Expecting '(' after identifier");
     }
     else quit("Expecting \"function\" after return type");
  }

   void identType(){
     if(lookAhead == Token.tInt) consume(Token.tInt);
     else if(lookAhead == Token.tVoid) consume(Token.tVoid);
     else return;
  }

   void params(){
     if(lookAhead == Token.tIdentifier) consume(Token.tIdentifier);
     while(true){
       if(lookAhead == Token.opComma){
         consume(Token.opComma);
         params();
       }
       else return;
     }
  }

   void block(){
     if(lookAhead == Token.opBrackO){
       consume(Token.opBrackO);
       items();
       if(lookAhead == Token.opBrackC) consume(Token.opBrackC);
       else quit("Expecting '}'");
     }
     else quit("Expectiong '{'");
  }

   void items(){
     item();
     itemsPrime();
  }

  void itemsPrime(){
    while(true){
      switch(lookAhead){
        case 12:
        case 15:
        case 16:
        case 18:
        case 42: item();
                 itemsPrime();
                 break;
        default: return;
      }
    }
  }


   void item(){
     if(lookAhead == Token.tDecl) decl();
     else stmt();
  }

   void decl(){
     consume(Token.tDecl);
     if(lookAhead == Token.tIdentifier){
       consume(Token.tIdentifier);
       if(lookAhead  == Token.opSemiColon) consume(Token.opSemiColon);
       else quit("Expecting ';'");
     }
     else quit("Expecting Identifier");
  }

   void stmt(){
     if(lookAhead == Token.tIf) ifSt();
     else if(lookAhead == Token.tWhile) whileSt();
     else if(lookAhead == Token.tPrint) print();
     else expr();
  }
  //If statement
   void ifSt(){
     consume(Token.tIf);
     if(lookAhead == Token.opParenO){
       consume(Token.opParenO);
       expr();
       if(lookAhead == Token.opParenC){
         consume(Token.opParenC);
         block();
       }
       else {
         System.out.println("Paren Error: 2");
         quit("Expecting ')'");
       }
     }
     else quit("Expecting '(' after 'if'");
  }

   void whileSt(){
     consume(Token.tWhile);
     if(lookAhead == Token.opParenO){
       consume(Token.opParenO);
       expr();
       if(lookAhead == Token.opParenC){
         consume(Token.opParenC);
         block();
       }
       else {
         System.out.println("Paren Error: 3");
         quit("Expecting ')'");
       }
     }
     else quit("Expecting '(' after 'while'");
  }

   void print(){
     consume(Token.tPrint);
     if(lookAhead == Token.opParenO){
       consume(Token.opParenO);
       if(lookAhead == Token.tIdentifier){
         consume(Token.tIdentifier);
         if(lookAhead == Token.opParenC){
           consume(Token.opParenC);
           if(lookAhead == Token.opSemiColon) consume(Token.opSemiColon);
           else quit("Expecting ';' after print()");
         }
         else{
           System.out.println("Paren Error 5");
           quit("Expecting ')'");
         }
       }
       else quit("Expecting Identifier");
     }
     else quit("Expecting '('");
   }

   void expr(){
     B();
     System.out.println("Expression");
     while(true){
       if(lookAhead == Token.opAssign){
         consume(Token.opAssign);
         expr();
       }
       else break;
     }
     if(lookAhead == Token.opSemiColon) consume(Token.opSemiColon);
  }

   void B(){
     C();
     while(true){
       if(lookAhead == Token.opOr){
         consume(Token.opOr);
         B();
       }
       else return;
     }
  }

   void C(){
     D();
     while(true){
       if(lookAhead == Token.opAnd){
         consume(Token.opAnd);
         C();
       }
       else return;
     }
  }

   void D(){
     G();
     while(true){
       if(lookAhead == Token.opEquals){
         consume(Token.opEquals);
         D();
       }
       else if(lookAhead == Token.opNotEquals){
         consume(Token.opNotEquals);
         D();
       }
       else return;
     }
  }

   void G(){
     E();
     while(true){
       if(lookAhead == Token.opLt){
         consume(Token.opLt);
         G();
       }
       else if(lookAhead == Token.opGt){
         consume(Token.opGt);
         G();
       }
       else if(lookAhead == Token.opGtoE){
         consume(Token.opGtoE);
         G();
       }
       else if(lookAhead == Token.opLtoE){
         consume(Token.opLtoE);
         G();
       }
       else return;
     }
  }

   void E(){
     T();
     while(true){
       if(lookAhead == Token.opAdd){
         consume(Token.opAdd);
         E();
       }
       else if(lookAhead == Token.opSubtract){
         consume(Token.opSubtract);
         E();
       }
       else return;
     }
  }

   void T(){
     F();
     while(true){
       if(lookAhead == Token.opMultiply){
         consume(Token.opMultiply);
         T();
       }
       else if(lookAhead == Token.opDivide){
         consume(Token.opDivide);
         T();
       }
       else return;
     }
  }

   void F(){
     if(lookAhead == Token.opParenO){
       consume(Token.opParenO);
       expr();
       if(lookAhead == Token.opParenC) consume(Token.opParenC);
       else{
         System.out.println("Paren Error 6");
         quit("Expecting ')'");              
       }
     }
     else if(lookAhead == Token.tIdentifier){
       consume(Token.tIdentifier); //Will look up Identifier in sTable later
       if(lookAhead == Token.opParenO){
         consume(Token.opParenO);
         params();
         if(lookAhead == Token.opParenC) consume(Token.opParenC);
         else {
           System.out.println("Paren Error 7");
           quit("Expecting ')'");
         }
       }
     }
     else if(lookAhead == Token.tInteger) consume(Token.tInteger); //Will look up Integer in sTable later
  }
}
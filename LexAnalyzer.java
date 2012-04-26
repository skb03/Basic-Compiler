/*
Lexical Analyzer
By: Seth Bane
4/2/2012
Purpose: This program is the beginnings of a front end compiler. It will accept an input file
and analyze lexically. It will output the appropriate tokens to be used in the next phase of 
syntax analysis.

Output: Currently outputs to a temporary file called BLexx.temp so that output can be sent to 
syntax analysis

Valid Lexemes are as follows based on an assignment by Dr. Gary Smith

Keywords
int void print function while return input decl (input was not part of the original grammar but is being added for functionality)
                                                (decl is used whenever an identifier is being declared)
decl (This is used to differentiate a declaration and a statement for later in Syntax analysis)
Identifiers
An identifier starts with an underscore or letter and is followed by zero or more letters, digits, or underscores. Reserved keywords(those listed above)
are not identifiers.

Integer Literals
An integer literal is a sequence of one or more decimal digits. To make things easier integer digits cannot start with a leading zero unless
the actual value is zero

Operators
; = , () {} || && != / * - + =< => ==

String literals
A string literal is a sequence of zero or more string characters surrounded by double quotes.

Comments
Comments are like java single comments.

Note: Keywords and identifiers are case sensitive

For consistency, this implementation requires that the following operators be preceded and followed by whitespace
" = , || && != / * - + =< => == " so Ex: a=8+5 is not valid with this implementation
*/

import java.io.*;
import java.util.*;

public class LexAnalyzer{
  private char lookAhead;
  private int state;
  private int EOF;
  private int LineError; //Used to track where a Lexical error is found
  private int opControl; //Used to preserve operator characters
  private int strCount;  //Tracks number of string literals in symbol table
  private int identCount; //Tracks number of identifiers in symbol table
  private BufferedReader inFile;
  private Formatter outFile;
  private ArrayList<Symbol> sTable;
  private StringBuffer inBuffer; //This will be used to record input in case the name needs to be saved
                                 //to the symbol table

  //Constructor that takes an FileInputStream as an argument
  public LexAnalyzer(BufferedReader input, Formatter output, ArrayList<Symbol> list){
    inFile = input;
    outFile = output;
    sTable = list;
    inBuffer = new StringBuffer();
    EOF = 0;
    LineError = 1;
    state = 0;
    opControl = 0;
    strCount = 0;
    identCount = 0;
  }
  //Get next Character
  public void getNextCharacter()
  {
    try{
      lookAhead = (char)inFile.read();
    }
    catch(Exception e)
    {
      quit(e);
      return;
    }
  }

  public void run(){
    while(true)
    {
      if(opControl == 0){
        try{
          do{
            EOF = inFile.read();
            lookAhead = (char)EOF;
          }while(lookAhead == '\r');
          if(lookAhead != '\n')
                      inBuffer.append(lookAhead);
        }
        catch(Exception e){
          quit(e);
          return;
        }
      }
      else opControl = 0;
      if(lookAhead == '\n')
             LineError++;
      if(EOF == -1)
             break;
      getNextToken();
    }
    lookAhead = '\n'; //In case the final token is a comment
    //Get final token
    getNextToken();
  }

  public void printToken(int tk){
    //Print token to output file
    /*
    Items added to the symbol table will be printed to the output file
    as follows: <token>.S<location in sTable> EX. 18.S5 is an identifier
    that is in the table at location 5. Current implementation prevents
    deletion from the symbol table
    */
    switch(tk){
      case 18://Add Identifier to Symbol table
           try{
              identCount++;
              sTable.add(new Symbol(tk, "ident" + identCount, 0));
              outFile.format("%d.S%08d ", tk, sTable.size());
              break;
              }
           catch(Exception e){
              System.out.println(e);
              quit(e);
           }
      case 21://Add String literal to symbol table
           try{
              strCount++;
              sTable.add(new Symbol(tk, "str" + identCount, 2));
              outFile.format("%d.S%d ", tk, sTable.size());
              break;
              }
           catch(Exception e){
              quit(e);
           }
      default:
           try{
               outFile.format("%d ", tk);
               break;
               }
           catch(Exception e){
               quit(e);
           }
    }
    //Reset State machine
    //Clear input buffer
    inBuffer.delete(1, inBuffer.length());
    state = 0;
  }

  //Exits the program on a System Exception
  void quit(Exception err){
    try{
        System.out.println(err);
        inFile.close();
        outFile.close();
        System.exit(1);
        return;
    }
    catch(Exception e){
      System.out.println(e);
      System.exit(1);
    }
  }
  //Exits the program on a Lexical Error
  void quit(String err){
    try{
        System.out.println(err);
        inFile.close();
        outFile.close();
        System.exit(1);
        return;
    }
    catch(Exception e){
      System.out.println(e);
      System.exit(1);
    }
  }

  //Analyze Character
  public void getNextToken()
  {
    /**********************************
    / Currently getNextToken() just prints the current token to the console.
    / When syntax is implemented LexAnalyzer will have to communicate to the syntax
    / analyzer.
    **********************************/
    //State Machine Switch statements
    switch(state)
    {
      case 0:
           switch(lookAhead)
           {
             case 'i': state = 1; //First cases send to keyword state machine
                       break;
             case 'v': state = 8;
                       break;
             case 'p': state = 12;
                       break;
             case 'f': state = 17;
                       break;
             case 'w': state = 25;
                       break;
             case 'r': state = 30;
                       break;
             case '0': state = 37; //Send to Integer 0 state machine
                       break;
             case 'd': state = 63;
                       break;
             case '1': //Non- Zero number entrys send to Integer state machine
             case '2':
             case '3':
             case '4':
             case '5':
             case '6':
             case '7':
             case '8':
             case '9': state = 38;
                       break;
             case ';': state = 39;
                       break;
             case '=': state = 40;
                       break;
             case ',': state = 42;
                       break;
             case '(': state = 43;
                       break;
             case ')': state = 44;
                       break;
             case '{': state = 45;
                       break;
             case '}': state = 46;
                       break;
             case '|': state = 47;
                       break;
             case '&': state = 49;
                       break;
             case '!': state = 51;
                       break;
             case '/': state = 53;
                       break;
             case '"': state = 55;
                       break;
             case '-': state = 57;
                       break;
             case '+': state = 58;
                       break;
             case '<': state = 59;
                       break;
             case '>': state = 61;
                       break;
             default:  state = 36; //Send to Identifer state machine
                       break;
           }
           break;
           //Cases 1 - 36 correspond to the keyword and identifier state machine
      case 1: if(lookAhead == 'n') state = 2;
              else if(lookAhead == 'f') state = 4;
              else state = 36;
              break;
      case 2: if(lookAhead == 't') state = 3;
              else if(lookAhead == 'p') state = 5;
              else state = 36;
              break;
      case 3: switch(lookAhead){
                case ' ':
                case '\t':
                case '\n': printToken(Token.tInt); //Whitespace will be discarded
                           break;
                //If followed by an operator looAhead needs to be preserved
                case ';':
                case ',':
                case '(':
                case ')':
                case '{':
                case '}':
                case '-':
                case '+':
                case '=':
                case '!':
                case '/':
                case '*':
                case '<':
                case '>': printToken(Token.tInt); //Will prevent run() from discarding the current
                          opControl = 1;       // character because it is part of a token
                          break;
                default: state = 36;
                         break;
              }
              break;
      case 4: switch(lookAhead){
                case ' ':
                case '\t':
                case '\n': printToken(Token.tIf); //Whitespace will be discarded
                           break;
                //If followed by an operator looAhead needs to be preserved
                case ';':
                case ',':
                case '(':
                case ')':
                case '{':
                case '}':
                case '-':
                case '+':
                case '=':
                case '!':
                case '/':
                case '*':
                case '<':
                case '>': printToken(Token.tIf); //Will prevent run() from discarding the current
                          opControl = 1;       // character because it is part of a token
                          break;
                default: state = 36;
                         break;
              }
              break;
      case 5: if(lookAhead == 'u') state = 6;
              else state = 36;
              break;
      case 6: if(lookAhead == 't') state = 7;
              else state = 36;
              break;
      case 7: switch(lookAhead){
                case ' ':
                case '\t':
                case '\n': printToken(Token.tInput); //Whitespace will be discarded
                           break;
                //If followed by an operator looAhead needs to be preserved
                case ';':
                case ',':
                case '(':
                case ')':
                case '{':
                case '}':
                case '-':
                case '+':
                case '=':
                case '!':
                case '/':
                case '*':
                case '<':
                case '>': printToken(Token.tInput); //Will prevent run() from discarding the current
                          opControl = 1;       // character because it is part of a token
                          break;
                default: state = 36;
                         break;
              }
              break;
      case 8: if(lookAhead == 'o') state = 9;
              else state = 36;
              break;
      case 9: if(lookAhead == 'i') state = 10;
              else state = 36;
              break;
      case 10: if(lookAhead == 'd') state = 11;
               else state = 36;
               break;
      case 11: switch(lookAhead){
                case ' ':
                case '\t':
                case '\n': printToken(Token.tVoid); //Whitespace will be discarded
                           break;
                //If followed by an operator looAhead needs to be preserved
                case ';':
                case ',':
                case '(':
                case ')': printToken(Token.tIdentifier);
                          printToken(Token.opParenC);
                          break;
                case '{':
                case '}':
                case '-':
                case '+':
                case '=':
                case '!':
                case '/':
                case '*':
                case '<':
                case '>': printToken(Token.tVoid); //Will prevent run() from discarding the current
                          opControl = 1;       // character because it is part of a token
                          break;
                default: state = 36;
                         break;
              }
              break;
      case 12: if(lookAhead == 'r') state = 13;
               else state = 36;
               break;
      case 13: if(lookAhead == 'i') state = 14;
               else state = 36;
               break;
      case 14: if(lookAhead == 'n') state = 15;
               else state =36;
               break;
      case 15: if(lookAhead == 't') state = 16;
               else state = 36;
               break;
      case 16: switch(lookAhead){
                case ' ':
                case '\t':
                case '\n': printToken(Token.tPrint); //Whitespace will be discarded
                           break;
                //If followed by an operator looAhead needs to be preserved
                case ';':
                case ',':
                case '(':
                case ')':
                case '{':
                case '}':
                case '-':
                case '+':
                case '=':
                case '!':
                case '/':
                case '*':
                case '<':
                case '>': printToken(Token.tPrint); //Will prevent run() from discarding the current
                          opControl = 1;       // character because it is part of a token
                          break;
                default: state = 36;
                         break;
              }
              break;
      case 17: if(lookAhead == 'u') state = 18;
               else state = 36;
               break;
      case 18: if(lookAhead == 'n') state = 19;
               else state = 36;
               break;
      case 19: if(lookAhead == 'c') state = 20;
               else state = 36;
               break;
      case 20: if(lookAhead == 't') state = 21;
               else state = 36;
               break;
      case 21: if(lookAhead == 'i') state = 22;
               else state = 36;
               break;
      case 22: if(lookAhead == 'o') state = 23;
               else state = 36;
               break;
      case 23: if(lookAhead == 'n') state = 24;
               else state = 36;
               break;
      case 24: switch(lookAhead){
                case ' ':
                case '\t':
                case '\n': printToken(Token.tFunction); //Whitespace will be discarded
                           break;                       //Function will be added to symbol table
                //If followed by an operator looAhead needs to be preserved
                case ';':
                case ',':
                case '(':
                case ')':
                case '{':
                case '}':
                case '-':
                case '+':
                case '=':
                case '!':
                case '/':
                case '*':
                case '<':
                case '>': printToken(Token.tFunction); //Will prevent run() from discarding the current
                          opControl = 1;       // character because it is part of a token
                          break;
                default: state = 36;
                         break;
              }
              break;
      case 25: if(lookAhead == 'h') state = 26;
               else state = 36;
               break;
      case 26: if(lookAhead == 'i') state = 27;
               else state = 36;
               break;
      case 27: if(lookAhead == 'l') state = 28;
               else state = 36;
               break;
      case 28: if(lookAhead == 'e') state = 29;
               else state = 36;
               break;
      case 29: switch(lookAhead){
                case ' ':
                case '\t':
                case '\n': printToken(Token.tWhile); //Whitespace will be discarded
                           break;
                //If followed by an operator looAhead needs to be preserved
                case ';':
                case ',':
                case '(':
                case ')':
                case '{':
                case '}':
                case '-':
                case '+':
                case '=':
                case '!':
                case '/':
                case '*':
                case '<':
                case '>': printToken(Token.tWhile); //Will prevent run() from discarding the current
                          opControl = 1;       // character because it is part of a token
                          break;
                default: state = 36;
                         break;
              }
              break;
      case 30: if(lookAhead == 'e') state = 31;
               else state = 36;
               break;
      case 31: if(lookAhead == 't') state = 32;
               else state = 36;
               break;
      case 32: if(lookAhead == 'u') state = 33;
               else state = 36;
               break;
      case 33: if(lookAhead == 'r') state = 34;
               else state = 36;
               break;
      case 34: if(lookAhead == 'n') state = 35;
               else state = 36;
               break;
      case 35: switch(lookAhead){
                case ' ':
                case '\t':
                case '\n': printToken(Token.tReturn); //Whitespace will be discarded
                           break;
                //If followed by an operator looAhead needs to be preserved
                case ';':
                case ',':
                case '(':
                case ')':
                case '{':
                case '}':
                case '-':
                case '+':
                case '=':
                case '!':
                case '/':
                case '*':
                case '<':
                case '>': printToken(Token.tReturn); //Will prevent run() from discarding the current
                          opControl = 1;       // character because it is part of a token
                          break;
                default: state = 36;
                         break;
              }
              break;
      case 36: //Identifiers will be added to symbol table in a later build
               switch(lookAhead){
                case ' ':
                case '\t':
                case '\n': printToken(Token.tIdentifier); //Whitespace will be discarded
                           break;
                //If followed by an operator lookAhead needs to be preserved
                case ';':
                case ',':
                case '(':
                case ')':
                case '{':
                case '}':
                case '-':
                case '+':
                case '=':
                case '!':
                case '/':
                case '*':
                case '<':
                case '>': printToken(Token.tIdentifier); //Will prevent run() from discarding the current
                          opControl = 1;       // character because it is part of a token
                          break;
                default: state = 36;
                         break;
              }
              break;
      case 37: if(lookAhead == ' ' || lookAhead == '\t' || lookAhead == '\n' || lookAhead == ';' || lookAhead == ')')printToken(Token.tInteger);
               else{
                 switch(lookAhead){
                   case '1': //Integers cannot start with a 0 unless the value itself is zero
                   case '2':
                   case '3':
                   case '4':
                   case '5':
                   case '6':
                   case '7':
                   case '8':
                   case '9': quit("Lex Error: Integers cannot start with a leading 0 Line: " + LineError);
                             break;
                   default: quit("Lex Error: Identifiers cannot start with a leading digit Line: " + LineError);
                            break;
                   }
                 }
                 break;
      case 38: if(lookAhead == ' ' || lookAhead == '\t' || lookAhead == '\n')printToken(Token.tInteger);
               else{
                 switch(lookAhead){
                   case '0':
                   case '1':
                   case '2':
                   case '3':
                   case '4':
                   case '5':
                   case '6':
                   case '7':
                   case '8':
                   case '9': state = 38;
                             break;
                   case ')': printToken(Token.tInteger);
                             printToken(Token.opParenC);
                             break;
                   case ';': printToken(Token.tInteger);
                             printToken(Token.opSemiColon);
                             break;
                   default:  quit("Lex Error: Integers must only contain digits Line:" + LineError);
                             break;
                   }
                 }
                 break;
      case 39: //Semi-colon
               if(lookAhead == ' ' || lookAhead == '\t' || lookAhead == '\n')printToken(Token.opSemiColon);
               else quit("Lex Error: ';' Cannot be followed by anything but whitespace Line: " + LineError);
               break;
      case 40: //Assignment operator
               if(lookAhead == ' ' || lookAhead == '\t' || lookAhead == '\n')printToken(Token.opAssign);
               else if(lookAhead == '=')state = 41; //Send to comparison state
               else{
                 quit("Lex Error: Incompatible character following '=' Line: " + LineError);
               }
               break;
      case 41: //Comparison operator
               if(lookAhead == ' ' || lookAhead == '\t' || lookAhead == '\n')printToken(Token.opEquals);
               else{
                 quit("Lex Error: Incompatible character following '==' Line: " + LineError);
               }
               break;
      case 42: //Comma
               if(lookAhead == ' ' || lookAhead == '\t' || lookAhead == '\n')printToken(Token.opComma);
               else{
                 quit("Lex Error: Incompatible character following ',' Line: " + LineError);
               }
               break;
               //The following operators can be immediately followed by any other character
      case 43: //Open Parenthesis
               if(lookAhead == ' ' || lookAhead == '\t' || lookAhead == '\n')printToken(Token.opParenO);
               else{ //Prevents loss of the following character if it is not whitespace
                  printToken(Token.opParenO);
                  state = 0;
                  getNextToken();
                  break;
               }
               break;
      case 44: //Close Parenthesis
               if(lookAhead == ' ' || lookAhead == '\t' || lookAhead == '\n')printToken(Token.opParenC);
               else{ //Prevents loss of the following character if it is not whitespace
                  printToken(Token.opParenC);
                  state = 0;
                  getNextToken();
                  break;
               }
               break;
      case 45: //Open Brackets
               if(lookAhead == ' ' || lookAhead == '\t' || lookAhead == '\n')printToken(Token.opBrackO);
               else{ //Prevents loss of the following character if it is not whitespace
                  printToken(Token.opBrackO);
                  state = 0;
                  getNextToken();
                  break;
               }
               break;
      case 46: //Close Brackets
               if(lookAhead == ' ' || lookAhead == '\t' || lookAhead == '\n')printToken(Token.opBrackC);
               else{ //Prevents loss of the following character if it is not whitespace
                  printToken(Token.opBrackC);
                  state = 0;
                  getNextToken();
                  break;
               }
               break;
      case 47: //Single Pipe must lead to double for logical OR
               if(lookAhead == '|') state = 48;
               else{
                 quit("Lex Error: Incompatible character following '|' Line: " + LineError);
               }
               break;
      case 48: //Logical OR in this implementation the OR must be preceded and followed by whitespace
               if(lookAhead == ' ' || lookAhead == '\t' || lookAhead == '\n')printToken(Token.opOr);
               else{
                 quit("Lex Error: Incompatible character following '||' Line: " + LineError);
               }
               break;
      case 49: //Single Ampersand must lead to double for logical AND
               if(lookAhead == '&') state = 50;
               else{
                 quit("Lex Error: Incompatible character following '&' Line: " + LineError);
               }
               break;
      case 50: //Logical AND
               if(lookAhead == ' ' || lookAhead == '\t' || lookAhead == '\n')printToken(Token.opAnd);
               else{ //Logical AND in this implementation must be preceded and followed by whitespace
                 quit("Lex Error: Incompatible character following '||' Line: " + LineError);
               }
               break;
      case 51: //Exclamation point, must lead to = in this implementation
               if(lookAhead == '=') state = 52;
               else{
                 quit("Lex Error: Incompatible character following '!' Line: " + LineError);
               }
               break;
      case 52: //Logical Not-Equals
               if(lookAhead == ' ' || lookAhead == '\t' || lookAhead == '\n')printToken(Token.opNotEquals);
               else{
                 quit("Lex Error: Incompatible character following '!=' Line: " + LineError);
               }
               break;
      case 53: // Division or if followed by another '/' is a Comment
               if(lookAhead == '/')state = 54;
               else if(lookAhead == ' ' || lookAhead == '\t' || lookAhead == '\n')printToken(Token.opDivide);
               else{
                 quit("Lex Error: Incompatible character following '/' Line: " + LineError);
               }
               break;
      case 54: //Comment, anything accept a newline character returns to the same state
               //Comments are ignored in the following steps of the compiler
               if(lookAhead == '\n')printToken(Token.tComment);
               else state = 54; //This line might be redundant
               break;
      case 55: //Quotes symbolizes the start of a string literal
               if(lookAhead == '"')state = 56;
               else state = 55;
               break;
      case 56: //End of string literal
               if(lookAhead == ' ' || lookAhead == '\t' || lookAhead == '\n')printToken(Token.tStringLit);
               break;
               //In future impelementation string literals will be saved to the symbol table
      case 57: //Subtraction operator
               if(lookAhead == ' ' || lookAhead == '\t' || lookAhead == '\n')printToken(Token.opSubtract);
               else{
                 quit("Lex Error: Incompatible character following '-' Line: " + LineError);
               }
               break;
      case 58: //Addition operator
               if(lookAhead == ' ' || lookAhead == '\t' || lookAhead == '\n')printToken(Token.opAdd);
               else{
                 quit("Lex Error: Incompatible character following '+' Line: " + LineError);
               }
               break;
      case 59: //Less than operator
               if(lookAhead == ' ' || lookAhead == '\t' || lookAhead == '\n')printToken(Token.opLt);
               else if(lookAhead == '=')state = 60;
               else{
                 quit("Lex Error: Incompatible character following '<' Line: " + LineError);
               }
               break;
      case 60: //Less than or equal to
               if(lookAhead == ' ' || lookAhead == '\t' || lookAhead == '\n')printToken(Token.opLtoE);
               else{
                 quit("Lex Error: Incompatible character following '<=' Line: " + LineError);
               }
               break;
      case 61: //Greater than
               if(lookAhead == ' ' || lookAhead == '\t' || lookAhead == '\n')printToken(Token.opGt);
               else if(lookAhead == '=')state = 62;
               else{
                 quit("Lex Error: Incompatible character following '>' Line: " + LineError);
               }
               break;
      case 62: //Greater than or equal to
               if(lookAhead == ' ' || lookAhead == '\t' || lookAhead == '\n')printToken(Token.opGtoE);
               else{
                 quit("Lex Error: Incompatible character following '>=' Line: " + LineError);
               }
               break;
      case 63: //Decl token
               if(lookAhead == 'e') state = 64;
               else state = 36;
               break;
      case 64: if(lookAhead == 'c') state = 65;
               else state = 36;
               break;
      case 65: if(lookAhead == 'l') state = 66;
               else state = 36;
               break;
      case 66: if(lookAhead == ' ' || lookAhead == '\t' || lookAhead == '\n')printToken(Token.tDecl);
               else state = 36;
               break;
      default: break;
         }
     }
}
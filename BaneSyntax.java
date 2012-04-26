/*
/ BaneSyntax.java
/ by: Seth Bane
/ Main entry point of a Syntax analyzer to be used as part of the
/ front end of a compiler
/ Input: Text file containing source code
/ Output: Success of Failure of Syntax stage
*/

import java.io.*;
import java.util.*;

public class BaneSyntax{
  public static void main(String args[]){
    //Variable declarations
    File tempFile; //Reference for file control
    BufferedReader inFile; //Source code input file, will be passed to Lex
    Formatter lexOutput; //Output from lexx will be sent to syntax
    String inFileName;
    Scanner sc;
    LexAnalyzer lexx;
    SyntaxAnalyzer syntax;
    ArrayList<Symbol> sTable;
    
    //Get input file name from user
    System.out.println("Enter file pathname: ");
    sc = new Scanner(System.in);
    inFileName = sc.nextLine();
    
    try{
      inFile = new BufferedReader(new FileReader(inFileName));
    }
    catch(Exception e){
      System.out.println(e);
      System.exit(1);
      return;
    }
    
    //Initialize symbol table and create lexx analyzer
    sTable = new ArrayList<Symbol>();
    
    //Create file for output
    tempFile = new File("BLexx.temp");
    if(tempFile.exists()){
      try{
        tempFile.delete();
        tempFile.createNewFile();
      }
      catch(Exception e){
        System.out.println("Error 1");
        System.out.println(e);
        System.exit(1);
        return;
      }
    }
    else{
      try{
        tempFile.createNewFile();
      }
      catch(Exception e){
        System.out.println("Error 2");
        System.out.println(e);
        System.exit(1);
        return;
      }
    }
    
    //Create output formatter
    try{
      lexOutput = new Formatter(tempFile);
    }
    catch(Exception e){
      System.out.println("Error 3");
        System.out.println(e);
        System.exit(1);
        return;
    }
    
    //Create lexx and run it
    lexx = new LexAnalyzer(inFile, lexOutput, sTable);
    lexx.run();
    
    //Close lexOutput to reset file pointer
    try{
      lexOutput.close();
    }
    catch(Exception e){
      System.out.println("Error 4");
        System.out.println(e);
        System.exit(1);
        return;
    }


    //Create Syntax analyzer and run it
    try{
      syntax = new SyntaxAnalyzer(tempFile);
      syntax.run();
    }
    catch(Exception e){
      System.out.println("Error 6");
        System.out.println(e);
        System.exit(1);
        return;
    }
    
    //Close all files and delete temporary file
    try{
        tempFile.delete();
        inFile.close();
    }
    catch(Exception e){
      System.out.println("Error 7");
        System.out.println(e);
        System.exit(1);
        return;
    }
  }
}
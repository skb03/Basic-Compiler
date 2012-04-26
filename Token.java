/*
Token Class
by: Seth Bane
4/2/2012
This class specifiers valid identifiers and their token value for use in the syntax analysis
*/
public class Token{
  public static int tInt        = 10;
  public static int tVoid       = 11;
  public static int tPrint      = 12;
  public static int tInput      = 13;
  public static int tFunction   = 14;
  public static int tIf         = 15;
  public static int tWhile      = 16;
  public static int tReturn     = 17;
  public static int tIdentifier = 18;
  public static int tInteger    = 19;
  public static int tSymbol     = 20;
  public static int tStringLit  = 21;
  public static int tComment    = 22;
  public static int opSemiColon = 23;
  public static int opAssign    = 24;
  public static int opComma     = 25;
  public static int opParenO    = 26; //Open paren
  public static int opParenC    = 27; //Close Paren
  public static int opBrackO    = 28; //Open Bracket
  public static int opBrackC    = 29; //Close Bracket
  public static int opOr        = 30; //Logical OR ||
  public static int opAnd       = 31; //Logical AND &&
  public static int opDivide    = 32;
  public static int opMultiply  = 33;
  public static int opSubtract  = 34;
  public static int opAdd       = 35;
  public static int opLtoE      = 36; //Less than or equal
  public static int opGtoE      = 37; //Greater than or equal
  public static int opLt        = 38; //Less than
  public static int opGt        = 39; //Greater than
  public static int opEquals    = 40;
  public static int opNotEquals = 41;
  public static int tDecl       = 42;
}
/*
/ Symbol.java
/ by: Seth Bane
/ Contains class descriptions of data types that go into the symbol
/ table of a compiler implementation
*/

public class Symbol{
  private String name;
  private int tokenType;
  private int dType; //Type of data the identifier points to if this symbol
                     // is an identifier
                     /*
                        0 = null
                        1 = integer
                        2 = string
                        3 = function
                     */
  //Will add value for later implementation

  Symbol(int tType, String inName, int dType){ //dType is only filled in if this is an
    //Initialize data values                     // identifier to represent the data type for
    name = inName;                               // semantic check later on
    tokenType = tType;
  }

  public String getName(){
    return name;
  }

  public int getTokenType(){
    return tokenType;
  }

  public void setTokenType(int tk){
    tokenType = tk;
  }

  public void setName(String str){
    name = str;
  }

  public int getDataType(){
    return dType;
  }

  public void setDataType(int inType){
    dType = inType;
  }
}


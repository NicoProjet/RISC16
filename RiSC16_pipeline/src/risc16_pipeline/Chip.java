package risc16_pipeline;
import java.awt.*;


public class Chip {
  private int data=0;      // data : int
  private String word="0000000000000000";     // data : string
  protected int state=0;     // 0 = idle  1=busy 2=latching 
  protected boolean isActive=true;//true=> l'instruction uilise cette chip
  protected Bus input[];
  private int busid[];     // input bus id
  private Bus output[];
  private int x=0,y=0,lg=0,ht=0,r=0;
  //protected Color color=new Color(0,180,214);//,180);//Color(168,192,246); 
  protected Color color=Color.cyan;
  //protected Color color=new Color(0,204,204);//new Color(0,153,255);
  private Color colorIdle=color,colorBusy=new Color(0,255,0), colorLatch=new Color(255,153,0), colorInactive=Color.gray;;
  protected int delay=0; //d�lais dans les chips
  
/////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////
  public Chip() {}
//----------------------------------------------------------------
  public Chip(int x,int y,int lg,int ht) {
    this.x=x;
    this.y=y;
    this.lg=lg;
    this.ht=ht;
    word=new String();}
//----------------------------------------------------------------
  public Chip(int x,int y,int lg,int ht,Color color) {
    this(x,y,lg,ht);
    this.color=color;
    this.colorIdle=color;}
//----------------------------------------------------------------
  public Chip(int x,int y,int r)/*,Color color)*/ {
    this.x=x;
    this.y=y;
    this.r=r; //radius
    //this.color=color;
    //this.colorIdle=color;
    word=new String();}
//----------------------------------------------------------------
  public Chip(int x,int y,int lg,int ht,Color color,Bus output,Bus input) {
    this(x,y,lg,ht);
    this.color=color;
    this.colorIdle=color;
    setInput(input);
    setOutput(output);
  }
//----------------------------------------------------------------
//----------------------------------------------------------------
  public Bus[] getInput(){   return input;}
  
  public void setInput(Bus[] in){
    input= in;
    busid= new int[in.length];
    for (int i = 0; i < in.length; i++)
      busid[i]=in[i].getId();}
  
  public void setInput(Bus in){
    input= new Bus[1];
    input[0]=in;
    busid= new int[1];
    busid[0]=in.getId();
  }
//----------------------------------------------------------------
  public Bus[] getOutput(){ return output;}
  public void setOutput(Bus[] out){ output= out;}
  public void setOutput(Bus out){
    output= new Bus[1];
    output[0]=out;}
//----------------------------------------------------------------

/////////////////////////////////////////////////////////////////
public void setIdle(){
	if(isActive){
		color=colorIdle;
	}
	state=0;
}
public void setBusy(){
	if(isActive){
		color=colorBusy;
	}
	//activeInput();
	state=1;
}
/*public void activeInput(){
	input[0].setColor(Color.green);
}*/
public void setLatch(){
	if(isActive){
		color=colorLatch;		
	}
	//desactiveInput();
	state=2;
}
/*public void desactiveInput(){
	input[0].setColor(Color.black);
}*/
public boolean isBusy(){return (state==1);}
public int getState(){return state;}
public void setColorIdle(){
	if(isActive){
		color=colorIdle; 
	}
}

public void setInactive(){color=colorInactive;isActive=false;}//si la chip n'est pas utilis�e pour une instruction
public void setActive(){color=colorIdle;state=0;isActive=true;}

public void setColorDefault(Color couleur){
	color =couleur;//if (isActive) juste sur cette ligne?
	colorIdle =couleur;
}
/////////////////////////////////////////////////////////////////
public void reset(){
	setIdle(); 
	this.data=0;
	this.word="0000000000000000";
	delay=0;
	}//+state=0?
/////////////////////////////////////////////////////////////////
  public int getData(){return data;}
  public void setData(int data){this.data=data;}
  public String getWord(){
	  
	  return word;}
  public void setWord(String word){this.word=word;}
/////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////
  public void act(){  
	  ++delay;
	  
	  if(!isActive){
		  this.data=input[0].getData(busid[0]);//on retire les signaux si la puce est inactive (gris�e)
	  }
	  
	  if (state==1 && delay==2)               
	   {
		   receive();
		   computes();//pour incrementeur, leftshift et signext
	       setLatch();
	       latch();
	   }
	  
	  if (state==0 && checkInput()){
	        setBusy();
	        
	        delay=1;
	  }

	  if (state==2 && delay==3){
		  setIdle();
	  }
  
  }
  public void act(boolean level){  // 1= raising edge || 0= fallingedge
	    if (level)
	    {
	 //------------------------------------------------------------
	      if (state==2)     setIdle();
	      //System.out.println("!! check "+checkInput());
	      
	      if (checkInput())  {        //  [verif si les entrees sont l�]
	        setBusy();
	        receive();                //  [copie dans latch entr�e]
	      computes();
	      }
	//------------------------------------------------------------
	  }else  {      //[pd l'�tat haut ===> ]  compute et prepare data sortie > latche out put
	//------------------------------------------------------------
	    if (state==1)                // si busy > latch
	     {
	       setLatch();
	       latch();
	     }
	//------------------------------------------------------------
	  }}
/////////////////////////////////////////////////////////////////
public boolean checkInput(){   // TRUE = > ok entree dispo
    //System.out.println("INC : check in    | id= "+busid[0]);
//  for (int i = 0; i < input.length; i++) input[i].isActive(busid[i]);
  return  input[0].isActive(busid[0]);
}
public boolean isInActive(int i){ return  input[i].isActive(busid[i]);}
//*****************************************************************
public void receive(){   this.data=input[0].getData(busid[0]);}  // par defaut
//*****************************************************************
public void computes(){}
//*****************************************************************
public void latch(){
  output[0].receive(data);
}
//*****************************************************************
public void receiveW(){  
	//System.out.println(">>>receive");
this.word=input[0].getWord(busid[0]);}
//------------------------------------------------------------
public int receive(int i){// i=> cas d'un composant � plusieurs entr�es
  this.data=input[i].getData(busid[i]);
  return this.data;
}
// difference entre receive et readInput : receive retire la donn�e du bus
public int readInput(int i){// i=> cas d'un composant � plusieurs entr�es
	  return input[i].readData();
}
public String receiveW(int i){
  //System.out.println(">>>receive");
  this.word=input[i].getWord(busid[i]);
  return this.word;
}

/*public void latchW(){
  output[0].receive(word);
}*/
/////////////////////////////////////////////////////////////////
public int add(int op1,int op2){ // addition bit � bit  (+carry)
  int nbit=16;
  String out=new String();
  boolean carry=false;
  String s1=new String(Integer.toBinaryString(op1));
  String s2=new String(Integer.toBinaryString(op2));
  while(s1.length()<nbit)  s1="0"+s1;
  while(s2.length()<nbit)  s2="0"+s2;
  for (int i=nbit-1; i>=0;i--)
  {
      if (s1.charAt(i) == '1' && s2.charAt(i) == '1'){  // 1+1=0 +carry
          if (carry) { out="1"+out; carry=true;}
          else       { out="0"+out; carry=true;}
      }else{
          if (s1.charAt(i)=='1' || s2.charAt(i)=='1'){   // 1+0=1 +carry
               if (carry) { out="0"+out; carry=true;}
               else       { out="1"+out; carry=false; }
          }else{
               if (carry) { out="1"+out; carry=false;} // 0+0 = 0 +carry
               else       { out="0"+out; carry=false;}
          }
      }
  }
  //System.out.println("ADD > S1= "+s1 + "        "+op1);
  //System.out.println("ADD > S2= "+s2 + "        "+op2);
  //System.out.println("ADD > XX= "+out+ "        "+Integer.parseInt(out,2));
  return   Integer.parseInt(out,2);
}
/*
 	public int add(int a,int b){
 		System.out.println("ALU ADD > \t  op1 = "+a+"   op2="+b);
 		if (a>=32768)  a=a-65536;
 		if (b>=32768)  b=b-65536;
 		System.out.println("ADD OP > >>\t  op1 = "+a+"   op2 ="+b);
 		int sum=a+b;
 		if (sum>65535)      sum=sum-65535;                 // -------------------------->>   OVERFLOW ??
 		//  System.out.println("ALU ADD > \t op1+op2 = "+super.getData());
 		 return sum;
 	}
*/

/////////////////////////////////////////////////////////////////
  public int getX(){return x;}
  public int getY(){return y;}
  public int getLg(){return lg;}
  public int getHt(){return ht;}
  public int getR(){return r;}
/////////////////////////////////////////////////////////////////
  public Color getColor(){return color;}
  public void setColor(Color color){this.color=color;} 
/////////////////////////////////////////////////////////////////
  public void drawRect(Graphics g){
      g.setColor(Color.darkGray);
      g.drawRect(x+1,y+1,lg,ht);
      g.setColor(Color.gray);
      g.drawRect(x+1,y+1,lg+1,ht+1);
      g.setColor(color);
      g.fillRect(x,y,lg,ht);
      g.setColor(Color.black);
      g.drawRect(x,y,lg,ht);
  }

  public void printText(Graphics g,int size,String txt,int x,int y,Color col){
      g.setColor(col);
      g.setFont(new Font("Arial", Font.BOLD, size));
      g.drawString(txt,x,y);
      setDFont(g);
  }

  public void setDFont(Graphics g){
    g.setColor(Color.black);
  g.setFont(new Font("Monospaced", Font.PLAIN, 12));
}

  public void printText(Graphics g,int size,String txt,int x,int y,Color col,int type){
  g.setColor(Color.white);
  g.setFont(new Font("Arial", Font.BOLD, size));
  g.drawString(txt,x-1,y-1);
  g.drawString(txt,x-1,y+1);
  g.drawString(txt,x+1,y+1);
  g.drawString(txt,x+1,y-1);
  g.setColor(col);
  g.drawString(txt,x,y);
  setDFont(g);
  }
  public void setLg(int lg){
	  this.lg=lg;
  }

  public void paint(Graphics g){
    g.setColor(Color.black);
    if (lg!=0 & ht!=0){   // rectangle
      drawRect(g);
    }else{                // circle
      g.setColor(Color.darkGray);
      g.fillOval(x+1, y+1, r, r);
      g.setColor(Color.gray);
      g.fillOval(x+1, y+1, r+1, r+1);
      g.setColor(color);
      g.fillOval(x, y, r, r);
      g.setColor(Color.black);
      g.drawOval(x, y, r, r);
  }}
/////////////////////////////////////////////////////////////////
}

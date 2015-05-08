import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class PaintableTextField {
	
	private int x,y,width,height;
	//private Color backgroundColor, textColor;
	private boolean selected;
	private ArrayList<String> textLines = new ArrayList<String>();
	private FontMetrics currentFontMetrics;
	private String defaultMessage;
	private int maxLines = -5, maxChars = -5;
	private boolean settingDefaultMessage, numsOnly, lettersOnly;

//	public InputBox(int x, int y, int width, int height, Color bc, Color tc, String text){
//		this.x = x;
//		this.y = y;
//		this.width = width;
//		this.height = height;
//		this.backgroundColor = bc;
//		this.textColor = tc;
//		
//		textLines.add(text);
//	}

	public PaintableTextField(int x, int y, int width, int height, String text, FontMetrics currentFontMetrics){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.currentFontMetrics = currentFontMetrics;
		this.defaultMessage = text;
		
		//Gives a starting line to prevent errors
		textLines.add("");
		//Add Default Message
		setToDefaultMessage();
	}
	
	public Rectangle getBounds(){
		return new Rectangle(x,y,width,height);
	}
		
	public void draw(Graphics2D g){
		
		//currentFontMetrics = g.getFontMetrics();
		
		//Draw Box
		//g.setColor(theme.getInputBoxBackgroundColor());
		g.fill(getBounds());
		
		//Draw Border
		//if (selected) 
			//g.setColor(theme.getInputBoxSelectedBorderColor());
		//else 
			//g.setColor(theme.getInputBoxBorderColor());
		g.draw(getBounds());
		
		//Draw Strings
		//g.setColor(theme.getFontColor());
		int yOffset = 0;
		for (String s: textLines){
			g.drawString(s, x, y+g.getFontMetrics().getHeight()+5 + yOffset);
			yOffset += g.getFontMetrics().getHeight()+2;
		}
	}
	
	public boolean ifSelected(){
		return selected;
	}

	public void checkIfPressed(MouseEvent e) {
		if (getBounds().contains(e.getX(), e.getY())){
			selected = true;
			//Clear text if showing default message (when clicked)
			if (getText().equals(defaultMessage))
				clear();
		}
		else{
			selected = false;
			//Replace with default message if blank (when clicked)
			if (getText().equals("")){
				setToDefaultMessage();
			}
		}
	}
	
	public void setToDefaultMessage(){
		settingDefaultMessage = true;
		for (char c : defaultMessage.toCharArray())
			addText(c);
		settingDefaultMessage = false;
	}
	
	private boolean checkIfValidChar(char c){
		
		if (numsOnly){
			try{
				Integer.parseInt(String.valueOf(c));
				return true;
			}catch(Exception e){
				say("Invalid");
				return false;
			}
		}
		else if(lettersOnly){
			try{
				Integer.parseInt(String.valueOf(c));
				return false;
			}catch(Exception e){
				say("Invalid");
				return true;
			}
		}
		else
			return true;
	}
	
	public void setNumsOnly(boolean b){
		numsOnly = b;
	}
	
	public void setLettersOnly(boolean b){
		lettersOnly = b;
	}
	
	public void setMaxChars(int i){
		maxChars = i;
	}
	
	public void addText(char key) {
		
		if (!willPutOverMaxChars(key) && !willPutOverMaxLines(key) && checkIfValidChar(key)){
			//Add char to last string of last line
			textLines.set(textLines.size()-1, textLines.get(textLines.size()-1) + key);

			//setup vars
			String currentLineString = textLines.get(textLines.size()-1);
			int index = textLines.size()-1;
			
			//If String goes past box border
			if (currentFontMetrics.stringWidth(currentLineString) > width)/* && (maxLines <= 0 || getLines()<maxLines))*/{
				//Remove last Character from previous string
				textLines.set(index, currentLineString.substring(0, currentLineString.length()-1));
				//Create a new line and add the last char from the previous string
				textLines.add(currentLineString.substring(currentLineString.length()-1));
				//Increase height of the box to fit new text line
				height += currentFontMetrics.getHeight()+2;
			}
		}
	}
	
	private boolean willPutOverMaxChars(char key) {
		if (!settingDefaultMessage && maxChars>0 && getTotalLength() >= maxChars)
			return true;
		else return false;
	}

	private boolean willPutOverMaxLines(char c){
		
		ArrayList<String> temp = new ArrayList<String>(textLines);
		
		//Add char to last string of last line
		temp.set(temp.size()-1, temp.get(temp.size()-1) + c);

		//If String goes past box border
		if (currentFontMetrics.stringWidth(temp.get(temp.size()-1)) > width && maxLines > 0 && getLines()>maxLines-1)
			return true;
		return false;
	}
	
	public void backSpace() {
		
		String currentLineString = textLines.get(textLines.size()-1);
		int index = textLines.size()-1;
		
		//remove line if empty
		if (textLines.size()>1) {
			if (currentLineString.equals("")){
				textLines.remove(index);
				currentLineString = textLines.get(textLines.size()-1);
				index = textLines.size()-1;
				height -= currentFontMetrics.getHeight()+2;
			}
		}
		
		//remove last char of string
		if (!currentLineString.equals(""))
			textLines.set(index, currentLineString.substring(0, currentLineString.length()-1));
	}
	
	public void clear(){
		for (int i = 0, beginningLength = getTotalLength(); i< beginningLength; i++){
			backSpace();
			//say(textLines);
		}
	}
	
	public int getTotalLength(){
		
		int total = 0;
		
		for (String s: textLines)
			total += s.length();
		
		return total;
	}
	
	public int getLines(){
		return textLines.size();
	}
	
	public String getText(){
		String text = "";
		
		//Concatenate strings
		for (String s: textLines){
			text += s;
		}
		
		return text;
	}

	public void keyTyped(KeyEvent k) {
		if (ifSelected()){
			if (k.getKeyChar() != 8 && k.getKeyChar() != 65535 && k.getKeyChar() != 10){
				addText(k.getKeyChar());
			}
			else if (k.getKeyChar() == 8){
				backSpace();
			}
		}
		
		//8 = backspace
		//10 = enter  ?
		//65535 = shift  ?
	}
	
	public void setMaxLines(int i){
		maxLines = i;
	}

	public void say(Object s) {
		System.out.println(this.getClass().getName() + ": " + s);
	}
	
}
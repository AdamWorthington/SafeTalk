
public class Player {
	private int x = 0;
	private int y = 0;
	private String name = "";
	
	public Player(){
		
	}
	public void updateX(int curr){
		x = curr;
	}
	public void updateY(int curr){
		y = curr;
	}
	public void updateName(String curr){
		name = curr;
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public String getName(){
		return name;
	}
}

import java.net.*;
import java.io.*;

public class ServerThread{
	
	static final int maxLog = 10;
	static Server2 serverList[] = new Server2[maxLog]; 
	static String nameList[] = new String[maxLog];
	

	public static void main(String[] args) throws IOException {
		int checkLogin = 0;
		if (args.length != 1) {
			System.err.println("Usage: java ServerThread <port number>");
			System.exit(1);
		}
		int portNumber = Integer.parseInt(args[0]);
		introMessage(portNumber);
		clearOnlineList();
		new recieveThreadMain(maxLog).start();
		
		try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
			while (true) {
				Server2 curr = new Server2(serverSocket.accept(), checkLogin = assignLogin(), maxLog);
				curr.start();
				if(checkLogin >= 0){
					serverList[checkLogin] = curr;
				}
			}
		} catch (IOException e) {
			System.err.println("Could not listen on port " + portNumber);
			System.exit(-1);
		}
	}
	
	public static int assignLogin(){
		for(int i = 0; i < maxLog; i++){
			if(ServerThread.nameList[i] == null){
				
				ServerThread.nameList[i] = "NOTNULL";
				return i;
			}
		}
		
		return -1;
	}
	
	public static void clearOnlineList(){
		File file = new File("E:\\eclipse\\workspace\\SafeTalk\\status\\online.txt");
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.print("");
		writer.close();
	}
	
	public static void introMessage(int portNumber) {
		try
	    {
	        final String os = System.getProperty("os.name");

	        if (os.contains("Windows"))
	        {
	            Runtime.getRuntime().exec("cls");
	        }
	        else
	        {
	            Runtime.getRuntime().exec("clear");
	        }
	    }
	    catch (final Exception e)
	    {
	        System.out.println("Failed to clear terminal (does not affect functionality)");
	    }
		
		System.out
				.println("Open source project initially developed by Phlux Software, (phluxsoftware@gmail.com).");
		System.out.println("Server successfully started on port " + portNumber
				+ ".");
	}

}

class recieveThreadMain extends Thread {

	int maxLog;
	public recieveThreadMain(int maxLog){
		this.maxLog = maxLog;
	}
	public void run() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String line;
		
		
		try {
			while ((line = reader.readLine()) != null) {
				processMessage(line);
			}
		} catch (IOException e) {
			print("Couldn't read the next line", 1);
			e.printStackTrace();
		}
	}
	
	public void print(String s, int type){
		if(type == 1){
			System.err.println(s);
		}
		else{
			System.out.println(s);
		}
		
	}

	private void processMessage(String line) {
		String instruction = "";
		int pos = 0;
		if(line.contains(" ")){
			pos = line.indexOf(" ");
			instruction = line.substring(0, pos);
		}
		else{
			instruction = line;
		}
		
		switch (instruction) {
		
		case "/msg": {
			
			line = line.substring(pos + 1);
			pos = line.indexOf(" ");
			String name = line.substring(0, pos);
			line = line.substring(pos + 1);
			boolean check = true;
			for (int i = 0; i < maxLog; i++) {
				if (ServerThread.nameList[i] != null) {
					if (ServerThread.nameList[i].equalsIgnoreCase(name)) {
					
						ServerThread.serverList[i].sendMessage("[SERVER]: " + line);
						check = false;
					}
				}
			}
			if(check){
				print("Couldn't find user: " + name, 0);
			}
			break;
		}
		case "/msg2": {
			line = line.substring(pos + 1);
			pos = line.indexOf(" ");
			String name = line.substring(0, pos);
			line = line.substring(pos + 1);
			ServerThread.serverList[Integer.parseInt(name)].sendMessage(line);
			break;
		}
		case "/slots": {
			for(int i = 0; i < maxLog; i++){
				if(ServerThread.serverList[i] != null){
					System.out.println("Position " + i + " is in use.");
				}
			}
			break;
		}
		case "/broadcast": {
			line = line.substring(pos + 1);
			
			for(int i = 0; i < maxLog; i++){
				if(ServerThread.serverList[i] != null){
					ServerThread.serverList[i].sendMessage("[SERVER BROADCAST]: " + line);
				}
			}
			break;
		}
		default: {
			print("The command \"" + instruction + "\" was not recognized.", 0);
			print("Please use the syntax \"/<command> <target> <info>\"", 0);
			
		}
		
		}
	}
}


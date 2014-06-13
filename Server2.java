import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Server2 extends Thread {

	public Socket sock; // Socket name for reference
	PrintWriter pwrite; // Writing tool to talk to client
	int id = -1;
	@SuppressWarnings("unused")
	private Socket socket;
	int maxLog;

	public Server2(Socket socket, int counter, int maxLog) { // basic
																// constructor
		super("ServerThread");
		this.sock = socket;
		this.id = counter;
		this.maxLog = maxLog;
	}

	public void run() {
		OutputStream ostream = null;
		try {
			ostream = sock.getOutputStream();
		} catch (IOException e1) {
			System.err.println("couldn't get output stream.");
			e1.printStackTrace();
		}
		pwrite = new PrintWriter(ostream, true);
		if (id == -1) {

			pwrite.println("Server is too busy at the moment. Try again later. SERVER_COMMAND:EXIT");
			return;
		} else {
			new recieveThread(sock, pwrite, id, maxLog).start();
		}
	}

	public void sendMessage(String input) {
		pwrite.println(input);
	}
}

class recieveThread extends Thread {
	BufferedReader receiveRead;
	String currentUser;
	Socket sock;
	PrintWriter pwrite;
	int id;
	int maxLog;
	Player player;
	boolean notInGame = true;
	
	public recieveThread(Socket socket, PrintWriter pwriter, int id, int maxLog) {
		this.sock = socket;
		this.pwrite = pwriter;
		this.id = id;
		this.maxLog = maxLog;
	}

	public void run() {

		String receiveMessage;
		boolean didLogin = false;

		InputStream istream = null;
		try {
			istream = sock.getInputStream();
		} catch (IOException e) {
			System.err.println("Couldn't establish input stream.");
			e.printStackTrace();
			System.exit(1);
		}
		receiveRead = new BufferedReader(new InputStreamReader(istream));

		while (!didLogin) {
			didLogin = authenticate(receiveRead);
		}

		if (didLogin) {
			
			while (true) {
				try {
					if ((receiveMessage = receiveRead.readLine()) != null) {
						processMessage(receiveMessage, "normal");
					}
				} catch (IOException e) {

					removeOnline();
					return;
				}
			}
		}
	}

	private void sendMessage(String s) {
		pwrite.println(s);
	}

	private void processMessage(String input, String special) {
		if(!input.startsWith("/pos")){
			record(input);
		}
		
		
		int checkWords = 0;
		String firstWord;
		String help = "/Hello - say hi to your friendly server\n"
				+ "/whoami - find out who you trully are on the inside... of the server.\n"
				+ "/help - What is the number for 911 again?\n"
				+ "/list - Lists all online user\n"
				+ "/msg <name> <message> - Sends a message to an online user\n"
				+ "/setpass <New Password> - resets your password";
				//+ "/msgGroup <name> <name> <name> ... <\"Message\">";
	
		//input = input.toLowerCase();
		if(input.contains(" ")){
			firstWord = input.substring(0, input.indexOf(" "));
			
			input = input.substring((input.indexOf(" ") + 1));
		}
		else{
			checkWords = 1;
			firstWord = input;
		}
		//Start commands
		
		switch (firstWord) {

		case "/hello": {
			sendMessage("Hey");
			break;
		}
		case "/list": {
			String temp = "";
			for (int i = 0; i < maxLog; i++) {
				if(ServerThread.nameList[i] != null){
					temp += ServerThread.nameList[i] + "\n";
				}
			}
			sendMessage(temp);
			break;
		}
		case "/whoami": {
			String p = sock.getRemoteSocketAddress().toString();
			sendMessage("You are " + currentUser + ". Connected on IP: " + p);
			break;
		}
		case "/help": {
			sendMessage(help);
			break;

		}
		case "/msg": {
			
			
			int pos = input.indexOf(" ");
			if(pos == -1){
				break;
			}
			String name = input.substring(0, pos);
			input = input.substring(pos + 1);
			if(name == null){
				name = "";
			}
			if(input == null){
				input = "";
			}
			if(input.contains("SERVER_COMMAND")){
				sendMessage("Message can not contain in \"SERVER_COMMAND\"");
				break;
			}
			boolean check = false;
			for (int i = 0; i < maxLog; i++) {
				if (ServerThread.nameList[i] != null) {
					if (ServerThread.nameList[i].equalsIgnoreCase(name)) {
						ServerThread.serverList[i].sendMessage("(Private)[" + currentUser + "]: " + input);
						sendMessage("(Sent) [" + name + "]: " + input);
						check = false;
					}
				}
			}
			if(check){
				sendMessage("Couldn't find user: " + name);
			}
			break;
		}
		case "/setpass":{
			File file = new File("E:\\eclipse\\workspace\\SafeTalk\\accounts\\" + currentUser + ".txt");
			PrintWriter writer = null;
			try {
				writer = new PrintWriter(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			writer.print(input);
			writer.close();
		}
		case "/msgGroup": {
			break;
		}
		
		case "/logout": {
			sendMessage("SERVER_COMMAND:EXIT");
			break;
		}
		
		case "/game":{
			if(notInGame){
				player = new Player();
				player.updateName(currentUser);
				ServerThread.playerList[id] = player;
				notInGame = false;
			}
		}
		
		case "/pos":{
			int pos = input.indexOf(" ");
			if(pos == -1){
				break;
			}
			String name = input.substring(0, pos);
			input = input.substring(pos + 1);
			player.updateX(Integer.parseInt(name));
			player.updateY(Integer.parseInt(input));
			sendMessage(posString() + " SERVER_COMMAND:POS");
			break;
		}
		
		default: {
			if(firstWord.startsWith("/")){
				sendMessage("Didn't recognize command...");
				break;
			}
			for(int i = 0; i < maxLog; i++){
				if(ServerThread.serverList[i] != null){
					if(checkWords == 0){
					ServerThread.serverList[i].sendMessage("[" + currentUser + "]: " + firstWord + " " + input);
					}
					else{
						ServerThread.serverList[i].sendMessage("[" + currentUser + "]: " + firstWord);
					}
				}
			}
			break;
		}
		}
		
		//End commands
		
	}

	private String posString(){
		String total = "";
		int counter = 0;
		for(int i = 0; i < maxLog; i++){
			if(ServerThread.playerList[i] != null){
				total += " " + ServerThread.playerList[i].getName() + " " + ServerThread.playerList[i].getX() + " " + ServerThread.playerList[i].getY();
				++counter;
			}
		}
		total = counter + total;
		return total;
	}
	
	private void record(String input) {
		String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("E:\\eclipse\\workspace\\SafeTalk\\logs\\" + currentUser + ".txt", true)))) {
		    out.println(timeStamp + ": " + input);
		}catch (IOException e) {
		    System.err.println("Couldn't find user record: " + currentUser);
		}
		
	}

	private boolean authenticate(BufferedReader receiveRead) {

		String receiveMessage = "";
		String username = "";
		String password = "";

		pwrite.println("Username: ");
		try {
			if ((receiveMessage = receiveRead.readLine()) != null) {
				username = (receiveMessage);
			}
		} catch (IOException e) {
			return false;
		}
		pwrite.println("Password:");
		try {
			if ((receiveMessage = receiveRead.readLine()) != null) {
				password = (receiveMessage);
			}
		} catch (IOException e) {
			return false;
		}

		if (!isOnline(username)) {
			pwrite.println("User: " + username + " is already logged in. 0");
			return false;
		}

		File f = new File("E:\\eclipse\\workspace\\SafeTalk\\accounts\\"
				+ username + ".txt");
		if (!f.exists() && !f.isDirectory()) {
			pwrite.println("Login Failed. Try again. 0");
			return false;
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(
					"E:\\eclipse\\workspace\\SafeTalk\\accounts\\" + username
							+ ".txt"));
		} catch (FileNotFoundException e) {
			System.err.println("Failed on creating a Buffered Reader");
			e.printStackTrace();
		}
		try {
			String line = br.readLine();
			if (line.equals(password)) {
				currentUser = username;
				System.out.println(currentUser + " has logged in to pos " + id);
				
				ServerThread.nameList[id] = currentUser;
				pwrite.println("Login passed 1");
				addOnline();
				return true;
			} else {
				pwrite.println("Login Failed. Try again. 0");
				return false;
			}
		} catch (IOException e) {
			System.err.println("Failed on readline");
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				System.err.println("couldn't close the Buffered Reader");
				e.printStackTrace();
			}
		}
		return false;

	}

	private boolean isOnline(String user) {
		ArrayList<String> names = null;
		try {
			names = onlineList();
		} catch (IOException e) {
			System.err.println("Failed to get list...");
		}
		for (int i = 0; i < names.size(); i++) {
			if (names.get(i).equalsIgnoreCase(user)) {
				return false;
			}
		}
		return true;
	}

	private ArrayList<String> onlineList() throws IOException {

		String file = "E:\\eclipse\\workspace\\SafeTalk\\status\\online.txt";
		ArrayList<String> names = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		// Read from the original file and write to the new
		// unless content matches data to be removed.
		while ((line = br.readLine()) != null) {
			names.add(line);
			if (!line.trim().equals(currentUser)) {
			}
		}
		br.close();
		return names;
	}

	public void addOnline() throws IOException {
		String path = "E:\\eclipse\\workspace\\SafeTalk\\status\\online.txt";
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(path, true)));
			out.println(currentUser);
			out.close();
		} catch (IOException e) {
			System.out.println("Failed on METHOD: addOnline.");
		}
	}

	public void removeOnline() {

		ServerThread.nameList[id] = null;
		ServerThread.serverList[id] = null;
		ServerThread.playerList[id] = null;

		String file = "E:\\eclipse\\workspace\\SafeTalk\\status\\online.txt";
		try {

			File inFile = new File(file);
			if (!inFile.isFile()) {
				System.out.println("Parameter is not an existing file");
				return;
			}
			// Construct the new file that will later be renamed to the original
			// filename.
			File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

			BufferedReader br = new BufferedReader(new FileReader(file));
			PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

			String line = null;

			// Read from the original file and write to the new
			// unless content matches data to be removed.
			while ((line = br.readLine()) != null) {

				if (!line.trim().equals(currentUser)) {

					pw.println(line);
					pw.flush();
				}
			}

			System.out.println(currentUser
					+ " has disconnected. (Exit Command)");
			pw.close();
			br.close();

			// Delete the original file
			if (!inFile.delete()) {
				System.out.println("Could not delete file");
				return;
			}

			// Rename the new file to the filename the original file had.
			if (!tempFile.renameTo(inFile))
				System.out.println("Could not rename file");

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			System.out
					.println("Failed at finding file in METHODD: removeOnline ");
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("Failed at IO in METHOD: removeOnline ");
		}
	}
	
}
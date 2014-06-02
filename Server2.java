import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server2 extends Thread {
	
	public Socket sock; // Socket name for reference
	PrintWriter pwrite; // Writing tool to talk to client
	int id = -1;
	@SuppressWarnings("unused")
	private Socket socket;

	public Server2(Socket socket, int counter) { // basic constructor
		super("ServerThread");
		this.sock = socket;
		this.id = counter;
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
		if(id == -1){
			
			pwrite.println("Server is too busy at the moment. Try again later. SERVER_COMMAND:EXIT");
			return;
		}
		else{
			new recieveThread(sock, pwrite, id).start();
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
	
	public recieveThread(Socket socket, PrintWriter pwriter, int id){
		this.sock = socket;
		this.pwrite = pwriter;
		this.id = id;
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
		
		while(!didLogin){
			didLogin = authenticate(receiveRead);
		}
		
		if (didLogin) {
			while (true) {
				try {
					if ((receiveMessage = receiveRead.readLine()) != null) {
						System.out.println(receiveMessage);
					}
				} catch (IOException e) {
					
					removeOnline();
					return;
				}
			}
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
			System.out.println("User: " + username + " is already logged in.");
			return false;
		}

		File f = new File("E:\\eclipse\\workspace\\SafeTalk\\accounts\\"
				+ username + ".txt");
		if (!f.exists() && !f.isDirectory()) {
			pwrite.println("Login Failed. Try again.");
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
				System.out.println(currentUser + " has logged in. ");
				ServerThread.nameList[id] = currentUser;
				pwrite.println("Login passed");
				addOnline();
				return true;
			} else {
				pwrite.println("Login Failed. Try again.");
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
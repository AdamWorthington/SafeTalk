import java.io.*;
import java.net.*;

public class Client2 {

	public static void main(String[] args) throws Exception {
		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);
		Socket sock = new Socket(hostName, portNumber);
		BufferedReader keyRead = new BufferedReader(new InputStreamReader(
				System.in));
		OutputStream ostream = sock.getOutputStream();
		PrintWriter pwrite = new PrintWriter(ostream, true);
		logGUI curr = new logGUI(pwrite);

		new recieveThread2(sock, curr).start();

		while (!curr.getStatus()) {
			curr.logInGUI();
		}

		String sendMessage;
		while (true) {
			sendMessage = keyRead.readLine();
			pwrite.println(sendMessage);
			System.out.flush();
		}
	}
}

class recieveThread2 extends Thread {
	Socket sock;
	logGUI wGUI;

	public recieveThread2(Socket sock, logGUI curr) {
		this.sock = sock;
		this.wGUI = curr;
	}

	public void run() {

		String receiveMessage;
		InputStream istream = null;
		try {
			istream = sock.getInputStream();
		} catch (IOException e) {
			System.err.println("Couldn't establish input stream.");
			e.printStackTrace();
			System.exit(1);
		}
		BufferedReader receiveRead = new BufferedReader(new InputStreamReader(
				istream));

		while (true) {
			try {
				if ((receiveMessage = receiveRead.readLine()) != null) {
					if (!wGUI.getStatus()) {
						if (receiveMessage.endsWith("1")) {
							wGUI.passed();
						}
					} else {
						
						if (receiveMessage.endsWith("SERVER_COMMAND:EXIT")) {
							System.exit(1);
						}
						else if(receiveMessage.endsWith("SERVER_COMMAND:POS")){
							String temp = receiveMessage;
							int pos = temp.indexOf(" ");
							String numberOnline = temp.substring(0, pos);
							temp = temp.substring(pos + 1);
							int tempNum = Integer.parseInt(numberOnline);
							wGUI.mGUI.game.racquet.setNumberOnline(tempNum);
							String name = "";
							int x = 0;
							int y = 0;
							
							for(int i = 0; i < tempNum; i++){
								pos = temp.indexOf(" ");
								name = temp.substring(0, pos);
								temp = temp.substring(pos + 1);
								pos = temp.indexOf(" ");
								x = Integer.parseInt(temp.substring(0, pos));
								temp = temp.substring(pos + 1);
								pos = temp.indexOf(" ");
								y = Integer.parseInt(temp.substring(0, pos));
								temp = temp.substring(pos + 1);
								wGUI.mGUI.game.racquet.setNamePos(i, name);
								wGUI.mGUI.game.racquet.setXPos(i, x);
								wGUI.mGUI.game.racquet.setYPos(i, y);
							}
							
						}
						else{
							wGUI.appendIt(receiveMessage);
						}
						
					}
				}
			} catch (IOException e) {
				System.out
						.println("Server dissconnected... Killing current application.");
				System.exit(1);
			}
		}
	}

}
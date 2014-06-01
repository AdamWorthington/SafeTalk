import java.io.*;
import java.net.*;

public class Client2 {
		
	public static void main(String[] args) throws Exception {
		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);
		Socket sock = new Socket(hostName, portNumber);
		BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in)); 
		OutputStream ostream = sock.getOutputStream();
		PrintWriter pwrite = new PrintWriter(ostream, true);
		
		new recieveThread2(sock).start();
		
		String sendMessage;
		while (true) {
			sendMessage = keyRead.readLine(); 
			pwrite.println(sendMessage); 
			System.out.flush(); 
		}
	}
	
}
class recieveThread2 extends Thread
{
	Socket sock;
	public recieveThread2(Socket sock){
		this.sock = sock;
	}
	public void run(){

		String receiveMessage;
		InputStream istream = null; 
		try {
			istream = sock.getInputStream();
		} catch (IOException e) {
			System.err.println("Couldn't establish input stream.");
			e.printStackTrace();
			System.exit(1);
		}
		BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));
		
		while (true) {
			try {
				if ((receiveMessage = receiveRead.readLine()) != null) {
					System.out.println(receiveMessage);
				}
			} catch (IOException e) {
				System.out.println("Other Client dissconnected... Killing current application.");
				System.exit(1);
			}
		} 
	}
}


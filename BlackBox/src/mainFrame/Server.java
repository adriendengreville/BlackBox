package mainFrame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	ServerSocket socketServer;
	Socket socketDuServer;
	BufferedReader in;
	PrintWriter out;
	
	public Server(){
		try{
			socketServer = new ServerSocket(6969);
			socketDuServer = socketServer.accept();
			out = new PrintWriter(socketDuServer.getOutputStream());
				out.flush();
				
			socketDuServer.close();
			socketServer.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
}

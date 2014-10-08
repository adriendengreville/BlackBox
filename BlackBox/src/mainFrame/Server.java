/*
 * Description : Cette classe est l'implémentation du serveur du chat.
 */

package mainFrame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	//ATTRIBUTS
		ServerSocket socketServer;
		Socket socketDuServer;
		BufferedReader in;
		PrintWriter out;
	
	//MÉTHODES
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
		}//ServerCSTR
	
	//SET-GETTER
		
}

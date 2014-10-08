/*
 * Description : Cette classe est l'implémentation du client du chat.
 */
package mainFrame;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import javafx.beans.property.StringProperty;


class Client /*implements User*/ {
	//ATTRIBUTS
		private String pseudo;  
		private String password;
		
		private String message;  
				
		private String serverAdress;
	
	//MÉTHODES
		public Client(){
//			Socket socketCL;
//			
//			try{
//				socketCL = new Socket(InetAddress.getLocalHost(), 6969);
//					socketCL.close();
//			}catch (UnknownHostException e){
//				e.printStackTrace();
//			}catch (IOException e){
//				e.printStackTrace();
//			}
		}//Client CSTR
	
		public void connect(){
			System.out.println();	
		}
		
		private void send(){
			
		}
	//SET-GETTER
		private void setUser(String name){
			this.pseudo = name;
		}
}

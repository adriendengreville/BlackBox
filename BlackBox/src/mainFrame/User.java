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
		
		private InetAddress adresseCL;
		private InetAddress adresseS;
				
		/*private Vector<Integer>*/ 
		int serverPrivateKey;
		int serverPublicKey;
		int clientPrivateKey;
		int clientPublicKey;
		
		private StringProperty serverAdress;
		
		private Controleur ctrl;
	
	//MÉTHODES
		public int encrypt(int publicKey) {
			// TODO Auto-generated method stub
			return publicKey;
		}
	
		public int decrypt(int privateKey) {
			// TODO Auto-generated method stub
			return privateKey;
		}
	
		public void setServerPrivateKey(int privateKey) {
			this.serverPrivateKey = privateKey;
		}
	
		public int getServerPrivateKey() {
			return this.serverPrivateKey;
		}
	
		public void setServerPublicKey(int publicKey) {
			this.serverPublicKey = publicKey;
		}
	
		public int getServerPublicKey() {
			return this.serverPrivateKey;
		}
		
		public void setClientPrivateKey(int privateKey) {
			this.clientPrivateKey = privateKey;
		}
	
		public int getClientPrivateKey() {
			return this.clientPrivateKey;
		}
	
		public void setClientPublicKey(int publicKey) {
			this.clientPublicKey = publicKey;
		}
	
		public int getClientPublicKey() {
			return this.clientPrivateKey;
		}
	
		public void computeRSA_Key() {
			// TODO Auto-generated method stub
		}
		
		public Client(){
			Socket socketCL;
			
			try{
				socketCL = new Socket(InetAddress.getLocalHost(), 6969);
					socketCL.close();
			}catch (UnknownHostException e){
				e.printStackTrace();
			}catch (IOException e){
				e.printStackTrace();
			}
		}//Client CSTR
		
		private void setUser(String name){
			this.pseudo = name;
		}
		
		public void connect(){
			//adresseCL = InetAddress.getLocalHost();
			ctrl = new Controleur();
			ctrl.setMessageBox("coucou");	
		}
		
		private void send(){
			
		}

}

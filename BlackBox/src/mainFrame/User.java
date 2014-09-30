package mainFrame;

import java.util.Vector;

import javafx.beans.property.StringProperty;

//public interface User {
//	//MÉTHODES
//		int encrypt (int publicKey);
//		int decrypt (int privateKey);
//		
//		void setPrivateKey(int privateKey);
//		int getPrivateKey();
//		
//		void setPublicKey(int publicKey);
//		int getPublicKey();
//		
//		void computeRSA_Key();
//}//IUser

class Client /*implements User*/ {
	//ATTRIBUTS
		private StringProperty pseudo; 
		private StringProperty password;
		
		private String message;  
		
		/*private Vector<Integer>*/ 
		int serverPrivateKey;
		int serverPublicKey;
		int clientPrivateKey;
		int clientPublicKey;
		
		private StringProperty serverAdress;
	
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

		}//Client CSTR
		
		private void setUser(String name){
		
		}

}

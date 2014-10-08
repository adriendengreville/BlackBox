/*
 * Description : Cette classe est l'implémentation de tout ce qui concerne le codage des informations.
 */
package mainFrame;

public class Cryptage {
	//ATTRIBUTS
		long privateKey;
		long publicKey;
	
	//MÉTHODES
		public long encrypt(long publicKey) {
			// TODO Auto-generated method stub
			return publicKey;
		}
	
		public long decrypt(long privateKey) {
			// TODO Auto-generated method stub
			return privateKey;
		}
	
		public void computeRSA_Key() {
			// TODO Auto-generated method stub
		}
		
	//SET-GETTER
		public void setPrivateKey(long privateKey) {
			this.privateKey = privateKey;
		}
	
		public long getPrivateKey() {
			return this.privateKey;
		}
	
		public void setPublicKey(long publicKey) {
			this.publicKey = publicKey;
		}
	
		public long getPublicKey() {
			return this.publicKey;
		}
}

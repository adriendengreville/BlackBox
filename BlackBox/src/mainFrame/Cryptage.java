/*
 * Description : Cette classe est l'implémentation de tout ce qui concerne le codage des informations.
 */
package mainFrame;

import java.util.Vector;

public class Cryptage {
	//ATTRIBUTS
		int privateKey;
		int publicKey;
		
		Vector<Integer> tablePr;
	
	//MÉTHODES
		public int encrypt(int publicKey) {
			// TODO Auto-generated method stub
			return publicKey;
		}
	
		public int decrypt(int privateKey) {
			// TODO Auto-generated method stub
			return privateKey;
		}
	
		public void computeRSA_Key() {
			
		}
		
		private void genererPremier() {
			int a = 0;
			int b = 0;
			int pr;
			
			for (int p = 0; p < 20000;) {
				b = 2;
				pr = 0;
				
				if (!(a % 2 == 0)){
					while (b <= Math.sqrt(a)){
						if (a % b == 0) {
							b = (int) Math.sqrt(a);
							pr = 1;
						}
						b++;
					}
					if (pr == 0) {
						p++;
						tablePr.add(a);
					}
				}
				a++;
			}
		}//genererPremier
		
	//SET-GETTER
		public void setPrivateKey(int privateKey) {
			this.privateKey = privateKey;
		}
	
		public int getPrivateKey() {
			return this.privateKey;
		}
	
		public void setPublicKey(int publicKey) {
			this.publicKey = publicKey;
		}
	
		public int getPublicKey() {
			return this.publicKey;
		}
		
	public static void main(String[] args) {
        Cryptage test = new Cryptage();
        test.genererPremier();
        
    }
}

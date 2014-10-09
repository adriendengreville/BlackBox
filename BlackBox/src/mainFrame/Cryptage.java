/*
 * Description : Cette classe est l'implémentation de tout ce qui concerne le codage des informations.
 */
package mainFrame;

import java.math.BigInteger;
import java.util.Vector;

public class Cryptage {
	//ATTRIBUTS
		int privateKey;
		int publicKey;
		
		Vector<Integer> tablePr = new Vector<Integer>();
	
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
			Integer p1;
			Integer q1;
			BigInteger p;
			BigInteger q;
			BigInteger n;
			BigInteger phi;
			
			genererPremier();		//on etabli la liste de nombres premiers
			
			p1 = tablePr.get((int) (Math.random() * tablePr.size()));
			
			do{	//on retente jusqu'à ce que p < q
				q1 = tablePr.get((int) (Math.random() * tablePr.size()));
			}while (p1 > q1);
			
			p = new BigInteger(p1.toString());
			q = new BigInteger(q1.toString());
			
			n 	= p.multiply(q);
			phi = (p.subtract(new BigInteger())) * (q1 - 1);
			
			System.out.println("p = " + p1 + "\n"+
					"q = " + q1 + "\n"+
					"n = " + n + "\n"+
					"phi = " + phi + "\n");
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
		
		private boolean isPremier(int number){
			int nb 		 = number;
			int compter  = 0;
			boolean test = false;
			int limite 	 = (int) (Math.sqrt(nb) + 1);
			
			if (nb % 2 == 0){
				test = true;
			}else{
				for (int i = 3; i < limite; i += 2, compter++){
					if (nb % i == 0)
						test = true;
				}
			}
			
			if (!test)
				System.out.println(nb + " nombre premier, nombre iterations = " + compter);
			else
				System.out.println(nb + " n'est pas premier.");
			
			return !test;
		}
		
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
        test.computeRSA_Key();
//        test.genererPremier();
//        test.isPremier(test.tablePr.get((int) (Math.random() * test.tablePr.size())));
        
    }
}

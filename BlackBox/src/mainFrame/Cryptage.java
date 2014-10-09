/*
 * Description : Cette classe est l'implémentation de tout ce qui concerne le codage des informations.
 */
package mainFrame;

import java.math.BigInteger;
import java.util.Vector;

public class Cryptage {
	//ATTRIBUTS
		BigInteger privateKey;
		BigInteger publicKey;
		String messageTest = "Bonjour je suis une tulipe codée";
		Vector<Integer> tableCodee;
		
		Vector<Integer> tablePr = new Vector<Integer>();
	
	//MÉTHODES
		
		public Cryptage(){
			genererPremier(); 	//on etablit la liste des nombres premiers
		}
		
		public Vector<Integer> encrypt(BigInteger publicKey, String message) {
			for (int i = 0; i < message.length(); ++i){
				
			}
			return null;
		}
	
		public String decrypt(BigInteger privateKey) {
			// TODO Auto-generated method stub
			return null;
		}
	
		public void computeRSA_Key() {
			Integer p1;
			Integer q1;
			BigInteger p;
			BigInteger q;
			BigInteger n;
			BigInteger phi;
			BigInteger d;
			BigInteger e;
			
			//déplacement de la création de la liste dans le constructeur
			
			p1 = tablePr.get((int) (Math.random() * tablePr.size()));
			
			do{	//on retente jusqu'à ce que p < q
				q1 = tablePr.get((int) (Math.random() * tablePr.size()));
			}while (p1 > q1);
			
			//nombres premiers choisis
			p = new BigInteger(p1.toString());
			q = new BigInteger(q1.toString());
			
			n 	= p.multiply(q);
			phi = (p.subtract(new BigInteger("1"))).multiply((q.subtract(new BigInteger("1"))));	//phi = (p-1) * (q-1)
			
			if (PGCD(p, q) == 1){
				System.out.println(p + " et " + q + " sont premiers entre eux.");
			}
			
			System.out.println("p = " + p1 + "\n"+
					"q = " + q1 + "\n"+
					"n = " + n + "\n"+
					"phi = " + phi + "\n");
			
			d = new BigInteger(q.add(new BigInteger("1")).toString());	// d fait parti du couple de clé privée 
			while(PGCD(phi, d) != 1)	//on augmente de un d jusqu'à ce qu'il soit premier avec phi
				d = d.add(new BigInteger("1"));
			
			System.out.println("d = " + d);
			
			e = new BigInteger(d.modInverse(phi).toString());	//e fait parti du couple de clés publiques et vaut d^-1 mod phi 
			
			System.out.println("e = " + e);
		}//computeRSA_Key
		
		private void genererPremier() {		//genere 20 000 nombres premiers en une seconde
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
		
		@SuppressWarnings("unused")
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
		}//isPremier
		
		private int PGCD(BigInteger a, BigInteger b){
			int res;
			
			while(! a.multiply(b).equals(new BigInteger("0"))){
				res = a.compareTo(b);
				if (res == 1)	//si a > b alors res = 1
					a = a.subtract(b);
				else
					b = b.subtract(a);
			}
			
			res = a.compareTo(new BigInteger("0"));
			if (res == 0)
				return b.intValue();
			else
				return a.intValue();
		}//PGCD
		
	//SET-GETTER
		public void setPrivateKey(BigInteger privateKey) {
			this.privateKey = privateKey;
		}
	
		public BigInteger getPrivateKey() {
			return this.privateKey;
		}
	
		public void setPublicKey(BigInteger publicKey) {
			this.publicKey = publicKey;
		}
	
		public BigInteger getPublicKey() {
			return this.publicKey;
		}
		
	public static void main(String[] args) {
        Cryptage test = new Cryptage();
        test.computeRSA_Key();
//        test.genererPremier();
//        test.isPremier(test.tablePr.get((int) (Math.random() * test.tablePr.size())));
        
    }
}

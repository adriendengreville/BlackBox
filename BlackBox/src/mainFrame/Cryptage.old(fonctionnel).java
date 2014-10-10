/*
 * Description : Cette classe est l'implémentation de tout ce qui concerne le codage des informations.
 */
package mainFrame;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Vector;

public class Cryptage {
	//ATTRIBUTS
		BigInteger commonKey;
		BigInteger privateKey;
		BigInteger publicKey;
		Vector<Byte> tableCodee;
		
		Vector<Integer> tablePr = new Vector<Integer>();
	
	//MÉTHODES
		
		public Cryptage(){
			genererPremier(); 	//on etablit la liste des nombres premiers
		}
		
		public BigInteger encrypt(String message) {
			
			
			byte[] tableByte = message.getBytes(/*Charset.forName("US-ASCII")*/);	//on converti la String en un tableau de byte
			
			tableByte = ajoutByte(tableByte);										//on ajoute un byte valant 1 pour éviter de se retrouver avec un bigInt négatif

			BigInteger bigByte = new BigInteger(tableByte);							//on stocke le tableau généré dans un BigInteger
			bigByte = bigByte.modPow(publicKey, commonKey); 						//on code le big integer avec les clés publiques et communes
			
			return bigByte;
		}
	
		public String decrypt(BigInteger messageCode) {
			messageCode = messageCode.modPow(privateKey, commonKey);				//on decode le big integer avec les clés privées et communes
			
			byte[] decrypted = removeByte(messageCode.toByteArray());				//on supprimer le byte en plus
			
			return new String(decrypted);
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
			
			int posInTable = 0;
			
			//déplacement de la création de la liste dans le constructeur
			posInTable = (int) (Math.random() * tablePr.size());//on prend un nombre dans la liste
			p1 = tablePr.get(posInTable);
			
			posInTable = posInTable + (int) (Math.random() * (tablePr.size()- posInTable - 10)); //on choisi une nouvelle position forcement supérieure
			q1 = tablePr.get(posInTable);
			
			
			//nombres premiers choisis
			p = new BigInteger(p1.toString());
			q = new BigInteger(q1.toString());
			
			n 	= p.multiply(q);
			phi = (p.subtract(new BigInteger("1"))).multiply((q.subtract(new BigInteger("1"))));	//phi = (p-1) * (q-1)
			
//			if (PGCD(p, q) == 1){
//				System.out.println(p + " et " + q + " sont premiers entre eux.");
//			}
			
			System.out.println("p = " + p1 + "\n"+
					"q = " + q1 + "\n"+
					"n = " + n + "\n"+
					"phi = " + phi + "\n");
			
			posInTable = posInTable + (int) (Math.random() * (tablePr.size() - posInTable));
			d = new BigInteger(tablePr.get(posInTable).toString());	// d fait parti du couple de clé privée (un autre nombre entier)
			System.out.println("d = " + d);
						
			e = new BigInteger(d.modInverse(phi).toString());	//e fait parti du couple de clés publique et vaut d^-1 mod phi 
			System.out.println("e = " + e);
			
			//on affecte les variables au attributs de la classe
			this.commonKey = n;		
			this.privateKey = d;
			this.publicKey = e;
		}//computeRSA_Key
		
		private void genererPremier() {		//genere 20 000 nombres premiers en une seconde
			int a = 0;
			int b = 0;
			int pr;
			
			for (int p = 0; p < 200000;) {
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
						if (a > 1000000)
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
//			System.out.println("etape 1");
			while(! a.multiply(b).equals(new BigInteger("0"))){
				res = a.compareTo(b);
				if (res == 1)	//si a > b alors res = 1
					a = a.subtract(b);
				else
					b = b.subtract(a);
//				System.out.println(res);
			}
//			System.out.println("etape 2");

			res = a.compareTo(new BigInteger("0"));
			if (res == 0)
				return b.intValue();
			else
				return a.intValue();
		}//PGCD
		
		BigInteger pow(BigInteger base, BigInteger exponent) {
			BigInteger result = BigInteger.ONE;
			while (exponent.signum() > 0) {
				if (exponent.testBit(0)) result = result.multiply(base);
				base = base.multiply(base);
				exponent = exponent.shiftRight(1);
			}
			return result;
		}//powBI
		
		private byte[] ajoutByte(byte[] input){
			byte[] toEdit = new byte[input.length+1];
			toEdit[0] = 1;
		    for (int i = 0; i < input.length; i++) {
		    	toEdit[i+1] = input[i];
		    }
		    return toEdit;
		}//ajoutByte
		
		private byte[] removeByte(byte[] input){
			byte[] toEdit = new byte[input.length-1];
		    for (int i = 0; i < toEdit.length; i++) {
		    	toEdit[i] = input[i+1];
		    }
		    return toEdit;
		}
		
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
		String messageTest = "b ns";
        BigInteger hello = test.encrypt(messageTest);
        System.out.print(hello);
        
        System.out.println("\nMessage décodé : " + test.decrypt(hello));
    }
}

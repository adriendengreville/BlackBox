package com.example.blackbox;
/*
 * Description : Cette classe est l'impl�mentation de tout ce qui concerne le codage des informations.
 */


import java.math.BigInteger;
import java.util.Vector;

public class Cryptage {
	//ATTRIBUTS
		private BigInteger 		commonKey;
		private BigInteger 		privateKey;
		private BigInteger 		publicKey;
//		private Vector<Byte> 	tableCodee;
		
		private Vector<Integer> tablePr = new Vector<Integer>();
		
//		private Vector<BigInteger> 	cypherTab;
//		private String 				plainPhrase; 
	
	//M�THODES
		
		public Cryptage(){
			genererPremier(); 	//on etablit la liste des nombres premiers (� REMPLACER PAR UNE LECTURE DE FICHIER POUR GAIN DE TEMPS)
		}//cryptageCSTR
		
		private BigInteger encrypt(char carac) {							//permet d'encoder un char en big integer avec les cl�s
			String 	   caracSTR = "" + carac; 										//on stock le char dans une string 
			BigInteger bigByte 	= new BigInteger(ajoutByte(caracSTR.getBytes()));	//on stocke la string g�n�r�e dans un BigInteger (on en profite pour ajouter un byte valant 1 au d�but pour ne pas fuck up les caract�res sp�ciaux)
			bigByte 			= bigByte.modPow(publicKey, commonKey); 			//on code le big integer avec les cl�s publiques et communes
			
			return bigByte;
		}//encrypt(char)
		
		public Vector<BigInteger> encrypt (String plainPhrase){			//permet le traitement d'une String pour l'encoder den un table de big Int (met 3ms en moyenne � encoder une phrase)
			Vector<BigInteger> cypherMessage = new Vector<BigInteger>();			
			
			for (int i = 0; i < plainPhrase.length(); i++)							//pour chaque caractere de la chaine
				cypherMessage.add(this.encrypt(plainPhrase.charAt(i)));				//on encode encode via la m�thode encrypt(char) et on stocke
	
			return cypherMessage;													//et on retourne ce magnifique vector plein de lettres encod�es
		}//encrypt(String)
	
		private String decrypt(BigInteger messageCode) {				//permet de d�coder les BigInteger avec les cl�s
			messageCode = messageCode.modPow(privateKey, commonKey);				//on decode le big integer avec les cl�s priv�es et communes
			messageCode = new BigInteger(removeByte(messageCode.toByteArray()));	//on retire le byte ajout� au d�but lors de l'encryption

			return new String(messageCode.toByteArray());						
		}//decrypt(BigInteger)
		
		public String decrypt(Vector<BigInteger> cypherTab){		//permet le traitement d'un Vector de BigInteger pour le d�coder (met 1ms en moyenne � d�coder une phrase)
			String plainPhrase = "";
			
			for(int i = 0; i < cypherTab.size(); i++)								//pour chaque �l�ment du tableau
				plainPhrase += decrypt(cypherTab.elementAt(i));						//on d�code et on range dans la String
			
			return plainPhrase;														//et on retourne la phrase fraichement d�cod�e
		}//decrypt(Vector<BigInteger)
	
		public void computeRSA_Key() {
//			Integer p1;
//			Integer q1;
			BigInteger p;
			BigInteger q;
			BigInteger n;
			BigInteger phi;
			BigInteger d;
			BigInteger e;
			
			int posInTable = 0;
			
			//d�placement de la cr�ation de la liste dans le constructeur
			
			posInTable = (int) (Math.random() * tablePr.size());									//on choisi al�atoirement un indice dans la liste (ou tout nombre est > � 1 000 000)

			//plus besoin des Integer interm�diaires
			
			p = new BigInteger(/*p1.toString()*/ tablePr.get(posInTable).toString());				//pour placer le nombre correspondant dans p
			
			posInTable = posInTable + (int) (Math.random() * (tablePr.size()- posInTable - 10)); 	//on choisi un nouvel indice forcement sup�rieur au pr�c�dent
			q = new BigInteger(tablePr.get(posInTable).toString());									//on place le nombre correspondant dans q
			
			n 	= p.multiply(q);																	//n   = p * q
			phi = (p.subtract(new BigInteger("1"))).multiply((q.subtract(new BigInteger("1"))));	//phi = (p-1) * (q-1)
			
//			if (PGCD(p, q) == 1){
//				System.out.println(p + " et " + q + " sont premiers entre eux.");
//			}
			
			System.out.println(/*"p = " + p1 + "\n"+*/
//					"q = " + q1 + "\n"+
					"n = " + n + "\n"//+
//					"phi = " + phi + "\n"
					);
			
			posInTable = posInTable + (int) (Math.random() * (tablePr.size() - posInTable));		//on choisit un autre indice dans la table pour avoir un autre nombre premier (donc forc�ment premier avec phi)
			d = new BigInteger(tablePr.get(posInTable).toString());									//d fait parti du couple de cl� priv�e
			System.out.println("d = " + d);
						
			e = new BigInteger(d.modInverse(phi).toString());										//e fait parti du couple de cl�s publique et vaut d^-1 mod phi 
			System.out.println("e = " + e);
			
			//on affecte les variables au attributs de la classe
			this.commonKey = n;
			this.privateKey = d;
			this.publicKey = e;
		}//computeRSA_Key
		
		private void genererPremier() {		//genere 200 000 nombres premiers en 1,5s (� REMPLACER PAR UNE LECTURE DE FICHIER)
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
		
		@SuppressWarnings("unused")
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
		
		@SuppressWarnings("unused")
		private BigInteger pow(BigInteger base, BigInteger exponent) {					//permet de calculer BigInteger puissance(BigInteger) 
			BigInteger result = BigInteger.ONE;
			while (exponent.signum() > 0) {
				if (exponent.testBit(0)) result = result.multiply(base);
				base = base.multiply(base);
				exponent = exponent.shiftRight(1);
			}
			return result;
		}//powBI
		
		private byte[] ajoutByte(byte[] input){									//permet d'ajouter un byte valant 1 au d�but d'un tableau de bytes
			byte[] toEdit = new byte[input.length+1];
			toEdit[0] = 1;
		    for (int i = 0; i < input.length; i++) {
		    	toEdit[i+1] = input[i];
		    }
		    return toEdit;
		}//ajoutByte
		
		private byte[] removeByte(byte[] input){								//permet d'enlever le premier byte d'un tableau de bytes (� utiliser avec la fonction ajoutByte
			byte[] toEdit = new byte[input.length-1];
		    for (int i = 0; i < toEdit.length; i++) {
		    	toEdit[i] = input[i+1];
		    }
		    return toEdit;
		}//removeByte
		
		public Vector<BigInteger> convert (String toConvert){					//permet de recevoir une String contenant un Vector<BigInteger> et de le reformer
//			System.out.println(System.currentTimeMillis());
			Vector<BigInteger> receivedData = new Vector<BigInteger>();
			StringBuilder tmp = new StringBuilder(toConvert);					//on utilise un StringBuilder pour pouvoir enlever facilement le crochet au d�but et � la fin de la chaine
			tmp.deleteCharAt(0);	
			tmp.deleteCharAt(tmp.length()-1);
			toConvert = tmp.toString();											//on remet dans une String pour traiter le contenu
			
//			System.out.println(toConvert);
			
			String tmpStr = "";													//String temporaire servant � stocker le nombre courant
			for(int i = 0; i < toConvert.length();){							//Une it�ration de i correspond � BigInteger r�cup�r�
				for (int j = i; j < toConvert.length(); j++, i++){				//Une it�ration de j correspond � un caract�re de la chaine trait�e
					if (!(toConvert.charAt(i) == ',') && !(toConvert.charAt(i) == ' ')){	//si on ne rencontre pas d'espace ni de virgule, on stocke le caract�re courant
						tmpStr += toConvert.charAt(i);
					}else{																	//sinon c'est qu'on est arriv� au bout du nombre courant et on passe au traitement du nombre
						j++;													
						i++;
						break;
					}
				}
				if (tmpStr.length() != 0)										//on v�rifie si la String n'est pas vide 
					receivedData.addElement(new BigInteger(tmpStr));			//avant de la transformer en BigInteger et de la stocker dans le vector
				tmpStr = "";													//on vide la String temporaire pour passer au nombre suivant
			}
//			System.out.println(receivedData.toString());
//			toConvert.split("\\,");
//			System.out.println(System.currentTimeMillis());			//pour v�rifier que l'algo d'extraction ne soit pas trop long
			return receivedData;
		}//convert(String)
		
	//SET-GETTER
		public void setPrivateKey(BigInteger privateKey) {
			this.privateKey = privateKey;
		}//setPrivateKey
	
		public String getPrivateKey() {
			return this.privateKey.toString();
		}//getPrivateKey
	
		public void setPublicKey(BigInteger publicKey) {
			this.publicKey = publicKey;
		}//setPublicKey
	
		public String getPublicKey() {
			return this.publicKey.toString();
		}//getPublicKey
		
		public void setCommonKey(BigInteger commonKey) {
			this.commonKey = commonKey;
		}//setCommonKey
	
		public String getCommonKey() {
			return this.commonKey.toString();
		}//getCommonKey
		
//	public static void main(String[] args) {
//		Cryptage test = new Cryptage();
//        test.computeRSA_Key();
//		String messageTest = "Bonjour je p�rle avec des accents et tout. Bon y a pas trop d'accents mais au moins je peux crypter une phrase de deux kilom�tres.";
//		
//		test.cypherTab = test.encrypt(messageTest);
//		
////        System.out.print(test.cypherTab.toString());
//        
//        String transfert = test.cypherTab.toString();
//        
////        Vector<BigInteger> transfertSuite = test.convert(transfert);
//        
//        test.plainPhrase = test.decrypt(test.convert(transfert));
//        
//        System.out.println("\nMessage d�cod� : " + test.plainPhrase);
//
//    }
}

/*
 * Description : Cette classe est l'implémentation de tout ce qui concerne le codage des informations.
 */


import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Vector;

public class Cryptage {
	//ATTRIBUTS
		BigInteger 		commonKey;
		BigInteger 		privateKey;
		BigInteger 		publicKey;
		Vector<Byte> 	tableCodee;
		
		Vector<Integer> tablePr = new Vector<Integer>();
		
		Vector<BigInteger> 	cypherTab;
		String 				plainPhrase; 
	
	//MÉTHODES
		
		public Cryptage(){
			genererPremier(); 	//on etablit la liste des nombres premiers (À REMPLACER PAR UNE LECTURE DE FICHIER POUR GAIN DE TEMPS)
		}//cryptageCSTR
		
		public BigInteger encrypt(char carac) {							//permet d'encoder un char en big integer avec les clés
			String 	   caracSTR = "" + carac; 										//on stock le char dans une string
			BigInteger bigByte 	= new BigInteger(caracSTR.getBytes());				//on stocke la strink généré dans un BigInteger
			bigByte 			= bigByte.modPow(publicKey, commonKey); 			//on code le big integer avec les clés publiques et communes
			
			return bigByte;
		}//encrypt(char)
		
		public Vector<BigInteger> encrypt (String plainPhrase){			//permet le traitement d'une String pour l'encoder den un table de big Int (met 3ms en moyenne à encoder une phrase)
			Vector<BigInteger> cypherMessage = new Vector<BigInteger>();			
			
			for (int i = 0; i < plainPhrase.length(); i++)							//pour chaque caractere de la chaine
				cypherMessage.add(this.encrypt(plainPhrase.charAt(i)));				//on encode encode via la méthode encrypt(char) et on stocke
	
			return cypherMessage;													//et on retourne ce magnifique vector plein de lettres encodées
		}//encrypt(String)
	
		public String decrypt(BigInteger messageCode) {					//permet de décoder les BigInteger avec les clés
			messageCode = messageCode.modPow(privateKey, commonKey);				//on decode le big integer avec les clés privées et communes
			
			return new String(messageCode.toByteArray());						
		}//decrypt(BigInteger)
		
		public String decrypt(Vector<BigInteger> cypherTab){		//permet le traitement d'un Vector de BigInteger pour le décoder (met 1ms en moyenne à décoder une phrase)
			String plainPhrase = "";
			
			for(int i = 0; i < cypherTab.size(); i++)								//pour chaque élément du tableau
				plainPhrase += decrypt(cypherTab.elementAt(i));						//on décode et on range dans la String
			
			return plainPhrase;														//et on retourne la phrase fraichement décodée
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
			
			//déplacement de la création de la liste dans le constructeur
			
			posInTable = (int) (Math.random() * tablePr.size());									//on choisi aléatoirement un indice dans la liste (ou tout nombre est > à 1 000 000)

			//plus besoin des Integer intermédiaires
			
			p = new BigInteger(/*p1.toString()*/ tablePr.get(posInTable).toString());				//pour placer le nombre correspondant dans p
			
			posInTable = posInTable + (int) (Math.random() * (tablePr.size()- posInTable - 10)); 	//on choisi un nouvel indice forcement supérieur au précédent
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
			
			posInTable = posInTable + (int) (Math.random() * (tablePr.size() - posInTable));		//on choisit un autre indice dans la table pour avoir un autre nombre premier (donc forcément premier avec phi)
			d = new BigInteger(tablePr.get(posInTable).toString());									//d fait parti du couple de clé privée
			System.out.println("d = " + d);
						
			e = new BigInteger(d.modInverse(phi).toString());										//e fait parti du couple de clés publique et vaut d^-1 mod phi 
			System.out.println("e = " + e);
			
			//on affecte les variables au attributs de la classe
			this.commonKey = n;
			this.privateKey = d;
			this.publicKey = e;
		}//computeRSA_Key
		
		private void genererPremier() {		//genere 200 000 nombres premiers en 1,5s (À REMPLACER PAR UNE LECTURE DE FICHIER)
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
		
		BigInteger pow(BigInteger base, BigInteger exponent) {					//permet de calculer BigInteger puissance(BigInteger) 
			BigInteger result = BigInteger.ONE;
			while (exponent.signum() > 0) {
				if (exponent.testBit(0)) result = result.multiply(base);
				base = base.multiply(base);
				exponent = exponent.shiftRight(1);
			}
			return result;
		}//powBI
		
		private byte[] ajoutByte(byte[] input){									//permet d'ajouter un byte valant 1 au début d'un tableau de bytes
			byte[] toEdit = new byte[input.length+1];
			toEdit[0] = 1;
		    for (int i = 0; i < input.length; i++) {
		    	toEdit[i+1] = input[i];
		    }
		    return toEdit;
		}//ajoutByte
		
		private byte[] removeByte(byte[] input){								//permet d'enlever le premier byte d'un tableau de bytes (à utiliser avec la fonction ajoutByte
			byte[] toEdit = new byte[input.length-1];
		    for (int i = 0; i < toEdit.length; i++) {
		    	toEdit[i] = input[i+1];
		    }
		    return toEdit;
		}//removeByte
		
		public Vector<BigInteger> convert (String toConvert){					//permet de recevoir une String contenant un Vector<BigInteger> et de le reformer
//			System.out.println(System.currentTimeMillis());
			Vector<BigInteger> receivedData = new Vector<BigInteger>();
			StringBuilder tmp = new StringBuilder(toConvert);					//on utilise un StringBuilder pour pouvoir enlever facilement le crochet au début et à la fin de la chaine
			tmp.deleteCharAt(0);	
			tmp.deleteCharAt(tmp.length()-1);
			toConvert = tmp.toString();											//on remet dans une String pour traiter le contenu
			
//			System.out.println(toConvert);
			
			String tmpStr = "";													//String temporaire servant à stocker le nombre courant
			for(int i = 0; i < toConvert.length();){							//Une itération de i correspond à BigInteger récupéré
				for (int j = i; j < toConvert.length(); j++, i++){				//Une itération de j correspond à un caractère de la chaine traitée
					if (!(toConvert.charAt(i) == ',') && !(toConvert.charAt(i) == ' ')){	//si on ne rencontre pas d'espace ni de virgule, on stocke le caractère courant
						tmpStr += toConvert.charAt(i);
					}else{																	//sinon c'est qu'on est arrivé au bout du nombre courant et on passe au traitement du nombre
						j++;													
						i++;
						break;
					}
				}
				if (tmpStr.length() != 0)										//on vérifie si la String n'est pas vide 
					receivedData.addElement(new BigInteger(tmpStr));			//avant de la transformer en BigInteger et de la stocker dans le vector
				tmpStr = "";													//on vide la String temporaire pour passer au nombre suivant
			}
//			System.out.println(receivedData.toString());
//			toConvert.split("\\,");
//			System.out.println(System.currentTimeMillis());			//pour vérifier que l'algo d'extraction ne soit pas trop long
			return receivedData;
		}//convert(String)
		
	//SET-GETTER
		public void setPrivateKey(BigInteger privateKey) {
			this.privateKey = privateKey;
		}//setPrivateKey
	
		public BigInteger getPrivateKey() {
			return this.privateKey;
		}//getPrivateKey
	
		public void setPublicKey(BigInteger publicKey) {
			this.publicKey = publicKey;
		}//setPublicKey
	
		public BigInteger getPublicKey() {
			return this.publicKey;
		}//getPublicKey
		
		public void setCommonKey(BigInteger commonKey) {
			this.commonKey = commonKey;
		}//setCommonKey
	
		public BigInteger getCommonKey() {
			return this.commonKey;
		}//getCommonKey
		
	public static void main(String[] args) {
		Cryptage test = new Cryptage();
        test.computeRSA_Key();
		String messageTest = "Bonjour je parle avec des accents et tout. Bon y a pas trop d'accents mais au moins je peux crypter une phrase de deux kilomètres.";
		
		test.cypherTab = test.encrypt(messageTest);
		
//        System.out.print(test.cypherTab.toString());
        
        String transfert = test.cypherTab.toString();
        
//        Vector<BigInteger> transfertSuite = test.convert(transfert);
        
        test.plainPhrase = test.decrypt(test.convert(transfert));
        
        System.out.println("\nMessage décodé : " + test.plainPhrase);

    }
}

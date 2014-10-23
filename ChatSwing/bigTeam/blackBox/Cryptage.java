package blackBox;
/*
 * Description : Cette classe est l'implémentation de tout ce qui concerne le codage des messages échangés.
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.Vector;

public class Cryptage {
//ATTRIBUTS------------------------------------------------------------------------------------------------
	private BigInteger 		commonKey;
	private BigInteger 		privateKey;
	private BigInteger 		publicKey;
	
	private Vector<Integer> tablePr = new Vector<Integer>();

//MÉTHODES-------------------------------------------------------------------------------------------------
	
	public Cryptage(){
		genererPremier(); 	//on etablit la liste des nombres premiers
	}//cryptageCSTR
	
	//permet d'encoder deux char en un big integer avec les clés
	private BigInteger encrypt(char carac1, char carac2) {
		//on stock les deux chars dans une string 
		String 	   caracSTR = "" + carac1 + carac2; 
		//on stocke la string générée dans un BigInteger (on en profite pour ajouter un byte valant 1 au début pour ne pas fuck up les caractéres spéciaux)
		BigInteger bigByte 	= new BigInteger(ajoutByte(caracSTR.getBytes()));
		//on code le big integer avec les clés publiques et communes
		bigByte 			= modPow(bigByte, publicKey, commonKey); 			
		
		return bigByte;
	}//encrypt(char, char)
	
	private BigInteger encrypt(char carac) {							
		//permet d'encoder un char en un big integer avec les clés
		//on stock le char dans une string 
		String 	   caracSTR = "" + carac; 										
		//on stocke la string générée dans un BigInteger (on en profite pour ajouter un byte valant 1 au début pour ne pas fuck up les caractéres spéciaux)
		BigInteger bigByte 	= new BigInteger(ajoutByte(caracSTR.getBytes()));
		//on code le big integer avec les clés publiques et communes
		bigByte 			= modPow(bigByte, publicKey, commonKey); 			
		
		return bigByte;
	}//encrypt(char)
	
	//permet le traitement d'une String pour l'encoder dans un table de big Int (met 3ms en moyenne à encoder une phrase)
	public Vector<BigInteger> encrypt (String plainPhrase){				
		Vector<BigInteger> cypherMessage = new Vector<BigInteger>();			
		//pour chaque paire de  caractères de la chaine on encode afin de ne pas juste faire une substitution
		for (int i = 0; i < plainPhrase.length(); i = i + 2){							
			if (i < plainPhrase.length() - 1){
				//on encode via la méthode encrypt(char, char) et on stocke
				cypherMessage.add(this.encrypt(plainPhrase.charAt(i), plainPhrase.charAt(i+1)));	
			}else
				//on encode via la méthode encrypt(char, char) et on stocke
				cypherMessage.add(this.encrypt(plainPhrase.charAt(i)));								
		}
		
		//et on retourne ce magnifique vector plein de lettres encodées
		return cypherMessage;													
	}//encrypt(String)
	
	//permet de décoder les BigInteger avec les clés
	private String decrypt(BigInteger messageCode) {
		//on decode le big integer avec les clés privées et communes
		messageCode = modPow(messageCode, privateKey, commonKey);
		//on retire le byte ajouté au début lors de l'encryption
		messageCode = new BigInteger(removeByte(messageCode.toByteArray()));	
		
		return new String(messageCode.toByteArray());						
	}//decrypt(BigInteger)
	
	//permet le traitement d'un Vector de BigInteger pour le décoder (met 1ms en moyenne à décoder une phrase)
	public String decrypt(Vector<BigInteger> cypherTab){				
		String plainPhrase = "";
		
		//pour chaque élément du tableau
		for(int i = 0; i < cypherTab.size(); i++)	
			//on décode et on range dans la String
			plainPhrase += decrypt(cypherTab.elementAt(i));						
		
		//et on retourne la phrase fraichement décodée
		return plainPhrase;														
	}//decrypt(Vector<BigInteger>)

	public void computeRSA_Key() {
		BigInteger p;
		BigInteger q;
		BigInteger n;
		BigInteger phi;
		BigInteger d;
		BigInteger e;
		
		int posInTable = 0;
		
		//on choisi aléatoirement un indice dans la liste
		posInTable = (int) (Math.random() * tablePr.size());					
		
		//pour placer le nombre correspondant dans p
		p = new BigInteger(tablePr.get(posInTable).toString());					
		
		//on choisi un nouvel indice forcement supérieur au précédent
		posInTable = posInTable + (int) (Math.random() * (tablePr.size()- posInTable - 10)); 
		//on place le nombre correspondant dans q
		q = new BigInteger(tablePr.get(posInTable).toString());					
		
		//n   = p * q
		n 	= p.multiply(q);
		//phi = (p-1) * (q-1)
		phi = (p.subtract(BigInteger.ONE)).multiply((q.subtract(BigInteger.ONE)));	
		
		System.out.println(/*"p = " + p1 + "\n"+*/
//					"q = " + q1 + "\n"+
				"n = " + n + "\n"//+
//					"phi = " + phi + "\n"
				);
		
		//on choisit un autre indice dans la table pour avoir un autre nombre premier (donc forcément premier avec phi)
		posInTable = posInTable + (int) (Math.random() * (tablePr.size() - posInTable));
		//d fait parti du couple de clé privée
		d = new BigInteger(tablePr.get(posInTable).toString());	
		//e fait parti du couple de clés publique et vaut d^-1 mod phi 
		System.out.println("d = " + d);
					
		e = modInv(d, phi);														
		System.out.println("e = " + e);
		
		//on affecte les variables au attributs de la classe
		this.commonKey = n;
		this.privateKey = d;
		this.publicKey = e;
	}//computeRSA_Key
	
	private void genererPremier() {										
		
		//Lis les nombres premiers depuis un fichier
		InputStream is = getClass().getResourceAsStream("prime.txt");
		//situe le fichier dans le package
		InputStreamReader isr = new InputStreamReader(is);	
		//ajout de la ligne lue dans le tableau
		BufferedReader br = new BufferedReader(isr);
		String line;
		try {
			while ((line = br.readLine()) != null) 
			{
				tablePr.addElement(Integer.parseInt(line));						
			}
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//fermeture des flux
		try {																	
			br.close();
			isr.close();
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}//genererPremier
	
	//permet de calculer l'inverse de b modulo n
	private BigInteger modInv(BigInteger b, BigInteger n){				
		BigInteger n0 = n;
		BigInteger b0 = b;
		BigInteger t0 = BigInteger.ZERO;
		BigInteger t = BigInteger.ONE;
		
		//q = n0 / b0
		BigInteger q = n0.divideAndRemainder(b0)[0];

		//r = n0 - q * b0
		BigInteger r = n0.subtract(q.multiply(b0));										
		
		//r > 0
		while (r.compareTo(BigInteger.ZERO) == 1){										
			//temp = t - q * t
			BigInteger temp = t0.subtract(q.multiply(t));								
			
			//temp >= 0 
			if (temp.compareTo(BigInteger.ZERO) == 1 || temp.equals(BigInteger.ZERO))	
				//temp = temp % n
				temp = temp.mod(n);														
			else
				//n - ((-temp) % n)
				temp = n.subtract(temp.negate().mod(n));								
			
			
			t0 = t;
			t = temp;
			n0 = b0;
			b0 = r;
			//q = n0 / b0
			q = n0.divideAndRemainder(b0)[0];
			//r = n0 - q * b0
			r = n0.subtract(q.multiply(b0));											
		}
		
		//b0 != 1
		if (!b0.equals(BigInteger.ONE))													
			return null;
		else
			return t;
	}//modInv
	
	//permet de calculer a^b modulo n
	private BigInteger modPow(BigInteger a, BigInteger b, BigInteger n) {
		//result = 1
		BigInteger result = BigInteger.ONE;									
		
		//tant_que b > 0
		while (b.compareTo(BigInteger.ZERO) == 1){
			//si b & 1 > 0
			if (b.and(BigInteger.ONE).compareTo(BigInteger.ZERO) == 1)
				//(result * a) % n 	
				result = result.multiply(a).mod(n);							
			
			//b = b >> 1
			b = b.shiftRight(1);
			//a² % n
			a = a.pow(2).mod(n);											
		}
		return result;
	}//modPow
	
	//permet d'ajouter un byte valant 1 au début d'un tableau de bytes
	private byte[] ajoutByte(byte[] input){								
		byte[] toEdit = new byte[input.length+1];
		toEdit[0] = 1;
	    for (int i = 0; i < input.length; i++) {
	    	toEdit[i+1] = input[i];
	    }
	    return toEdit;
	}//ajoutByte
	
	//permet d'enlever le premier byte d'un tableau de bytes (à utiliser avec la fonction ajoutByte)
	private byte[] removeByte(byte[] input){							
		byte[] toEdit = new byte[input.length-1];
	    for (int i = 0; i < toEdit.length; i++) {
	    	toEdit[i] = input[i+1];
	    }
	    return toEdit;
	}//removeByte
	
	//permet de recevoir une String contenant un Vector<BigInteger> et de le reformer
	public Vector<BigInteger> convert (String toConvert){				
		Vector<BigInteger> receivedData = new Vector<BigInteger>();
		//on utilise un StringBuilder pour pouvoir enlever facilement le crochet au début et à la fin de la chaine
		StringBuilder tmp = new StringBuilder(toConvert);					
		tmp.deleteCharAt(0);	
		tmp.deleteCharAt(tmp.length()-1);
		//on remet dans une String pour traiter le contenu
		toConvert = tmp.toString();											
		
		//String temporaire servant à stocker le nombre courant
		String tmpStr = "";
		//Une itération de i correspond à BigInteger récupéré
		for(int i = 0; i < toConvert.length();){
			//Une itération de j correspond à un caractére de la chaine traitée
			for (int j = i; j < toConvert.length(); j++, i++){		
				//si on ne rencontre pas d'espace ni de virgule, on stocke le caractère courant
				if (!(toConvert.charAt(i) == ',') && !(toConvert.charAt(i) == ' ')){	
					tmpStr += toConvert.charAt(i);
				//sinon c'est qu'on est arrivé au bout du nombre courant et on passe au traitement du nombre
				}else{														
					j++;													
					i++;
					break;
				}
			}
			//on vérifie si la String n'est pas vide 
			if (tmpStr.length() != 0)
				//avant de la transformer en BigInteger et de la stocker dans le vector
				receivedData.addElement(new BigInteger(tmpStr));
			//on vide la String temporaire pour passer au nombre suivant
			tmpStr = "";													
		}

		return receivedData;
	}//convert(String)
	
	public static void main(String[] args){
		Cryptage crypto = new Cryptage();
		crypto.computeRSA_Key();
		String test = "BBB";
		
		Vector<BigInteger> CypherTab = crypto.encrypt(test);
		System.out.println(CypherTab.toString() + "\n");
		System.out.println(crypto.decrypt(CypherTab));
	} 
	
//SET-GETTER------------------------------------------------------------------------------------------------
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
}//Cryptage

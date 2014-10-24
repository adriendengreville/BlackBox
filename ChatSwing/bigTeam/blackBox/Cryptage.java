package blackBox;
/*
 * Description : Cette classe est l'implémentation de tout ce qui concerne le codage des messages échangés.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

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
	
	public Vector<BigInteger> encrypt (String plainPhrase){				//permet le traitement d'une String pour l'encoder den un table de big Int (met 3ms en moyenne é encoder une phrase)
		Vector<BigInteger> cypherMessage = new Vector<BigInteger>();			
		
		for (int i = 0; i < plainPhrase.length(); i = i + 2){							
			//pour chaque paire de  caractères de la chaine on encode afin de ne pas juste faire une substitution
			if (i < plainPhrase.length() - 1){
				cypherMessage.add(this.encrypt(plainPhrase.charAt(i), plainPhrase.charAt(i+1)));	
				//on encode via la méthode encrypt(char, char) et on stocke
			}else
				cypherMessage.add(this.encrypt(plainPhrase.charAt(i)));								
			//on encode via la méthode encrypt(char, char) et on stocke
		}
		
		return cypherMessage;													//et on retourne ce magnifique vector plein de lettres encodées
	}//encrypt(String)

	private String decrypt(BigInteger messageCode) {					//permet de décoder les BigInteger avec les clés
		messageCode = modPow(messageCode, privateKey, commonKey);				//on decode le big integer avec les clés privées et communes
		messageCode = new BigInteger(removeByte(messageCode.toByteArray()));	//on retire le byte ajouté au début lors de l'encryption
		
		return new String(messageCode.toByteArray());						
	}//decrypt(BigInteger)
	
	public String decrypt(Vector<BigInteger> cypherTab){				//permet le traitement d'un Vector de BigInteger pour le décoder (met 1ms en moyenne à décoder une phrase)
		String plainPhrase = "";
		
		for(int i = 0; i < cypherTab.size(); i++)								//pour chaque élément du tableau
			plainPhrase += decrypt(cypherTab.elementAt(i));						//on décode et on range dans la String
		
		return plainPhrase;														//et on retourne la phrase fraichement décodée
	}//decrypt(Vector<BigInteger>)

	public void computeRSA_Key() {
		BigInteger p;
		BigInteger q;
		BigInteger n;
		BigInteger phi;
		BigInteger d;
		BigInteger e;
		
		int posInTable = 0;
					
		posInTable = (int) (Math.random() * tablePr.size());					//on choisi aléatoirement un indice dans la liste (ou tout nombre est > é 1 000 000)
		
		p = new BigInteger(tablePr.get(posInTable).toString());					//pour placer le nombre correspondant dans p
		
		posInTable = posInTable + (int) (Math.random() * (tablePr.size()- posInTable - 10)); //on choisi un nouvel indice forcement supérieur au précédent
		q = new BigInteger(tablePr.get(posInTable).toString());					//on place le nombre correspondant dans q
		
		n 	= p.multiply(q);													//n   = p * q
		phi = (p.subtract(BigInteger.ONE)).multiply((q.subtract(BigInteger.ONE)));	//phi = (p-1) * (q-1)
		
		System.out.println(/*"p = " + p1 + "\n"+*/
//					"q = " + q1 + "\n"+
				"n = " + n + "\n"//+
//					"phi = " + phi + "\n"
				);
		
		posInTable = posInTable + (int) (Math.random() * (tablePr.size() - posInTable));//on choisit un autre indice dans la table pour avoir un autre nombre premier (donc forcément premier avec phi)
		d = new BigInteger(tablePr.get(posInTable).toString());					//d fait parti du couple de clé privée
		System.out.println("d = " + d);
					
		e = modInv(d, phi);														//e fait parti du couple de clés publique et vaut d^-1 mod phi 
		System.out.println("e = " + e);
		
		//on affecte les variables au attributs de la classe
		this.commonKey = n;
		this.privateKey = d;
		this.publicKey = e;
	}//computeRSA_Key
	
	private void genererPremier() {										//Lis les nombres premiers depuis un fichier

		InputStream is = getClass().getResourceAsStream("prime.txt");			//situe le fichier dans le package
		InputStreamReader isr = new InputStreamReader(is);						
		BufferedReader br = new BufferedReader(isr);
		String line;
		try {
			while ((line = br.readLine()) != null) 
			{
				tablePr.addElement(Integer.parseInt(line));						//ajout de la ligne lue dans le tableau
			}
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {																	//fermeture des flux
			br.close();
			isr.close();
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}//genererPremier
	
	private BigInteger modInv(BigInteger b, BigInteger n){				//permet de calculer l'inverse de b modulo n
		BigInteger n0 = n;
		BigInteger b0 = b;
		BigInteger t0 = BigInteger.ZERO;
		BigInteger t = BigInteger.ONE;
		BigInteger q = n0.divideAndRemainder(b0)[0];									//q = n0 / b0
		BigInteger r = n0.subtract(q.multiply(b0));										//r = n0 - q * b0
		
		while (r.compareTo(BigInteger.ZERO) == 1){										//r > 0
			BigInteger temp = t0.subtract(q.multiply(t));								//temp = t - q * t
			
			if (temp.compareTo(BigInteger.ZERO) == 1 || temp.equals(BigInteger.ZERO))	//temp >= 0 
				temp = temp.mod(n);														//temp = temp % n
			else
				temp = n.subtract(temp.negate().mod(n));								//n - ((-temp) % n)
			
			t0 = t;
			t = temp;
			n0 = b0;
			b0 = r;
			q = n0.divideAndRemainder(b0)[0];											//q = n0 / b0
			r = n0.subtract(q.multiply(b0));											//r = n0 - q * b0
		}
		
		if (!b0.equals(BigInteger.ONE))													//b0 != 1
			return null;
		else
			return t;
	}//modInv
	
	private BigInteger modPow(BigInteger a, BigInteger b, BigInteger n) {//permet de calculer a^b modulo n
		BigInteger result = BigInteger.ONE;									//result = 1
		
		while (b.compareTo(BigInteger.ZERO) == 1){							//tant_que b > 0
			if (b.and(BigInteger.ONE).compareTo(BigInteger.ZERO) == 1)		//si b & 1 > 0
				result = result.multiply(a).mod(n);							//(result * a) % n 
			
			b = b.shiftRight(1);											//b = b >> 1
			a = a.pow(2).mod(n);											//aé % n
		}
		return result;
	}//modPow
	
	private byte[] ajoutByte(byte[] input){								//permet d'ajouter un byte valant 1 au début d'un tableau de bytes
		byte[] toEdit = new byte[input.length+1];
		toEdit[0] = 1;
	    for (int i = 0; i < input.length; i++) {
	    	toEdit[i+1] = input[i];
	    }
	    return toEdit;
	}//ajoutByte
	
	private byte[] removeByte(byte[] input){							//permet d'enlever le premier byte d'un tableau de bytes (à utiliser avec la fonction ajoutByte)
		byte[] toEdit = new byte[input.length-1];
	    for (int i = 0; i < toEdit.length; i++) {
	    	toEdit[i] = input[i+1];
	    }
	    return toEdit;
	}//removeByte
	
	public Vector<BigInteger> convert (String toConvert){				//permet de recevoir une String contenant un Vector<BigInteger> et de le reformer
		Vector<BigInteger> receivedData = new Vector<BigInteger>();
		StringBuilder tmp = new StringBuilder(toConvert);					//on utilise un StringBuilder pour pouvoir enlever facilement le crochet au début et à la fin de la chaine
		tmp.deleteCharAt(0);	
		tmp.deleteCharAt(tmp.length()-1);
		toConvert = tmp.toString();											//on remet dans une String pour traiter le contenu
		
		String tmpStr = "";													//String temporaire servant à stocker le nombre courant
		for(int i = 0; i < toConvert.length();){							//Une itération de i correspond à BigInteger récupéré
			for (int j = i; j < toConvert.length(); j++, i++){				//Une itération de j correspond à un caractére de la chaine traitée
				if (!(toConvert.charAt(i) == ',') && !(toConvert.charAt(i) == ' ')){	//si on ne rencontre pas d'espace ni de virgule, on stocke le caractère courant
					tmpStr += toConvert.charAt(i);
				}else{														//sinon c'est qu'on est arrivé au bout du nombre courant et on passe au traitement du nombre
					j++;													
					i++;
					break;
				}
			}
			if (tmpStr.length() != 0)										//on vérifie si la String n'est pas vide 
				receivedData.addElement(new BigInteger(tmpStr));			//avant de la transformer en BigInteger et de la stocker dans le vector
			tmpStr = "";													//on vide la String temporaire pour passer au nombre suivant
		}

		return receivedData;
	}//convert(String)
	
	public static void main(String[] args){
		Cryptage crypto = new Cryptage();	//initialisation module 
		crypto.computeRSA_Key();			//démarrage
		String exemple = "Portez ce vieux whisky au juge blond qui fume sur son île intérieure,"
				+ " à côté de l'alcôve ovoïde, où les bûches se consument dans l'âtre,"
				+ " ce qui lui permet de penser à la cænogénèse de l'être dont il est question"
				+ " dans la cause ambiguë entendue à Moÿ, dans un capharnaüm qui,"
				+ " pense-t-il, diminue çà et là la qualité de son œuvre.";
		
		Vector<Long> time = new Vector<Long>();
		for (int i = 0; i < 1000; i++){		//on mesure 1000 chiffrements / déchiffrements
			long timeStart = System.nanoTime();
			Vector<BigInteger> CypherTab = crypto.encrypt(exemple);
			crypto.decrypt(CypherTab);
			time.add(System.nanoTime() - timeStart);
			System.out.println(time.get(i));
		}
		
		long averageDuration = 0;
		time.remove(Collections.min(time));
		time.remove(Collections.max(time));

		for (int i = 0; i < time.size(); i++)
			averageDuration += time.get(i);
		
		averageDuration /= time.size();
		
		System.out.println(TimeUnit.MILLISECONDS.convert(averageDuration, TimeUnit.NANOSECONDS));
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

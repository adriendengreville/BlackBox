package testing;

import java.math.BigInteger;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class MainTest {

	public static void main(String[] args) {
		Cryptage moduleCrypto = new Cryptage();
		moduleCrypto.computeRSA_Key();
		
		Scanner scan = new Scanner(System.in);
		String testPhrase = scan.nextLine();
		
		long execDuration = System.nanoTime();
		
		System.out.println("\nPhrase initiale : " + testPhrase);
		
		Vector<BigInteger> CypherPhrase = moduleCrypto.encrypt(testPhrase);
		System.out.println("Phrase chiffrée : " + CypherPhrase);
		
		String phraseDechiffree = moduleCrypto.decrypt(CypherPhrase);
		System.out.println("Phrase déchiffrée : " + phraseDechiffree);
		
		execDuration = System.nanoTime() - execDuration;
		System.out.println("\nDurée de l'opération : " + TimeUnit.MILLISECONDS.convert(execDuration, TimeUnit.NANOSECONDS) + " ms.");

	}
}

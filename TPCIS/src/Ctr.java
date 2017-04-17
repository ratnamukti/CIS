
/*
 * Aplikasi kalkulator untuk enkripsi & dekripsi plaintext menggunakan mode CTR. 
 * 
 * @author 	1306397904 - Desi Ratna Mukti & 
 * 			1306464266 - Abidzar Gifari
 * 
 * */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.crypto.*;

public class Ctr {

	public static final String AB = "0123456789ABCDEF";
	public static SecureRandom rnd = new SecureRandom();
	public Cipher c;
	public static ArrayList<byte[]> blocks = new ArrayList<byte[]>();
	public static int n;
	public static int x;
	public static int y;
	public static String rs = "";
	public static byte[] plaintext;
	public static byte[] key;
	public static byte[] nonce;
	public static byte[] result;
	public static String ciphertext = "";

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		key = new byte[AES.BLOCK_SIZE]; /* 128-bit AES key */
		nonce = new byte[AES.BLOCK_SIZE];
		Scanner input = new Scanner(System.in);
	
		// baca input plaintext dan ubah jadi byte
		Path path = Paths.get("input.txt");
		plaintext = Files.readAllBytes(path);

		// baca input key dan ubah jadi byte
		String k = input.nextLine();
		key = Util.hex2byte(k);
		rs = randomString(32);
		
	}
		
	public static String enkripsi(String input, String inKey, File output){
		n = plaintext.length;
		x = n / 16;
		y = n % 16;
		if (y != 0) {
			x++;
		}

		for(int p=0; p < x; p++){
			if(plaintext.length > 16){
				byte[] newArray = Arrays.copyOfRange(plaintext, 0, 15);
				blocks.add(newArray);
				plaintext = Arrays.copyOfRange(plaintext, 16, n-1);
			} else {	
				byte[] newArray = Arrays.copyOfRange(plaintext, 0, n-1);
				blocks.add(newArray);
			}	
		}
		
		for (int j = 0; j < x; j++) {
			// ambil random nonce dan ubah jadi byte
			nonce = Util.hex2byte(rs);
			//System.out.println(">> nonce" + nonce);
			// enkripsi key dan nonce menggunakan AES
			AES testAES;
			testAES = new AES();
			testAES.setKey(key);
			result = testAES.encrypt(nonce);
			nonce[15] = nonce[15]++;
			
			byte[] encrypted = xor(blocks.get(j), result);
			ciphertext += encrypted;
		} 
		return ciphertext;
	}
	
	public static String dekripsi(String input, String inKey, String output){
		String decCiphertext = "";
		return decCiphertext;
	}


	public static byte[] xor(byte[] input1, byte[] input2) {
		byte[] temp = new byte[input1.length];
		for (int i = 0; i < input1.length; i++) {
			byte xoredByte = (byte) ((int) input1[i] ^ (int) input2[i]);
			temp[i] = xoredByte;
		}
		return temp;
	}

	/*
	 * Method untuk membuat pseudorandom string untuk nonce
	 * 
	 * @param panjang string yang ingin dibangun
	 * 
	 */
	public static String randomString(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}
}

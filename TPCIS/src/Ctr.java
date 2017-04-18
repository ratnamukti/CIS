
/* Tugas Pemrograman CIS
 * Aplikasi kalkulator untuk enkripsi & dekripsi plaintext menggunakan mode CTR. 
 * 
 * @author 	1306397904 - Desi Ratna Mukti & 
 * 			1306464266 - Abidzar Gifari
 * 
 * */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.sound.sampled.Line;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;

public class Ctr {

	public static final String AB = "0123456789ABCDEF";
	public static SecureRandom rnd = new SecureRandom();
	public static ArrayList<byte[]> blocks = new ArrayList<byte[]>();
	public static int n;
	public static int x;
	public static int y;
	public static String rs = "";
	public static byte[] plaintext;
	public static String decPlaintext = "";
	public static byte[] decPlaintextByte;
	public static byte[] key;
	public static String stringNonce = "";
	public static byte[] nonce;
	public static byte[] result;
	public static String ciphertext = "";
	public static byte[] ciphertextByte;
	public static FileInputStream filein;
	public static FileOutputStream fileout;
	public static String abspathP = "cello.mp3";
	public static String abspathK = "key.txt";
	public static String abspathO = "output.txt";

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		enkripsi(abspathP, abspathK, abspathO);
	}

	public static void setNonce(byte[] keyInit) {
		if (keyInit.length == 16) {
			stringNonce = "ED623F26BEEFECED981EAA0C8E8E088E";
		} else if (keyInit.length == 24) {
			stringNonce = "ED623F26BEEFECED981EAA0C8E8E088E1234567812345678";
		} else {
			stringNonce = "ED623F26BEEFECED981EAA0C8E8E088E12345678123456781234567812345678";
		}
	}

	public static byte[] readFilePlaintext(String pathPlaintext) throws IOException {
		Path path = Paths.get(pathPlaintext);
		byte[] pt = Files.readAllBytes(path);
		return pt;
	}

	public static byte[] readFileKey(String pathKey) throws IOException {
		String line = "";
		File key = new File(pathKey);
		Scanner input = new Scanner(key);
		while (input.hasNextLine()) {
			line = input.nextLine();
		}
		input.close();
		return Util.hex2byte(line);
	}

	public static void enkripsi(String inputPath, String inKeyPath, String outputPath) throws IOException {
		key = new byte[16]; /* 128-bit AES key */
		nonce = new byte[16];

		plaintext = readFilePlaintext(inputPath);
		key = readFileKey(inKeyPath);

		setNonce(key);
		nonce = Util.hex2byte(stringNonce);
		n = plaintext.length;
		x = n / 16;
		y = n % 16;

		// jika panjang plaintext bukan kelipatan 16, buat block baru
		if (y != 0) {
			x++;
		}

		// memotong plaintext menjadi blocks dengan panjang 16byte
		for (int p = 0; p < x; p++) {
			if (plaintext.length >= 16) {
				byte[] newArray = Arrays.copyOfRange(plaintext, 0, 16);
				blocks.add(newArray);
				plaintext = Arrays.copyOfRange(plaintext, 16, n);
			} else {
				byte[] newArray = Arrays.copyOfRange(plaintext, 0, n);
				blocks.add(newArray);
			}
		}

		// lakukan enkripsi AES terhadap setiap block
		for (int j = 0; j < blocks.size(); j++) {
			// enkripsi key dan nonce menggunakan AES
			System.out.println(j);
			AES testAES;
			testAES = new AES();
			testAES.setKey(key);
			result = testAES.encrypt(nonce);

			// increment nonce pada 16byte block berikutnya
			nonce[15] = (byte) nonce[15]++;

			// lakukan operasi XOR antara current block dengan hasil enkripsi

			byte[] encrypted = xor(blocks.get(j), result);
			String temp = Util.toHEX1(encrypted);
			ciphertext += temp;

		}
		System.out.println(ciphertext);
		ciphertextByte = Util.hex2byte(ciphertext);
		// TODO menulis kedalem file hehe.
		FileOutputStream fos = new FileOutputStream(abspathO);
		fos.write(ciphertextByte);
		fos.close();
	}

	public static void dekripsi(String inputPath, String inKeyPath, String outputPath) throws IOException {
		key = new byte[16]; /* 128-bit AES key */
		nonce = new byte[16];

		ciphertextByte = readFilePlaintext(inputPath);

		key = readFileKey(inKeyPath);

		setNonce(key);
		nonce = Util.hex2byte(stringNonce);
		n = ciphertextByte.length;
		x = n / 16;
		y = n % 16;

		// jika panjang plaintext bukan kelipatan 16, buat block baru
		if (y != 0) {
			x++;
		}

		// memotong plaintext menjadi blocks dengan panjang 16byte
		for (int p = 0; p < x; p++) {
			if (ciphertextByte.length >= 16) {
				byte[] newArray = Arrays.copyOfRange(ciphertextByte, 0, 16);
				blocks.add(newArray);
				ciphertextByte = Arrays.copyOfRange(ciphertextByte, 16, n);
			} else {
				byte[] newArray = Arrays.copyOfRange(ciphertextByte, 0, n);
				blocks.add(newArray);
			}
		}

		// lakukan enkripsi AES terhadap setiap block
		for (int j = 0; j < blocks.size(); j++) {
			// enkripsi key dan nonce menggunakan AES
			System.out.println(j);
			AES testAES;
			testAES = new AES();
			testAES.setKey(key);
			result = testAES.encrypt(nonce);

			// increment nonce pada 16byte block berikutnya
			nonce[15] = (byte) nonce[15]++;

			// lakukan operasi XOR antara current block dengan hasil enkripsi
			byte[] encrypted = xor(blocks.get(j), result);
			String temp = Util.toHEX1(encrypted);
			// ciphertext = String.join("", temp);
			decPlaintext += temp;

		}
		decPlaintextByte = Util.hex2byte(decPlaintext);
		// TODO menulis kedalem file hehe.
		FileOutputStream fos = new FileOutputStream(abspathO);
		fos.write(decPlaintextByte);
		fos.close();
	}

	/*
	 * Method untuk melakukan operasi XOR pada setiap bit
	 * 
	 * @param byte array1 dan byte array2
	 * 
	 */
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

import java.util.Scanner;
import java.io.IOException;
import java.security.SecureRandom;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;

public class Ctr {

	static final String AB = "0123456789ABCDEF";
	static SecureRandom rnd = new SecureRandom();

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		byte[]  key = new byte[AES.BLOCK_SIZE];	/* 128-bit AES key */
		byte[]  nonce = new byte[AES.BLOCK_SIZE]; 
		AES	testAES;
		byte[] result;	

		Path path = Paths.get("path/to/file");
	    // si plaintext di jadiin hex2byte
		byte[] plaintext = Files.readAllBytes(path);
		
		Scanner input = new Scanner(System.in);
		String k = input.nextLine();
		
		String rs = randomString(32);
		nonce = Util.hex2byte(rs);
		key = Util.hex2byte(k);
		System.out.println(nonce);
		
	    testAES = new AES();
	    testAES.setKey(key);
	    result = testAES.encrypt(nonce);
	    
	    // plaintext dibagi2 jadi block dulu tergantung opsi key (128, 192, 256 bit)
	    // pi panjangnya selalu sama no matter panjang key berapa
	    // hasil encrypt aes-key di XOR dengan block2 plaintext
	      
	}

	public static String randomString(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}
}

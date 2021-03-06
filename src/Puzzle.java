import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import javax.crypto.SecretKey;

/**
 * A class that will generate a single puzzle
 * @author Thomas Fisher, James Parry-Turner
 */

public class Puzzle {
	
	private int m_number;
	private SecretKey m_keyPart;
	private SecretKey m_encryptionKey;
	private byte[] m_encryptedPuzzle;
	private byte[] m_unencryptedPuzzle;
	private String m_cryptogram;
	private DES m_encryptor;
	
	private final int NUM_OF_ZERO_BYTES = 16;
	private final int START_OF_ZERO_BITS = 2;
	private final int END_OF_ZERO_BITS = 8;
	
	/**
	 * A constructor that generates a puzzle by concatenating each byte 
	 * array together and then encrypting it with the DES encrypt method
	 * @param Integer number for unique puzzle number
	 * @throws Exception
	 */
	public Puzzle(int number)throws Exception {
		m_number = number;
		m_encryptor = new DES();
		
		m_encryptionKey = m_encryptor.generateRandomKey();
		
		// First 128 zero bits
		byte[] zeros = new byte[NUM_OF_ZERO_BYTES];
		
		// 16 bit number part
		byte[] numberPart = CryptoLib.smallIntToByteArray(number);
		
		// 64 bit key part
		m_keyPart = m_encryptor.generateRandomKey();
		
		m_unencryptedPuzzle = buildPuzzle(zeros, numberPart, m_keyPart.getEncoded());
		
		// Set last 48 bits of encryption key to 0s
		byte[] encryptKey = m_encryptionKey.getEncoded();
		Arrays.fill(encryptKey, START_OF_ZERO_BITS, END_OF_ZERO_BITS, (byte) 0);
		m_encryptionKey = CryptoLib.createKey(encryptKey);

		// Encrypt
		m_encryptedPuzzle = m_encryptor.encrypt(m_unencryptedPuzzle, m_encryptionKey);
		
		// Turn into plaintext
		m_cryptogram = CryptoLib.byteArrayToString(m_encryptedPuzzle);
	}
	
	/**
	 * Build a puzzle given 3 seperate byte array parts
	 * @param zeros, the first part of the puzzle, 128 zero bits
	 * @param numberPart, the middle part of the puzzle, a unique 16-bit puzzle number
	 * @param keyPart, the final part of the puzzle, a 64-bit DES key
	 * @return The concatenated byte array, representing the whole puzzle
	 * @throws IOException
	 */
	public byte[] buildPuzzle(byte[] zeros, byte[] numberPart, byte[] keyPart) throws IOException {
		// Concatenate each part of the puzzle
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		stream.write(zeros);
		stream.write(numberPart);
		stream.write(keyPart);
		return stream.toByteArray();
	}
	
	/**
	 * Accessor method for puzzle number
	 * @return Integer puzzle number
	 */
	public int getPuzzleNo() {
		return m_number;
	}
	
	/**
	 * Accessor method to get the key part of the puzzle
	 * @return A byte array of the key
	 */
	public byte[] getKey() {
		return m_keyPart.getEncoded();
	}
	
	/**
	 * Give a meaningful way to display puzzle 
	 * @return A string of the puzzle
	 */
	public String toString() {
		return m_cryptogram;
		
	}
}

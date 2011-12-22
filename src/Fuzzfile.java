import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;




public class Fuzzfile {

	public String filePath;
	private File file;
	private byte[] readedByte;
	private String[] readedHex;


	public Fuzzfile(String filePath) {
		this.filePath = filePath;
		file = new File(filePath);
		
		readByte();
	}


	public String getName() {
		return filePath.substring(filePath.lastIndexOf(File.separator)+1, filePath.length());
	}
	

	public byte[] readByte() {
		
		if (file.length() > Integer.MAX_VALUE) {
			// File is too large
			System.out.println("File too large");
			System.exit(0);
		}
		readedByte = new byte[(int) file.length()];
		FileInputStream strm = null;
		try {
			strm = new FileInputStream(file);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		try {
			strm.read(readedByte);
		} catch (IOException e) {

			e.printStackTrace();
		}
		return readedByte;

	}

	public String[] readHex() {
		String[] byteToHex = new String[256]; 
		char[] nibbleToHex = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'}; 
		for (int i = 0; i < 256; ++i) {
			byteToHex[i] = Character.toString(nibbleToHex[i >>> 4]) + nibbleToHex[i & 0x0f];
		}
		readedHex = new String[readedByte.length];
		for (int i = 0;i<readedByte.length;i++) {

			String hex = byteToHex[readedByte[i] & 0x0ff];

			readedHex[i] = hex;
		}
		return readedHex;
	}

	public byte[] insertFuzz(int start, int end, String stuff) {
		byte[] stuffArray = makeBuf(stuff,end-start);
		byte[] insertFuzz = new byte[readedByte.length+stuffArray.length];
		System.arraycopy(readedByte, 0, insertFuzz, 0, start); 
		System.arraycopy(stuffArray, 0, insertFuzz, start, stuffArray.length);
		System.arraycopy(readedByte, start, insertFuzz, start+stuffArray.length, readedByte.length-start);
		return insertFuzz;
	}


	public byte[] overwriteFuzz(int start, int end, String stuff) {
		byte[] overwriteFuzz = readedByte;
		byte s = getByte(stuff);
		
		for (int i = start;i<end;i++) {
			overwriteFuzz[i] = s;
		}
		return overwriteFuzz;
	}

	public byte[] randomOverwriteFuzz(int size, String stuff) {
		byte randomOverwriteFuzz[] = readedByte.clone();
		byte s = getByte(stuff);
		int[] randomIndex = getRandoms(size,randomOverwriteFuzz.length);
		for (int i = 0;i<randomIndex.length;i++) {
			randomOverwriteFuzz[randomIndex[i]] = s;
		}

		return randomOverwriteFuzz;
	}

	private  byte[] insertElement(byte original[], byte element, int[] index, int size) {
		if (size == 0) {
			return original;
		}

		int length = original.length;
		byte destination[] = new byte[length+1];
		
		System.arraycopy(original, 0, destination, 0, index[size-1]);
		destination[index[size-1]] = element;
		System.arraycopy(original, index[size-1], destination, index[size-1]+1, length-index[size-1]);
		return insertElement(destination,element,index,size-1);
		
	}
	  
	public byte[] randomInsertFuzz(int size, String stuff) {
		byte[] randomInsertFuzz = new byte[readedByte.length+size];
		byte element = getByte(stuff);
		int[] randomIndex = getRandoms(size,readedByte.length);
		randomInsertFuzz = insertElement(readedByte,element,randomIndex,size);

		return randomInsertFuzz;
	}

	public byte[] replaceFuzz(String replace,  String stuff, int nReplace) {
		byte replaceThis = getByte(replace);
		byte replaceWith =  getByte(stuff);;
		byte[] replaceFuzz = readedByte;
		int i = 0, count=0;
		while (count<=nReplace) {
			if (replaceFuzz[i] == replaceThis) {
				replaceFuzz[i] = replaceWith;
				count++;
			}
			i++;
		}
		return replaceFuzz;
	}

	private byte[] makeBuf(String s, int size) {
		byte[] buffer = new byte[size];
		byte stuff = getByte(s);
		for (int i = 0;i<size;i++) {
			buffer[i] = stuff;
		}
		return buffer;
	}

	public long getSize() {
		return readedByte.length;
	}

	private byte getByte(String s) {
		if (s.length()==2)
			return Integer.decode("0x"+s) .byteValue();
		else
			return s.getBytes()[0];
	}
	
	private int[] getRandoms(int n, int range) {
		int[] randoms = new int[n];
		int count = 0;
		Random random = new Random();
		
		while (count != n) {
			int r = random.nextInt(range);
			if (Arrays.binarySearch(randoms, r)<0) {
				randoms[count] = r;
				count++;
			}
		}
		
		return randoms;
		
	}
	
	
}

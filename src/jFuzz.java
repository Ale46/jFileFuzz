import jargs.gnu.CmdLineParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.xpath.XPathExpressionException;




public class jFuzz {

	public static File[] getFiles(String path){
		File dir = new File(path);
		return dir.listFiles();
	}

	public static void printHex(String[] a, int nCol) {
		String temp = a[0]+"\t";
		for (int i = 1;i<a.length;i++) {
			String hex = a[i];
			temp =temp+hex+"\t";
			if (((temp.length() % (nCol*3))) == 0) {
				System.out.println(temp);
				temp ="";
			}else if ((a.length - i)==1) {
				System.out.println(temp);
			}
		}
	}

	private static void save (byte[] myBytes, String filePath) throws IOException{
		File dirSave = new File(filePath.substring(filePath.lastIndexOf(".")+1, filePath.length()));
		dirSave.mkdir();
		FileOutputStream out = new FileOutputStream(dirSave.getPath()+File.separator+filePath);
		out.write((myBytes));  
		out.close();  
	}

	private static void printUsage() {
		System.err.println("This is the help.. Readme ftw!");
		System.err.println();
		System.err.println("Utility switch:");
		System.err.println();
		System.err.println("--print  _int   \tPrint file in HEX format with specified column number.");
		System.err.println("--size   _bool  \tIf set print the size of file.");
		System.err.println();
		System.err.println("Essential fuzzing switch:");
		System.err.println();
		System.err.println("--file   _String\tSet the file you want to fuzz.");
		System.err.println("--rofuzz _String\tOverwrite data at random position.");
		System.err.println("--rifuzz _String\tInsert data at random position.");
		System.err.println("--ifuzz  _String\tInsert data from \"start\" to \"end\" .");
		System.err.println("--ofuzz  _String\tOverwrite data from \"start\" to \"end\".");
		System.err.println("--refuzz _String\tReplace selected data with custom data.");
		System.err.println();
		System.err.println("Essential switch for random fuzz:");
		System.err.println();
		System.err.println("--n      _int   \tSet the number of genereted fuzzed files.");
		System.err.println("--rn     _int   \tSet the number of insert\\overwrite.");
		System.err.println("--rp     _int   \tSet the percentage of insert\\overwrite.");
		System.err.println();
		System.err.println("Essential switch for insert fuzz:");
		System.err.println();
		System.err.println("--start  _int   \tData will be inserted from this position.");
		System.err.println("--end    _int   \tData will be inserted behind this position.");
		System.err.println();
		System.err.println("Essential switch for debug test:");
		System.err.println();
		System.err.println("--debug  _bool  \tStart debug test.");
		System.err.println("--dir    _String\tSet directory where the fuzzed files are located.");
		System.err.println("--prog   _String\tSet the software to attack.");
		System.err.println("--timeout _int\tSet time to wait before exit the application. (ms value, default 3500)");
		System.err.println();
		System.err.println("Generally examples:");
		System.err.println();
		System.err.println("Fuzz of the file \"test.pdf\" that overwrite random bytes with FF for 30 times generating 100 differents fuzzed files");
		System.err.println("jfuzz --file test.pdf --rofuzz FF --rn 30 --n 100");
		System.err.println();
		System.err.println("Fuzz of the file \"test.pdf\" that insert in random position FF for 30 times generating 100 differents fuzzed files");
		System.err.println("jfuzz --file test.pdf --rifuzz FF --rn 30 --n 100");
		System.err.println();
		System.err.println("Fuzz of the file \"test.pdf\" inserting FF from the 31st byte to the 42nd byte");
		System.err.println("jfuzz --file test.pdf --ifuzz FF --start 31 --end 42");
		System.err.println();
		System.err.println("Fuzz of the file \"test.pdf\" overwriting FF from the 31st byte to the 42nd byte");
		System.err.println("jfuzz --file test.pdf --ofuzz FF --start 31 --end 42");
		System.err.println();
		System.err.println("Replace the first 20 bytes of test.pdf with 00 value with FF");
		System.err.println("jfuzz --refuzz 00rFF --file test.pdf --rn 20");
		System.err.println();
		System.err.println("Test the application C:\\reader.exe with the fuzzed files located on C:\\pdfs\\");
		System.err.println("jfuzz --debug --dir C:\\pdfs\\ --prog C:\\reader.exe");

	}

	public static void main(String[] args) throws IOException, XPathExpressionException, InterruptedException {

		CmdLineParser parser = new CmdLineParser();
		CmdLineParser.Option debug = parser.addBooleanOption("debug");
		CmdLineParser.Option file = parser.addStringOption("file");
		CmdLineParser.Option print = parser.addIntegerOption("print");
		CmdLineParser.Option size = parser.addBooleanOption("size");
		CmdLineParser.Option dir = parser.addStringOption("dir");
		CmdLineParser.Option rofuzz = parser.addStringOption("rofuzz");
		CmdLineParser.Option rifuzz = parser.addStringOption("rifuzz");
		CmdLineParser.Option ofuzz = parser.addStringOption("ofuzz");
		CmdLineParser.Option ifuzz = parser.addStringOption("ifuzz");
		CmdLineParser.Option refuzz = parser.addStringOption("refuzz");
		CmdLineParser.Option n = parser.addIntegerOption("n");
		CmdLineParser.Option rn = parser.addIntegerOption("rn");
		CmdLineParser.Option rp = parser.addDoubleOption("rp");
		CmdLineParser.Option start = parser.addIntegerOption("start");
		CmdLineParser.Option end = parser.addIntegerOption("end");
		CmdLineParser.Option prog = parser.addStringOption("prog");
		CmdLineParser.Option help = parser.addBooleanOption("help");
		CmdLineParser.Option debugTime = parser.addStringOption("timeout");
		if (args.length==0 )
			printUsage();

		try {
			parser.parse(args);
		}
		catch ( CmdLineParser.OptionException e ) {
			System.err.println(e.getMessage());

			System.exit(2);
		}

		Boolean debugValue = (Boolean)parser.getOptionValue(debug);
		String fileValue = (String) parser.getOptionValue(file);

		Integer printValue = (Integer) parser.getOptionValue(print);
		Boolean sizeValue = (Boolean) parser.getOptionValue(size);
		String dirValue = (String) parser.getOptionValue(dir);
		String rofuzzValue = (String) parser.getOptionValue(rofuzz);
		String rifuzzValue = (String) parser.getOptionValue(rifuzz);
		String ofuzzValue = (String) parser.getOptionValue(ofuzz);
		String ifuzzValue = (String) parser.getOptionValue(ifuzz);
		String refuzzValue = (String) parser.getOptionValue(refuzz);
		Integer nValue = (Integer) parser.getOptionValue(n);
		Integer rnValue = (Integer) parser.getOptionValue(rn);
		Double rpValue = (Double) parser.getOptionValue(rp);
		Integer startValue = (Integer) parser.getOptionValue(start);
		Integer endValue = (Integer) parser.getOptionValue(end);
		String progValue = (String) parser.getOptionValue(prog);
		Boolean helpValue = (Boolean)parser.getOptionValue(help,true);
		String debugTimeValue  =  (String) parser.getOptionValue(debugTime,"3500");
		Fuzzfile f = null;
		if (fileValue != null)
			f = new Fuzzfile(fileValue);


		if(printValue != null)
			printHex(f.readHex(),printValue);


		if (sizeValue != null)
			System.out.println("Size of the file is: "+f.getSize());

		if (rofuzzValue != null && nValue != null && (rnValue != null || rpValue != null)) {
			if (rpValue != null) {
				rnValue = Math.round((float) (f.getSize()*rpValue/100));
			}
			System.out.println("Fuzzing "+nValue+" files overwriting at random positions "+rnValue+ " "+rofuzzValue);
			for (int i = 0;i<nValue;i++) {
				System.out.println("Created "+i+"fuzzed"+f.getName());
				save(f.randomOverwriteFuzz(rnValue, rofuzzValue),i+"fuzzed"+f.getName());
			}
		}

		else if (rifuzzValue != null && nValue != null && (rnValue != null || rpValue != null)) {
			if (rpValue != null) {
				rnValue = Math.round((float) (f.getSize()*rpValue/100));
			}
			System.out.println("Fuzzing "+nValue+" files inserting at random positions "+rnValue+ " "+rifuzzValue);
			for (int i = 0;i<nValue;i++) {
				System.out.println("Created "+i+"fuzzed"+f.getName());
				save(f.randomInsertFuzz(rnValue, rifuzzValue),i+"fuzzed"+f.getName());
			}
		}

		else if (ifuzzValue != null && startValue != null && endValue != null) {
			System.out.println("Fuzzing inserting from "+startValue+ " to "+endValue+"  "+(endValue-startValue)+" "+ifuzzValue);
			save(f.insertFuzz(startValue, endValue, ifuzzValue),"fuzzed"+f.getName());
		}

		else if (ofuzzValue != null && startValue != null && endValue != null) {
			System.out.println("Fuzzing overwriting from "+startValue+ " to "+endValue+"  "+(endValue-startValue)+" "+ofuzzValue);
			save(f.overwriteFuzz(startValue, endValue, ofuzzValue),"fuzzed"+f.getName());

		}

		else if (refuzzValue != null && rnValue != null) {
			String replace = refuzzValue.split("r")[0];
			String stuff = refuzzValue.split("r")[1];
			System.out.println("Fuzzing replacing all the first "+rnValue+"occuruncies of "+replace+ "with "+stuff);
			save(f.replaceFuzz(replace, stuff, rnValue),"fuzzed"+f.getName());
		}

		else if (debugValue != null && dirValue != null && progValue != null) {
			File[] files = getFiles(dirValue);
			System.out.println("Testing "+files.length+" files. Please wait..");
			for (int i = 0;i<files.length;i++) {
				CrashLogger cl = new CrashLogger(progValue, debugTimeValue, files[i].getPath());
				cl.testCrash();


			}
		}
		else if (helpValue)
			printUsage();
		else
			System.err.println("Missing important swith, check your syntax.");

	}
}

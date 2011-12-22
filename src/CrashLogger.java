import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.io.InputStreamReader;
import java.util.ArrayList;


public class CrashLogger{
	
	private String crashPath = (new File(".")).getAbsolutePath()+File.separator+"util"+File.separator;
	private String program, delay, filename;
	
	public CrashLogger(String program, String delay, String filename) {
		if (System.getProperty("os.name").contains("Windows")) {
			crashPath+="crash.exe";
		}else {
			crashPath+="crash";
		}
		if (!new File(crashPath).exists()) {
			System.out.println(crashPath);
			System.err.println("Compile crash.c under the util directory and rename it as crash");
			System.exit(-1);
		}
			
		this.program = program;
		this.delay = delay;
		this.filename = filename;
		
	}
	
	private ArrayList<String> exec(String cmd) throws IOException {
		
		ArrayList<String> msgLog = new ArrayList<String>();
		try {
			String line;
			Process p = Runtime.getRuntime().exec(crashPath+ " " +cmd);
			BufferedReader input =
				new BufferedReader
				(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				msgLog.add(line);
			}
			input.close();
		} catch (Exception err) {
			err.printStackTrace();
		}
		return msgLog;
	}

	public void testCrash() throws IOException {
		String cmd = "\""+program+"\" "+delay+" \""+ filename+"\"";
		
		ArrayList<String> logged = exec(cmd);
		
		for (String s : logged) {
			if (s.contains("[*] Process terminated normally.")) {
				File ffile = new File(filename);
				ffile.deleteOnExit();
				System.out.println(filename+ " is ok.");
				break;
			}
			if (s.contains("[*] Access Violation")||s.contains("[*] Divide by Zero")|| s.contains("[*] Stack Overflow")){
				System.out.println(filename+ " crashed.. Logging results");
				//System.out.println("Dump->"+s);
				logCrash(logged, filename);
				break;
			}
				
		}
	}

	private void logCrash(ArrayList<String> toLog, String filename) throws IOException {
		FileWriter fstream = new FileWriter(filename+".log");
		BufferedWriter out = new BufferedWriter(fstream);
		out.write("[!] Crash on: "+filename);
		out.newLine();
		for (String s : toLog) {
			out.append(s);
			out.newLine();
		}
		out.close();
		
	}




}

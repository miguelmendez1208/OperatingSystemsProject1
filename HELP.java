package osProject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class HELP {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			int x;
			Runtime rt = Runtime.getRuntime();

			 // String dir = System.getProperty("user.dir");

			  // directory from where the program was launched
			  // e.g /home/mkyong/projects/core-java/java-io
			 // System.out.println(dir);
			  Process proc = rt.exec("java RAM.java");
			
			//InputStream is = proc.getInputStream();
			//InputStreamReader isr = proc.getInputStream();
			OutputStream os = proc.getOutputStream();

			PrintWriter pw = new PrintWriter(os);
			//pw.printf("read 0");
			//pw.flush();

			BufferedReader sc = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			pw.printf("read 0\n");
			pw.flush();
			String line = sc.readLine();
			System.out.println("line1!: " + line);
			pw.printf("read 1\n");
			pw.flush();
			line = sc.readLine();
			System.out.println("line2!: " + line);
			pw.printf("read 2\n");
			pw.flush();
			line = sc.readLine();
			System.out.println("line3!: " + line);

			proc.waitFor();
			//sc.close();
			int exitVal = proc.exitValue();

			System.out.println("Process exited: " + exitVal);
			
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}

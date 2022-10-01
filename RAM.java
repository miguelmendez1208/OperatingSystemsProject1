package osProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class RAM {
	public static int[] memory = new int[2000];

	public static void main(String args[]) {
		try {
			readFile("sample1.txt");
			listen();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public RAM() {
		try {
			readFile();
			listen();
			// listen(); // call listening while loop
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public RAM(String name) {
		try {
			readFile(name);
			listen();

			// listen(); // call listening while loop
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// while loop that just listens and waits for input commands
	public static void listen() {
		Scanner sc = new Scanner(System.in); // create scanner
		String command = null;
		while (sc.hasNext()) { // while there is another input
			command = sc.nextLine(); // read input
			String[] commandArray = command.split(" ");
			if (commandArray[0].equals("read")) { // maybe change this to the number input for read
				int addre = Integer.parseInt(commandArray[1]); // get the address from the input
				System.out.println(readMemory(addre)); // call the read memory function with the address
			} else if (commandArray[0].equals("write")) { // maybe change this to the number input for write
				int valu = Integer.parseInt(commandArray[1]); // get the value from the input
				int addre = Integer.parseInt(commandArray[2]); // get the address from the input
				writeMemory(valu, addre); // call the write function with the value and the address
			}

		}

	}

	public static void readFile() throws IOException {
		readFile("sample1.txt");
	}

	public static void readFile(String name) throws IOException {
		File f = new File(name); // Creation of File Descriptor for input file
		FileReader fr = new FileReader(f); // Creation of File Reader object
		BufferedReader br = new BufferedReader(fr); // Creation of BufferedReader object
		int counter = 0;
		String line = br.readLine();
		while (line != null) // Read char by Char
		{
			if (line.isEmpty()) { // skip empty lines
				line = br.readLine();
			} else {
				int firstChar = line.charAt(0);
				if (firstChar == 46) { // .XXXX numbers, redirect address
					String arrStr = line.substring(1); // skip the dot, grab the numbers
					int firstNum = Integer.parseInt(arrStr);
					counter = firstNum;
					line = br.readLine();
				} else if (firstChar < 48 | firstChar > 57) { // skip non numbers
					counter++;
					line = br.readLine();
				} else { // grab only the first numbers not the others
					String[] arrStr = line.split(" ");
					int firstNum = Integer.parseInt(arrStr[0]);
					memory[counter] = firstNum;
					counter++;
					line = br.readLine();
				}
			}
		}
		br.close();

	}

	public static void writeMemory(int value, int address) {
		// x is variable and n is the place in memory
		memory[address] = value;
	}

	public static int readMemory(int address) {
		// System.out.println(memory[address]);
		return memory[address];
	}

	public static void printMemory() {
		for (int x : memory) {
			System.out.println(x);
		}
	}
}

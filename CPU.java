package osProject;

import java.util.Scanner;
import java.lang.Runtime;
import java.io.*;

public class CPU {
	public static int PC; // PC Counter
	public static int SP; // Stack Pointer Counter
	public static int IR; // Instruction Register
	public static int AC;
	public static int X;
	public static int Y;
	public static boolean end; // use this to break process
	public static boolean KernelMode; // KernelMode
	public static int count;// Counter (not implemented)
	public static int timer;// timer (not implemented)

	public CPU() {
	}

	public static void main(String args[]) {
		PC = -1;
		SP = 999; //end of user memory
		KernelMode = false;
		end = false;
		try {
			int x;
			Runtime rt = Runtime.getRuntime();
			String command = " ";
			if (args.length > 0) {
				command = "java RAM.java " + args[0];
			} else {
				command = "java RAM.java";
			}
			// if(args.length >1) {
			// timer = Integer.parseInt(args[1]);
			// }else {
			// timer = 10;
			// }
			Process proc = rt.exec(command);

			InputStream is = proc.getInputStream();
			OutputStream os = proc.getOutputStream();

			PrintWriter pw = new PrintWriter(os);
			// BufferedReader sc = new BufferedReader(new InputStreamReader(is)); //Buffered
			// reader wrapped over input stream reader wrapped over InputStream XD
			while (!isAtEnd()) {
				ScanToken(pw, is);
			}

			// exit stuff idk
			proc.destroy(); // end the sub process
			int exitVal = proc.exitValue();
			System.out.println(" ");
			System.out.println("Process exited: " + exitVal);

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	// pw = input into ram, is = output from ram
	public static int readNextInstruction(PrintWriter pw, InputStream is) { // This probably should be renamed to
		int num = 0; // "readNextValue"
		PC++;
		String statement = "read " + PC + "\n";
		if (PC >= 1000 && KernelMode == false) { // memory access violation
			Int(pw, is);
		}
		pw.printf(statement); // print "read X\n" to the print writer
		pw.flush(); // Printwriter is just the RAM's input, so you're telling RAM "read _"
		BufferedReader sc = new BufferedReader(new InputStreamReader(is)); // Buffered reader wrapped over input stream
																			// reader wrapped over InputStream XD
		String line;
		try {
			line = sc.readLine();// get the output through the scanners next line.
			num = Integer.parseInt(line); // translate output(input?) to an int
			return num; // return the int
		} catch (IOException e) {
			e.printStackTrace();
		}
		return num; // return the int
	}

	public static int readInstructionX(PrintWriter pw, InputStream is, int val) {
		int num = 0;
		String statement = "read " + val + "\n";
		if (val > 1000 && KernelMode == false) { // memory access violation
			Int(pw, is);
		}
		pw.printf(statement); // print "read X\n" to the print writer
		pw.flush(); // Printwriter is just the RAM's input, so you're telling RAM "read _"
		BufferedReader sc = new BufferedReader(new InputStreamReader(is)); // Buffered reader wrapped over input stream
																			// reader wrapped over InputStream XD
		try {
			String line = sc.readLine();
			num = Integer.parseInt(line); // translate output(input?) to an int
			return num; // return the int
		} catch (IOException e) {
			e.printStackTrace();
		} // get the output through the scanners next line.
		return num; // return the int
	}

	public static boolean isAtEnd() { // return if pc is past the end
		return (PC >= 999) | (end == true);
	}

	public static boolean isAtSystemEnd() {
		return PC >= 1999;
	}

	public static void printAll() {
		System.out.print("{PC = " + (PC + 1));
		System.out.print(", SP = " + SP);
		System.out.print(", IR = " + IR);
		System.out.print(", AC = " + AC);
		System.out.print(", X = " + X);
		System.out.print(", Y = " + Y + "}");
	}

	public static void ScanToken(PrintWriter pw, InputStream is) {
		IR = readNextInstruction(pw, is);// this to read instruction
		int val = 0;// place holder value. this to read value / argument :)
		// printAll();
		count++;
		// if(count>=timer && KernelMode==false) { //Couldn't get the timer interrupt to
		// work
		// TimerInt(pw,is);
		// }
		switch (IR) {
		case 1:
			val = readNextInstruction(pw, is); // load value into ac
			Load_value(val);
			break;
		case 2:
			Load_addr(pw, is, PC); // load val at address into ac
			break;
		case 3:
			val = readNextInstruction(pw, is);
			LoadInd_addr(pw, is, val);
			break;
		case 4:
			val = readNextInstruction(pw, is);
			LoadIdxX_addr(pw, is, val);
			break;
		case 5:
			val = readNextInstruction(pw, is);
			LoadIdxY_addr(pw, is, val);
			break;
		case 6:
			LoadSpX(pw, is);
			break;
		case 7:
			Store_addr(pw, is, PC);
			break;
		case 8:
			get();
			break;
		case 9:
			val = readNextInstruction(pw, is);
			Put_port(val);
			break;
		case 10:
			AddX();
			break;
		case 11:
			AddY();
			break;
		case 12:
			SubX();
			break;
		case 13:
			SubY();
			break;
		case 14:
			CopyToX();
			break;
		case 15:
			CopyFromX();
			break;
		case 16:
			CopyToY();
			break;
		case 17:
			CopyFromY();
			break;
		case 18:
			CopyToSp();
			break;
		case 19:
			CopyFromSp();
			break;
		case 20:
			val = readNextInstruction(pw, is);
			Jump_addr(val);
			break;
		case 21:
			val = readNextInstruction(pw, is);
			JumpIfEqual_addr(val);
			break;
		case 22:
			val = readNextInstruction(pw, is);
			JumpIfNotEqual_addr(val);
			break;
		case 23:
			val = readNextInstruction(pw, is);
			Call_addr(pw, is, val);
			break;
		case 24:
			Ret(pw, is);
			break;
		case 25:
			IncX();
			break;
		case 26:
			DecX();
			break;
		case 27:
			Push(pw, is);
			break;
		case 28:
			Pop(pw, is);
			break;
		case 29:
			if (!KernelMode) {
				Int(pw, is);// ???
			}
			break;
		case 30:
			IRet(pw, is);// ???
			break;
		case 50:
			end = true;
			return;
		case 0:
			// do nothing;
			break;
		default:
			return;
		}
	}

	// functions
	public static void Load_value(int num) {// 1
		AC = num;
	}

	public static void Load_addr(PrintWriter pw, InputStream is, int addr) {// 2
		int num = readInstructionX(pw, is, addr);
		AC = num;

	}

	public static void LoadInd_addr(PrintWriter pw, InputStream is, int addr) {// 3
		// pt 1 load value found at address
		int num = readInstructionX(pw, is, addr);
		// pt2 now read at that value
		int num2 = readInstructionX(pw, is, num);
		AC = num2;

	}

	public static void LoadIdxX_addr(PrintWriter pw, InputStream is, int addr) {// 4
		int NewIndex = addr + X;
		int num = readInstructionX(pw, is, NewIndex);
		AC = num;

	}

	public static void LoadIdxY_addr(PrintWriter pw, InputStream is, int addr) {// 5
		int NewIndex = addr + Y;
		int num = readInstructionX(pw, is, NewIndex);
		AC = num;

	}

	public static void LoadSpX(PrintWriter pw, InputStream is) {// 6
		int NewIndex = SP + X;
		int num = readInstructionX(pw, is, NewIndex);
		AC = num;

	}

	public static void Store_addr(PrintWriter pw, InputStream is, int addr) {// 7
		pw.printf("write " + AC + "\n");
		pw.flush();
	}

	public static void get() {// 8
		int random = (int) (Math.random() * 100);
		AC = random;
	}

	public static void Put_port(int port) {// 9
		if (port == 1) {
			System.out.print(AC);
		} else {
			System.out.print((char) AC);
		}
	}

	public static void AddX() {// 10
		AC += X;
	}

	public static void AddY() {// 11
		AC += Y;
	}

	public static void SubX() {// 12
		AC -= X;
	}

	public static void SubY() {// 13
		AC -= Y;
	}

	public static void CopyToX() {// 14
		X = AC;
	}

	public static void CopyFromX() {// 15
		AC = X;
	}

	public static void CopyToY() {// 16
		Y = AC;
	}

	public static void CopyFromY() {// 17
		AC = Y;
	}

	public static void CopyToSp() {// 18
		SP = AC;
		SP--; //Decrement stack pointer? unsure about this
	}

	public static void CopyFromSp() {// 19
		AC = SP;
	}

	public static void Jump_addr(int Addr) { // 20
		PC = Addr - 1; // You have to subtract 1 from the address so it when it loads the next
						// instruction, it loads the right address
	}

	public static void JumpIfEqual_addr(int Addr) {// 21
		if (AC == 0) {
			PC = Addr - 1;
		}
	}

	public static void JumpIfNotEqual_addr(int Addr) {// 22
		if (AC != 0) {
			PC = Addr - 1;
		}
	}

	public static void Call_addr(PrintWriter pw, InputStream is, int Addr) {// 23
		pw.printf("write " + (PC) + " " + (SP) + "\n"); // print "read X\n" to the print writer
		pw.flush(); // Printwriter is just the RAM's input, so you're telling RAM "read _"
		// mem.writeMemory(PC, SP); // save the PC counter address to the stack pointer
		// address? Maybe increment
		PC = Addr - 1; // Now change PC to address
		SP--; // Decrement Stack Pointer
	}

	public static void Ret(PrintWriter pw, InputStream is) {// 24
		SP++; // Increment Stack Pointer (This is popping)
		//
		int num = readInstructionX(pw, is, SP);
		//
		// int Addr = mem.readMemory(SP);// pop address from stack
		PC = num - 1;// Now change back? Could we not have just copied from SP?
	}

	public static void IncX() {// 25
		X += 1;
	}

	public static void DecX() {// 26
		Y -= 1;
	}

	public static void Push(PrintWriter pw, InputStream is) {// 27
		pw.printf("write " + (AC) + " " + (SP) + "\n"); // print "read X\n" to the print writer
		pw.flush(); // Printwriter is just the RAM's input, so you're telling RAM "read _"
		// mem.writeMemory(AC, SP); // Were pushing AC onto the stack, probably
		// increment
		SP--;
	}

	public static void Pop(PrintWriter pw, InputStream is) {// 28
		SP++;
		int num = readInstructionX(pw, is, SP);
		AC = num;
		// AC = mem.readMemory(SP); // maybe change?
	}

	public static void Int(PrintWriter pw, InputStream is) {// 29 ?????
		KernelMode = true;
		pw.printf("write " + (SP) + " " + (1999) + "\n"); // print "read X\n" to the print writer
		pw.flush(); // Printwriter is just the RAM's input, so you're telling RAM "read _"
		// mem.writeMemory(SP, 1999);// save Stack pointer to system stack
		SP = 1999;// switch to system stack
		SP--;// decrement system stack
		pw.printf("write " + (PC) + " " + (SP) + "\n"); // print "read X\n" to the print writer
		pw.flush(); // Printwriter is just the RAM's input, so you're telling RAM "read _"
		// mem.writeMemory(PC, SP); // save PC counter to system stack
		SP--; // decrement system stack
		PC = 1499; // switch address to 1500, interrupt instructions
		while (!isAtSystemEnd()) {
			ScanToken(pw, is);
		}
		KernelMode = false;
	}

	public static void TimerInt(PrintWriter pw, InputStream is) {// 29 ?????
		KernelMode = true;
		pw.printf("write " + (SP) + " " + (1999) + "\n"); // print "read X\n" to the print writer
		pw.flush(); // Printwriter is just the RAM's input, so you're telling RAM "read _"
		// mem.writeMemory(SP, 1999);// save Stack pointer to system stack
		SP = 1999;// switch to system stack
		SP--;// decrement system stack
		pw.printf("write " + (PC) + " " + (SP) + "\n"); // print "read X\n" to the print writer
		pw.flush(); // Printwriter is just the RAM's input, so you're telling RAM "read _"
		// mem.writeMemory(PC, SP); // save PC counter to system stack
		SP--; // decrement system stack
		PC = 999; // switch address to 1500, interrupt instructions
		while (!isAtSystemEnd()) {
			ScanToken(pw, is);
		}
		KernelMode = false;
	}

	public static void IRet(PrintWriter pw, InputStream is) {// 30 ?????
		SP++;
		int num = readInstructionX(pw, is, SP);
		PC = num - 1;
		// PC = mem.readMemory(SP); // grab PC
		SP++;
		// pt 2 delete later maybe
		int num2 = readInstructionX(pw, is, SP);
		SP = num2;
		// while (!isAtEnd()) {
		// ScanToken(pw, is);
		// }
	}

	public static void End() {// 50
		end = true;
		return; // ???
	}
}

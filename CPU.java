package osProject;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.lang.Runtime;
import java.io.*;

public class CPU {
	public static int PC;
	public static int SP;
	public static int IR;
	public static int AC;
	public static int X;
	public static int Y;

	public CPU() {
	}

	public static void main(String args[]) {
		PC = -1;
		SP = 1000;
		try {
			int x;
			Runtime rt = Runtime.getRuntime();

			Process proc = rt.exec("java RAM.java");

			InputStream is = proc.getInputStream();
			OutputStream os = proc.getOutputStream();

			PrintWriter pw = new PrintWriter(os);
			while (!isAtEnd()) {
				ScanToken(pw, is);
			}

			//exit stuff idk
			proc.waitFor(); // ??
			int exitVal = proc.exitValue();
			System.out.println("Process exited: " + exitVal);

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static void testFunction(PrintWriter pw, InputStream is) {
		pw.printf("read 0"); // print "read X\n" to the print writer
		pw.flush(); // Printwriter is just the RAM's input, so you're telling RAM "read _"
		Scanner sc = new Scanner(is); // initialize scanner to receive input
		String line = sc.nextLine();// get the output through the scanners next line.
		System.out.println(line);
	}
	
	// pw = input into ram, is = output from ram
	public static int readNextInstruction(PrintWriter pw, InputStream is) { // This probably should be renamed to
		int num = 0;																	// "readNextValue"
		PC++;
		pw.println("read " + PC); // print "read X\n" to the print writer
		pw.flush(); // Printwriter is just the RAM's input, so you're telling RAM "read _"
		Scanner sc = new Scanner(is); // initialize scanner to receive input
		String line = sc.nextLine();// get the output through the scanners next line.
		num = Integer.parseInt(line); // translate output(input?) to an int
		sc.close(); // close scanner unnecessary?
		return num; // return the int
	}

	public static boolean isAtEnd() { // return if pc is past the end
		return PC >= 999;
	}

	public static boolean isAtSystemEnd() {
		return PC >= 1999;
	}

	public static void printAll() {
		System.out.println("PC = " + PC);
		System.out.println("SP = " + SP);
		System.out.println("IR = " + IR);
		System.out.println("AC = " + AC);
		System.out.println("X = " + X);
		System.out.println("Y = " + Y);
	}

	public static void ScanToken(PrintWriter pw, InputStream is) {
		IR = readNextInstruction(pw, is);// this to read instruction
		int val = 0;// place holder value. this to read value / argument :)
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
			Int(pw, is);// ???
			break;
		case 30:
			IRet(pw, is);// ???
			break;
		case 50:
			return;
		default:
			return;
		}
	}
	//functions 
	public static void Load_value(int num) {// 1
		AC = num;
	}

	public static void Load_addr(PrintWriter pw, InputStream is, int addr) {// 2
		pw.printf("read " + (addr) + "\n"); // print "read X\n" to the print writer
		pw.flush(); // Printwriter is just the RAM's input, so you're telling RAM "read _"
		Scanner sc = new Scanner(is); // initialize scanner to receive input
		String line = sc.nextLine(); // get the output through the scanners next line.
		int num = Integer.parseInt(line); // translate output(input?) to an int
		AC = num;
		sc.close();
		
	}

	public static void LoadInd_addr(PrintWriter pw, InputStream is, int addr) {// 3
		// pt 1 load value found at address
		pw.printf("read " + (addr) + "\n"); // print "read X\n" to the print writer
		pw.flush(); // Printwriter is just the RAM's input, so you're telling RAM "read _"
		Scanner sc = new Scanner(is); // initialize scanner to receive input
		String line = sc.nextLine(); // get the output through the scanners next line.
		int num = Integer.parseInt(line); // translate output(input?) to an int
		sc.close();// just reuse same scanner? >.<
		// pt2 now read at that value
		pw.printf("read " + (num) + "\n"); // print "read X\n" to the print writer
		pw.flush(); // Printwriter is just the RAM's input, so you're telling RAM "read _"
		Scanner sc2 = new Scanner(is); // initialize scanner to receive input
		String line2 = sc2.nextLine(); // get the output through the scanners next line.
		int num2 = Integer.parseInt(line2); // translate output(input?) to an int
		AC = num2;
		sc2.close();

		// int NewIndex = mem.readMemory(addr);
		// AC = mem.readMemory(NewIndex);
	}

	public static void LoadIdxX_addr(PrintWriter pw, InputStream is, int addr) {// 4
		int NewIndex = addr + X;
		pw.printf("read " + (NewIndex) + "\n"); // print "read X\n" to the print writer
		pw.flush(); // Printwriter is just the RAM's input, so you're telling RAM "read _"
		Scanner sc = new Scanner(is); // initialize scanner to receive input
		String line = sc.nextLine(); // get the output through the scanners next line.
		int num = Integer.parseInt(line); // translate output(input?) to an int
		AC = num;
		// AC = mem.readMemory(NewIndex);
		sc.close();
	}

	public static void LoadIdxY_addr(PrintWriter pw, InputStream is, int addr) {// 5
		int NewIndex = addr + Y;
		pw.printf("read " + (NewIndex) + "\n"); // print "read X\n" to the print writer
		pw.flush(); // Printwriter is just the RAM's input, so you're telling RAM "read _"
		Scanner sc = new Scanner(is); // initialize scanner to receive input
		String line = sc.nextLine(); // get the output through the scanners next line.
		int num = Integer.parseInt(line); // translate output(input?) to an int
		AC = num;
		sc.close();
		// AC = mem.readMemory(NewIndex);
	}

	public static void LoadSpX(PrintWriter pw, InputStream is) {// 6
		int NewIndex = SP + X;
		pw.printf("read " + (NewIndex) + "\n"); // print "read X\n" to the print writer
		pw.flush(); // Printwriter is just the RAM's input, so you're telling RAM "read _"
		Scanner sc = new Scanner(is); // initialize scanner to receive input
		String line = sc.nextLine(); // get the output through the scanners next line.
		int num = Integer.parseInt(line); // translate output(input?) to an int
		AC = num;
		sc.close();
		// AC = mem.readMemory(NewIndex);
	}

	public static void Store_addr(PrintWriter pw, InputStream is, int addr) {// 7
		pw.printf("write " + AC + "\n");
		pw.flush();
		// mem.writeMemory(AC, addr);
	}

	public static void get() {// 8
		int random = (int) (Math.random() * 100);
		AC = random;
	}

	public static void Put_port(int port) {// 9
		if (port == 1) {
			System.out.println(AC);
		} else {
			System.out.println((char) AC);
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
	}

	public static void CopyFromSp() {// 19
		AC = SP;
	}

	public static void Jump_addr(int Addr) { // 20
		PC = Addr; // ???
	}

	public static void JumpIfEqual_addr(int Addr) {// 21
		if (AC == 0) {
			PC = Addr;
		}
	}

	public static void JumpIfNotEqual_addr(int Addr) {// 22
		if (AC != 0) {
			PC = Addr;
		}
	}

	public static void Call_addr(PrintWriter pw, InputStream is, int Addr) {// 23
		pw.printf("write " + (PC) + " " + (SP) + "\n"); // print "read X\n" to the print writer
		pw.flush(); // Printwriter is just the RAM's input, so you're telling RAM "read _"
		// mem.writeMemory(PC, SP); // save the PC counter address to the stack pointer
		// address? Maybe increment
		PC = Addr; // Now change PC to address
		SP--; // Decrement Stack Pointer
	}

	public static void Ret(PrintWriter pw, InputStream is) {// 24
		SP++; // Increment Stack Pointer (This is popping)
		//
		pw.printf("read " + (SP) + "\n"); // print "read X\n" to the print writer
		pw.flush(); // Printwriter is just the RAM's input, so you're telling RAM "read _"
		Scanner sc = new Scanner(is); // initialize scanner to receive input
		String line = sc.nextLine(); // get the output through the scanners next line.
		int num = Integer.parseInt(line); // translate output(input?) to an int
		sc.close();
		//
		// int Addr = mem.readMemory(SP);// pop address from stack
		PC = num;// Now change back? Could we not have just copied from SP?
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
		pw.printf("read " + (SP) + "\n"); // print "read X\n" to the print writer
		pw.flush(); // Printwriter is just the RAM's input, so you're telling RAM "read _"
		Scanner sc = new Scanner(is); // initialize scanner to receive input
		String line = sc.nextLine(); // get the output through the scanners next line.
		int num = Integer.parseInt(line); // translate output(input?) to an int
		AC = num;
		sc.close();
		// AC = mem.readMemory(SP); // maybe change?
	}

	public static void Int(PrintWriter pw, InputStream is) {// 29 ?????
		pw.printf("write " + (SP) + " " + (1999) + "\n"); // print "read X\n" to the print writer
		pw.flush(); // Printwriter is just the RAM's input, so you're telling RAM "read _"
		// mem.writeMemory(SP, 1999);// save Stack pointer to system stack
		SP = 1999;// switch to system stack
		SP--;// decrement system stack
		pw.printf("write " + (PC) + " " + (SP) + "\n"); // print "read X\n" to the print writer
		pw.flush(); // Printwriter is just the RAM's input, so you're telling RAM "read _"
		// mem.writeMemory(PC, SP); // save PC counter to system stack
		SP--; // decrement system stack
		PC = 1500; // switch address to 1500, interrupt instructions
		while (!isAtSystemEnd()) {
			ScanToken(pw, is);
		}
	}

	public static void IRet(PrintWriter pw, InputStream is) {// 30 ?????
		SP++;
		pw.printf("read " + (SP) + "\n"); // print "read X\n" to the print writer
		pw.flush(); // Printwriter is just the RAM's input, so you're telling RAM "read _"
		Scanner sc = new Scanner(is); // initialize scanner to receive input
		String line = sc.nextLine(); // get the output through the scanners next line.
		int num = Integer.parseInt(line); // translate output(input?) to an int
		PC = num;
		// PC = mem.readMemory(SP); // grab PC
		SP++;
		// pt 2 delete later maybe
		pw.printf("read " + (SP) + "\n"); // print "read X\n" to the print writer
		pw.flush(); // Printwriter is just the RAM's input, so you're telling RAM "read _"
		Scanner sc2 = new Scanner(is); // initialize scanner to receive input
		String line2 = sc2.nextLine(); // get the output through the scanners next line.
		int num2 = Integer.parseInt(line2); // translate output(input?) to an int
		SP = num2;

		// SP = mem.readMemory(SP); // Grab stack pointer
		while (!isAtEnd()) {
			ScanToken(pw, is);
		}
	}

	public static void End() {// 50
		return; // ???
	}
}

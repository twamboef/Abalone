package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ServerTUI implements ServerView {
	
	private PrintWriter consoleout;
	private BufferedReader consolein;
	
	/**
	 * Creates a new ServerTUI.
	 * @ensures new ServerTUI object with autoflushing PrintWriter
	 */
	public ServerTUI() {
		consoleout = new PrintWriter(System.out, true);
		consolein = new BufferedReader(new InputStreamReader(System.in));
	}
	
	/**
	 * Prints message to console.
	 * @param message to show in console
	 */
	@Override
	public void showMessage(String message) {
		consoleout.println(message);
	}
	
	/**
	 * Asks for string through console.
	 * @param question to show user
	 */
	@Override
	public String getString(String question) {
		showMessage(question);
		String result = "";
		try {
			result = consolein.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Asks for an integer through console.
	 * @param question to show user
	 */
	@Override
	public int getInt(String question) {
		showMessage(question);
		int result;
		while (true) {
			try {
				result = Integer.parseInt(consolein.readLine());
				break;
			} catch (NumberFormatException e) {
				showMessage("Please enter an integer");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * Asks for a yes/no answer through console.
	 * @param question to show user
	 */
	@Override
	public boolean getBoolean(String question) {
		showMessage(question);
		String result = "";
		while (true) {
			try {
				result = consolein.readLine().toLowerCase();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (result.equals("y") || result.equals("yes")) {
				return true;
			}
			if (result.equals("n") || result.equals("no")) {
				return false;
			}
			showMessage("Please answer with y/yes or n/no");
		}
	}
}

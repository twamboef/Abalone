package client;

import java.io.IOException;
import java.net.InetAddress;

import exceptions.ExitProgram;
import exceptions.ProtocolException;
import exceptions.ServerUnavailableException;

public interface ClientView{
	public  void start() throws ServerUnavailableException, ProtocolException, IOException, ExitProgram, InterruptedException;
	
	public void handleUserInput(String input) throws ServerUnavailableException, ProtocolException;
	/*
	 * writes the message to the output
	 * @param the message
	 */
	public void showMessage(String msg);
	/*
	 * asks the user for a valid IP. If it is not valid, ask again
	 * @return a valid IP
	 */
	public InetAddress getIP();
	/*
	 * prints question and asks for a string
	 * @param the question for the user
	 * @return the user input, a string
	 */
	public String getString(String question);
	/*
	 * prints question and asks for an integer
	 * @param the question for the user
	 * @return the user input, an integer
	 */
	public int getInt(String question);
	/*
	 * prints question and asks for a yes or no (y/n)
	 * @param the question for the user
	 * @return the user input, a boolean
	 */
	public Boolean getBoolean(String question);
}
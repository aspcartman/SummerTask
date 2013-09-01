package ru.ipccenter.aspcartman.JavaCommandLine;

import org.apache.commons.io.output.CloseShieldOutputStream;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * MIPT
 * Autor: aspcartman
 * Date: 02.09.13
 */
public class Kernel
{
	private CommandFlow commandFlow;
	private InputStream stdin;
	private OutputStream stdout;

	public Kernel()
	{
		stdin = new ShieldedStdInputStream();
		stdout = new ShieldedStdOutputStream();
		commandFlow = new CommandFlow(stdin,stdout);
	}

	public void Run()
	{
		for (; ; )
		{
			PrintWelcome();
			String input = ReadInput();
			if (input == null)
			{
				break;
			}

			Command[] parsedCommands = SyntaxInterpreter.Parse(input);
			try
			{
				commandFlow.PushChain(parsedCommands);
				commandFlow.Wait();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}


	private  void PrintWelcome()
	{
		String welcomeString = "username*machine: ";
		System.out.printf(welcomeString);
	}

	private  String ReadInput()
	{
		BufferedReader bfr = new BufferedReader(new InputStreamReader(stdin));
		String inputString = null;
		try
		{
			inputString = bfr.readLine();
		}
		catch (Exception e)
		{
			System.err.printf("Error: %s\n", e.getMessage());
		}

		return inputString;
	}

}

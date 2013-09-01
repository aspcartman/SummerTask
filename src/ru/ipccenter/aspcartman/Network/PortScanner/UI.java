package ru.ipccenter.aspcartman.Network.PortScanner;

import java.util.Arrays;
import java.util.ListIterator;

/**
 * MIPT
 * Autor: aspcartman
 * Date: 26.08.13
 */

/**
 * User Interface class. This class parses input arguments, creates an instance of PortScanner, passes input data there
 * and starts the scanning. This class implements PortScannerDelegate protocol and prints opened\closed ports as
 * PortScanner tells it too.
 */
class UI implements PortScannerDelegate
{
	private String targetAddress = null;
	private int startPort = 0;
	private int endPort = 0;
	private int socketTimeout = 100;
	private boolean printClosedPorts = false;
	private int currentCheckingPortBarLength;
	private int openedPortsCount;
	private int closedPortsCount;

	/**
	 * Main method of the class. Execute it to get things moving.
	 *
	 * @param args The console arguments.
	 * @throws Exception when things gone pretty bad.
	 */
	public void Run(String[] args) throws Exception
	{
		// TODO: use a special class to hold options as attributes and pass it around
		ParseConsoleArgsOrPrintUsageAndDie(args);
		PrepareForScanning();
		Scan();
		PrintFinalResults();
		CleanUpAfterScanning();
		StripInstanceVariableToDefaultValues();
	}

	//  READING INPUT

	private void PrintFinalResults()
	{
		System.out.printf("\rScan finished. %d of %d ports are open.\n", openedPortsCount, openedPortsCount + closedPortsCount);
	}

	private void StripInstanceVariableToDefaultValues()
	{
		socketTimeout = 100;
		printClosedPorts = false;
	}

	//  SCANNING

	private void CleanUpAfterScanning()
	{
		RemoveCurrentPortBar();
		NullifyOpenClosedPortsCounters();
		NullifyTargetAddressAndPortRange();
	}

	private void RemoveCurrentPortBar()
	{
		String backspaceChar = "\b";
		String removalString = "";
		for (int i = 0; i < currentCheckingPortBarLength; ++ i)
		{
			removalString = removalString.concat(backspaceChar);
		}
		System.out.print(removalString);
		currentCheckingPortBarLength = 0;
	}

	private void NullifyTargetAddressAndPortRange()
	{
		targetAddress = null;
		startPort = 0;
		endPort = 0;
	}

	private void Scan() throws Exception
	{
		PortScanner portScanner = new PortScanner(targetAddress, startPort, endPort);
		portScanner.delegate = this;
		portScanner.socketTimeout = socketTimeout;
		portScanner.Scan();
	}

	private void PrepareForScanning()
	{
		PrepareCurrentPortBar();
		NullifyOpenClosedPortsCounters();
	}

	private void PrepareCurrentPortBar()
	{
		System.out.printf("\n");
	}
	//  CHECKING PORT BAR

	private void NullifyOpenClosedPortsCounters()
	{
		openedPortsCount = 0;
		closedPortsCount = 0;
	}

	private void ParseConsoleArgsOrPrintUsageAndDie(String[] args)
	{
		try
		{
			ParseConsoleArgs(args);
		}
		catch (Exception e)
		{
			System.err.printf("USAGE: PortScanner [-a] [-i num] address startPort [endPort]\n" +
					"-a: Print closed ports too\n" +
					"-i num: Connection Timeout, default %d.\n", socketTimeout);
			System.exit(- 1);
		}
	}

	private void ParseConsoleArgs(String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new Exception("Wrong number of arguments");
		}

		ListIterator<String> iterator = Arrays.asList(args).listIterator();
		String currentArgument = iterator.next();

		// TODO: use JOPT library instead!
		if (currentArgument.equals("-a"))
		{
			printClosedPorts = true;
			currentArgument = iterator.next();
		}

		if (currentArgument.equals("-i"))
		{
			currentArgument = iterator.next(); // Getting a number after -i
			socketTimeout = Integer.parseInt(currentArgument);
			if (socketTimeout <= 0)
			{
				throw new Exception("Socket timeout should be greater than zero.");
			}
			currentArgument = iterator.next();
		}

		targetAddress = currentArgument; // No checks, it literally can be whatever
		currentArgument = iterator.next();

		startPort = Integer.parseInt(currentArgument);

		if (iterator.hasNext())
		{
			currentArgument = iterator.next();
			endPort = Integer.parseInt(currentArgument);
		}
	}

	//  PORT SCANNER DELEGATE

	public void PortScannerWillCheckPort(PortScanner portScanner, String address, int port)
	{
		UpdateCurrentPortBar(port);
	}

	private void UpdateCurrentPortBar(int port)
	{
		String checkingPortString = String.format("\rChecking %d", port);
		System.out.printf(checkingPortString);
		System.out.flush();
		currentCheckingPortBarLength = checkingPortString.length();
	}

	public void PortScannerDidFindOpenPort(PortScanner portScanner, String address, int port)
	{
		System.out.printf("\r%s : %d is Open\n", address, port);
		openedPortsCount++;
	}

	// PRINTING FINAL RESULTS

	public void PortScannerDidFindClosedPort(PortScanner portScanner, String address, int port)
	{
		if (printClosedPorts)
		{
			System.out.printf("\r%s : %d is Closed\n", address, port);
		}
		closedPortsCount++;
	}
}

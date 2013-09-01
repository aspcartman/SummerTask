package ru.ipccenter.aspcartman.Network.PortScanner;

public class Main
{
	public static void main(String[] args)
	{
		try
		{
			UI userInterface = new UI();
			userInterface.Run(args);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(- 1);
		}
	}
}



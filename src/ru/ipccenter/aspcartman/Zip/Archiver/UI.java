package ru.ipccenter.aspcartman.Zip.Archiver;

import java.io.IOException;
import java.util.Arrays;

/**
 * MIPT
 * Autor: aspcartman
 * Date: 31.08.13
 */
abstract class UI
{
	static boolean ShouldAppend = false;
	static boolean ShouldExtract = false;
	static String[] fileNames = null;
	static String zipName = null;
	static String extractPath = null;

	static void Run(String[] args) throws IOException
	{
		ParseConsoleArgsOrPrintUsageAndDie(args);

		Archiver archiver = new Archiver();
		if (ShouldExtract)
		{
			archiver.Extract(zipName, extractPath);
			return;
		}

		if (ShouldAppend)
		{
			archiver.AppendFilesToArchive(fileNames, zipName);
			return;
		}

		archiver.Archive(fileNames, zipName);
	}

	private static void ParseConsoleArgsOrPrintUsageAndDie(String[] args)
	{
		try
		{
			ParseConsoleArgs(args);
		}
		catch (Exception e)
		{
			System.err.printf("USAGE: Archiver [-a] file/directory ... output.zip\n" + "-a: Append\n");
			System.exit(- 1);
		}
	}

	private static void ParseConsoleArgs(String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new Exception("Wrong number of arguments");
		}

		int rangeStart = 0;
		if (args[0].equals("-a"))
		{
			ShouldAppend = true;
			rangeStart += 1;
		}
		else if (args[0].equals("-u"))
		{
			if (args.length < 3)
			{
				throw new Exception("Wrong number of arguments");
			}
			ShouldExtract = true;
			extractPath = args[1];
			rangeStart += 2;
		}/* Not a very beautiful block here */

		fileNames = Arrays.copyOfRange(args, rangeStart, args.length - 1);
		zipName = args[args.length - 1];
	}
}

package ru.ipccenter.aspcartman.JavaCommandLine;

import org.apache.commons.lang3.ArrayUtils;

/**
 * MIPT
 * Autor: aspcartman
 * Date: 11.08.13
 */
public class SyntaxInterpreter
{
	public static Command[] Parse(String input)
	{
		String fullCommandString = RemoveLeadingAndTrailingSpaces(input);
		String[] commandsWithArgs = fullCommandString.split(" *\\| *");

		Command[] myCommands = {};
		for (String aCommandWithArgs : commandsWithArgs)
		{
			Command command = CommandFromStringWithArgs(aCommandWithArgs);
			myCommands = ArrayUtils.add(myCommands, command);
		}
		return myCommands;
	}

	private static Command CommandFromStringWithArgs(String string)
	{
		string = RemoveLeadingAndTrailingSpaces(string);
		String[] commandAndArgsSeperated = DelimiteCommandAndArgs(string);

		return new Command(commandAndArgsSeperated);
	}

	private static String RemoveLeadingAndTrailingSpaces(String string)
	{
		return string.trim();
	}

	private static String[] DelimiteCommandAndArgs(String string)
	{
		return string.split(" ");
	}
}

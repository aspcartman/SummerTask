package ru.ipccenter.aspcartman.JavaCommandLine;

public class Command
{
	private String[] params;

	public Command(String[] commandParams)
	{
		this.params = commandParams;
	}

	public String toString()
	{
		String string = "";
		for (String arg : this.params)
		{
			string = string.concat(" ");
			string = string.concat(arg);
		}
		string = string.trim();
		return string;
	}

	public String[] params()
	{
		return params;
	}
}

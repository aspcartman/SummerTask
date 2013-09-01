package ru.ipccenter.aspcartman.JavaCommandLine;

import org.apache.commons.io.output.CloseShieldOutputStream;

import java.io.*;
import java.util.concurrent.*;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		Kernel kernel = new Kernel();
		kernel.Run();
	}
}

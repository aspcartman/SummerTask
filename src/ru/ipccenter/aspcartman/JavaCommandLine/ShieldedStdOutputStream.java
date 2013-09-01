package ru.ipccenter.aspcartman.JavaCommandLine;

import java.io.IOException;
import java.io.OutputStream;

/**
 * MIPT
 * Autor: aspcartman
 * Date: 02.09.13
 */
public class ShieldedStdOutputStream extends OutputStream
{
	@Override
	public void write(int i) throws IOException
	{
		System.out.write(i);
	}

	@Override
	public void close()
	{

	}
}

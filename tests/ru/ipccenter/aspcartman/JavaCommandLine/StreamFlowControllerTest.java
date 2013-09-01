package ru.ipccenter.aspcartman.JavaCommandLine;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * MIPT
 * Autor: aspcartman
 * Date: 01.09.13
 */
public class StreamFlowControllerTest
{
	private PipedInputStream in;
	private PipedOutputStream out;
	private PipedInputStream reader;
	private PipedOutputStream writer;
	private CommandFlow controller;

	@Before
	public void setUp() throws Exception
	{
		in = new PipedInputStream();
		out = new PipedOutputStream();
		reader = new PipedInputStream(out);
		writer = new PipedOutputStream(in);

//		controller = new CommandFlow();
	}

	@After
	public void tearDown() throws Exception
	{
		in.close();
		out.close();
		reader.close();
		writer.close();
	}

	@Test
	public void testConnectStreams() throws Exception
	{
		String dataString = "Ololo";
		byte[] expected = dataString.getBytes();
		byte[] result = new byte[dataString.length()];

//		controller.ConnectStreams(in, out);
		writer.write(expected);
		reader.read(result);

		Assert.assertArrayEquals(expected,result);
	}

	@Test
	public void testCloseConnection() throws Exception
	{

	}
}

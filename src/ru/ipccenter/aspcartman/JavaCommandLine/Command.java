package ru.ipccenter.aspcartman.JavaCommandLine;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

/**
 * MIPT
 * Autor: aspcartman
 * Date: 11.08.13
 */
public class Command implements Callable<Integer>
{
	private final Object Ready;
	private String[] params;
	private volatile boolean streamsAreReady = false;
	private volatile InputStream inputStream = null;
	private volatile OutputStream outputStream = null;

	public Command(String[] params)
	{
		this.params = params;
		Ready = new Object();
	}

	public Integer call()
	{
		ProcessBuilder processBuilder = new ProcessBuilder(params);
		processBuilder.redirectErrorStream(true);
		int result = 0;

		try
		{
			Process process = processBuilder.start();
			PubliciseStreamsOfProcess(process);
			WaitForProcess(process);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			ReleaseStreamsAccessorsLock();
		}

		return result;
	}

	private void WaitForProcess(Process process) throws InterruptedException
	{
		synchronized (process)
		{
			process.wait();
		}
	}

	private void PubliciseStreamsOfProcess(Process process)
	{
		inputStream = process.getInputStream();
		outputStream = process.getOutputStream();
		ReleaseStreamsAccessorsLock();
	}

	private void ReleaseStreamsAccessorsLock()
	{
		synchronized (Ready)
		{
			streamsAreReady = true;
			Ready.notifyAll();
		}
	}

	public InputStream GetInputStream() throws InterruptedException
	{
		synchronized (Ready)
		{
			if (! streamsAreReady)
			{
				Ready.wait();
			}
			return inputStream;
		}
	}

	public OutputStream GetOutputStream() throws InterruptedException
	{
		synchronized (Ready)
		{
			if (! streamsAreReady)
			{
				Ready.wait();
			}
			return outputStream;
		}
	}
}

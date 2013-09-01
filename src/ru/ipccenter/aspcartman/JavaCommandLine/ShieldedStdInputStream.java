package ru.ipccenter.aspcartman.JavaCommandLine;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * MIPT
 * Autor: aspcartman
 * Date: 15.08.13
 */
public class ShieldedStdInputStream extends InputStream
{
	private static volatile PipedInputStream staticPIS;
	private static volatile PipedOutputStream staticPOS;
	private static Boolean initialized = false;

	public ShieldedStdInputStream()
	{
		if (! initialized)
		{
			CreatePipe();
			StartWorkerThread();
			initialized = true;
		}
	}

	private static void StartWorkerThread()
	{
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					for (; ; )
					{
						byte[] buffer = new byte[1024];
						int count;
						while ((count = System.in.read(buffer)) != - 1)
						{
							staticPOS.write(buffer, 0, count);
							staticPOS.flush();
						}
						if (Thread.currentThread().isInterrupted())
						{
							break;
						}
						staticPOS.close();
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
					System.err.println(e);
				}
			}
		});
		thread.start();
	}

	private static void CreatePipe()
	{
		staticPIS = MakePipedInputStream();
		staticPOS = MakePipedOutputStream(staticPIS);
	}

	private static PipedOutputStream MakePipedOutputStream(PipedInputStream pipedInputStream)
	{
		try
		{
			return new PipedOutputStream(pipedInputStream);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.err.println(e);
		}
		return null;
	}

	private static PipedInputStream MakePipedInputStream()
	{
		return new PipedInputStream();
	}

	@Override
	public void close() throws IOException
	{
		Revive();
	}

	public static void Revive()
	{
		System.err.println("REVIVING!");
		PipedInputStream oldPIS = staticPIS;
		PipedOutputStream oldPOS = staticPOS;

		CreatePipe();

		try
		{
			oldPIS.close();
			oldPOS.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.err.println(e);
		}
	}

	@Override
	public int read() throws IOException
	{
		return staticPIS.read();
	}

	@Override
	public int read(byte[] bytes) throws IOException
	{
		return staticPIS.read(bytes);
	}

	@Override
	public int read(byte[] bytes, int i, int i2) throws IOException
	{
		return staticPIS.read(bytes, i, i2);
	}

	@Override
	public synchronized void reset() throws IOException
	{
		staticPIS.reset();
	}

	@Override
	public boolean markSupported()
	{
		return staticPIS.markSupported();
	}

	@Override
	public int available() throws IOException
	{
		return staticPIS.available();
	}

	@Override
	public long skip(long l) throws IOException
	{
		return staticPIS.skip(l);
	}

	@Override
	public synchronized void mark(int i)
	{
		staticPIS.mark(i);
	}
}

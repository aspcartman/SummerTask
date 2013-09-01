package ru.ipccenter.aspcartman.JavaCommandLine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * MIPT
 * Autor: aspcartman
 * Date: 02.09.13
 */
public class Redirector
{
	private InputStream stdin;
	private OutputStream stdout;

	private ExecutorCompletionService<Integer> redirectService;
	private int count = 0;

	public Redirector(InputStream stdin, OutputStream stdout)
	{
		this.stdin = stdin;
		this.stdout = stdout;
		this.redirectService = NewService();
	}

	private ExecutorCompletionService<Integer> NewService()
	{
		ExecutorService service = Executors.newCachedThreadPool();
		return new ExecutorCompletionService<Integer>(service);
	}

	public void ConnectToStdin(OutputStream out)
	{
		ConnectStreams(stdin, out);
	}

	public void ConnectToStdout(InputStream in)
	{
		ConnectStreams(in, stdout);
	}

	public void ConnectStreams(InputStream in, OutputStream out)
	{
		if (in == null || out == null)
		{
			return; /* Silence! */
		}

		Callable<Integer> task = RedirectionTask(in, out);
		redirectService.submit(task);
		count++;
	}

	private Callable<Integer> RedirectionTask(final InputStream in, final OutputStream out)
	{
		return new Callable<Integer>()
		{
			@Override
			public Integer call()
			{
				return RedirectStreams(in, out);
			}
		};
	}

	private Integer RedirectStreams(InputStream in, OutputStream out)
	{
		try
		{
			Redirect(in, out);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return 0;
	}

	private void Redirect(InputStream in, OutputStream out) throws IOException
	{
		byte[] buffer = new byte[1024];
		int count;
		while ((count = in.read(buffer)) > 0)
		{
			out.write(buffer, 0, count);
		}
		out.flush();
		out.close();
	}

	public void TimbleStdin()
	{
		try
		{
			stdin.close(); /* Ugly */
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public void Wait() throws InterruptedException
	{
		int copiedCount = count;
		while (copiedCount > 0)
		{
			redirectService.take();
			copiedCount--;
			count--;
		}
	}
}

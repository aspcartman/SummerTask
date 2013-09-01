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
 * Date: 01.09.13
 */
public class CommandFlow
{

	private Command lastCommandPushed = null;
	private ExecutorCompletionService<Integer> redirectService;
	private ExecutorCompletionService<Integer> executionService;
	private InputStream stdin;
	private OutputStream stdout;
	private int count = 0;

	public CommandFlow(InputStream stdin, OutputStream stdout)
	{
		this.stdin = stdin;
		this.stdout = stdout;

		executionService = NewService();
		redirectService = NewService();
	}

	private ExecutorCompletionService<Integer> NewService()
	{
		ExecutorService service = Executors.newCachedThreadPool();
		return new ExecutorCompletionService<Integer>(service);
	}

	public void PushChain(Command[] commands) throws InterruptedException
	{
		PushCommandsToChain(commands);
		CloseChain();
	}

	public void ConnectToStdin(OutputStream out)
	{
		ConnectStreams(stdin, out);
	}

	public void Wait() throws InterruptedException, IOException
	{
		WaitForExecutionService();
		FreeStdin();
		WaitForRedirectionService();
		count = 0;
	}

	private void WaitForExecutionService() throws InterruptedException
	{
		for (int i = 0; i < count; ++ i)
		{
			executionService.take();
		}
	}

	private void FreeStdin() throws IOException
	{
		stdin.close();
	}

	private void WaitForRedirectionService() throws InterruptedException
	{
		for (int i = 0; i < count + 1; ++ i)
		{
			redirectService.take();
		}
	}

	private void PushCommandsToChain(Command[] commands) throws InterruptedException
	{
		for (Command command : commands)
		{
			Push(command);
		}
	}

	private void Push(Command command) throws InterruptedException
	{
		PushToExecution(command);
		PushToRedirection(command);
		lastCommandPushed = command;
		count++;
	}

	private void PushToRedirection(Command command) throws InterruptedException
	{
		if (lastCommandPushed == null)
		{
			ConnectToStdin(command.GetOutputStream());
		}
		else
		{
			ConnectStreams(lastCommandPushed.GetInputStream(), command.GetOutputStream());
		}
	}

	private void PushToExecution(Command command)
	{
		executionService.submit(command);
	}

	private void CloseChain() throws InterruptedException
	{
		if (lastCommandPushed == null)
		{
			throw new RuntimeException("Closing empty chain");
		}

		ConnectToStdout(lastCommandPushed.GetInputStream());
		lastCommandPushed = null;
	}

	private void ConnectToStdout(InputStream in)
	{
		ConnectStreams(in, stdout);
	}

	private void ConnectStreams(InputStream in, OutputStream out)
	{
		if (in == null || out == null)
		{
			return; /* Silence! */
		}

		Callable<Integer> task = RedirectionTask(in, out);
		redirectService.submit(task);
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
}
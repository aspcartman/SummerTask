package ru.ipccenter.aspcartman.JavaCommandLine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
	private ExecutorCompletionService<Integer> executionService;
	private Redirector redirector;

	private int count = 0;
	private Command lastCommandPushed;

	public CommandFlow(InputStream stdin, OutputStream stdout)
	{
		executionService = NewService();
		redirector = new Redirector(stdin,stdout);
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

	private void PushCommandsToChain(Command[] commands) throws InterruptedException
	{
		for (Command command : commands)
		{
			PushToExecution(command);
			PushToRedirection(command);
			lastCommandPushed = command;
			count++;
		}
	}

	private void PushToRedirection(Command command) throws InterruptedException
	{
		if (lastCommandPushed == null)
		{
			redirector.ConnectToStdin(command.GetOutputStream());
		}
		else
		{
			redirector.ConnectStreams(lastCommandPushed.GetInputStream(), command.GetOutputStream());
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

		redirector.ConnectToStdout(lastCommandPushed.GetInputStream());
		lastCommandPushed = null;
	}

	public void Wait() throws InterruptedException, IOException
	{
		WaitForExecutionService();
		FreeStdin();
		redirector.Wait();
		count = 0;
	}

	private void FreeStdin() throws IOException
	{
		redirector.TimbleStdin();
	}

	private void WaitForExecutionService() throws InterruptedException
	{
		for (int i = 0; i < count; ++ i)
		{
			executionService.take();
		}
	}
}
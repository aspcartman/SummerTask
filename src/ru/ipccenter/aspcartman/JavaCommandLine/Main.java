package ru.ipccenter.aspcartman.JavaCommandLine;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CloseShieldOutputStream;

import java.io.*;
import java.util.concurrent.*;

public class Main
{
	private static ExecutorCompletionService<Integer> commandsExecutor;
	private static ExecutorCompletionService<Object> redirectExecutor;

	public static void main(String[] args) throws IOException
	{
		InitExecutorServicies();

		for (; ; )
		{
			PrintWelcome();
			String input = ReadInput();
			if (input == null)
			{
				break;
			}

			Command[] commands = SyntaxInterpreter.Parse(input);
			RunCommands(commands);
			WaitCompletion(commands.length);
		}
	}

	private static void InitExecutorServicies()
	{
		commandsExecutor = ExecutorServiceForCommands();
		redirectExecutor = ExecutorServiceForRedirectionWorkers();
	}

	private static ExecutorCompletionService<Integer> ExecutorServiceForCommands()
	{
		ExecutorService exs = Executors.newCachedThreadPool();
		return new ExecutorCompletionService<Integer>(exs);
	}

	private static ExecutorCompletionService<Object> ExecutorServiceForRedirectionWorkers()
	{
		ExecutorService exs = Executors.newCachedThreadPool();
		return new ExecutorCompletionService<Object>(exs);
	}

	private static void RunCommands(Command[] commands)
	{
		CommandThread prevThread = null;
		for (int i = commands.length - 1; i >= 0; -- i)
		{
			CommandThread thread = new CommandThread(commands[i]);
			commandsExecutor.submit(thread);
			redirectExecutor.submit(PipingProcsTask(thread, prevThread));
			prevThread = thread;
		}
		redirectExecutor.submit(PipingProcsTask(null, prevThread));
	}

	private static Callable<Object> PipingProcsTask(final CommandThread proc1, final CommandThread proc2)
	{
		return new Callable<Object>()
		{
			@Override
			public Object call()
			{
				try
				{
					InputStream inputStream;
					OutputStream outputStream;

					if (proc1 != null)
					{
						inputStream = proc1.GetInputStream();
					}
					else // If noone on the left
					{
						inputStream = new ShieldedStdInputStream();
					}

					if (proc2 != null)
					{
						outputStream = proc2.GetOutputStream();
					}
					else // If noone on the right
					{
						outputStream = new CloseShieldOutputStream(System.out);
					}

					RedirectStreams(inputStream, outputStream);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
					System.err.println(e);
				}
				return null; // Not the nicest thing
			}
		};
	}

	private static int RedirectStreams(InputStream from, OutputStream to)
	{
		if (from == null || to == null)
			return 0;
		try
		{
			byte[] buffer = new byte[1024];
			int count = 0;
			while ((count = from.read(buffer)) > 0)
			{
				to.write(buffer,0,count);
			}
			to.flush();
			to.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return 0;
	}

	private static void WaitCompletion(int count)
	{

		try
		{
			WaitForCommands(count);
			ShieldedStdInputStream.Revive(); // We need to free stdin from read() block of 1 redirector
			WaitForRedirects(count);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	private static void WaitForCommands(int count) throws InterruptedException
	{
		for (int i = 0; i < count; ++ i)
		{
			try
			{
				commandsExecutor.take().get();
			}
			catch (ExecutionException e)
			{
				e.printStackTrace();
				System.err.println(e);
			}
		}
	}

	private static void WaitForRedirects(int count) throws InterruptedException
	{
		for (int i = 0; i < count+1; ++ i)
		{
			try
			{
				redirectExecutor.take().get();
			}
			catch (ExecutionException e)
			{
				e.printStackTrace();
				System.err.println(e);
			}
		}
	}

	private static void PrintWelcome()
	{
		String welcomeString = "username*machine: ";
		System.out.printf(welcomeString);
	}

	private static String ReadInput()
	{
		BufferedReader bfr = new BufferedReader(new InputStreamReader(new ShieldedStdInputStream()));
		String inputString = null;
		try
		{
			inputString = bfr.readLine();
		}
		catch (Exception e)
		{
			System.err.printf("Error: %s\n", e.getMessage());
		}

		return inputString;
	}
}

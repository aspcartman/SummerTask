/**
 * MIPT
 * Autor: aspcartman
 * Date: 10.08.13
 */

package ru.ipccenter.aspcartman.Network.PortScanner;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * This class checks a given port range for open ports. It can check a single port if
 * no (endPort = 0) range provided.
 * Common usage:
 * 1. Create an instance, supplementing it with target address, startPort and optionally endPort numbers
 * 2. Set a delegate object confirming to PortScannerDelegate interface
 * 3. Optionally set a timeout per port
 * 4. Hit Scan().
 */
public class PortScanner
{
	/**
	 * This value defines how long should scanner wait for port to answer. If this value is too small,
	 * then some ports may be mistakenly returned as closed, if it's too high - scanning could take too long to
	 * complete.
	 * Default Value: 100
	 */
	public int socketTimeout = 100;
	/**
	 *
	 */
	public PortScannerDelegate delegate = null;
	private String address = null;
	private int startPort = 0;
	private int endPort = 0;

	public PortScanner(String address, int startPort, int endPort)
	{
		this.address = address;
		this.startPort = startPort;
		this.endPort = (endPort == 0) ? startPort : endPort; // If endPort == 0, then just check a single port.
	}

	/**
	 * Starts the Scan. To grab the output use a delegate.
	 *
	 * @throws Exception if address, port range or delegate was not provided.
	 */
	public void Scan() throws Exception
	{
		ValidateForScanning();

		for (int port = startPort; port <= endPort; ++ port)
		{
			TellThatWillCheckPort(port);

			boolean result = CheckPort(port);
			if (result)
			{
				TellThatPortIsOpen(port);
			}
			else
			{
				TellThatPortIsClosed(port);
			}
		}
	}

	private void TellThatPortIsClosed(int port)
	{
		delegate.PortScannerDidFindClosedPort(this, address, port);
	}

	private void TellThatPortIsOpen(int port)
	{
		delegate.PortScannerDidFindOpenPort(this, address, port);
	}

	private void TellThatWillCheckPort(int port)
	{
		delegate.PortScannerWillCheckPort(this, address, port);
	}

	private boolean CheckPort(int port)
	{
		Socket testSocket = new Socket();
		InetSocketAddress inetSocketAddress = new InetSocketAddress(this.address, port);
		try
		{
			testSocket.connect(inetSocketAddress, this.socketTimeout);
			testSocket.close();
		}
		catch (IOException e)
		{
			return false;
		}
		return true;
	}

	private void ValidateForScanning() throws Exception
	{
		if (delegate == null)
		{
			throw new Exception("No delegate has been set.");
		}
		if (address == null)
		{
			throw new Exception("No address has been set.");
		}
		if (startPort == 0 && endPort == 0)
		{
			throw new Exception("No ports has been set.");
		}
	}
}


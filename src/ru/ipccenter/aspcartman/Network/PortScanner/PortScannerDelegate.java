package ru.ipccenter.aspcartman.Network.PortScanner;

/**
 * The delegate protocol for getting results from Port Scanner.
 */
public interface PortScannerDelegate
{
	void PortScannerWillCheckPort(PortScanner portScanner, String address, int port);

	void PortScannerDidFindOpenPort(PortScanner portScanner, String address, int port);

	void PortScannerDidFindClosedPort(PortScanner portScanner, String address, int port);
}
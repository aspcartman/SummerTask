package ru.ipccenter.aspcartman.Zip.Comparator;

import ru.ipccenter.aspcartman.Zip.ArchiveProvider;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.zip.ZipFile;

/**
 * This abstract class provides a user interface. It is responsible for opening a file, starting a comparison and
 * returning results to the user.
 */
abstract class UI
{
	public static void Run(String[] args)
	{
		ZipFile file1 = null;
		ZipFile file2 = null;
		try
		{
			file1 = ArchiveProvider.OpenZipFileOnPath(args[0]);
			file2 = ArchiveProvider.OpenZipFileOnPath(args[1]);
		}
		catch (Exception e)
		{
			file1 = OpenUsingFileChooserOrDie();
			file2 = OpenUsingFileChooserOrDie();
		}

		PrintWriter pw = getPrintWriterOrDie();

		ArrayList<ArchiveCREntry> crEntries = ArchiveComparator.Compare(file1, file2);
		for (ArchiveCREntry crEntry : crEntries)
		{
			pw.println(crEntry.toString());
			System.out.println(crEntry.toString());
		}
		pw.flush();
		System.out.flush();
	}

	private static PrintWriter getPrintWriterOrDie()
	{
		try
		{
			File resultingFile = OpenFileToSaveToOrDie();
			resultingFile.createNewFile();
			return new PrintWriter(resultingFile);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(- 1);
		}
		return null;
	}

	private static File OpenFileToSaveToOrDie()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setSelectedFile(new File("fileToSave.txt"));
		int res = fileChooser.showSaveDialog(null);
		if (res == JFileChooser.APPROVE_OPTION)
		{
			return fileChooser.getSelectedFile();
		}
		System.exit(- 1);
		return null;
	}

	private static ZipFile OpenUsingFileChooserOrDie()
	{
		try
		{
			JFileChooser fileChooser = new JFileChooser();

			int res = fileChooser.showOpenDialog(null);
			if (res == JFileChooser.APPROVE_OPTION)
			{
				return new ZipFile(fileChooser.getSelectedFile());
			}
			System.exit(- 1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(- 1);
		}
		return null; /* Should never get here */
	}
}

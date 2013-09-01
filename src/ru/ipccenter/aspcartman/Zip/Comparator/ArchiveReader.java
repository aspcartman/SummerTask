package ru.ipccenter.aspcartman.Zip.Comparator;

import ru.ipccenter.aspcartman.Zip.ArchiveEntryValidator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * MIPT
 * Autor: aspcartman
 * Date: 28.08.13
 */
class ArchiveReader
{
	private ZipFile zipFile;

	public ArchiveReader(ZipFile zipFile)
	{
		this.zipFile = zipFile;
	}

	public ArrayList<ZipEntry> ArchiveContents()
	{
		ArrayList<ZipEntry> zipEntriesList = new ArrayList<ZipEntry>();
		Enumeration<? extends ZipEntry> entries = zipFile.entries();

		while (entries.hasMoreElements())
		{
			ZipEntry entry = entries.nextElement();
			if (! ArchiveEntryValidator.isValid(entry))
			{
				continue;
			}

			zipEntriesList.add(entry);
		}

		return zipEntriesList;
	}

	public void close()
	{
		try
		{
			zipFile.close();
		}
		catch (IOException e)
		{
			/* I don't find this one important, silencing it down. */
		}
	}
}

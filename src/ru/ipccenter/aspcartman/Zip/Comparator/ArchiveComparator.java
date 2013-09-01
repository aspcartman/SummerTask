package ru.ipccenter.aspcartman.Zip.Comparator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This class provides a method for comparing two ZipFiles
 */
public class ArchiveComparator
{
	/**
	 * @param zipFile1
	 * @param zipFile2
	 * @return List of ComparisonResults entries which can be easily printed by toString() method
	 */
	public static ArrayList<ArchiveCREntry> Compare(ZipFile zipFile1, ZipFile zipFile2)
	{
		ArchiveReader reader1 = new ArchiveReader(zipFile1);
		ArchiveReader reader2 = new ArchiveReader(zipFile2);

		ArrayList<ZipEntry> files1 = reader1.ArchiveContents();
		ArrayList<ZipEntry> files2 = reader2.ArchiveContents();

		reader1.close();
		reader2.close();

		ArrayList<ArchiveCREntry> crs = new ArrayList<ArchiveCREntry>();

		Iterator<ZipEntry> it1 = files1.iterator();
		while (it1.hasNext())
		{
			ZipEntry file1 = it1.next();
			Iterator<ZipEntry> it2 = files2.iterator();
			while (it2.hasNext())
			{
				ZipEntry file2 = it2.next();

				boolean name = NamesAreEqual(file1, file2);
				boolean size = SizesAreEqual(file1, file2);
				boolean hash = HashesAreEqual(file1, file2);

				if (name && size && hash)
				{
					AddCREntry_Unchanged(crs, file1, file2);
				}
				else if (size && hash)
				{
					AddCREntry_Renamed(crs, file1, file2);
				}
				else if (name)
				{
					AddCREntry_Changed(crs, file1, file2);
				}
				else
				{
					continue;
				}

				it1.remove();
				it2.remove();
				break;
			}
		}
		ProcessRemovedFiles(files1, crs);
		ProcessAddedFiles(files2, crs);
		return crs;
	}

	private static boolean HashesAreEqual(ZipEntry file1, ZipEntry file2)
	{
		return (file1.getCrc() == file2.getCrc());
	}

	private static boolean SizesAreEqual(ZipEntry file1, ZipEntry file2)
	{
		return (file1.getSize() == file2.getSize());
	}

	private static boolean NamesAreEqual(ZipEntry file1, ZipEntry file2)
	{
		return (file1.getName().equals(file2.getName()));
	}

	private static void ProcessRemovedFiles(ArrayList<ZipEntry> files1, ArrayList<ArchiveCREntry> crs)
	{
		for (ZipEntry removedFile : files1)
		{
			AddCREntry_Removed(crs, removedFile);
		}
	}

	private static void ProcessAddedFiles(ArrayList<ZipEntry> files2, ArrayList<ArchiveCREntry> crs)
	{
		for (ZipEntry addedFile : files2)
		{
			AddCREntry_Added(crs, addedFile);
		}
	}

	private static void AddCREntry_Added(ArrayList<ArchiveCREntry> crs, ZipEntry file)
	{
		ArchiveCREntry archiveCREntry = new ArchiveCREntry(null, file, ArchiveEntryCR.ADDED);
		crs.add(archiveCREntry);
	}

	private static void AddCREntry_Removed(ArrayList<ArchiveCREntry> crs, ZipEntry file)
	{
		ArchiveCREntry archiveCREntry = new ArchiveCREntry(file, null, ArchiveEntryCR.REMOVED);
		crs.add(archiveCREntry);
	}

	private static void AddCREntry_Renamed(ArrayList<ArchiveCREntry> crs, ZipEntry file1, ZipEntry file2)
	{
		ArchiveCREntry archiveCREntry = new ArchiveCREntry(file1, file2, ArchiveEntryCR.RENAMED);
		crs.add(archiveCREntry);
	}

	private static void AddCREntry_Changed(ArrayList<ArchiveCREntry> crs, ZipEntry file1, ZipEntry file2)
	{
		ArchiveCREntry archiveCREntry = new ArchiveCREntry(file1, file2, ArchiveEntryCR.CHANGED);
		crs.add(archiveCREntry);
	}

	private static void AddCREntry_Unchanged(ArrayList<ArchiveCREntry> crs, ZipEntry file1, ZipEntry file2)
	{
		ArchiveCREntry archiveCREntry = new ArchiveCREntry(file1, file2, ArchiveEntryCR.UNCHANGED);
		crs.add(archiveCREntry);
	}
}

package ru.ipccenter.aspcartman.Zip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipFile;

/**
 * This abstract class is being used for quick opening of archives. Just a small helper.
 */
public abstract class ArchiveProvider
{
	public static ZipFile OpenTestZipFile1() throws IOException
	{
		return OpenZipFileOnPath("./assets/Test1.zip");
	}

	public static ZipFile OpenTestZipFile2() throws IOException
	{
		return OpenZipFileOnPath("./assets/Test2.zip");
	}

	public static ZipFile OpenDummyArchive() throws IOException
	{
		return OpenZipFileOnPath("./assets/Dummy.zip");
	}

	public static ZipFile OpenZipFileOnPath(String path) throws IOException
	{
		File file = OpenFileOnPath(path);
		return new ZipFile(file);
	}

	static File OpenFileOnPath(String path) throws FileNotFoundException
	{
		File file = new File(path);
		if (! file.exists())
		{
			throw new FileNotFoundException(path);
		}
		return file;
	}


}

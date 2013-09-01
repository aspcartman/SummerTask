package ru.ipccenter.aspcartman.Zip;

import java.util.zip.ZipEntry;

/**
 * There are times, when files in archives are not supposed to be
 * viewed by users or be there at all. This class provides a check for that.
 */
public abstract class ArchiveEntryValidator
{
	public static boolean isValid(ZipEntry entry)
	{
		return isValid(entry.getName());
	}

	public static boolean isValid(String name)
	{
		if (macosxCheck(name))
		{
			return false;
		}
		if (dsstoreCheck(name))
		{
			return false;
		}
		return true;
	}

	private static boolean macosxCheck(String name)
	{
		// MacOSX loves to insert some system files into every single archive
		// it creates.
		return name.contains("__MACOSX");
	}

	private static boolean dsstoreCheck(String name)
	{
		// MacOSX loves to insert some system files into every single archive
		// it creates.
		return name.contains(".DS_Store");
	}
}
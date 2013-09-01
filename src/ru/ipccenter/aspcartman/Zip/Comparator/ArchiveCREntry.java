package ru.ipccenter.aspcartman.Zip.Comparator;

import java.io.Serializable;
import java.util.zip.ZipEntry;

/**
 * MIPT
 * Autor: aspcartman
 * Date: 29.08.13
 */
class ArchiveCREntry implements Serializable
{
	public ZipEntry file1 = null;
	public ZipEntry file2 = null;
	public ArchiveEntryCR comparisonResult;

	public ArchiveCREntry(ZipEntry file1, ZipEntry file2, ArchiveEntryCR comparisonResult)
	{
		this.file1 = file1;
		this.file2 = file2;
		this.comparisonResult = comparisonResult;
	}

	@Override
	public String toString()
	{
		String result = "";
		result += (file1 != null) ? file1.getName() : " ---- ";
		result += " < -- > ";
		result += (file2 != null) ? file2.getName() : " ---- ";
		result += " = ";
		result += comparisonResult;
		return result;
	}
}

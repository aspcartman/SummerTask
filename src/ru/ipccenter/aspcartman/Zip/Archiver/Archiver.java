package ru.ipccenter.aspcartman.Zip.Archiver;

import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import ru.ipccenter.aspcartman.Zip.ArchiveEntryValidator;

import java.io.*;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * This class archives or appends to archive passed files.
 */
public class Archiver
{

	public static final String TMP = ".tmp";

	/**
	 * Add files to archive by coping original archive contents to
	 * tmp archive, adding new content, removing original and renaming tmp one.
	 *
	 * @param filenames Paths to files
	 * @param zipName
	 * @throws IOException
	 */
	public void AppendFilesToArchive(String[] filenames, String zipName) throws IOException
	{
		FileList fileList = GetFileList(filenames);
		ZipFile originalZip = new ZipFile(zipName);
		ZipOutputStream newZipOS = CreateZip(zipName + TMP);

		CopyContentsOfArchives(originalZip, newZipOS);
		WriteFileListToZipStream(fileList, newZipOS);

		CloseZipOutputStream(newZipOS);
		DeleteOriginalFile(zipName);
		RenameTmpFile(zipName);
	}

	private FileList GetFileList(String[] fileNames) throws FileNotFoundException
	{
		FileList filesToArchive = new FileList();

		for (String filename : fileNames)
		{
			File file = new File(filename);
			if (! file.exists())
			{
				throw new FileNotFoundException("No such file or directory" + file.getName());
			}
			AddFileOrDirectory(filesToArchive, file);
		}
		return filesToArchive;
	}

	private void AddFileOrDirectory(Map<String, File> filesToArchive, File file)
	{
		if (file.isFile())
		{
			AddFile(filesToArchive, file);
		}
		else
		{
			AddFilesFromDirectoryRecursive(filesToArchive, file);
		}
	}

	private void AddFile(Map<String, File> filesToArchive, File file)
	{
		String name;
		name = file.getName();
		filesToArchive.put(name, file);
	}

	private void AddFilesFromDirectoryRecursive(Map<String, File> filesToArchive, File directory)
	{
		Collection<File> filesInDirectory = FileUtils.listFiles(directory, null, true);
		String directoryAbsolutePath = directory.getAbsolutePath();
		for (File fileInDir : filesInDirectory)
		{
			String fileAbsolutePath = fileInDir.getAbsolutePath();
			String name = fileAbsolutePath.substring(directoryAbsolutePath.length() + 1);
			if (! ArchiveEntryValidator.isValid(name))
			{
				continue;
			}
			filesToArchive.put(name, fileInDir);
		}
	}

	private void WriteFileListToZipStream(Map<String, File> files, ZipOutputStream zipOutputStream) throws IOException
	{
		for (Map.Entry<String, File> entry : files.entrySet())
		{
			WriteFileToZipStream(entry.getValue(), zipOutputStream, entry.getKey());
		}
	}

	private void WriteFileToZipStream(File file, ZipOutputStream zipOutputStream, String name) throws IOException
	{
		FileInputStream fileInputStream = new FileInputStream(file);
		ZipEntry zipEntry = new ZipEntry(name);
		zipOutputStream.putNextEntry(zipEntry);

		IOUtils.copy(fileInputStream, zipOutputStream);

		zipOutputStream.closeEntry();
		fileInputStream.close();
	}

	private ZipOutputStream CreateZip(String zipName) throws IOException
	{
		File zipFile = new File(zipName);
		if (zipFile.exists())
		{
			throw new FileExistsException("Output file already exists");
		}
		zipFile.createNewFile();
		FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
		return new ZipOutputStream(fileOutputStream);
	}

	private void CopyContentsOfArchives(ZipFile originalZip, ZipOutputStream newZipOS) throws IOException
	{
		Enumeration<? extends ZipEntry> entries = originalZip.entries();
		while (entries.hasMoreElements())
		{
			ZipEntry e = entries.nextElement();
			newZipOS.putNextEntry(e);
			if (! e.isDirectory())
			{
				IOUtils.copy(originalZip.getInputStream(e), newZipOS);
			}
			newZipOS.closeEntry();
		}
	}

	private void CloseZipOutputStream(ZipOutputStream newZipOS) throws IOException
	{
		newZipOS.flush();
		newZipOS.close();
	}

	private void DeleteOriginalFile(String zipName) throws IOException
	{
		File file = new File(zipName);
		if (! file.delete())
		{
			throw new IOException("Could not delete original archive");
		}
	}

	private void RenameTmpFile(String zipName) throws IOException
	{
		File tmpFile = new File(zipName + TMP);
		File original = new File(zipName);
		FileUtils.moveFile(tmpFile, original);
	}

	/**
	 * Create archive with passed files
	 *
	 * @param fileNames Paths to files
	 * @param zipName   Resulting zip file name (including extension)
	 * @throws IOException
	 */
	public void Archive(String[] fileNames, String zipName) throws IOException
	{
		FileList fileList = GetFileList(fileNames);
		ZipOutputStream zipOutputStream = CreateZip(zipName);
		WriteFileListToZipStream(fileList, zipOutputStream);
		zipOutputStream.close();
	}

	/**
	 * Extracts files from archive to passed location
	 *
	 * @param zipName
	 * @param outputPath
	 * @throws IOException
	 */
	public void Extract(String zipName, String outputPath) throws IOException
	{
		String absoluteOutputPath = CreateOutputDirectory(outputPath);
		System.out.println(absoluteOutputPath);

		ZipFile zipFile = new ZipFile(zipName);
		Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
		while (enumeration.hasMoreElements())
		{
			ZipEntry entry = enumeration.nextElement();
			WriteInputStreamToFile(zipFile.getInputStream(entry), absoluteOutputPath + entry.getName());
		}
		zipFile.close();
	}

	private String CreateOutputDirectory(String outputPath) throws IOException
	{
		File outputDirectory = new File(outputPath);
		if (! outputDirectory.exists())
		{
			outputDirectory.mkdirs();
		}
		if (! outputDirectory.isDirectory())
		{
			throw new IOException("Output path is a file, not a directory " + outputDirectory.getAbsolutePath());
		}
		return outputDirectory.getAbsolutePath() + "/";
	}

	private void WriteInputStreamToFile(InputStream inputStream, String path) throws IOException
	{
		FileOutputStream fileOutputStream = CreateExtractingFile(path);
		IOUtils.copy(inputStream, fileOutputStream);
		fileOutputStream.close();
	}

	private FileOutputStream CreateExtractingFile(String name) throws IOException
	{
		File file = new File(name);
		CreateDirectoryStructureIfNeeded(file);
		boolean created = file.createNewFile();
		if (! created)
		{
			throw new FileExistsException("File being extracted already exists");
		}

		return new FileOutputStream(file);
	}

	private void CreateDirectoryStructureIfNeeded(File file)
	{
		File parentFile = file.getParentFile();
		if (! parentFile.exists())
		{
			parentFile.mkdirs();
		}
	}
}
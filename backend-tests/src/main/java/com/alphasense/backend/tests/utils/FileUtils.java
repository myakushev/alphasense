package com.alphasense.backend.tests.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public final class FileUtils {

    private static final String DEFAULT_OUTPUT_FOLDER = "target/outPut";

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    private FileUtils() {
    }

    public static String readPdfFile(String filePath) {
        try {
            File myFile = new File(filePath);
            PDDocument document = PDDocument.load(myFile);
            PDFTextStripper s = new PDFTextStripper();
            return s.getText(document);
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract text from PDF " + filePath, e);
        }
    }

    public static String readTextFile(String filePath) {
        File myFile = new File(filePath);
        try {
            return Files.readAllLines(myFile.toPath()).stream().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file " + filePath, e);
        }
    }

    public static void saveDataIntoFile(String data, String outPutFolder, String fileName) {
        createOutputFolderIfNotExists(outPutFolder);
        try {
            com.google.common.io.Files.write(data.getBytes(), new File(outPutFolder + fileName));
        } catch (IOException e) {
            throw new RuntimeException("Failed write data in to file..", e);
        }
    }

    public static void createOutputFolderIfNotExists(String outPutFilePath) {
        File path = new File(outPutFilePath);
        boolean isDirectoryCreated = path.exists();

        if (!isDirectoryCreated) {
            File pathOutPut = new File(DEFAULT_OUTPUT_FOLDER);
            pathOutPut.mkdir();
        }
    }

    public static List<String> getFilesInFolder(String folder) {
        try {
            return Files.walk(Paths.get(folder))
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Failed to get files in folder: {}. Exception: {}", folder, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void saveBase64DataIntoFile(String data, String outPutFolder, String fileName) {
        createOutputFolderIfNotExists(outPutFolder);
        byte[] decodedPdf = Base64.decodeBase64(data);
        try {
            org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(outPutFolder + fileName), decodedPdf);
        } catch (IOException e) {
            throw new RuntimeException("Failed write Base64 data in to file.", e);
        }
    }
}

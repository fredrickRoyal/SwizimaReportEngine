package com.swizima.reportengine.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.docx4j.Docx4J;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.FontTablePart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swizima.reportengine.dto.ConvertableFileRequestDTO;
import com.swizima.reportengine.dto.ReportMergeRequestDTO;
import com.swizima.reportengine.dto.ReportPreviewRequestDTO;
import com.swizima.reportengine.dto.SystemFeedback;
import com.swizima.reportengine.service.FileStorageProperties;
import com.swizima.reportengine.service.FileStorageService;
import com.zeonpad.docgenerator.ZDocGenerator;
 

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility; 

@RestController
public class ReportGereratorController {

	private static final Logger logger = LoggerFactory.getLogger(ReportGereratorController.class);

	@Autowired
	private FileStorageProperties fileStorageProperties;

	@Autowired
	private FileStorageService fileStorageService;

	@PostMapping("/preview")
	public SystemFeedback generateReport(@RequestBody ReportPreviewRequestDTO request) {
		SystemFeedback feedback = new SystemFeedback();
		feedback.setResponse(false);
		feedback.setMessage("An error message occured while generating report.");

		try {

			String url = previewReport(request);
			if (url != null) {
				feedback.setResponse(true);
				feedback.setMessage(url);
			}

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			feedback.setResponse(false);
			feedback.setMessage(ex.getMessage());
		}
		return feedback;
	}

	@PostMapping("/merge")
	public SystemFeedback generateReport(@RequestBody ReportMergeRequestDTO request) {
		SystemFeedback feedback = new SystemFeedback();
		feedback.setResponse(false);
		feedback.setMessage("An error message occured while generating report.");

		try {

			List<String> files = new ArrayList<String>();
			for (String file : request.getFiles()) {
				files.add(file);
				System.out.println("getFileUrl: " + file);
			}

			if (!files.isEmpty()) {
				String url = mergeDocument(files, request.getOutputFile(), request.getRefId());
				if (url != null) {
					feedback.setResponse(true);
					feedback.setMessage(url);
				} else {
					feedback.setMessage(
							"System is not able to Merge the provided files. Please contact your systems Admin.");
				}
			} else {
				feedback.setMessage("No files have been provided to merge");
			}

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			feedback.setResponse(false);
			feedback.setMessage(ex.getMessage());
		}
		return feedback;
	}

	@PostMapping("/convertopdf")
	public SystemFeedback convertDoc(@RequestBody ConvertableFileRequestDTO request) {
		SystemFeedback feedback = new SystemFeedback();
		feedback.setResponse(false);
		feedback.setMessage("An error message occured while generating report.");

		try {

			String fileExtension = getFileExtension(request.getInputFile());

			System.out.println("fileExtension: " + fileExtension);

			if (fileExtension.equalsIgnoreCase("docx")) {

				String url = convertDocxToPDF(request.getInputFile(), request.getOutputFile(), request.getRefId());

				if (url != null) {
					feedback.setResponse(true);
					feedback.setMessage(url);
				}

			}

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			feedback.setResponse(false);
			feedback.setMessage(ex.getMessage());
		}
		return feedback;
	}

	protected String previewReport(ReportPreviewRequestDTO request) {

		String downloadLink = null;
		try {

			String templatesFolder = fileStorageProperties.getReporttemplatesDir();
			String outputFolder = fileStorageProperties.getReportoutputDir();

			Files.createDirectories(Paths.get(outputFolder + request.getRefId()).toAbsolutePath().normalize());

			String reportfileName = outputFolder + request.getRefId() + "/" + request.getInputFileName();

			// createJson(outputFileName, outputFolder, jsonString);

			createJson(request.getOutputFileName(), outputFolder + request.getRefId() + "/", request.getJsonString());

			String destination = reportfileName + ".docx";

			String api = "" + reportfileName + ".json";

			File templateFile = new File(templatesFolder + request.getInputFileName() + ".docx");

			File apiFile = new File(api);

			ZDocGenerator obj = new ZDocGenerator();

			obj.generateDocument(templateFile.getAbsolutePath(), apiFile.getAbsolutePath(), destination);

			createPDF(destination, reportfileName + ".pdf");

			// downloadLink = fileStorageProperties.getDowloadUrl() +
			// "request=Document_preview&fileName=" + outputFileName;

			downloadLink = ServletUriComponentsBuilder.fromCurrentContextPath().path("/previewReport/")
					.path(request.getRefId() + "/").path(request.getInputFileName() + ".pdf").toUriString();

		} catch (Exception e) {

			System.out.println(e.getMessage());
		}

		return downloadLink;
	}

	private void createJson(String fileName, String folder, String jsonString) throws IOException {

		FileWriter file = new FileWriter(folder + "" + fileName + ".json");

		try {

			file.write(jsonString);

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		} finally {
			file.flush();
			file.close();

		}

	}

	private static void createPDF(String inputFile, String outputFile) {
		try {
			// long start = System.currentTimeMillis();

			InputStream templateInputStream = new FileInputStream(inputFile);
			WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(templateInputStream);
			// wordMLPackage.set
			MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

			Mapper fontMapper = new IdentityPlusMapper();
			wordMLPackage.setFontMapper(fontMapper);

			FontTablePart fontTablePart = wordMLPackage.getMainDocumentPart().getFontTablePart();
			fontTablePart.processEmbeddings();
			Set<String> fontsInUse = wordMLPackage.getMainDocumentPart().fontsInUse();
			// Make each embedded font available to the font mapper.
			for (String s : fontsInUse) {
				PhysicalFont physicalFont = PhysicalFonts.get(s);
				fontMapper.put(s, physicalFont);
			}
			// Now you can access your fonts, such as 'Comic Sans' or 'Arial Unicode MS'.
			PhysicalFont font = PhysicalFonts.getPhysicalFonts().get("Tahoma");
			fontMapper.put(Mapper.FONT_FALLBACK, font);

			FileOutputStream os = new FileOutputStream(outputFile);
			Docx4J.toPDF(wordMLPackage, os);

			os.flush();
			os.close();

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private String mergeDocument(List<String> files, String outFile, String refId) {
		String downloadLink = null;
		try {

			String rootDirectory = fileStorageProperties.getReportoutputDir();

			Files.createDirectories(Paths.get(rootDirectory + refId).toAbsolutePath().normalize());

			// Instantiating PDFMergerUtility class
			PDFMergerUtility PDFmerger = new PDFMergerUtility();

			// Setting the destination file
			PDFmerger.setDestinationFileName(rootDirectory + refId + "/" + outFile + ".pdf");

			// adding the source files

			// File file=new File(rootDirectory + refId + "/"+"1.pdf");
			// FileUtils.copyURLToFile(new
			// URL("http://localhost:8083/previewReport/kfredrick35/Application_form.pdf"),
			// file );

			// PDFmerger.addSource(file);

			for (int i = 0; i < files.size(); i++) {

				File file = new File(rootDirectory + refId + "/" + i + ".pdf");
				FileUtils.copyURLToFile(new URL(files.get(i)), file);

				PDFmerger.addSource(file);

				/*
				 * if (files.get(i).contains(".pdf")) {
				 * 
				 * PDFmerger.addSource(new File(files.get(i))); } else if
				 * (files.get(i).contains(".PDF")) {
				 * 
				 * PDFmerger.addSource(new File(files.get(i))); } else { String filename =
				 * rootDirectory + outFile + "_" + i + ".pdf"; FileUtils.copyURLToFile(new
				 * URL(files.get(i)), new File(filename));
				 * 
				 * PDFmerger.addSource(new File(filename)); }
				 */
				
				PDFmerger.mergeDocuments(); 

			}

			// Merging the two documents
			//PDFmerger.mergeDocuments(); 
			// downloadLink = FileManager.getAPI() + "request=Document_preview&fileName=" +
			// outFile;
			downloadLink = ServletUriComponentsBuilder.fromCurrentContextPath().path("/previewReport/")
					.path(refId + "/").path(outFile + ".pdf").toUriString();

			System.out.println("Documents merged");
		} catch (Exception ex) {
			System.out.println("Documents merging Exception:: "+ex);
		}

		return downloadLink;
	}

	private String convertDocxToPDF(String inputFile, String outputFile, String refId) {
		String downloadLink = null;
		try {

			String rootDirectory = fileStorageProperties.getReportoutputDir();

			Files.createDirectories(Paths.get(rootDirectory + refId).toAbsolutePath().normalize());

			long start = System.currentTimeMillis();
			// GenericConvertor genericConv = new GenericConvertor();
			// genericConv.convert(inputFile, outputFile);
			// System.err.println("Generate" + outputFile + " with " +
			// (System.currentTimeMillis() - start) + "ms");

			// 1) Load DOCX into XWPFDocument
			InputStream is = new FileInputStream(new File(inputFile));
			XWPFDocument document = new XWPFDocument(is);

			// 2) Prepare Pdf options
			PdfOptions options = PdfOptions.create();

			// 3) Convert XWPFDocument to Pdf
			OutputStream out = new FileOutputStream(new File(rootDirectory + refId + "/" + outputFile));
			PdfConverter.getInstance().convert(document, out, options);

			System.err.println("Generate" + outputFile + " with " + (System.currentTimeMillis() - start) + "ms");

			// downloadLink = FileManager.getAPI() + "request=Document_preview&fileName=" +
			// outputFile;
			downloadLink = ServletUriComponentsBuilder.fromCurrentContextPath().path("/previewReport/")
					.path(refId + "/").path(outputFile + ".pdf").toUriString();

		} catch (Throwable e) {
			e.printStackTrace();
		}

		return downloadLink;
	}

	private static String getFileExtension(String inputFile) {
		File file = new File(inputFile);
		String fileName = file.getName();
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		else
			return "";
	}

	@GetMapping("/previewReport/{refId}/{fileName:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String refId, @PathVariable String fileName,
			HttpServletRequest request) {

		String rootDirectory = fileStorageProperties.getReportoutputDir();

		// Load file as Resource
		Resource resource = fileStorageService.loadFileAsResource(fileName, rootDirectory + refId);

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Could not determine file type.");
		}

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
				.header(HttpHeaders.CONTENT_TYPE, "application/pdf").body(resource);
	}
}

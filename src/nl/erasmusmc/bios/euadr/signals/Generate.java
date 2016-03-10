package nl.erasmusmc.bios.euadr.signals;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.DefaultParser;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.euretos.brain.Brain;
import com.euretos.brain.Path;
import com.euretos.brain.PathElement;
import com.euretos.brain.PathElt;
import com.euretos.brain.PathResponse;
import com.opencsv.CSVReader;

public class Generate {

    final static String[] semanticTypes = new String[]{"Chemicals & Drugs",  "Anatomy", "Genes & Molecular Sequences", "Disorders", "Physiology"};

    public static void main(String[] args) {
	Options options = new Options().addOption("server", true, "brain URL").addOption("user", true, "username").addOption("password", true, "password")
		.addOption("outputDirectory", true, "output directory").addOption("input",true,"input filename");

	try {
	    CommandLineParser parser = new DefaultParser();
	    CommandLine cmd = parser.parse(options, args);
	    String inputFile = cmd.getOptionValue("input");
	    String outputDirectory = cmd.getOptionValue("outputDirectory");
	    String server = cmd.hasOption("server") ? cmd.getOptionValue("server") : "https://www.euretos-brain.com/spine-ws";
	    String user = cmd.getOptionValue("user");
	    String password = cmd.getOptionValue("password");

	    if (inputFile != null && outputDirectory != null && server != null && user != null && password != null) {
		process(inputFile, outputDirectory, server, user, password);
	    } else {
		new HelpFormatter().printHelp(Generate.class.getName(), options);
	    }
	} catch (ParseException e) {
	    e.printStackTrace();
	}
    }

    /**
     * @param inputFile
     * @param outputDirectory
     * @param server
     * @param user
     * @param password
     */
    private static void process(String inputFile, String outputDirectory, String server, String user, String password) {
	List<DrugDisease> drugDiseasePairs = new ArrayList<DrugDisease>();

	try {
	    CSVReader reader = new CSVReader(new FileReader(inputFile), '\t');
	    String [] line;
	    while ((line = reader.readNext()) != null) {
		drugDiseasePairs.add(new DrugDisease(line[2], line[0]));
	    }
	    reader.close();
	} catch (IOException e1) {
	    e1.printStackTrace();
	}

	Brain brain = new Brain(server, user, password);
	brain.connect();

	/*
	 * Generate the triples from a drug to a number of semantic groups
	 */
	for (String drugName : getUniqueDrugNames(drugDiseasePairs)){
	    System.out.println( "generating triples for drug " + drugName );
	    generateTriples( brain, outputDirectory, "Disease - Total occurrences (B-to-X) - ", drugName, "Chemicals & Drugs" );
	}

	/*
	 * Generate the triples from a disease to a number of semantic groups
	 */
	for (String diseaseName : getUniqueDiseaseNames(drugDiseasePairs)){
	    System.out.println( "generating triples for disease " + diseaseName );
	    generateTriples( brain, outputDirectory, "Drug - Total occurrences (B-to-X) - ", diseaseName, "Disorders" );
	}

	/*
	 * generate the indirectly connections between drug and disease
	 * 
	 */
	for (DrugDisease drugDisease : drugDiseasePairs){
	    System.out.println( "generating triples for drug " + drugDisease.getDrug() + " and disease " + drugDisease.getDisease() );
	    generateIndirectConnections( brain, "Indirectly connected (A-X-B) - ", outputDirectory, drugDisease.getDrug(), "Chemicals & Drugs", drugDisease.getDisease(), "Disorders" );
	}
    }

    private static void generateIndirectConnections(Brain brain, String label, String directory, String sourceConceptName, String sourceSemanticGroup, String targetConceptName, String targetSemanticGroup){
	String outFileName = directory + "/" + label + sourceConceptName.replaceAll("/", "_") + " - " + targetConceptName.replaceAll("/", "_") + ".xlsx";

	try {
	    /* total occurrances (B-to-X) - <conceptName> - all categories */
	    Workbook wb = new XSSFWorkbook();
	    XSSFSheet sheet = (XSSFSheet) wb.createSheet();

	    createHeader( sheet, new String[]{ "pathWeight", 
		    "tier0Concept/uuid", "tier0Concept/name", "tier0Concept/category", 
		    "tier1Concept/uuid", "tier1Concept/name", "tier1Concept/category", 
		    "tier2Concept/uuid", "tier2Concept/name", "tier2Concept/category", 
		    "tier01TripleInformation/0/tripleUuid", "tier01TripleInformation/0/predicateName", 
		    "tier12TripleInformation/0/tripleUuid", "tier12TripleInformation/0/predicateName", 
		    "tier01TripleInformation/1/tripleUuid", "tier01TripleInformation/1/predicateName", 
		    "tier12TripleInformation/1/tripleUuid", "tier12TripleInformation/1/predicateName", 
		    "tier01TripleInformation/2/tripleUuid", "tier01TripleInformation/2/predicateName", 
		    "tier12TripleInformation/2/tripleUuid", "tier12TripleInformation/2/predicateName", 
		    "tier01TripleInformation/3/tripleUuid", "tier01TripleInformation/3/predicateName", 
		    "tier12TripleInformation/3/tripleUuid", "tier12TripleInformation/3/predicateName", 
		    "tier01TripleInformation/4/tripleUuid", "tier01TripleInformation/4/predicateName", 
		    "tier12TripleInformation/4/tripleUuid", "tier12TripleInformation/4/predicateName", 
		    "tier01TripleInformation/5/tripleUuid", "tier01TripleInformation/5/predicateName", 
		    "tier12TripleInformation/5/tripleUuid", "tier12TripleInformation/5/predicateName", 
		    "tier01TripleInformation/6/tripleUuid", "tier01TripleInformation/6/predicateName", 
		    "tier12TripleInformation/6/tripleUuid", "tier12TripleInformation/6/predicateName", 
		    "tier01TripleInformation/7/tripleUuid", "tier01TripleInformation/7/predicateName", 
		    "tier12TripleInformation/7/tripleUuid", "tier12TripleInformation/7/predicateName", 
		    "tier01TripleInformation/8/tripleUuid", "tier01TripleInformation/8/predicateName",
		    "tier12TripleInformation/8/tripleUuid", "tier12TripleInformation/8/predicateName", 
		    "tier01TripleInformation/9/tripleUuid", "tier01TripleInformation/9/predicateName",
		    "tier12TripleInformation/9/tripleUuid", "tier12TripleInformation/9/predicateName"} ); 


	    List<String> sourceUuids = brain.getUUID(sourceConceptName, sourceSemanticGroup);
	    List<String> targetUuids = brain.getUUID(targetConceptName, targetSemanticGroup);

	    if ( sourceUuids != null && !sourceUuids.isEmpty() && targetUuids != null && !targetUuids.isEmpty() ){
		int page = 0;
		int count = 0;
		String previousUuid = "";
		List<Path> indirectPaths = null;
		do {
		    indirectPaths = brain.getIndirectPaths(sourceUuids, targetUuids, page, 1000);

		    if (indirectPaths != null && indirectPaths.size() > 0 ){

			XSSFRow row = null;
			int c = 0;

			/*
			 * collect information
			 */
			 for (Path path : indirectPaths){
			     for (PathElt triple : path.getTriples()){
				 if (IsNotSourceOrTarget(path.getSourceUuid(),path.getTargetUuid(),triple.getSourceUuid())){
				     if (!triple.getSourceUuid().equals(previousUuid)){
					 row = sheet.createRow(++count);
					 c = 0;
					 previousUuid = triple.getSourceUuid();
					 row.createCell(c++).setCellValue(path.getPathWeight());

					 row.createCell(c++).setCellValue(path.getSourceUuid());
					 row.createCell(c++).setCellValue(path.getSourceName());
					 row.createCell(c++).setCellValue(sourceSemanticGroup);

					 row.createCell(c++).setCellValue(triple.getSourceUuid());
					 row.createCell(c++).setCellValue(triple.getSourceName());
					 row.createCell(c++).setCellValue(triple.getSourceCategory());

					 row.createCell(c++).setCellValue(path.getTargetUuid());
					 row.createCell(c++).setCellValue(path.getTargetName());
					 row.createCell(c++).setCellValue(targetSemanticGroup);
				     }
				 }
				 if (IsNotSourceOrTarget(path.getSourceUuid(),path.getTargetUuid(),triple.getTargetUuid())){
				     if (!triple.getTargetUuid().equals(previousUuid)){
					 row = sheet.createRow(++count);
					 c = 0;
					 previousUuid = triple.getTargetUuid();
					 row.createCell(c++).setCellValue(path.getPathWeight());

					 row.createCell(c++).setCellValue(path.getSourceUuid());
					 row.createCell(c++).setCellValue(path.getSourceName());
					 row.createCell(c++).setCellValue(sourceSemanticGroup);

					 row.createCell(c++).setCellValue(triple.getTargetUuid());
					 row.createCell(c++).setCellValue(triple.getTargetName());
					 row.createCell(c++).setCellValue(triple.getTargetCategory());

					 row.createCell(c++).setCellValue(path.getTargetUuid());
					 row.createCell(c++).setCellValue(path.getTargetName());
					 row.createCell(c++).setCellValue(targetSemanticGroup);
				     }
				 }
				 row.createCell(c++).setCellValue(triple.getTripleUuid());
				 row.createCell(c++).setCellValue(triple.getPredicateName());
			     }
			 }

			 page++;
		    }
		} while ( indirectPaths != null && indirectPaths.size() > 0);
	    }

	    wb.write(new FileOutputStream(outFileName));
	    wb.close();

	} catch (Exception e){
	    e.printStackTrace();
	    System.err.println("exception: " + e.getMessage());
	}
    }

    private static Boolean IsNotSourceOrTarget(String sourceUuid, String targetUuid, String tripleSourceUuid) {
	return !sourceUuid.equals(tripleSourceUuid) && !targetUuid.equals(tripleSourceUuid);
    }

    private static HashSet<String> getUniqueDrugNames(List<DrugDisease> drugDiseasePairs) {
	HashSet<String> result = new HashSet<String>();
	for (DrugDisease drugDisease : drugDiseasePairs){
	    result.add(drugDisease.getDrug());
	}
	return result;
    }

    private static HashSet<String> getUniqueDiseaseNames(List<DrugDisease> drugDiseasePairs) {
	HashSet<String> result = new HashSet<String>();
	for (DrugDisease drugDisease : drugDiseasePairs){
	    result.add(drugDisease.getDisease());
	}
	return result;
    }

    private static void generateTriples(Brain brain, String directory, String label, String sourceConceptName, String sourceSemanticType) {
	try {

	    List<String> sourceUuids = brain.getUUID( sourceConceptName, sourceSemanticType );
	    if ( sourceUuids == null || sourceUuids.isEmpty() ){
		throw new MappingException( sourceConceptName + " not mapped" );
	    }


	    /* generate the required output */

	    String b2xFileName = directory + "/" + label + sourceConceptName.replaceAll("/", "_") + " - all categories.xlsx";
	    try {
		/* total occurrances (B-to-X) - <conceptName> - all categories */
		Workbook wb = new XSSFWorkbook();
		for (String sourceUuid : sourceUuids){
		    for (String semanticType : semanticTypes){
			XSSFSheet sheet = (XSSFSheet) wb.createSheet(semanticType);

			createHeader( sheet, new String[]{ "pathWeight", 
				"tier0Concept/uuid", "tier0Concept/name", "tier0Concept/category", 
				"tier1Concept/uuid", "tier1Concept/name", "tier1Concept/category", 
				"tier01TripleInformation/0/tripleUuid", "tier01TripleInformation/0/predicateName", 
				"tier01TripleInformation/1/tripleUuid", "tier01TripleInformation/1/predicateName", 
				"tier01TripleInformation/2/tripleUuid", "tier01TripleInformation/2/predicateName", 
				"tier01TripleInformation/3/tripleUuid", "tier01TripleInformation/3/predicateName", 
				"tier01TripleInformation/4/tripleUuid", "tier01TripleInformation/4/predicateName", 
				"tier01TripleInformation/5/tripleUuid", "tier01TripleInformation/5/predicateName", 
				"tier01TripleInformation/6/tripleUuid", "tier01TripleInformation/6/predicateName", 
				"tier01TripleInformation/7/tripleUuid", "tier01TripleInformation/7/predicateName", 
				"tier01TripleInformation/8/tripleUuid", "tier01TripleInformation/8/predicateName" } );

			int page = 0;
			int count = 0;
			PathResponse directPaths = null;
			do {
			    directPaths = brain.searchKeywordToSemanticType("direct", false, true, sourceUuid, semanticType, "pwd", "DESC", page, 1000);
			    for (Path path : directPaths.getPaths()){
				count++;
				//System.out.println(semanticType + ", " + count + "," + path.getTargetName());
				XSSFRow row = sheet.createRow(count);
				int c = 0;
				row.createCell(c++).setCellValue(path.getPathWeight());
				row.createCell(c++).setCellValue(path.getTargetUuid());
				row.createCell(c++).setCellValue(path.getTargetName());
				row.createCell(c++).setCellValue(semanticType);
				row.createCell(c++).setCellValue(path.getSourceUuid());
				row.createCell(c++).setCellValue(path.getSourceName());
				row.createCell(c++).setCellValue(sourceSemanticType);
				for (PathElt pathElt : path.getTriples()){
				    row.createCell(c++).setCellValue(pathElt.getTripleUuid());
				    row.createCell(c++).setCellValue(pathElt.getPredicateName());
				}
			    }
			    page++;
			} while (directPaths != null && directPaths.getFirst() != null && page < directPaths.getTotalPages());
		    }
		}

		wb.write(new FileOutputStream(b2xFileName));
		wb.close();
	    } catch (Exception e){
		System.err.println("error in processing " + b2xFileName);
		e.printStackTrace();
	    }

	} catch (MappingException e1) {
	    System.err.println(e1.getMessage());
	}
    }

    private static void createHeader(XSSFSheet sheet, String[] headerLabels) {
	XSSFRow header = sheet.createRow(0);

	for ( int i = 0 ; i < headerLabels.length ; i++ ){
	    header.createCell(i).setCellValue(headerLabels[i]);
	}
    }

}

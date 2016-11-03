package nl.erasmusmc.bios.euadr.signals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.opencsv.CSVReader;

import nl.erasmusmc.mi.biosemantics.rxnorm.IdGroupResponse;
import nl.erasmusmc.mi.biosemantics.rxnorm.PropConceptGroupResponse;
import nl.erasmusmc.mi.biosemantics.rxnorm.RxNorm;
import nl.erasmusmc.mi.biosemantics.rxnorm.SpellingSuggestionGroupResponse;

public class MapCompoundToUMLS {

    static Logger logger = LogManager.getLogger();
    static RxNorm rxnorm = new RxNorm();

    public static void main(String[] args) throws IOException {

	Options options = new Options()
		.addOption("outputDirectory", true, "output directory")
		.addOption("input",true,"input filename");

	try {
	    CommandLineParser parser = new DefaultParser();
	    CommandLine cmd = parser.parse(options, args);
	    String inputFile = cmd.getOptionValue("input");
	    String outputDirectory = cmd.getOptionValue("outputDirectory");

	    if (inputFile != null && outputDirectory != null) {
		process(inputFile, outputDirectory);
	    } else {
		new HelpFormatter().printHelp(Generate.class.getName(), options);
	    }
	} catch (ParseException e) {
	    e.printStackTrace();
	}
    }

    private static String findUMLS(String searchTerm){
	SpellingSuggestionGroupResponse spellingSuggestionGroupResponse = rxnorm.spellingSuggestions(searchTerm);
	if (spellingSuggestionGroupResponse != null && spellingSuggestionGroupResponse.getSuggestionGroup() != null && spellingSuggestionGroupResponse.getSuggestionGroup().getSuggestionList() != null){
	    for (String suggestion : spellingSuggestionGroupResponse.getSuggestionGroup().getSuggestionList().getSuggestion()){
		if (suggestion.replaceAll("\\s+","").equalsIgnoreCase(searchTerm.replaceAll("\\s+",""))) {
		    System.out.println("found term:" + suggestion + " for " + searchTerm);
		    IdGroupResponse idGroupResponse = rxnorm.name(suggestion);
		    if (idGroupResponse.getIdGroup() != null && idGroupResponse.getIdGroup().getRxnormId() != null && !idGroupResponse.getIdGroup().getRxnormId().isEmpty()){
			String rxcui = idGroupResponse.getIdGroup().getRxnormId().get(0);
			PropConceptGroupResponse property = rxnorm.property(rxcui, "UMLSCUI");
			if (property.getPropConceptGroup() != null && property.getPropConceptGroup().getPropConcept() != null && !property.getPropConceptGroup().getPropConcept().isEmpty()){
			    return property.getPropConceptGroup().getPropConcept().get(0).getPropValue();
			}
		    }
		}
	    }
	}
	return null;
    }
    
    private static void process(String inputFile, String outputDirectory) throws IOException {
	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputDirectory + "/" + FilenameUtils.getBaseName(inputFile) + "_mapping.tsv"))));
	try {
	    CSVReader reader = new CSVReader(new FileReader(inputFile), '\t');
	    String [] line;

	    while ((line = reader.readNext()) != null) {
		String name = line[2];
		String umls = findUMLS(name);

		if (umls == null || umls.isEmpty()){
		    logger.info(line[2]);
		    bw.write("{\"" + name + "\",\"\"},\n");
		}
		else{
		    bw.write("{\"" + name + "\",\"" + umls + "\"},\n");
		}
	    }
	    reader.close();
	} catch (IOException e) {
	    logger.error(e.getMessage());
	}
	bw.close();
    }


}

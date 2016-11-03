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

import nl.erasmusmc.mi.bios.etox.rxnorm.RxNorm;

public class MapCompoundToATC {
    
    static Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws IOException {
	
	Options options = new Options()
		.addOption("server", true, "rxnorm server")
		.addOption("database", true, "rxnorm database")
		.addOption("user", true, "username")
		.addOption("password", true, "password")
		.addOption("outputDirectory", true, "output directory")
		.addOption("input",true,"input filename");

	try {
	    CommandLineParser parser = new DefaultParser();
	    CommandLine cmd = parser.parse(options, args);
	    String inputFile = cmd.getOptionValue("input");
	    String outputDirectory = cmd.getOptionValue("outputDirectory");
	    String server = cmd.hasOption("server") ? cmd.getOptionValue("server") : "localhost";
	    String database = cmd.getOptionValue("database");
	    String user = cmd.getOptionValue("user");
	    String password = cmd.getOptionValue("password");
	    

	    if (inputFile != null && outputDirectory != null && server != null && user != null && password != null) {
		process(inputFile, outputDirectory, new RxNorm(server, database, user, password));
	    } else {
		new HelpFormatter().printHelp(Generate.class.getName(), options);
	    }
	} catch (ParseException e) {
	    e.printStackTrace();
	}
    }

    private static void process(String inputFile, String outputDirectory, RxNorm rxnorm) throws IOException {
 	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputDirectory + "/" + FilenameUtils.getBaseName(inputFile) + "_mapping.tsv"))));
	try {
	    CSVReader reader = new CSVReader(new FileReader(inputFile), '\t');
	    String [] line;
	    
	    while ((line = reader.readNext()) != null) {
		String atc = rxnorm.getAtc(line[0]);
		if (atc == null){
		    logger.info(line[0]);
		}
		else{
		    bw.write("{\"" + line[0] + "\",\"" + atc + "\"},\n");
		}
	    }
	    reader.close();
	} catch (IOException e) {
	    logger.error(e.getMessage());
	}
	bw.close();
    }
    

}

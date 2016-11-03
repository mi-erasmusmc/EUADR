package nl.erasmusmc.bios.euadr.signals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CombineAXBSignals {

    public static void main(String[] args) throws IOException {
	FilenameFilter filenameFilter = new FilenameFilter() { 
	    public boolean accept(File dir, String filename){ 
		return filename.endsWith(".xlsx") && filename.startsWith("Indirectly connected (A-X-B) -"); 
	    }
	};
	String dirName = "/Users/mulligen/Workspaces/EMC/EUADR/data/signals_dili";
	BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/mulligen/Workspaces/EMC/EUADR/data/signals_dili/Indirectly connected (A-X-B).tsv"));
	
	File dir = new File(dirName);
	bw.write( StringUtils.join(new String[]{
			"combination",
			"pathWeight",
			"tier0Concept/uuid",
			"tier0Concept/name",
			"tier0Concept/category",
			"tier1Concept/uuid",
			"tier1Concept/name",
			"tier1Concept/category",
			"tier2Concept/uuid",
			"tier2Concept/name",
			"tier2Concept/category",
			"tier01TripleInformation/0/tripleUuid",
			"tier01TripleInformation/0/predicateName",
			"tier12TripleInformation/0/tripleUuid",
			"tier12TripleInformation/0/predicateName",
			"tier01TripleInformation/1/tripleUuid",
			"tier01TripleInformation/1/predicateName",
			"tier12TripleInformation/1/tripleUuid",
			"tier12TripleInformation/1/predicateName",
			"tier01TripleInformation/2/tripleUuid",
			"tier01TripleInformation/2/predicateName",
			"tier12TripleInformation/2/tripleUuid",
			"tier12TripleInformation/2/predicateName",
			"tier01TripleInformation/3/tripleUuid",
			"tier01TripleInformation/3/predicateName",
			"tier12TripleInformation/3/tripleUuid",
			"tier12TripleInformation/3/predicateName",
			"tier01TripleInformation/4/tripleUuid",
			"tier01TripleInformation/4/predicateName",
			"tier12TripleInformation/4/tripleUuid",
			"tier12TripleInformation/4/predicateName",
			"tier01TripleInformation/5/tripleUuid",
			"tier01TripleInformation/5/predicateName",
			"tier12TripleInformation/5/tripleUuid",
			"tier12TripleInformation/5/predicateName",
			"tier01TripleInformation/6/tripleUuid",
			"tier01TripleInformation/6/predicateName",
			"tier12TripleInformation/6/tripleUuid",
			"tier12TripleInformation/6/predicateName",
			"tier01TripleInformation/7/tripleUuid",
			"tier01TripleInformation/7/predicateName",
			"tier12TripleInformation/7/tripleUuid",
			"tier12TripleInformation/7/predicateName",
			"tier01TripleInformation/8/tripleUuid",
			"tier01TripleInformation/8/predicateName",
			"tier12TripleInformation/8/tripleUuid",
			"tier12TripleInformation/8/predicateName",
			"tier01TripleInformation/9/tripleUuid",
			"tier01TripleInformation/9/predicateName",
			"tier12TripleInformation/9/tripleUuid",
			"tier12TripleInformation/9/predicateName"}, "\t" ) + "\n");

	for ( File file : dir.listFiles(filenameFilter) ){
	    try {
		List<String> values = new ArrayList<String>();
		FileInputStream fis = new FileInputStream(file);
		XSSFWorkbook myWorkBook = new XSSFWorkbook (fis);
		XSSFSheet mySheet = myWorkBook.getSheetAt(0);
		Iterator<Row> rowIterator = mySheet.iterator();
		
		/* skip header */
		if (rowIterator.hasNext()) { 
		    rowIterator.next();
		}
		
		while (rowIterator.hasNext()) { 
		    values.clear();
		    values.add(FilenameUtils.removeExtension(file.getName()));
		    Row row = rowIterator.next(); // For each row, iterate through each columns 
		    Iterator<Cell> cellIterator = row.cellIterator(); 
		    while (cellIterator.hasNext()) { 
			Cell cell = cellIterator.next(); 
			switch (cell.getCellType()) { 
			case Cell.CELL_TYPE_STRING: 
			    values.add(cell.getStringCellValue());
			    break; 
			case Cell.CELL_TYPE_NUMERIC: 
			    Double value = cell.getNumericCellValue();
			    values.add(value.toString());
			    break; 
			case Cell.CELL_TYPE_BOOLEAN: 
			    Boolean bvalue = cell.getBooleanCellValue();
			    values.add(bvalue.toString());
			    break; 
			default : 
			}

			bw.write(StringUtils.join(values,"\t") + "\n");
		    } 
		}
		myWorkBook.close();
		fis.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    } 
	}
	bw.close();
    }
}

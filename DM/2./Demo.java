import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.*;




class CSV {
	String filename, delimiter;
	ArrayList<String[]> csvLines;
	
	CSV(String filename, String delimiter){
		this.filename = filename;
		this.delimiter = delimiter;
		csvLines = new ArrayList<>();
	}
	
	public void readCSV() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			String line;
			while((line = br.readLine())!=null) {
				String[] currLine = line.split(delimiter);
				csvLines.add(currLine);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void printCSV() {
		for(String[] currLine : csvLines) {
			for(String s : currLine) {
				System.out.print(s + " ");
			}
			System.out.println();
		}
	}
	
	public void writeCSV(String filename) {
		try {
			PrintWriter out = new PrintWriter(filename);
			for(String[] currLine : csvLines) {
				String line = "";
				for(String s : currLine) {
					line += s + ",";
				}
				out.write(line);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
class MissingValuesHandler {
	CSV csv;

	public MissingValuesHandler(CSV csv) {
		this.csv = csv;
	}

	public void replaceNumericValues(int col) {
		double total = 0;
		int number = 0;
		for (String[] currLine : csv.csvLines) {
			if (!currLine[col].equals("-")) {
				total += Double.parseDouble(currLine[col]);
				number++;
			}
		}
		double mean = total / number;
		for (String[] currLine : csv.csvLines) {
			if (currLine[col].equals("-")) {
				currLine[col] = Double.toString(mean);
			}
		}
	}

	public void replaceStringValues(int col) {
		HashMap<String, Integer> hm = new HashMap<>();
		for (String[] currLine : csv.csvLines) {
			if (!currLine[col].equals("-")) {
				if (hm.containsKey(currLine[col])) {
					hm.put(currLine[col], hm.get(currLine[col]) + 1);
				} else {
					hm.put(currLine[col], 1);
				}
			}
		}
		int max = 0;
		String value = "";
		for (Map.Entry<String, Integer> m : hm.entrySet()) {
			if (m.getValue() > max) {
				max = m.getValue();
				value = m.getKey();
			}
		}
		
		for(String[] currLine : csv.csvLines) {
			if(currLine[col].equals("-")) {
				currLine[col] = value;
			}
		}

	}

	public CSV replaceMissingValues() {
		System.out.println("ReplaceMissingValues------------------");
		int numCols = csv.csvLines.get(0).length;
		for (int i = 0; i < numCols; i++) {
			for (String[] currLine : csv.csvLines) {
				try {
					Double value = Double.parseDouble(currLine[i]);
					replaceNumericValues(i);
				} catch (Exception e) {
					String value = currLine[i];
					replaceStringValues(i);
				}
			}
		}
		System.out.println("------------------------------------");
		return csv;
	}
}
public class Demo {

	public static void main(String[] args) {
		String filename = "Input.csv";
		String delimiter = ",";
		CSV csv = new CSV(filename, delimiter);
		csv.readCSV();
		csv.printCSV();	
		MissingValuesHandler m = new MissingValuesHandler(csv);
		csv = m.replaceMissingValues();
		csv.printCSV();
	}
}
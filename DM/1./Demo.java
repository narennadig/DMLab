
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


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
 class Aggregate {
	CSV csv;
	public Aggregate(CSV csv) {
		this.csv = csv;
	}
	
	public CSV doAggregate(int col) {
		System.out.println("Aggregating-----------------------------------");
		double total = 0;
		for(String[] currLine : csv.csvLines) {
			total += Double.parseDouble(currLine[col]);
		}
		double mean = total/csv.csvLines.size();
		System.out.println("Mean of the attribute: " + mean);
		System.out.println("--------------------------------------------");
		return csv;
	}
}
 class Discretize {
	CSV csv;
	public Discretize(CSV csv) {
		this.csv = csv;
	}
	
	public CSV doDiscretize(int col) {
		System.out.println("Discretizing-----------------------------------");
		int numberOfPartitions = 3;
		double max = 0, min = 99999;
		for(String[] currLine : csv.csvLines) {
			double value = Double.parseDouble(currLine[col]);
			if(max < value) {
				max = value;
			}
			if(value < min) {
				min = value;
			}
		}
		
		double part1 = (double)(max-min)/3.0;
		double part2 = 2*part1;
		
		System.out.println("Min: " + min);
		System.out.println("Max: " + max);
		System.out.println("Mean1: " + part1);
		System.out.println("Mean2: " + part2);
		
		for(String[] currLine : csv.csvLines) {
			double value = Double.parseDouble(currLine[col]);
			if(value < part1) {
				currLine[col] = "Stage1";
			}
			else if(value >= part1 && value<=part2){
				currLine[col] = "Stage2";
			}
			else {
				currLine[col] = "Stage3";
			}
		}
		System.out.println("---------------------------------------------------");
		return csv;
	}
}


 class StratifiedSampling {
	CSV csv;
	public StratifiedSampling(CSV csv) {
		this.csv = csv;
	}
	
	public CSV doSample() {
		System.out.println("Sampling----------------------------");
		HashMap<String, Integer> sizeMap = new HashMap<>();
		HashMap<String, ArrayList<String[]>> listMap = new HashMap<>();
		for(String[] currLine : csv.csvLines) {
			String type = currLine[0];
			if(sizeMap.containsKey(type)) {
				sizeMap.put(type, sizeMap.get(type)+1);
			}
			else {
				sizeMap.put(type, 1);
			}
			
			if(listMap.containsKey(type)) {
				ArrayList<String[]> arr = listMap.get(type);
				arr.add(currLine);
				listMap.put(type, arr);
			}
			else {
				ArrayList<String[]> arr = new ArrayList<>();
				arr.add(currLine);
				listMap.put(type, arr);
			}
		}
		
		int min = 99999;
		for(Map.Entry<String, Integer> m : sizeMap.entrySet()) {
			if(m.getValue() < min) {
				min = m.getValue();
			}
		}
		
		csv.csvLines = new ArrayList<>();
		for(Map.Entry<String, ArrayList<String[]>> m : listMap.entrySet()) {
			ArrayList<String[]> arr = m.getValue();
			for(int i=0; i<min; i++) {
				csv.csvLines.add(arr.get(i));
			}
		}
		System.out.println("---------------------------------");	
		return csv;
	}
}
public class Demo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String filename = "Input.csv";
		String outFilename = "Output.csv";
		String delimiter = ",";
		CSV csv = new CSV(filename, delimiter);
		csv.readCSV();
		csv.printCSV();
		Aggregate a = new Aggregate(csv);
		csv = a.doAggregate(1);
		csv.printCSV();
		Discretize d = new Discretize(csv);
		csv = d.doDiscretize(1);
		csv.printCSV();
		StratifiedSampling ss = new StratifiedSampling(csv);
		csv = ss.doSample();
		csv.printCSV();
		csv.writeCSV(outFilename);
	}

}


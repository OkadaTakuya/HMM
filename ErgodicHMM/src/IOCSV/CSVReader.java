/**
 * Read and Write CSVfiles. for learn
 * @author OkadaTakuya
 */
package IOCSV;

import java.io.*;

public class CSVReader {
	private BufferedReader in;
	private int[] ret;
	private String filename;
	/**
	 * Constructor
	 * @param filename file name of csv.
	 */
	public CSVReader(String filename){
		this.filename = filename;
		try{
			in = new BufferedReader(new FileReader(new File(filename)));
		}catch(Exception e){
			System.out.println("File not found");
		}
	}
	public int[] readLine(){
		String str;
		String[] tokens;
		try{
			str = in.readLine();
			tokens = str.split(",");
			ret = new int[tokens.length];
			for(int i = 0;i < tokens.length;i++){
				ret[i] = Integer.parseInt(tokens[i]);
			}
			return ret;
		}catch(Exception e){
			System.out.println(e);
		}
		return null;
	}
	public void rewind(){
		try{
			in.close();
			in = new BufferedReader(new FileReader(new File(filename)));
		}catch(Exception e){
			
		}
		
	}
}
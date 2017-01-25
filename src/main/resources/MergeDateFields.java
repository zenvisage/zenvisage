/**
 * 
 */

/**
 * @author tarique
 *
 */
public class MergeDateFields {
	
	String filename="";
	int day=2;
	int month=3;
	int year=4;
	
	public static void main(String[] args) {
		BufferedReader br = new BufferedReader(new FileReader("file.txt"));
		BufferedReader br1 = new BufferedReader(new FileReader("file1.txt"));
		try {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();
		    while (line != null) {
		    	words=line.split(",");
		    	
		    	sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    String everything = sb.toString();
		} finally {
		    br.close();
		}
	}

	
	
	
}

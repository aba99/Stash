package atco.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class CustomProperties {
	
	private HashMap<String,String> properties = new HashMap<String,String>();

	public void load(String configFile) {
		Scanner sc = null;
		try {
			sc = new Scanner(new File(configFile));
			while (sc.hasNext()) {
				String line=sc.nextLine().trim();
				if (line.length()>0)
					if (!line.startsWith("#")) 
						if (line.contains("="))
							properties.put(line.split("=")[0].trim(), line.substring(line.indexOf("=")+1).trim());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}		
	}

	public String getProperty(String propertyName) {
		return properties.get(propertyName);
	}

	public CustomProperties(String file) {
		load(file);
	}

}

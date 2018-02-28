package edu.uiuc.zenvisage.api;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Readconfig {
	
	static Properties props = new Properties();
	static String configloc = "/src/main/resources/config.properties";
	static File f = new File("");
	static String path = f.getAbsoluteFile().getParent()+configloc;
	
//	ClassLoader classLoader = getClass().getClassLoader();
//	File fi = new File(classLoader.getResource("config.properties").getFile());
	
	static{
			try {
				FileInputStream in = new FileInputStream(path);
				props.load(in);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	static public int getPort(){
		return Integer.valueOf(props.getProperty("port"));
	}
	
	static public String getMetatable(){
		return props.getProperty("metatable");
	}
	
	static public String getMetafilelocation(){
		return props.getProperty("metafilelocation");
	}
	
	static public String getUsername(){
		return props.getProperty("username");
	}
	
	static public String getPassword(){
		return props.getProperty("password");
	}
	
	static public int getPostgresport() {
		return Integer.valueOf(props.getProperty("postgresport"));
	}
	
	static public boolean getBackendLogger() {
		return Boolean.valueOf(props.getProperty("logger"));
	}
	
	static public boolean getBackendQueriesLog() {
		return Boolean.valueOf(props.getProperty("querieslog"));
	}
	
	static public boolean getLoginAvaliable() {
		return Boolean.valueOf(props.getProperty("loginavailable"));
	}

	
}

package edu.uiuc.zenvisage.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@SuppressWarnings("serial")
public class UploadHandleServlet extends HttpServlet {
	public List<String> names;
	public List<FileItem> fileList;
  public String overwrite;
	
	public UploadHandleServlet() {
		this.names = new ArrayList<String> ();
    this.overwrite = "true";
	}
	
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String message = "";
        try{
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setHeaderEncoding("UTF-8"); 
            if(!ServletFileUpload.isMultipartContent(request)){
                return;
            }
            this.fileList = upload.parseRequest(request);
            for(FileItem item : fileList){
               	if(item.isFormField()){
                  String value = item.getString("UTF-8");
                    //////////// For dataset upload testing purposes only.//////////
                  if(value.substring(0,2).equals("!!")){
                      int space_idx = value.indexOf(" ");
                      File newFile = new File(value.substring(2,space_idx)+".csv");
                      PrintWriter pw = new PrintWriter(newFile);
                      pw.write(value.substring(space_idx+1));
                      pw.close();
                      names.add(value.substring(2,space_idx));
                      this.names.add(newFile.getAbsolutePath().toString());
                      break;
                  }
                  String fieldName = item.getFieldName();
                  if(fieldName.equals("overwrite") || fieldName.equals("Overwrite")) {
                    if(value.substring(0,1).equals("f") || value.substring(0,1).equals("F")) {
                      this.overwrite = "false";
                    }
                    continue;
                  }
                  System.out.println("form_field" + value);
                  names.add(value);
               	} else{
            		String filename = item.getName();
            		if(filename==null) continue;
            		//File newFile = new File("../src/main/resources/uploaded_data/"+filename);
            		File newFile = new File(filename);
            		item.write(newFile);
                    message = "success";
                    this.names.add(newFile.getAbsolutePath().toString());
                }
            }
            this.names.add(this.overwrite);
            System.out.println("names: " + names);
        }catch (Exception e) {
            message= "fail";
            e.printStackTrace();
            
        }
//        System.out.println(message);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
    
    public List<String> upload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doPost(request, response);
    	return this.names;
    }
}
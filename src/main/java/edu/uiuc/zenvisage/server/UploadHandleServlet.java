package edu.uiuc.zenvisage.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
	
	public UploadHandleServlet() {
		this.names = new ArrayList<String> ();
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
            System.out.println(fileList.size());
            
            for(FileItem item : fileList){
               	if(item.isFormField()){
                  String value = item.getString("UTF-8");              
                  names.add(value);
               	} else{
            		String filename = item.getName();
            		if(filename==null) continue;
            		//File newFile = new File("../src/main/resources/uploaded_data/"+filename);
//            		System.out.println(newFile.getCanonicalPath()); 
            		File newFile = new File(filename);
            		item.write(newFile);
                    message = "success";
                    this.names.add(newFile.getAbsolutePath().toString());
                }
            }
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
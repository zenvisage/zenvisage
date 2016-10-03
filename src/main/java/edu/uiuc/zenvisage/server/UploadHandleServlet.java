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

public class UploadHandleServlet extends HttpServlet {
	List<String> names;
	
	public UploadHandleServlet() {
		this.names = new ArrayList<String> ();
	}
	
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String savePath = System.getProperty("user.dir");
//        savePath = savePath.substring(0, savePath.lastIndexOf("/target"));
     // Configure a repository (to ensure a secure temp location is used)
        String message = "";
        try{
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setHeaderEncoding("UTF-8"); 
            if(!ServletFileUpload.isMultipartContent(request)){
                return;
            }
            List<FileItem> list = upload.parseRequest(request);
            System.out.println(list.size());
            for(FileItem item : list){
//            	if(item.isFormField()){
//                    String fieldName = item.getFieldName();
//                    String value = item.getString("UTF-8");
////                    System.out.print(value);
//                    names.add(value);
//                }else{
//                    String filename = item.getName();
//                    //System.out.println(filename);
//                    if(filename==null || filename.trim().equals("")){
//                        continue;
//                    }
//                    filename = filename.substring(filename.lastIndexOf("\\")+1);
//                    InputStream in = item.getInputStream();
//                    FileOutputStream out = new FileOutputStream(filename);
////                    FileOutputStream out = new FileOutputStream(savePath + filename);
//                    byte buffer[] = new byte[1024];
//                    int len = 0;
//                    while((len=in.read(buffer))>0){
//                    	System.out.println(buffer);
//                        out.write(buffer, 0, len);
//                    }
//                    in.close();
//                    out.close();
               	if(item.isFormField()){
                  String value = item.getString("UTF-8");
                  System.out.print(value);
                  names.add(value);
               	} else{
            		String filename = item.getName();
            		if(filename==null) continue;
//            		ServletContext context = request.getServletContext();
//            		String path = context.getRealPath("/");
            		File newFile = new File(filename);
//            		System.out.println(newFile.getAbsolutePath().toString());
//            		file.createNewFile();
            		System.out.println(newFile.getCanonicalPath());
            		item.write(newFile);
                    //item.delete();
                    message = "success";
                    this.names.add(newFile.getAbsolutePath().toString());
                }
            }
            
        }catch (Exception e) {
            message= "fail";
            e.printStackTrace();
            
        }
        System.out.println(message);
//                request.setAttribute("message",message);
//                request.getRequestDispatcher("/message.jsp").forward(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
    
    public List<String> upload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doPost(request, response);
    	return this.names;
    }
}
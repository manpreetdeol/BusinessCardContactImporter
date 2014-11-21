package com.example.businesscardimporter;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

public class HttpFileUpload {
    URL connectURL;
    String responseString;
    String Title;
    String Description;
    byte[ ] dataToServer;
    FileInputStream fileInputStream = null;

    HttpFileUpload(String urlString, String vTitle, String vDesc){
            try{
                    connectURL = new URL(urlString);
                    Title= vTitle;
                    System.out.println("This is the title "+Title);
                    Description = vDesc;
            }catch(Exception ex){
            	System.out.println("Exception");
                Log.i("HttpFileUpload","URL Malformatted");
            }
    }

    String Send_Now(String sourecFileUri){
    		String filename = sourecFileUri;
           String response =  Sending(filename);
           
           return response;
    }

    public String Sending(String filename){
//            String iFileName = "card.png";
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            String Tag="fSnd";
            String s = null;
            File sourceFile = new File(filename); 
            try
            {
            	    fileInputStream = new FileInputStream(sourceFile);
//            	
//                    Log.e(Tag,"Starting Http File Sending to URL");

                    // Open a HTTP connection to the URL
                    HttpURLConnection conn = (HttpURLConnection)connectURL.openConnection();

                    // Allow Inputs
                    conn.setDoInput(true);

                    // Allow Outputs
                    conn.setDoOutput(true);

                    // Don't use a cached copy.
                    conn.setUseCaches(false);

                    // Use a post method.
                    conn.setRequestMethod("POST");
                    

                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
//                    conn.setRequestProperty("uploaded_file", filename); 
                    conn.connect();
                    
                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                    System.out.println("sending title");
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"title\""+ lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(Title);
                    
                    System.out.println("FLushed");
                    System.out.println("Title which we are sending: " + Title);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                        
                    dos.writeBytes("Content-Disposition: form-data; name=\"description\""+ lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(Description);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                        
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\"" + filename +"\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    Log.e(Tag,"Headers are written");
                    
                    
                    // create a buffer of maximum size
                    int bytesAvailable = fileInputStream.available();
////                        
                    int maxBufferSize = 1 * 1024 * 1024;
                    int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    byte[ ] buffer = new byte[bufferSize];
//
//                    // read file and write it into form...
                    int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0)
                    {
                            dos.write(buffer, 0, bufferSize);
                            
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable,maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0,bufferSize);
                           // System.out.println("bytesread" +bytesRead);
                    }
                    
                    // send multipart form data necessary after file data
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                   // close streams
                    fileInputStream.close();
                        
                   
                   
                        
                    Log.e(Tag,"File Sent, Response: "+String.valueOf(conn.getResponseCode()));
                         
                    InputStream is = conn.getInputStream();
                        
                    // retrieve the response from server
                    int ch;

                    StringBuffer b =new StringBuffer();
                    
                    while( ( ch = is.read() ) != -1 ) { 
                    	b.append( (char)ch ); 
                    }
                    
                    s=b.toString();
                    Log.i("Response",s);
                    dos.flush();
                    dos.close();
                                        
            }
            
            catch (MalformedURLException ex)
            {
                    Log.e(Tag, "URL error: " + ex.getMessage(), ex);
            }

            catch (IOException ioe)
            {
                    Log.e(Tag, "IO error: " + ioe.getMessage(), ioe);
            }
			return s;
    }
}
package com.example.businesscardimporter;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

public class PreProcessing {
	
//	static String imageFile = "card2.png";

	public static void inputForSmoothing(String filename) {
		
	  	String excludeDotJpg = filename.split("\\.")[0];
	  	String perspective_transform_file = excludeDotJpg +"_prespective.png";
	  	String ROI_file = excludeDotJpg +"_roi.png";
	  	String binary_roi_file = excludeDotJpg +"_binary_roi.png";
		
		Mat result = smooth(filename);
		Highgui.imwrite(perspective_transform_file, result);
		
		 Mat img_grayROI =  Highgui.imread(perspective_transform_file, Highgui.CV_LOAD_IMAGE_GRAYSCALE);
//		  Highgui.imwrite("img_grayROI.png", img_grayROI);
//		  Mat img_gray2 = img_gray;
		  
		  Imgproc.GaussianBlur(img_grayROI, img_grayROI, new Size(15,15),50.00);
//		  Highgui.imwrite("img_blur.png", img_grayROI);
//		  
//		  Imgproc.adaptiveThreshold(img_grayROI, img_grayROI, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 15, 4);
//		  Highgui.imwrite("img_threshold.png", img_grayROI);
		  
		  Imgproc.threshold(img_grayROI, img_grayROI, -1, 255, Imgproc.THRESH_BINARY_INV+Imgproc.THRESH_OTSU);
//		  Highgui.imwrite("img_threshold2ROI.png", img_grayROI);
//		  
//		  Imgproc.Canny(img_grayROI, img_grayROI, 80, 100);
//		  Highgui.imwrite("img_cannyROI.png", img_grayROI);
		  
//		  Imgproc.erode(img_grayROI, img_grayROI, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,2)));        

		  Imgproc.dilate(img_grayROI, img_grayROI, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2)));
		  
		  Mat heirarchy= new Mat();
          Point shift=new Point(150,0);
		  
		  List<MatOfPoint> contours = new ArrayList<MatOfPoint>();    

	        Imgproc.findContours(img_grayROI, contours, heirarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
	        double[] cont_area =new double[contours.size()]; 
	        
		  for(int i=0; i< contours.size();i++){
	        	 if (Imgproc.contourArea(contours.get(i)) > 50 ){
	                 Rect rect = Imgproc.boundingRect(contours.get(i));
	                 cont_area[i]=Imgproc.contourArea(contours.get(i));
	                 
	                 if (rect.height > 25){
	                     Core.rectangle(result, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),new Scalar(0,0,255));
//	                     Imgproc.drawContours(img_grayROI, contours, i, new Scalar(0,0,0),-1,8,heirarchy,2,shift);
//	                     Core.rectangle(img_grayROI, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),new Scalar(0,0,255));
	                     
//	                     Mat ROI = img_grayROI.submat(rect.y, rect.y + rect.height, rect.x, rect.x + rect.width);

	                     System.out.println(rect.x +"-"+ rect.y +"-"+ rect.height+"-"+rect.width);
	                     
//	                     Highgui.imwrite("final.png",img_grayROI);
	                 }
	             }
	        }
		  Highgui.imwrite(ROI_file,result);		  
		  
		  Mat img_binarized =  Highgui.imread(ROI_file, Highgui.CV_LOAD_IMAGE_GRAYSCALE);
		  
		  Imgproc.threshold(img_binarized, img_binarized, -1, 255, Imgproc.THRESH_BINARY_INV+Imgproc.THRESH_OTSU);
		  
		  Highgui.imwrite(binary_roi_file, img_binarized);
	}	
	
	public static Mat warp(Mat inputMat,Mat startM) {
        int resultWidth = 1000;
        int resultHeight = 1000;
        
//        Imgproc.equalizeHist(inputMat, inputMat);

        Mat outputMat = new Mat(resultWidth, resultHeight, CvType.CV_8UC4);
        
        Point ocvPOut1 = new Point(0, 0);
        Point ocvPOut2 = new Point(0, resultHeight);
        Point ocvPOut3 = new Point(resultWidth, resultHeight);
        Point ocvPOut4 = new Point(resultWidth, 0);
        
        System.out.println(ocvPOut1 +"-"+ ocvPOut2 +"-"+ ocvPOut3+"-"+ocvPOut4);
        
        List<Point> dest = new ArrayList<Point>();
        dest.add(ocvPOut1);
        dest.add(ocvPOut2);
        dest.add(ocvPOut3);
        dest.add(ocvPOut4);
        Mat endM = Converters.vector_Point2f_to_Mat(dest);      

        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(startM, endM);

        Imgproc.warpPerspective(inputMat, 
                                outputMat,
                                perspectiveTransform,
                                new Size(resultWidth, resultHeight), 
                                Imgproc.INTER_CUBIC);
        
        return outputMat;
    }
	
//	static {
//		 System.loadLibrary("opencv_java2410");
//	}
	
	static {
	    if (!OpenCVLoader.initDebug()) {
	        // Handle initialization error
	    	System.out.println("Initialization error");
	    }
	}

	 public static Mat smooth(String filename) { 
		 		 
		  	String excludeDotJpg = filename.split("\\.")[0];
		  	String gray_file = excludeDotJpg +"gray.png";
		  	String threshold_file = excludeDotJpg +"threshold.png";
		  	String canny_file = excludeDotJpg +"canny.png";
		  	
//		  	Mat img = Highgui.imread(filename, Highgui.CV_LOAD_IMAGE_COLOR);
		  	
//		  Bitmap b = BitmapFactory.decodeByteArray(filename.getBytes(), 0, filename.getBytes().length);
		  Mat img_gray =  Highgui.imread(filename, Highgui.CV_LOAD_IMAGE_GRAYSCALE);
		  Highgui.imwrite(gray_file, img_gray);
//		  Mat img_gray2 = img_gray;
		  
		  Imgproc.threshold(img_gray, img_gray, -1, 255, Imgproc.THRESH_BINARY_INV+Imgproc.THRESH_OTSU);
		  Highgui.imwrite(threshold_file, img_gray);
		  
		  Imgproc.Canny(img_gray, img_gray, 80, 100);
		  Highgui.imwrite(canny_file, img_gray);
		  
//		  Imgproc.GaussianBlur(img, img, new Size(15,15),50.00);
//		  Highgui.imwrite("img_blur.png", img);
		  
//		  Imgproc.adaptiveThreshold(img, img, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 15, 4);
//		  Highgui.imwrite("img_threshold.png", img);
		  
		 
		  
		  List<MatOfPoint> contours = new ArrayList<MatOfPoint>();    

	        Imgproc.findContours(img_gray, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
	       
	        double maxArea = -1;
	        int maxAreaIdx = -1;
	        MatOfPoint temp_contour = contours.get(0); //the largest is at the index 0 for starting point
	        MatOfPoint2f approxCurve = new MatOfPoint2f();
	        MatOfPoint largest_contour = contours.get(0);
	        //largest_contour.ge
	        List<MatOfPoint> largest_contours = new ArrayList<MatOfPoint>();
	        //Imgproc.drawContours(imgSource,contours, -1, new Scalar(0, 255, 0), 1);

	        for (int idx = 0; idx < contours.size(); idx++) {
	            temp_contour = contours.get(idx);
	            double contourarea = Imgproc.contourArea(temp_contour);
	            //compare this contour to the previous largest contour found
	            if (contourarea > maxArea) {
	                //check if this contour is a square
	                MatOfPoint2f new_mat = new MatOfPoint2f( temp_contour.toArray() );
	                int contourSize = (int)temp_contour.total();
	                MatOfPoint2f approxCurve_temp = new MatOfPoint2f();
	                Imgproc.approxPolyDP(new_mat, approxCurve_temp, contourSize*0.05, true);
	                if (approxCurve_temp.total() == 4) {
	                    maxArea = contourarea;
	                    maxAreaIdx = idx;
	                    approxCurve=approxCurve_temp;
	                    largest_contour = temp_contour;
	                }
	            }
	        }

	       Imgproc.cvtColor(img_gray, img_gray, Imgproc.COLOR_BayerBG2RGB);
	       Mat sourceImage =Highgui.imread(filename);
	       double[] temp_double;
	       temp_double = approxCurve.get(0,0);       
	       Point p1 = new Point(temp_double[0], temp_double[1]);
	       
	       //Core.circle(imgSource,p1,55,new Scalar(0,0,255));
	       //Imgproc.warpAffine(sourceImage, dummy, rotImage,sourceImage.size());
	       temp_double = approxCurve.get(1,0);       
	       Point p2 = new Point(temp_double[0], temp_double[1]);	       
	       
	      // Core.circle(imgSource,p2,150,new Scalar(255,255,255));
	       temp_double = approxCurve.get(2,0);       
	       Point p3 = new Point(temp_double[0], temp_double[1]);
	       //Core.circle(imgSource,p3,200,new Scalar(255,0,0));
	       temp_double = approxCurve.get(3,0);       
	       Point p4 = new Point(temp_double[0], temp_double[1]);
	      // Core.circle(imgSource,p4,100,new Scalar(0,0,255));
	       
	       
	       // rearranging the points - BL, TL, TR, BR
	       TreeMap<Double, String> sortX = new TreeMap<Double, String>();
	       TreeMap<Double, Double> sortY = new TreeMap<Double, Double>(Collections.reverseOrder());
	       
	       sortX.put(p1.x, "p1");
	       sortX.put(p2.x, "p2");
	       sortX.put(p3.x, "p3");
	       sortX.put(p4.x, "p4");
	       
	       sortY.put(p1.y, p1.x);
	       sortY.put(p2.y, p2.x);
	       sortY.put(p3.y, p3.x);
	       sortY.put(p4.y, p4.x);
	       
	       Point points[]=new Point[4];
	       int i=0;
	       for (Entry<Double, Double> entry : sortY.entrySet()){
	    	    Double keyY = entry.getKey();
	    	    Double valueX = entry.getValue();
	    	    points[i]=new Point(valueX,keyY);
	    	    System.out.println(valueX+" "+keyY);
	    	    i++;
	    	}
	       
	       Point topLeft;
	       Point topRight;
	       if(points[0].x < points[1].x)
	       {
	    	   topLeft=new Point(points[0].x,points[0].y);
	    	   topRight=new Point(points[1].x,points[1].y);
	       }
	       else
	       {
	    	   topLeft=new Point(points[1].x,points[1].y);
	    	   topRight=new Point(points[0].x,points[0].y);
	       }
	       
	       Point bottomLeft;
	       Point bottomRight;
	       
	       if(points[2].x < points[3].x)
	       {
	    	   bottomLeft=new Point(points[2].x,points[2].y);
	    	   bottomRight=new Point(points[3].x,points[3].y);
	       }
	       else
	       {
	    	   bottomLeft=new Point(points[3].x,points[3].y);
	    	   bottomRight=new Point(points[2].x,points[2].y);
	       }
	       
	       System.out.println("Bottom Left P1:"+bottomLeft.x+" "+bottomLeft.y);
	       System.out.println("Top Left P2:"+topLeft.x+" "+topLeft.y);
	       System.out.println("Top Right P3:"+topRight.x+" "+topRight.y);
	       System.out.println("Bottom Right P4:"+bottomRight.x+" "+bottomRight.y);
	    	   
   
	       List<Point> source = new ArrayList<Point>();
	       source.add(bottomLeft);
	       source.add(topLeft);
	       source.add(topRight);
	       source.add(bottomRight);
	       
	       Mat startM = Converters.vector_Point2f_to_Mat(source);
	       Mat result=warp(sourceImage,startM);
	       return result;
  
	        
	 }
}

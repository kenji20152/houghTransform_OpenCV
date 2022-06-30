package test;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * houghTrasform class.
 * @author kenji
 */

public class houghTransform {
	
	private static String inputImage = "test.bmp";
	private static String outputImage = "test_output.bmp";
	
	public static void main(String[] args) {
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		//Input image
		Mat inputRGBImage = Imgcodecs.imread(inputImage, 0);
		
		//Input Mat size
		final int width = inputRGBImage.width();
		final int height = inputRGBImage.height();
		
		//Output Image
		Mat outputRGBImage = new Mat(height, width, CvType.CV_32SC3);
		
		//Change the image type
		inputRGBImage.convertTo(outputRGBImage, CvType.CV_32SC3);
		
		//RGB component of image
		byte[] color = new byte[3];
		
		//Maximum value of green to extract the whitest part of the image
		double g_Max = 0;
		
		//Find the largest value of the green component
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				inputRGBImage.get(i, j, color);//Get RGB(-127~128)
				color[1] += 128;//RGB is 0~255
				if(color[1] > g_Max) {
					g_Max = color[1];
				}
			}
		}
		
		//Pixels with less than 85% of the brightest lightness value are painted black.
		double toBlack = g_Max * 0.85;
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				// BRG, not RGB
				inputRGBImage.get(i, j, color);
				color[1] += 128;
				if(color[1] < toBlack) {
					int r = 0;
					int g = 0;
					int b = 0;
					outputRGBImage.put(i, j, r, g, b);
				}else {
					int r = color[2] + 128;
					int g = color[1] + 128;
					int b = color[0] + 128;
					outputRGBImage.put(i, j, r, g, b);
				}	
			}
		}
        
		//Change the image type
		Mat inputCricleImage = outputRGBImage;
		Mat outputCricleImage = new Mat(height, width, CvType.CV_8UC1);
		inputCricleImage.convertTo(inputCricleImage, CvType.CV_8UC1);
        
		//circle detection
		Imgproc.HoughCircles(inputCricleImage, outputCricleImage,
							Imgproc.CV_HOUGH_GRADIENT, 1, 10, 160, 50,
							0, 100);//CV_32FC3
        
		//Return the center of the circle
		double[] circleCenter;
		double rho;
		Point pt = new Point();
		for (int i = 0; i < outputCricleImage.cols(); i++){
			circleCenter = outputCricleImage.get(0, i);
			pt.x = circleCenter[0];
			pt.y = circleCenter[1];
			rho = circleCenter[2];
			//Draw a circumference
			Imgproc.circle(inputCricleImage, pt, (int)rho, new Scalar(0, 165, 255),2);
			//Draw the center point
			Imgproc.circle(inputCricleImage, pt, 2, new Scalar(0, 0, 255),3);
			//Display the center point
			System.out.println(pt);
		}
		inputCricleImage.convertTo(outputCricleImage, CvType.CV_8UC3);
		Imgcodecs.imwrite(outputImage, outputCricleImage);
	}
}
package coronaVAIRUS;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

class HoughCirclesRun {
	public Mat run(String filename) {
        // Load an image
        Mat src = Imgcodecs.imread(filename, Imgcodecs.IMREAD_COLOR);
        // Check if image is loaded fine
        if( src.empty() ) {
            System.out.println("Error opening image!");
            	System.out.println("Program Arguments: [image_name -- default "
            			+ filename +"] \n");
            System.exit(-1);
        }

        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.medianBlur(gray, gray, 5);
        Mat circles = new Mat();
        Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.0,
                (double)gray.rows()/16, // change this value to detect circles with different distances to each other
                100.0, 30.0, 1, 30); // change the last two parameters
                // (min_radius & max_radius) to detect larger circles

        for (int x = 0; x < circles.cols(); x++) {
            double[] c = circles.get(0, x);
            Point center = new Point(Math.round(c[0]), Math.round(c[1]));
            // circle center
            Imgproc.circle(src, center, 1, new Scalar(0,100,100), 3, 8, 0 );
            // circle outline
            int radius = (int) Math.round(c[2]);
            Imgproc.circle(src, center, radius, new Scalar(255,0,255), 3, 8, 0 );
        }

        //HighGui.imshow("detected circles", src);
        //HighGui.waitKey();
        //System.exit(0);
        return src;
    }
}
public class HoughCircles {
	
	private String filename = "";
	
	public HoughCircles (String filename) {
		// Load the native library.
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		this.filename = filename;
	}
	
	public static BufferedImage Mat2BufferedImage(Mat mat){
		BufferedImage bufImage = null;
		try {  
		//Encoding the image
	      MatOfByte matOfByte = new MatOfByte();
	      Imgcodecs.imencode(".jpg", mat, matOfByte);
	      //Storing the encoded Mat in a byte array
	      byte[] byteArray = matOfByte.toArray();
	      //Preparing the Buffered Image
	      InputStream in = new ByteArrayInputStream(byteArray);
	      bufImage = ImageIO.read(in);
	      
	    }catch (IOException e) {
	    	e.printStackTrace();
	    }
	    return bufImage;
	   }
	
    public BufferedImage detectar() {
    	HoughCirclesRun hcr = new HoughCirclesRun();
        return Mat2BufferedImage(hcr.run(this.filename));
        
        
    }
}
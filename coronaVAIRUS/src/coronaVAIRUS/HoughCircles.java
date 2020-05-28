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



//TODO
//adaptar para encontrar so os virus parecidos


class HoughCirclesRun {
	public Mat run(Mat imagem) {
        // Load an image
        Mat src = imagem;

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
	
	private Mat imagem;
	
	public HoughCircles (Mat imagem) {
		// Load the native library.
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		this.imagem = imagem;
	}
	
    public BufferedImage detectar() {
    	HoughCirclesRun hcr = new HoughCirclesRun();
        return Corona.Mat2BufferedImage(hcr.run(this.imagem));        
    }
}
package coronaVAIRUS;

import java.awt.Color;
import java.awt.Graphics;
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
	
	private int max_diff = 30;
	
	BufferedImage limiarizacao(BufferedImage imagem) {
		BufferedImage imagemL = Corona.deepCopy(imagem);
        for (int i = 0; i < imagem.getWidth(); i++) {
            for (int j = 0; j < imagem.getHeight(); j++) {
                Color color = new Color(imagem.getRGB(i,j));
                double lum = Luminance.intensity(color);
                if (lum >= Corona.getThreshold()) imagemL.setRGB(i, j, Color.WHITE.getRGB());
                else                  imagemL.setRGB(i, j, Color.BLACK.getRGB());
            }   
        } 
        return imagemL;
    }
	
	int[] calculaHistograma(BufferedImage img){
		int[] hist = new int[256];
        for(int y = 0; y < img.getHeight();y++){
            for(int x = 0; x < img.getWidth();x++){
                Color color = new Color(img.getRGB(x,y));
                int r = color.getRed();
                hist[r] += 1;
            }
        }
		return hist;
    }
	
	public boolean ehVirus(BufferedImage subimagem, Mat template) {
		int[] histograma = calculaHistograma(limiarizacao(subimagem));
		int[] histogramaT = calculaHistograma(limiarizacao(Corona.Mat2BufferedImage(template)));
		
		int s = histograma[0]+histograma[255];
		int sT = histogramaT[0]+histogramaT[255];

		double percent = histograma[255]/(double)s * 100;
		double percentT = histogramaT[255]/(double)sT * 100;

		double d = Math.abs(percent - percentT);
		System.out.println("Percent subimagem: "+percent);
		System.out.println("Percent template: " +percentT);
		System.out.println("Diferenca: "+d);
		return d > max_diff;
	}
	
	public Mat run(Mat imagem,Mat template) {
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
        int w=0,h=0;
        for (int x = 0; x < circles.cols(); x++) {
            double[] c = circles.get(0, x);
            BufferedImage subimagem = Corona.Mat2BufferedImage(imagem).getSubimage((int)Math.round(c[0]-c[2]), (int)Math.round(c[1]-c[2]), (int)Math.round(2*c[2]), (int)Math.round(2*c[2]));
//            Graphics g = FrameR.getPanel().getGraphics();
//           g.drawImage(subimagem,w,h,null);
//            w+= (int)Math.round(2*c[2]);
            if (ehVirus(subimagem,template)) {
            	Point center = new Point(Math.round(c[0]), Math.round(c[1]));
            	// circle center
            	Imgproc.circle(src, center, 1, new Scalar(0,100,100), 3, 8, 0 );
            	// circle outline
            	int radius = (int) Math.round(c[2]);
            	Imgproc.circle(src, center, radius, new Scalar(255,0,255), 3, 8, 0 );
        	}
        }

        //HighGui.imshow("detected circles", src);
        //HighGui.waitKey();
        //System.exit(0);
        return src;
    }
}
public class HoughCircles {
	
	private Mat imagem;
	private Mat template;
	
	public HoughCircles (Mat imagem, Mat template) {
		// Load the native library.
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		this.imagem = imagem;
		this.template = template;
	}
	
    public BufferedImage detectar() {
    	HoughCirclesRun hcr = new HoughCirclesRun();
        return Corona.Mat2BufferedImage(hcr.run(this.imagem,this.template));        
    }
}
package coronaVAIRUS;

import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileSystemView;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

//TODO
// adaptar para encontrar multiplos virus

class CorrelacaoCruzadaRun{
    Boolean use_mask = false;
    Mat img = new Mat(), templ = new Mat();
    Mat mask = new Mat();
    int match_method = Imgproc.TM_SQDIFF;
    JLabel imgDisplay = new JLabel(), resultDisplay = new JLabel();
    
    public BufferedImage run(Mat img, Mat templ) {
    	Mat result = new Mat();
        Mat img_display = new Mat();
        img.copyTo(img_display);
        int result_cols = img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;
        result.create(result_rows, result_cols, CvType.CV_32FC1);
        Boolean method_accepts_mask = (Imgproc.TM_SQDIFF == match_method || match_method == Imgproc.TM_CCORR_NORMED);
        if (use_mask && method_accepts_mask) {
            Imgproc.matchTemplate(img, templ, result, match_method, mask);
        } else {
            Imgproc.matchTemplate(img, templ, result, match_method);
        }
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
        Point matchLoc;
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        if (match_method == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED) {
            matchLoc = mmr.minLoc;
        } else {
            matchLoc = mmr.maxLoc;
        }
        Imgproc.rectangle(img_display, matchLoc, new Point(matchLoc.x + templ.cols(), matchLoc.y + templ.rows()),
                new Scalar(0, 0, 0), 2, 8, 0);
        Imgproc.rectangle(result, matchLoc, new Point(matchLoc.x + templ.cols(), matchLoc.y + templ.rows()),
                new Scalar(0, 0, 0), 2, 8, 0);
        Image tmpImg = HighGui.toBufferedImage(img_display);
        ImageIcon icon = new ImageIcon(tmpImg);
        imgDisplay.setIcon(icon);
        result.convertTo(result, CvType.CV_8UC1, 255.0);
        tmpImg = HighGui.toBufferedImage(result);
        icon = new ImageIcon(tmpImg);
        return Corona.Mat2BufferedImage(img_display);
        //resultDisplay.setIcon(icon);
    }

}
public class CorrelacaoCruzada {
	
	private Mat img;
	private Mat templ;
    
    public CorrelacaoCruzada (Mat img, Mat templ) {
		// Load the native library.
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		this.img = img;
		this.templ = templ;
	}
    
    public BufferedImage detectar() {
    	CorrelacaoCruzadaRun ccr = new CorrelacaoCruzadaRun();
        return ccr.run(this.img,this.templ);        
    }
}
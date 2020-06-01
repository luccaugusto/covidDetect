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
import java.util.Random;

class HoughCirclesRun {
	private int contador = 0;

	//Compara os tons do vírus encontrado com os tons do vírus selecionado como template
	public boolean ehVirus(Mat subimagem, double percentT) {
		int[] histograma = Utils.calculaHistograma2(Utils.limiarizacao(Utils.Mat2BufferedImage(subimagem)));
		int s = histograma[0]+histograma[255];
		double percent = histograma[255]/(double)s * 100;
		double d = Math.abs(percent - percentT);
		return d > (FrameR.getMaxSlider()-FrameR.getMaxDiff());
	}

	//Detecção dos vírus
	public Mat run(Mat imagem, Mat template) {
		//Load an image
		Mat src = new Mat();
		imagem.copyTo(src);

		//Calcula histograma e porcentagem de pretos no template
		int[] histogramaT = Utils.calculaHistograma2(Utils.limiarizacao(Utils.Mat2BufferedImage(template)));
		int sT = histogramaT[0]+histogramaT[255];
		double percentT = histogramaT[255]/(double)sT * 100;
		int max_radius = (int)(((template.cols()/2 + template.rows()/2) / 2) * 1.7);
		Mat gray = new Mat();
		Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.medianBlur(gray, gray, 7);
		Mat circles = new Mat();
		Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.0,
				(double)gray.rows()/16, //Change this value to detect circles with different distances to each other
				100.0, 30.0, 1, max_radius); //Change the last two parameters
		//(min_radius & max_radius) to detect larger circles
		for (int x = 0; x < circles.cols(); x++) {
			double[] c = circles.get(0, x);
			int x_canto= (int)Math.round(c[0]-c[2]), 
			    y_canto= (int)Math.round(c[1]-c[2]),
			    lado   = (int)Math.round(2*c[2]);
			x_canto = x_canto < 0 ? 0 : x_canto;
			y_canto = y_canto < 0 ? 0 : y_canto;
			Mat subimagem = null;
			if (x_canto+lado <= imagem.rows() && y_canto+lado <= imagem.cols()) {
				subimagem = imagem.submat(y_canto,y_canto+lado,x_canto,x_canto+lado);
			}

			if (subimagem != null && ehVirus(subimagem,percentT)) {
				contador++;
				Point center = new Point(Math.round(c[0]), Math.round(c[1]));
				//Circle center
				Imgproc.circle(src, center, 1, new Scalar(0,100,100), 3, 8, 0 );
				//Circle outline
				int radius = (int) Math.round(c[2]);
				Color color = Utils.corAleatoria();
				Imgproc.circle(src, center, radius, new Scalar(color.getRed(),color.getGreen(),color.getBlue()), 3, 8, 0 );
			}
		}
		FrameR.setNumVirus(contador);
		return src;
	}
}

public class HoughCircles {

	private Mat imagem;
	private Mat template;

	public HoughCircles (Mat imagem, Mat template) {
		this.imagem = imagem;
		this.template = template;
	}

	public BufferedImage detectar() {
		HoughCirclesRun hcr = new HoughCirclesRun();
		return Utils.Mat2BufferedImage(hcr.run(this.imagem,this.template));        
	}
}

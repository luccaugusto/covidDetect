package coronaVAIRUS;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javax.imageio.ImageIO;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

public class Utils {
	
	//Gera cores aleatoriamente
	public static Color corAleatoria() {
		Random rand = new Random();
		final float saturation = 0.9f;//1.0 for brilliant, 0.0 for dull
		final float luminance = 1.0f; //1.0 for brighter, 0.0 for black
		final float hue = rand.nextFloat();	
		return Color.getHSBColor(hue, saturation, luminance);
	}

	//Tranforma uma imagem do tipo bufferedImage para o tipo Mat
	public static Mat bufferedImage2Mat(BufferedImage sourceImg) {
		DataBuffer dataBuffer = sourceImg.getRaster().getDataBuffer();
		byte[] imgPixels = null;
		Mat imgMat = null;
		int width = sourceImg.getWidth();
		int height = sourceImg.getHeight();
		if(dataBuffer instanceof DataBufferByte) {
			imgPixels = ((DataBufferByte)dataBuffer).getData();
		}
		if(dataBuffer instanceof DataBufferInt) {
			int byteSize = width * height;
			imgPixels = new byte[byteSize*3];
			int[] imgIntegerPixels = ((DataBufferInt)dataBuffer).getData();
			for(int p = 0; p < byteSize; p++) {
				imgPixels[p*3 + 0] = (byte) ((imgIntegerPixels[p] & 0x00FF0000) >> 16);
				imgPixels[p*3 + 1] = (byte) ((imgIntegerPixels[p] & 0x0000FF00) >> 8);
				imgPixels[p*3 + 2] = (byte) (imgIntegerPixels[p] & 0x000000FF);
			}
		}
		if(imgPixels != null) {
			imgMat = new Mat(height, width, CvType.CV_8UC3);
			imgMat.put(0, 0, imgPixels);
		}
		return imgMat;
	}

	//Limizariza a imagem
	public static BufferedImage limiarizacao(BufferedImage imagem) {
		BufferedImage imagemL = deepCopy(imagem);
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

	//Calcula histograma
	public static int[] calculaHistograma2(BufferedImage img){
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

	//Tranforma uma imagem do tipo Mat para o tipo bufferedImage
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

	//Duplica uma imagem
	public static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(bi.getRaster().createCompatibleWritableRaster());
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
}

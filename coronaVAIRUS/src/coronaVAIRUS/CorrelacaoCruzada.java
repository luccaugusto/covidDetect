package coronaVAIRUS;


import java.util.ArrayList;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

//TODO
// OK adaptar para encontrar multiplos virus
// Slider max_diff
// Slider max_virus
class CorrelacaoCruzadaRun{
    Boolean use_mask = false;
    Mat img = new Mat(), templ = new Mat();
    int match_method = Imgproc.TM_SQDIFF_NORMED;
    JLabel imgDisplay = new JLabel(), resultDisplay = new JLabel();
    private int max_diff = 10;
    private int max_virus = 10;
    
	public boolean ehVirus(Mat subimagem, double percentT) {
		int[] histograma = Corona.calculaHistograma2(Corona.limiarizacao(Corona.Mat2BufferedImage(subimagem)));
		int s = histograma[0]+histograma[255];
		double percent = histograma[255]/(double)s * 100;
		
		double d = Math.abs(percent - percentT);
		return d < max_diff;
	}
    
	// Só pega virús do mesmo tamanho da imagem selecionada
    public BufferedImage run (Mat imagem, Mat templ) {
    	Mat aux = new Mat();
    	Mat img = new Mat();
    	imagem.copyTo(img);
    	Mat result = new Mat();
    	
    	ArrayList<Point> virus = new ArrayList<Point>();
    	
    	int[] histogramaT = Corona.calculaHistograma2(Corona.limiarizacao(Corona.Mat2BufferedImage(templ)));
        int sT = histogramaT[0]+histogramaT[255];
        double percentT = histogramaT[255]/(double)sT * 100;

        //Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
        
        int result_cols = img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;
        result.create(result_rows, result_cols, CvType.CV_32FC1);
        img.copyTo(aux);
        Core.MinMaxLocResult mmr = null;
        Point min_loc = null;
        //int max_num = (img.cols() * img.rows()) / ( templ.cols() * templ.rows());
        
        //matchtemplate na aux e botar max_loc na lista de encontrados
        //desenha retangulos e retorna a img
        for(int i=0; i<max_virus; i++) {
        	//pega o valor minimo do template matching
	        Imgproc.matchTemplate(aux, templ, result, match_method); 
	        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
	        mmr = Core.minMaxLoc(result);
	        
	        //Apaga os encontrados
	        min_loc = mmr.minLoc;
	        if (ehVirus(aux.submat((int)min_loc.y,(int)(min_loc.y+templ.rows()),(int)min_loc.x,(int)(min_loc.x+templ.cols())), percentT))
	        	virus.add(min_loc);
	        Point centro = new Point(min_loc.x + templ.cols()/2,min_loc.y+templ.rows()/2);
	        int raio =(templ.cols() < templ.rows()) ? templ.cols()/2 : templ.rows()/2;
	        Imgproc.circle(aux, centro, raio, new Scalar(255,255,255),-1);
        }
                
        Color color = Corona.corAleatoria();
        for (Point p : virus) {
        	Imgproc.rectangle(img, p, new Point(p.x + templ.cols(), p.y + templ.rows()),
        			new Scalar(color.getRed(),color.getGreen(),color.getBlue()), 2, 8, 0);
        	color = Corona.corAleatoria();
        }
        
        return (BufferedImage) HighGui.toBufferedImage(img);
    }

}
public class CorrelacaoCruzada {
	
	private Mat img;
	private Mat templ;
    
    public CorrelacaoCruzada (Mat img, Mat templ) {
		this.img = img;
		this.templ = templ;
	}
    
    public BufferedImage detectar() {
    	CorrelacaoCruzadaRun ccr = new CorrelacaoCruzadaRun();
        return ccr.run(this.img,this.templ);        
    }
}
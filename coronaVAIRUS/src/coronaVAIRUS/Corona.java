package coronaVAIRUS;

//TODO
// OK Ler imagem png, tiff jpg 
// OK Exibir imagem com opcao de zoom e histograma
// OK extrair amostra da imagem atravez de um retangulo vermelho
// OK Mudar o nome do evento no frameR
// OK Criar icones Botoes
// OK colorir virus com cores diferentes
// OK Mover funcao de colorir para o utils
// OK Limiarizacao
// OK Eliptica de Hough
// OK Detectar e contar quantos virus existem na imagem
// OKCorrigir erro de Mat channels pra imagem 2 no hough channels
//OK Correlacao Cruzada
// LBPH
// Descritor de haralick
// descritores de forma
// circularidade

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileSystemView;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Corona extends JFrame implements ActionListener ,ChangeListener{
	//Definição de variáveis relacionadas a tela
	private FrameR frameR = new FrameR();
	private JPanel contentPane, panelMenu, panelH, panel, panelSlider;
	private JButton buttonUpload, buttonZoomP, buttonZoomM, buttonSelecionar, buttonLimiarizar, buttonOriginal, buttonRotular, buttonCalc;
	private int x1,y1,x2,y2;
	private static MouseHandler mouse;
	private Graphics g;
	private Point mouseReleased, mousePressed, mousePos;
	private JColorChooser Cores;
	private int[] histograma = new int[256];
	private int[] histogramaDiscreto = new int[128];
	private int offset = 110; //Espaço ocupado pelos botões
	private int offsetx = 5; //Espaço ocupado pelos botões
	private static int threshold = 190; //Limite limiarização
	private static int ImgAtual = 0;
	
	//Variável do retângulo atual
	Ponto ret1 = new Ponto();
	Ponto ret2 = new Ponto();

	private int newImageWidth;
	private int newImageHeight;

	private String corFundo = "#00a388";

	//Upload da imagem
	private Icon up   = new  ImageIcon(getClass().getResource("upload.png"));
	//Zoom +
	private Icon zp   = new  ImageIcon(getClass().getResource("zoom_mais.png"));
	//Zoom -
	private Icon zm   = new  ImageIcon(getClass().getResource("zoom_menos.png"));
	//Selecionar
	private Icon se   = new  ImageIcon(getClass().getResource("selecionar.png"));
	//Detectar vírus
	private Icon de   = new  ImageIcon(getClass().getResource("detectar.png"));
	//Restaurar
	private Icon re   = new  ImageIcon(getClass().getResource("restaurar.png"));
	//Limiarizar
	private Icon li   = new  ImageIcon(getClass().getResource("limiarizar.png"));
	//Rotular
	private Icon ro   = new  ImageIcon(getClass().getResource("rotulacao.png"));

	//Tamanho do Canvas
	private int inicioL = 0;
	private int inicioA = 80;
	private int Largura = 800;
	private int Altura  = 540;

	//Tamanho do canvas do histograma
	private int inicioLH = 810;
	private int inicioAH = 80;
	private int LarguraH = 400;
	private int AlturaH  = 540;

	//Tamanho do slider
	private int inicioS = inicioA+Altura+10;
	private int LarguraS = Largura;
	private int AlturaS = 60;

	//Imagem carregada
	private static BufferedImage imagem = null;
	private static BufferedImage imagemL = null;
	private static BufferedImage template = null;
	private static BufferedImage templateL = null;
	private static BufferedImage newImage = null;
	private static Mat imagemM = null;
	private static Mat templateM = null;

	//Variável do zoom
	private double Zoom = 0;

	private enum Ferramentas {
		NORMAL,
		SELECAO,
	};

	private Ferramentas ferramentaAtual = Ferramentas.NORMAL;

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		if (!source.getValueIsAdjusting()) {
			threshold = source.getValue();
			limiarizacao();
			ImgAtual = 1;
		}
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				try{
					//Load the native OpenCV library
					System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
					Corona frame = new Corona();
					frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
					frame.setVisible(true);
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		});
	}

	public Corona() {
		//Inicializando ambiente
		setTitle("Corona Finder");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(0,0,1220,600);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);

		//Painel com botões
		panelMenu = new JPanel();
		panelMenu.setBounds(0,0,800,80);
		contentPane.add(panelMenu);

		//Botão upload
		buttonUpload = new JButton();
		buttonUpload.addActionListener((ActionListener) this);
		buttonUpload.setIcon(up);
		buttonUpload.setBackground(Color.decode(corFundo));
		buttonUpload.setHorizontalTextPosition(SwingConstants.CENTER); 

		//Botão selecionar
		buttonSelecionar = new JButton();
		buttonSelecionar.addActionListener((ActionListener) this);
		buttonSelecionar.setIcon(se);
		buttonSelecionar.setBackground(Color.decode(corFundo));
		buttonSelecionar.setHorizontalTextPosition(SwingConstants.CENTER); 

		//Botão Zoom +
		buttonZoomP = new JButton();
		buttonZoomP.addActionListener((ActionListener) this);
		buttonZoomP.setIcon(zp);
		buttonZoomP.setBackground(Color.decode(corFundo));
		buttonZoomP.setHorizontalTextPosition(SwingConstants.CENTER); 

		//Botão Zoom -
		buttonZoomM = new JButton();
		buttonZoomM.addActionListener((ActionListener) this);
		buttonZoomM.setIcon(zm);
		buttonZoomM.setBackground(Color.decode(corFundo));
		buttonZoomM.setHorizontalTextPosition(SwingConstants.CENTER); 

		//Botão imagem original
		buttonOriginal = new JButton();
		buttonOriginal.addActionListener((ActionListener) this);
		buttonOriginal.setIcon(re);
		buttonOriginal.setBackground(Color.decode(corFundo));
		buttonOriginal.setHorizontalTextPosition(SwingConstants.CENTER);
		
		//Botão limiarizar
		buttonLimiarizar = new JButton();
		buttonLimiarizar.addActionListener((ActionListener) this);
		buttonLimiarizar.setIcon(li);
		buttonLimiarizar.setBackground(Color.decode(corFundo));
		buttonLimiarizar.setHorizontalTextPosition(SwingConstants.CENTER);
		
		//Botão limiarizar
		buttonRotular = new JButton();
		buttonRotular.addActionListener((ActionListener) this);
		buttonRotular.setIcon(ro);
		buttonRotular.setBackground(Color.decode(corFundo));
		buttonRotular.setHorizontalTextPosition(SwingConstants.CENTER);

		//Botão para abrir tela de contagem
		buttonCalc = new JButton();
		buttonCalc.addActionListener((ActionListener) this);
		buttonCalc.setIcon(de);
		buttonCalc.setBackground(Color.decode(corFundo));
		buttonCalc.setHorizontalTextPosition(SwingConstants.CENTER);

		//Configurar grupo de botões
		GroupLayout g1_panelMenu = new GroupLayout(panelMenu);
		g1_panelMenu.setHorizontalGroup(
				g1_panelMenu.createParallelGroup(Alignment.CENTER)
				.addGroup( g1_panelMenu.createSequentialGroup()
					.addGap(130)
					.addComponent(buttonUpload)
					.addGap(10)
					.addComponent(buttonZoomP)
					.addGap(10)
					.addComponent(buttonZoomM)
					.addGap(10)
					.addComponent(buttonLimiarizar)
					.addGap(10)
					.addComponent(buttonRotular)
					.addGap(10)
					.addComponent(buttonOriginal)
					.addGap(10)
					.addComponent(buttonSelecionar)
					.addGap(10)
					.addComponent(buttonCalc)
					)
				);

		g1_panelMenu.setVerticalGroup(
				g1_panelMenu.createParallelGroup(Alignment.CENTER)
				.addGroup(g1_panelMenu.createSequentialGroup()
					.addGap(20)
					.addGroup(g1_panelMenu.createParallelGroup(Alignment.BASELINE)
						.addComponent(buttonUpload)
						.addComponent(buttonZoomP)
						.addComponent(buttonZoomM)
						.addComponent(buttonLimiarizar)
						.addComponent(buttonRotular)
						.addComponent(buttonOriginal)
						.addComponent(buttonSelecionar)
						.addComponent(buttonCalc)
						)
					)
				);
		panelMenu.setLayout(g1_panelMenu);

		//Painel de imagem
		panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBounds(inicioL,inicioA,Largura,Altura);
		contentPane.add(panel);
		panel.setLayout(null);

		//Painel do histograma
		panelH = new JPanel();
		panelH.setBackground(Color.WHITE);
		panelH.setBounds(inicioLH,inicioAH,LarguraH,AlturaH);
		contentPane.add(panelH);
		panelH.setLayout(null);

		//Painel com botões
		panelSlider = new JPanel();
		panelSlider.setBackground(Color.WHITE);
		panelSlider.setBounds(0,inicioS,LarguraS,AlturaS);
		contentPane.add(panelSlider);

		//Slider
		int min = 0, max = 255;
		JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, threshold);
		slider.setPreferredSize(new Dimension(500,50));
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		//Set the spacing for the minor tick mark
		slider.setMinorTickSpacing(50);
		slider.addChangeListener(this);

		panelSlider.add(slider);

		mouse  = new MouseHandler();
		this.addMouseListener( mouse );
		this.addMouseMotionListener( mouse );
	}

	public static int getThreshold() {
		return threshold;
	}

	public static Mat getMatImg() {
		return Utils.bufferedImage2Mat(imagem);
	}

	public static Mat getMatTemplate() {
		return Utils.bufferedImage2Mat(template);
	}

	public static Mat getImagemM( ) {
		return imagemM;
	}

	public static Mat getTemplateM() {
		return templateM;
	}

	//Ações que os botões irão executar
	public void actionPerformed(ActionEvent arg0){
		if(arg0.getSource() == buttonUpload){
			do_buttonUpload_actionPerfomed(arg0);
		} else if(arg0.getSource() == buttonSelecionar){
			do_buttonSelecionar_actionPerfomed(arg0);
		} else if(arg0.getSource() == buttonZoomP){
			do_buttonZoomP_actionPerfomed(arg0);
		} else if(arg0.getSource() == buttonZoomM){
			do_buttonZoomM_actionPerfomed(arg0);
		} else if(arg0.getSource() == buttonOriginal){
			do_buttonOriginal_actionPerfomed(arg0);
		}else if(arg0.getSource() == buttonLimiarizar){
			do_buttonLimiarizar_actionPerfomed(arg0);
		} else if(arg0.getSource() == buttonRotular){
			do_buttonRotular_actionPerfomed(arg0);
		}else if(arg0.getSource() == buttonCalc){
			do_buttonCalc_actionPerfomed(arg0);
		}
	}

	//Botão de upload selecionado
	protected void do_buttonUpload_actionPerfomed(ActionEvent arg0){
		//Limpa o canvas
		panel.repaint();
		panelH.repaint();
		zeraHistogramas();
		//Escolher a imagem
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView());
		File selectedFile= null;
		int returnValue = jfc.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			selectedFile = jfc.getSelectedFile();
		}
		try {
			String filename = selectedFile.getAbsolutePath();
			imagem = ImageIO.read(new File(filename));   
			imagemM = Imgcodecs.imread(filename);
			newImageWidth=imagem.getWidth();
			newImageHeight=imagem.getHeight();
			g = panel.getGraphics();
			g.drawImage(imagem,0,0, null); 
			calculaHistograma(imagem);  

		} catch (IOException e) {
		}
	} 

	//Botão para seleção selecionado
	protected void do_buttonSelecionar_actionPerfomed(ActionEvent arg0){
		ferramentaAtual = Ferramentas.SELECAO;
	}

	//Botão para Zoom + selecionado
	protected void do_buttonZoomP_actionPerfomed(ActionEvent arg0){
		panel.repaint();
		String zoomLevel;
		zoomLevel = JOptionPane.showInputDialog("Digite a porcentagem do zoom:");
		if ((zoomLevel != null) && (zoomLevel.length() > 0)) {    
			try {
				zoomLevel.replaceAll( "," , "." );
				Zoom = Double.parseDouble(zoomLevel);
				Zoom = (Zoom/100)+1;
			}catch(Exception e){
			}  
		}
		redimensionarImagem();
	}
	
	//Botão para Zoom - selecionado
	protected void do_buttonZoomM_actionPerfomed(ActionEvent arg0){
		panel.repaint();
		String zoomLevel;
		zoomLevel = JOptionPane.showInputDialog("Digite a porcentagem do zoom:");
		if ((zoomLevel != null) && (zoomLevel.length() > 0)) {    
			try {
				zoomLevel.replaceAll( "," , "." );
				Zoom = Double.parseDouble(zoomLevel);
				Zoom = 1-(Zoom/100);
			}catch(Exception e){
			}  
		}
		redimensionarImagem();
	}
	
	//Botão para exibir imagem original selecionada
	protected void do_buttonOriginal_actionPerfomed(ActionEvent arg0){
		g = panel.getGraphics();
		g.drawImage(imagem,0,0,null);
		ImgAtual = 0;
	} 

	//Botão para limiarizar imagem selecionado
	protected void do_buttonLimiarizar_actionPerfomed(ActionEvent arg0){
		limiarizacao();
		ImgAtual = 1;
	} 
	
	//Botão para rotular imagem selecionado
	protected void do_buttonRotular_actionPerfomed(ActionEvent arg0){
		limiarizarToRotular();
		rotulacao();
		ImgAtual = 2;
	} 
	

	//Botão para abrir a janela para detectar os vírus
	protected void do_buttonCalc_actionPerfomed(ActionEvent arg0){
		frameR.setNumVirus(0);
		frameR.setVisible(true);
	}  

	//Função para calcular a maior frequência de tom na imagem
	int maior() {
		int maior=histograma[0];
		for (int i=1; i< histograma.length; ++i)
			if (histograma[i] > maior)
				maior = histograma[i];
		return maior;
	}

	//Zerar histograma
	void zeraHistogramas() {
		for(int i=0; i<histograma.length; i++) {
			histograma[i] = 0;
			histogramaDiscreto[i/2]=0;
		}
	}

	//Permite um histograma mais compacto para uma melhor visualização
	void discretizaHistograma() {
		int j=0;
		int soma=0;
		for (int i=0; i<histograma.length; i++) {
			soma += histograma[i];
			if (i % 2 == 1) {
				histogramaDiscreto[j] = soma/2;
				soma = 0;
				++j;
			}
		}
	}

	//Calcula o histograma
	void calculaHistograma(BufferedImage img){
		for(int y = 0; y < img.getHeight();y++){
			for(int x = 0; x < img.getWidth();x++){
				Color color = new Color(img.getRGB(x,y));
				int r = color.getRed();
				histograma[r] += 1;
			}
		}
		discretizaHistograma();
		exibeHistograma();
	}

	//Exibe o histograma
	void exibeHistograma() {
		//Ponto inicial das linhas, move da esquerda para a direita no eixo x;
		g = panelH.getGraphics();
		int x=0;
		int y;
		int intervalo = LarguraH/histogramaDiscreto.length;
		int maior = maior();
		for(int i=0; i < histogramaDiscreto.length; i++) {
			y = histogramaDiscreto[i] * AlturaH / maior;
			//Printa para cada valor uma coluna de largura igual
			for(int j=0; j< intervalo; ++j, ++x) 
				g.drawLine(x,AlturaH,x,AlturaH-y);
		}
	}

	//Limiarização da imagem
	void limiarizacao() {
		imagemL = Utils.deepCopy(imagem);
		for (int i = 0; i < imagem.getWidth(); i++) {
			for (int j = 0; j < imagem.getHeight(); j++) {
				Color color = new Color(imagem.getRGB(i,j));
				double lum = Luminance.intensity(color);
				if (lum >= threshold) imagemL.setRGB(i, j, Color.WHITE.getRGB());
				else                  imagemL.setRGB(i, j, Color.BLACK.getRGB());
			}   
		} 
		g = panel.getGraphics();
		g.drawImage(imagemL,0,0,null);

	}
	
	 void limiarizarToRotular() {
			imagemL = Utils.deepCopy(imagem);
			BufferedImage borrada = null;
			//Borra um poucoa imagem para evitar buracos nos objetos
			int radius = 11;
		    int size = radius * 2 + 1;
		    float weight = 1.0f / (size * size);
		    float[] data = new float[size * size];
		    for (int i = 0; i < data.length; i++) {
		        data[i] = weight;
		    }
		    Kernel kernel = new Kernel(size,size,data);
		    ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_ZERO_FILL, null);
		    borrada = op.filter(imagemL, null);
			for (int i = 0; i < borrada.getWidth(); i++) {
				for (int j = 0; j < borrada.getHeight(); j++) {
					Color color = new Color(borrada.getRGB(i,j));
					double lum = Luminance.intensity(color);
					if (lum >= threshold) imagemL.setRGB(i, j, Color.WHITE.getRGB());
					else                  imagemL.setRGB(i, j, Color.BLACK.getRGB());
				}   
			} 
			g = panel.getGraphics();
			g.drawImage(imagemL,0,0,null);
		 
	 
	 }
	

	//Rotula a imagem
	void rotulacao() {
		int w = imagemL.getWidth();
		int h = imagemL.getHeight();
		//Lista de rótulos e cores
		Map<Integer, Color> dic = new HashMap<Integer, Color>();
		int ultimoRot = 0;
		int totalRotulos = 0;
		Color color;
		ArrayList<int[]> equivalentes = new ArrayList<int[]>();
		//Matriz de rótulos
		int[][] rotulos = new int[w][h];
		//Anula os rótulos
		for (int i=0; i< w; i++) {
			for (int j=0; j<h; j++) {
				rotulos[i][j] = -1;
			}
		}
		int A = -1; //Esquerda
		int B = -1; //Diagonal esquerda para cima
		int C = -1; //Cima
		int D = -1; //Diagonal direita para cima
		boolean cima=true;
		boolean esquerda=true;
		boolean direita=true;
		for (int i=0; i< w; i++) {
			for (int j=0; j<h; j++) {
				color = new Color(imagemL.getRGB(i,j));
				//Ponto de objeto, calcula os vizinhos
				if (color.getRGB() == Color.BLACK.getRGB()) {
					//Vizinhaça
					A = -1;
					B = -1;
					C = -1;
					D = -1;
					cima=true;
					esquerda=true;
					direita=true;
					//Olha tudo se não for o primeiro ponto
					//Olha em cima se não for a primeira linha
					//Olha a esquerda se não for a primeira coluna
					//Olha a direita em cima se não for a ultima coluna
					if (j==0) cima = false;
					if (i==0) esquerda = false;
					if (i==w-1) direita = false;
					if (cima) {
						C = rotulos[i][j-1];
						if (direita) D = rotulos[i+1][j-1];
						if (esquerda) B = rotulos[i-1][j-1];
					}
					if (esquerda) {
						A = rotulos[i-1][j];
						if (cima) B = rotulos[i-1][j-1];
					}
					int P=-1;
					if(C == -1 && A == -1) {
						P = ultimoRot++;
						totalRotulos++;
					}else if(C == A) {
						P = A;
					}else if(C != -1 || A != -1) {
						P = (C != -1) ? C : A;
					}else if(C != A){
						P = A;
						//Unifica os rótulos
						for(int yTmp = 0; yTmp < w; yTmp++) {
							for(int xTmp = 0; xTmp < h; xTmp++) {
								if(rotulos[yTmp][xTmp] == C) {
									rotulos[yTmp][xTmp] = A;
									dic.put(A,Utils.corAleatoria());
								}
							}
						}
						totalRotulos--;
					}
					rotulos[i][j] = P;
					dic.put(P,Utils.corAleatoria());					
				}
			}
		}
		newImage = Utils.deepCopy(imagemL);
		for(int x = 0; x < w; x++) {
			for(int y = 0; y < h; y++) {	
				if(rotulos[x][y] != -1) {
					newImage.setRGB(x, y, dic.get(rotulos[x][y]).getRGB());
				}
				else {
					newImage.setRGB(x, y, Color.WHITE.getRGB());
				}
			}
		}
		g = panel.getGraphics();
		g.drawImage(newImage,0,0,null);
	}
	
	//Zoom positivo e negativo
	void redimensionarImagem() { 
		newImageWidth =  (int)(newImageWidth * Zoom);
		newImageHeight = (int)(newImageHeight * Zoom);
		g = panel.getGraphics();
		g.drawImage(imagem, 0, 0, newImageWidth , newImageHeight , null);
	}

	//Classe para lidar com eventos de mouse
	class MouseHandler extends MouseAdapter
	{
		//Variáveis das coordenadas do recorte
		private Ponto ReMin = new Ponto();
		private Ponto ReMax = new Ponto();
		private int x1 = -1;
		private int y1 = -1;
		
		public void retangulo() {
			g = panel.getGraphics();
			if (ImgAtual == 1) {
				limiarizacao();
				g.drawImage(imagemL,0,0,newImageWidth,newImageHeight,null);
			}else if(ImgAtual == 2) {
				limiarizarToRotular();
				g.drawImage(imagemL,0,0,newImageWidth,newImageHeight,null);
			}
			else {
				g.drawImage(imagem,0,0,newImageWidth,newImageHeight,null);
			}
			//selecionar(ReMin,ReMax);
			g.setColor(new Color(255,0,0));
			
			//Reta superior
			g.drawLine(ReMin.x-offsetx,ReMax.y-offset,ReMax.x-offsetx,ReMax.y-offset);
			g.drawLine(ReMin.x-offsetx-1,ReMax.y-offset-1,ReMax.x-offsetx-1,ReMax.y-offset-1);
			//Reta esquerda
			g.drawLine(ReMin.x-offsetx,ReMin.y-offset,ReMin.x-offsetx,ReMax.y-offset);
			g.drawLine(ReMin.x-offsetx-1,ReMin.y-offset-1,ReMin.x-offsetx-1,ReMax.y-offset-1);
			//Reta inferior
			g.drawLine(ReMin.x-offsetx,ReMin.y-offset,ReMax.x-offsetx,ReMin.y-offset);
			g.drawLine(ReMin.x-offsetx-1,ReMin.y-offset-1,ReMax.x-offsetx-1,ReMin.y-offset-1);
			//Reta direita
			g.drawLine(ReMax.x-offsetx,ReMin.y-offset,ReMax.x-offsetx,ReMax.y-offset);
			g.drawLine(ReMax.x-offsetx-1,ReMin.y-offset-1,ReMax.x-offsetx-1,ReMax.y-offset-1);

			ret1.x = ReMin.x;
			ret1.y = ReMin.y;
			ret2.x = ReMax.x;
			ret2.y = ReMax.y;	

			int altura = (ReMax.y-offset) - (ReMin.y-offset);
			int largura = (ReMax.x-offsetx) - (ReMin.x-offsetx);

			template = imagem.getSubimage(ReMin.x-offsetx, ReMin.y-offset, largura, altura);
			templateM = imagemM.submat(ReMin.y-offset,ReMax.y-offset,ReMin.x-offsetx,ReMax.x-offsetx);
			if(imagemL != null) templateL = imagemL.getSubimage(ReMin.x-offsetx, ReMin.y-offset, largura, altura);
			
			FrameR.setMaxSliderCC((imagemM.cols() * imagemM.rows()) / ( templateM.cols() * templateM.rows()));

			ReMin.x = ReMin.y = ReMax.x = ReMax.y = -1;
		}

		//Selecionar
		//Apaga os pixels fora da area selecionada, plotando linhas de um 
		//lado ao outro da tela ignorando os pixels dentro da selecao
		void selecionar(Ponto p1, Ponto p2) {
			g = panel.getGraphics();
			g.setColor(Color.WHITE);
			for (int i=inicioA-100; i<Altura; ++i) {
				if (i > p1.y-100 && i < p2.y-100) {
					g.drawLine(inicioL,i,p1.x,i);//Linha até a borda esquerda do retangulo
					g.drawLine(p2.x,i,Largura,i);//Linha da borda esquerda até o fim
				} else { //Dentro do retângulo selecionado
					//Plota uma linha com a cor do fundo de um lado a outro
					g.drawLine(inicioL,i,Largura,i);
				}
			}
		}

		// Métodos para capturar eventos
		// Captura um clique e define seu significado conforme a ferramenta em uso
		public void mousePressed( MouseEvent e ){
			x1 = e.getX();
			y1 = e.getY();
			if (y1 > offset && y1 < offset+imagem.getHeight() && x1 < imagem.getWidth() && x1 > offsetx  && (x2 != x1 && y2 != y1) && ferramentaAtual == Ferramentas.SELECAO) {
				if (ReMin.x == -1) {
					ReMin.x = x1;
					ReMin.y = y1;
				}else if(ReMax.x == -1) {
					ReMax.x = x1;
					ReMax.y = y1;
					//Troca valores caso ponto minimo seja maximo em x ou em y
					if(ReMin.x > ReMax.x) {
						int aux = ReMax.x;
						ReMax.x = ReMin.x;
						ReMin.x = aux;
					}
					if(ReMin.y > ReMax.y) {
						int aux = ReMax.y;
						ReMax.y = ReMin.y;
						ReMin.y = aux;
					}
					retangulo();
				}
			} 
			x2=x1;
			y2=y1;
		}

		public void mouseDragged( MouseEvent e ){
			if(ferramentaAtual == Ferramentas.SELECAO) {
				x1 = e.getX();
				y1 = e.getY();
				x2=x1;
				y2=y1;
			}
		}
	}
}
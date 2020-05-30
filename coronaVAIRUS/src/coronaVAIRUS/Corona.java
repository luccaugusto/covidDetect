package coronaVAIRUS;

//TODO
// OK Ler imagem png, tiff jpg 
// OK Exibir imagem com opcao de zoom e histograma
// OK extrair amostra da imagem atravez de um retangulo vermelho
// OK Mudar o nome do evento no frameR
// OK Criar icones Botoes
// Detectar e contar quantos virus existem na imagem
// Mostrar numero de virus
// colorir virus com cores diferentes
// Mover funcao de colorir para o utils
// indicar o total de virus
// Slider max_diff
//		OK Limiarizacao
//		OK Eliptica de Hough
//		Corrigir erro de Mat channels pra imagem 2 no hough channels
//		LBPH
// 		Correlacao Cruzada
//		Descritor de haralick
//		descritores de forma
//			circularidade

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
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileSystemView;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

public class Corona extends JFrame implements ActionListener ,ChangeListener{
	// DefiniÃ§Ã£o de variÃ¡veis relacionadas Ã  tela
	private FrameR frameR = new FrameR();
	private JPanel contentPane, panelMenu, panelH, panel, panelSlider;
	private JButton buttonUpload, buttonZoomP, buttonZoomM, buttonSelecionar, buttonDetectar, buttonCalc;
	private int x1,y1,x2,y2;
	private static MouseHandler mouse;
	private Graphics g;
	private Point mouseReleased, mousePressed,mousePos;
	private JColorChooser Cores;
    private int[] histograma = new int[256];
    private int[] histogramaDiscreto = new int[128];
    private int offset = 110; // espaco ocupado pelos botoes
    private int offsetx = 5; // espaco ocupado pelos botoes
    private static int threshold = 190; //limite limiarizacao


	//variavel do retangulo atual
	Ponto ret1 = new Ponto();
	Ponto ret2 = new Ponto();
    
    private int newImageWidth;
	private int newImageHeight;

	private String corFundo = "#00a388";

	//upload
    private Icon up   = new  ImageIcon(getClass().getResource("upload.png"));
	//zoom +
	private Icon zp   = new  ImageIcon(getClass().getResource("zoom_mais.png"));
	//zoom -
	private Icon zm   = new  ImageIcon(getClass().getResource("zoom_menos.png"));
	//selecionar
	private Icon se   = new  ImageIcon(getClass().getResource("selecionar.png"));
	//detectar
	private Icon de   = new  ImageIcon(getClass().getResource("detectar.png"));
	//limiarizar
	private Icon li   = new  ImageIcon(getClass().getResource("limiarizar.png"));
	

	//Tamanho do Canvas
	private int inicioL = 0;
	private int inicioA = 80;
	private int Largura = 800;
	private int Altura  = 540;

	//tamanho do canvas do histograma
	private int inicioLH = 810;
	private int inicioAH = 80;
	private int LarguraH = 400;
	private int AlturaH  = 540;
	
	//tamanho do slider
	private int inicioS = inicioA+Altura+10;
	private int LarguraS = Largura;
	private int AlturaS = 60;

	//Imagem carregada
	private static BufferedImage imagem = null;
	private static BufferedImage imagemL = null;
	private static BufferedImage template = null;
	private static BufferedImage templateL = null;
	
    private static Mat imagemM = null;
    private static Mat templateM = null;

	//variÃ¡vel do zoom
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
        }
		
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				try{
					// load the native OpenCV library
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
		//Inicializando Ambiente
		setTitle("Corona Finder");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(0,0,1220,600);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);

		//Painel com botoes
		panelMenu = new JPanel();
		panelMenu.setBounds(0,0,800,80);
		contentPane.add(panelMenu);

		//botao upload
		buttonUpload = new JButton();
		buttonUpload.addActionListener((ActionListener) this);
        buttonUpload.setIcon(up);
		buttonUpload.setBackground(Color.decode(corFundo));
		buttonUpload.setHorizontalTextPosition(SwingConstants.CENTER); 

		//botao Selecionar
		buttonSelecionar = new JButton();
		buttonSelecionar.addActionListener((ActionListener) this);
		buttonSelecionar.setIcon(se);
		buttonSelecionar.setBackground(Color.decode(corFundo));
		buttonSelecionar.setHorizontalTextPosition(SwingConstants.CENTER); 

		//botao Zoom +
		buttonZoomP = new JButton();
		buttonZoomP.addActionListener((ActionListener) this);
		buttonZoomP.setIcon(zp);
		buttonZoomP.setBackground(Color.decode(corFundo));
		buttonZoomP.setHorizontalTextPosition(SwingConstants.CENTER); 

		//botao Zoom -
		buttonZoomM = new JButton();
		buttonZoomM.addActionListener((ActionListener) this);
		buttonZoomM.setIcon(zm);
		buttonZoomM.setBackground(Color.decode(corFundo));
		buttonZoomM.setHorizontalTextPosition(SwingConstants.CENTER); 

		//botÃ£o limiarizar virus
		buttonDetectar = new JButton();
		buttonDetectar.addActionListener((ActionListener) this);
		buttonDetectar.setIcon(li);
		buttonDetectar.setBackground(Color.decode(corFundo));
		buttonDetectar.setHorizontalTextPosition(SwingConstants.CENTER);

		//botÃ£o para abrir tela de contagem
		buttonCalc = new JButton();
		buttonCalc.addActionListener((ActionListener) this);
		buttonCalc.setIcon(de);
		buttonCalc.setBackground(Color.decode(corFundo));
		buttonCalc.setHorizontalTextPosition(SwingConstants.CENTER);

		//configurar grupo de botoes
		GroupLayout g1_panelMenu = new GroupLayout(panelMenu);
		g1_panelMenu.setHorizontalGroup(
				g1_panelMenu.createParallelGroup(Alignment.CENTER)
				.addGroup( g1_panelMenu.createSequentialGroup()
					.addGap(130)
					.addComponent(buttonUpload)
					.addGap(10)
					.addComponent(buttonSelecionar)
					.addGap(10)
					.addComponent(buttonZoomP)
					.addGap(10)
					.addComponent(buttonZoomM)
					.addGap(10)
					.addComponent(buttonDetectar)
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
						.addComponent(buttonSelecionar)
						.addComponent(buttonZoomP)
						.addComponent(buttonZoomM)
						.addComponent(buttonDetectar)
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
		
		//Painel com botoes
		panelSlider = new JPanel();
		panelSlider.setBackground(Color.WHITE);
		panelSlider.setBounds(0,inicioS,LarguraS,AlturaS);
		contentPane.add(panelSlider);
		
		
        int min = 0, max = 255;
        JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, threshold);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        // Set the spacing for the minor tick mark
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
	
	public void actionPerformed(ActionEvent arg0){
		if(arg0.getSource() == buttonUpload){
			do_buttonUpload_actionPerfomed(arg0);
		} else if(arg0.getSource() == buttonSelecionar){
			do_buttonSelecionar_actionPerfomed(arg0);
		} else if(arg0.getSource() == buttonZoomP){
				do_buttonZoomP_actionPerfomed(arg0);
		} else if(arg0.getSource() == buttonZoomM){
			do_buttonZoomM_actionPerfomed(arg0);
		} else if(arg0.getSource() == buttonDetectar){
			do_buttonDetectar_actionPerfomed(arg0);
		} else if(arg0.getSource() == buttonCalc){
			do_buttonCalc_actionPerfomed(arg0);
		}
	}

	protected void do_buttonUpload_actionPerfomed(ActionEvent arg0){
        panel.repaint();
        panelH.repaint();
		zeraHistogramas();
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
	
	//init_offset = ((h * y0) + x) bytes
	//offset = width - x1 + width - x0
	//ateh height-y1

	protected void do_buttonSelecionar_actionPerfomed(ActionEvent arg0){
            ferramentaAtual = Ferramentas.SELECAO;
	}

	protected void do_buttonZoomP_actionPerfomed(ActionEvent arg0){
        panel.repaint();
		String zoomLevel;
		zoomLevel = JOptionPane.showInputDialog("Digite a porcentagem do zoom:");
		if ((zoomLevel != null) && (zoomLevel.length() > 0)) {    
			try {
				zoomLevel.replaceAll( "," , "." );
				Zoom = Double.parseDouble(zoomLevel);
                Zoom = (Zoom/100)+1;
				//ferramenta_atual = Ferramentas.ROTACAO;
				//mouse.rotation();
			}catch(Exception e){
				//JOptionPane.showMessageDialog(null, "Digite apenas nÃºmeros inteiros");
			}  
		}
		redimensionarImagem();

	}

	protected void do_buttonZoomM_actionPerfomed(ActionEvent arg0){
        panel.repaint();
		String zoomLevel;
		zoomLevel = JOptionPane.showInputDialog("Digite a porcentagem do zoom:");
		if ((zoomLevel != null) && (zoomLevel.length() > 0)) {    
			try {
				zoomLevel.replaceAll( "," , "." );
				Zoom = Double.parseDouble(zoomLevel);
                Zoom = 1-(Zoom/100);
				//ferramenta_atual = Ferramentas.ROTACAO;
				//mouse.rotation();

			}catch(Exception e){
				//JOptionPane.showMessageDialog(null, "Digite apenas nÃºmeros inteiros");
			}  
		}
		redimensionarImagem();
	}

	protected void do_buttonDetectar_actionPerfomed(ActionEvent arg0){
        limiarizacao();
	} 

	protected void do_buttonCalc_actionPerfomed(ActionEvent arg0){
		frameR.setVisible(true);
	}  

	int maior() {
		int maior=histograma[0];
		for (int i=1; i< histograma.length; ++i)
			if (histograma[i] > maior)
				maior = histograma[i];
		return maior;
	}

	void zeraHistogramas() {
		for(int i=0; i<histograma.length; i++) {
			histograma[i] = 0;
			histogramaDiscreto[i/2]=0;
		}
	}

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

	void exibeHistograma() {
		//ponto inicial das linhas, move da esquerda para a direita no eixo x;
		g = panelH.getGraphics();
		int x=0;
		int y;
		int intervalo = LarguraH/histogramaDiscreto.length;
		int maior = maior();
		for(int i=0; i < histogramaDiscreto.length; i++) {
			y = histogramaDiscreto[i] * AlturaH / maior;
			//printa para cada valor uma coluna de largura igual
			for(int j=0; j< intervalo; ++j, ++x) 
				g.drawLine(x,AlturaH,x,AlturaH-y);
		}
	}

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
//		rotulacao();
        g = panel.getGraphics();
        g.drawImage(imagemL,0,0,null);
    }

	void rotulacao() {
		int w = imagemL.getWidth();
		int h = imagemL.getHeight();
		int ultimo_rot = 0;
		Color color;
		ArrayList<int[]> equivalentes = new ArrayList<int[]>();
		//matriz de rotulos
		int[][] rotulos = new int[w][h];
		//anula os rotulos
		for (int i=0; i< w; i++)
			for (int j=0; j<h; j++)
				rotulos[i][j] = -1;

		for (int i=0; i< w; i++) {
			for (int j=0; j<h; j++) {
				color = new Color(imagemL.getRGB(i,j));
				//ponto de objeto, calcula os vizinhos
				if (color.getRGB() == Color.BLACK.getRGB()) {
					//vizinhaca
					int A = -1;
					int B = -1;
					int C = -1;
					int D = -1;

					boolean cima=true;
					boolean esquerda=true;
					boolean direita=true;
					//olha tudo se nao for o primeiro ponto
					//olha em cima se nao for a primeira linha
					//olha a esquerda se nao for a primeira coluna
					//olha a direita em cima se nao for a ultima coluna
					if (j==0) cima = false;
					if (i==0) esquerda = false;
					if (i==w-1) direita = false;

					if (cima) {
						C = rotulos[i][j-1];
						if (direita) D = rotulos[i+1][j-1];
						if (esquerda) B = rotulos[i-1][j-1];
					}

					if (esquerda) A = rotulos[i-1][j];

					//se os vizinhos nao foram rotulados marca o ponto atual com um novo rotulo
					if (A+B+C+D == -4) {
						rotulos[i][j] = ultimo_rot;
						ultimo_rot++;
					//senao se forem iguais atribui o mesmo rotulo
					} else if(A == B && B == C && C == D){
						rotulos[i][j] = A;
					//se forem diferentes coloca os rotulos numa tabela de equivalencias
					}else{
						int[] aux = {A,B};
						int[] aux1 = {A,C};
						int[] aux2 = {A,D};
						equivalentes.add(aux);
						equivalentes.add(aux1);
						equivalentes.add(aux2);
					}
				}
			}
		}
		//Unifica rotulos equivalentes
		//para cada entrada na tabela de equivalencias
		//varre a matriz de rotulos toda e unifica 
		for (int[] i: equivalentes) {
			for (int j=0; j< w; j++) {
				for (int k=0; k<h; k++) {
					if (rotulos[j][k] == i[1])
						rotulos[j][k] = i[0];
				}
			}
		}
	}


	//Zoom
	void redimensionarImagem() { 
        newImageWidth =  (int)(newImageWidth * Zoom);
		newImageHeight = (int)(newImageHeight * Zoom);
        g = panel.getGraphics();
        g.drawImage(imagem, 0, 0, newImageWidth , newImageHeight , null);
/*
		int newImageWidth = (int)(imagem.getWidth() * Zoom);
		int newImageHeight = (int)(imagem.getHeight() * Zoom);
		BufferedImage resizedImage = new BufferedImage(newImageWidth , newImageHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(imagem, 0, 0, newImageWidth , newImageHeight , null);
		g.dispose();
		imagem = resizedImage;
		calculaHistograma(imagem);

*/
    }
	

	
	//Classe para lidar com eventos de mouse
	class MouseHandler extends MouseAdapter
	{
		//variÃ¡veis das coordenadas do recorte
		private Ponto ReMin = new Ponto();
		private Ponto ReMax = new Ponto();

		private int x1 = -1;
		private int y1 = -1;

		public void retangulo() {
			g = panel.getGraphics();
        	g.drawImage(imagem,0,0,newImageWidth,newImageHeight,null);
			//selecionar(ReMin,ReMax);
			g.setColor(Color.RED);

			//reta superior
			g.drawLine(ReMin.x-offsetx,ReMax.y-offset,ReMax.x-offsetx,ReMax.y-offset);
			//reta esquerda
			g.drawLine(ReMin.x-offsetx,ReMin.y-offset,ReMin.x-offsetx,ReMax.y-offset);
			//reta inferior
			g.drawLine(ReMin.x-offsetx,ReMin.y-offset,ReMax.x-offsetx,ReMin.y-offset);
			//reta direita
			g.drawLine(ReMax.x-offsetx,ReMin.y-offset,ReMax.x-offsetx,ReMax.y-offset);
			
			ret1.x = ReMin.x;
			ret1.y = ReMin.y;
			ret2.x = ReMax.x;
			ret2.y = ReMax.y;	
			
			int altura = (ReMax.y-offset) - (ReMin.y-offset);
			int largura = (ReMax.x-offsetx) - (ReMin.x-offsetx);

			template = imagem.getSubimage(ReMin.x-offsetx, ReMin.y-offset, largura, altura);
			templateM = imagemM.submat(ReMin.y-offset,ReMax.y-offset,ReMin.x-offsetx,ReMax.x-offsetx);
			if(imagemL != null) templateL = imagemL.getSubimage(ReMin.x-offsetx, ReMin.y-offset, largura, altura);
			
			ReMin.x = ReMin.y = ReMax.x = ReMax.y = -1;
		}

		//calcula o tamanho da reta fg
		//Tamanho da reta = sqrt(dx^2 + dy^2)
		public int tamanho_reta(Ponto f, Ponto g /*hihihi*/) {
			double tam = Math.sqrt( ( (g.x-f.x)*(g.x-f.x) + (g.y-f.y)*(g.y-f.y) ) );
			return (int) Math.round(tam);
		}

		//selecionar
		//apaga os pixels fora da area selecionada
		//plotando linhas de um lado ao outro da tela
		//ignorando os pixels dentro da selecao
		void selecionar(Ponto p1, Ponto p2) {
			g = panel.getGraphics();
			g.setColor(Color.WHITE);
			for (int i=inicioA-100; i<Altura; ++i) {
				if (i > p1.y-100 && i < p2.y-100) {
					g.drawLine(inicioL,i,p1.x,i);//linha atÃ© a borda esquerda do retangulo
					g.drawLine(p2.x,i,Largura,i);//linha da borda esquerda atÃ© o fim
				} else { //dentro do retangulo selecionado
					//plota uma linha com a cor do fundo de um lado a outro
					g.drawLine(inicioL,i,Largura,i);
				}
			}
			
		}


		// MÃ©todos para capturar eventos ==================================
		// Captura um clique e define seu significado
		// conforme a ferramenta em uso
		public void mousePressed( MouseEvent e ){
			x1 = e.getX();
			y1 = e.getY();
			if (y1 > offset && ferramentaAtual == Ferramentas.SELECAO) {
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
					//selecionar(new Ponto(ReMin.x,ReMin.y),new Ponto(ReMax.x,ReMax.y));
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
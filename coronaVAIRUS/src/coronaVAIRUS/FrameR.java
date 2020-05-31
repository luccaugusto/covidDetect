package coronaVAIRUS;

import java.awt.Color;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

//TODO
//Slider max_diff
//Slider max_virus
//OK Printar numero de virus
//OK adaptar para encontrar multiplos virus


public class FrameR  extends JFrame implements ActionListener, ChangeListener{

	private static JPanel contentPane,panelMenu, panel, panelS;
	private JButton buttonLBPH, buttonCC, buttonHough;
	private JLabel virus;
	private static Graphics g;
	private static int numVirus = 0;
	private static int maxDiff = 0;
	private static int maxVirus = 10;
	private static int ultimo = 0; // 0- HC 1-CC 2-LPBH
	static int minSlider = 0;
	static int maxSlider = 50;
	static int maxSliderCC = 0;
	JSlider slider = new JSlider(JSlider.VERTICAL, minSlider, maxSlider, maxDiff);
	JSlider sliderV = new JSlider(JSlider.VERTICAL, minSlider, maxSlider, maxDiff);

	//correlacao cruzada
	private Icon cc  = new  ImageIcon(getClass().getResource("correlacao_cruzada.png"));
	//hough circles
	private Icon lb  = new  ImageIcon(getClass().getResource("lbph.png"));
	//hough circles
	private Icon ihc  = new  ImageIcon(getClass().getResource("hc.png"));

	private String corFundo = "#00a388";
	
	public static int getNumVirus() {
		return numVirus;
	}
	
	public static void setNumVirus(int num) {
		numVirus = num;
	}

	public static int getMaxDiff() {
		return maxDiff;
	}
	
	public static void setMaxDiff(int num) {
		maxDiff = num;
	}

	public static int getMaxVirus() {
		return maxVirus;
	}
	
	public static void setMaxVirus(int num) {
		maxVirus = num;
	}
	
	public static int getMinSlider() {
		return minSlider;
	}
	
	public static void setMinSlider(int num) {
		minSlider = num;
	}
	
	public static int getMaxSlider() {
		return maxSlider;
	}
	
	public static void setMaxSlider(int num) {
		maxSlider = num;
	}
	
	public static void setMaxSliderCC(int num) {
		maxSliderCC = num;
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				try{
					FrameR frame = new FrameR();
					frame.setVisible(true);
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		});
	}

	public static Graphics getG() {
		return g;
	}

	public static JPanel getPanel() {
		return panel;
	}

	public FrameR() {
		//Inicializando Ambiente
		setTitle("Corona Results");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(500,0,1000,600);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);


		//Painel com botoes
		panelMenu = new JPanel();
		panelMenu.setBounds(0,0,90,600);
		panelMenu.setBackground(Color.WHITE);
		contentPane.add(panelMenu);

		//botao Hough 
		buttonHough = new JButton();
		buttonHough.addActionListener(this);
		buttonHough .setIcon(ihc);
		buttonHough .setBackground(Color.decode(corFundo));
		buttonHough .setHorizontalTextPosition(SwingConstants.CENTER); 

		//botao CC
		buttonCC = new JButton();
		buttonCC.addActionListener(this);
		buttonCC.setIcon(cc);
		buttonCC.setBackground(Color.decode(corFundo));
		buttonCC.setHorizontalTextPosition(SwingConstants.CENTER); 

		//botao LBPH
		buttonLBPH = new JButton();
		buttonLBPH.addActionListener(this);
		buttonLBPH.setIcon(lb);
		buttonLBPH.setBackground(Color.decode(corFundo));
		buttonLBPH.setHorizontalTextPosition(SwingConstants.CENTER); 
		
		virus = new JLabel();
		virus.setText("Vírus: "+numVirus);
		
		GroupLayout g1_panelMenu = new GroupLayout(panelMenu);
		g1_panelMenu.setHorizontalGroup(
				g1_panelMenu.createParallelGroup(Alignment.CENTER)
				//.addGroup( g1_panelMenu.createSequentialGroup()
				.addGap(10)
				.addComponent(buttonHough)
				.addComponent(buttonCC)
				.addComponent(buttonLBPH)
				.addComponent(virus)
				//)

				);

		g1_panelMenu.setVerticalGroup(
				g1_panelMenu.createParallelGroup(Alignment.CENTER)
				.addGap(10)
				.addGroup(g1_panelMenu.createSequentialGroup()
					.addGap(30)
					//.addGroup(g1_panelMenu.createParallelGroup(Alignment.BASELINE)
					.addComponent(buttonHough)
					.addGap(30)
					.addComponent(buttonCC)
					.addGap(30)
					.addComponent(buttonLBPH)
					.addGap(30)
					.addComponent(virus)
					//	)
					)
				);

		panelMenu.setLayout(g1_panelMenu);

		//Painel de imagem
		panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBounds(90,0,710,600);
		contentPane.add(panel);
		panel.setLayout(null);
		
		//Painel do slider
		panelS = new JPanel();
		panelS.setBounds(800,0,90,600);
		panelS.setBackground(Color.WHITE);
		contentPane.add(panelS);
		

		
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		// Set the spacing for the minor tick mark
		slider.setMinorTickSpacing(10);
		slider.setName("Max Dif");

		slider.addChangeListener(this);

		panelS.add(slider);
		
		
		sliderV.setPaintTicks(true);
		sliderV.setPaintLabels(true);
		// Set the spacing for the minor tick mark
		sliderV.setMinorTickSpacing(10);
		sliderV.setName("Max Vírus");

		sliderV.addChangeListener(this);

		panelS.add(sliderV);

	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		if (!source.getValueIsAdjusting()) {
			if (source == slider) {
				maxDiff = source.getValue();
				ActionEvent a = null;
				if (ultimo == 0) {
					do_buttonHough_actionPerfomed(a);
				}else if (ultimo == 1) {
					do_buttonCC_actionPerfomed(a);
				}
			}else {
				maxVirus = source.getValue();
				ActionEvent a = null;
				if (ultimo == 1) {
					do_buttonCC_actionPerfomed(a);
				}
			}
		}
	}

	public void actionPerformed(ActionEvent arg0){
		if(arg0.getSource() == buttonHough){
			do_buttonHough_actionPerfomed(arg0);
		}
		if(arg0.getSource() == buttonCC){
			do_buttonCC_actionPerfomed(arg0);
		}
		if(arg0.getSource() == buttonLBPH){
			do_buttonLBPH_actionPerfomed(arg0);
		}
	}

	protected void do_buttonHough_actionPerfomed(ActionEvent arg0){
		ultimo=0;
		HoughCircles hc = new HoughCircles(Corona.getImagemM(),Corona.getTemplateM());
		BufferedImage detectados = hc.detectar();
		g = panel.getGraphics();
		g.drawImage(detectados, 0, 0, null);
		virus.setText("Vírus: "+numVirus);
	}

	protected void do_buttonCC_actionPerfomed(ActionEvent arg0){
		ultimo=1;
		CorrelacaoCruzada cc = new CorrelacaoCruzada(Corona.getImagemM(), Corona.getTemplateM());
		//cc.detectar();
		BufferedImage detectados = cc.detectar();
		g = panel.getGraphics();
		g.drawImage(detectados, 0, 0, null);
		virus.setText("Vírus: "+numVirus);
	}

	protected void do_buttonLBPH_actionPerfomed(ActionEvent arg0){
		ultimo=2;
		virus.setText("Vírus: "+numVirus);
	}
}

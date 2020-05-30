package coronaVAIRUS;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class FrameR  extends JFrame implements ActionListener{

	private static JPanel contentPane,panelMenu, panel;
    private JButton buttonLBPH, buttonCC, buttonHough;
    private static Graphics g;
    
  //correlacao cruzada
  	private Icon cc  = new  ImageIcon(getClass().getResource("correlacao_cruzada.png"));
  	//hough circles
  	private Icon lb  = new  ImageIcon(getClass().getResource("lbph.png"));
  //hough circles
  	private Icon ihc  = new  ImageIcon(getClass().getResource("hc.png"));
  	
	private String corFundo = "#00a388";

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
		setBounds(560,0,800,600);
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

        GroupLayout g1_panelMenu = new GroupLayout(panelMenu);
		g1_panelMenu.setHorizontalGroup(
				g1_panelMenu.createParallelGroup(Alignment.CENTER)
                //.addGroup( g1_panelMenu.createSequentialGroup()
                .addGap(10)
				.addComponent(buttonHough)
				.addComponent(buttonCC)
				.addComponent(buttonLBPH)
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
    	HoughCircles hc = new HoughCircles(Corona.getMatImg(),Corona.getMatTemplate());
		BufferedImage detectados = hc.detectar();
		g = panel.getGraphics();
		g.drawImage(detectados, 0, 0, null);
    }

    protected void do_buttonCC_actionPerfomed(ActionEvent arg0){
    	CorrelacaoCruzada cc = new CorrelacaoCruzada(Corona.getMatImg(), Corona.getMatTemplate());
		BufferedImage detectados = cc.detectar();
		g = panel.getGraphics();
		g.drawImage(detectados, 0, 0, null);
    }
    
    protected void do_buttonLBPH_actionPerfomed(ActionEvent arg0){

    }
}
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

	private JPanel contentPane,panelMenu, panel;
    private JButton buttonLBPH;
    private Graphics g;

	private String corFundo = "#00a388";

	private Icon lbphI   = new  ImageIcon(getClass().getResource("detectar.png"));

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

		//botao LBPH
		buttonLBPH = new JButton();
		buttonLBPH.addActionListener(this);
        buttonLBPH.setIcon(lbphI);
		buttonLBPH.setBackground(Color.decode(corFundo));
		buttonLBPH.setHorizontalTextPosition(SwingConstants.CENTER); 

        GroupLayout g1_panelMenu = new GroupLayout(panelMenu);
		g1_panelMenu.setHorizontalGroup(
				g1_panelMenu.createParallelGroup(Alignment.CENTER)
                .addGroup( g1_panelMenu.createSequentialGroup()
                .addGap(10)
				.addComponent(buttonLBPH)
                )
                    		
			);

        g1_panelMenu.setVerticalGroup(
				g1_panelMenu.createParallelGroup(Alignment.CENTER)
				.addGroup(g1_panelMenu.createSequentialGroup()
                    .addGap(30)
					.addGroup(g1_panelMenu.createParallelGroup(Alignment.BASELINE)
						.addComponent(buttonLBPH)

						)
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
	    if(arg0.getSource() == buttonLBPH){
		    do_buttonLBPH_actionPerfomed(arg0);
	    }
    }

    protected void do_buttonLBPH_actionPerfomed(ActionEvent arg0){
    	HoughCircles hc = new HoughCircles(Corona.getFilename());
		BufferedImage detectados = hc.detectar();
		g = panel.getGraphics();
		g.drawImage(detectados, 0, 0, null);
    }
}
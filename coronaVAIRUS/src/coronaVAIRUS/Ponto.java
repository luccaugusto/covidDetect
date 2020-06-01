package coronaVAIRUS;

import java.util.ArrayList;

public class Ponto {
	public static ArrayList<Ponto> lista = new ArrayList<Ponto>();
	int x;
	int y;
	public Ponto() {
		this.x = -1;
		this.y = -1;
	}
	
	public Ponto(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
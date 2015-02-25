package LRHMM;

import java.text.DecimalFormat;

public class LeftToRightHMMverViterbi {
	private State[] unit;
	private double[][] alpha;
	private double[][] beta;
	private double[][][] gamma;
	private int[] O;
	private double likelihood;
	private double prevLikelihood;
	private int[] pass;
	
	public LeftToRightHMMverViterbi(int[] O,double[][] A,double[][] B){
		unit = new State[A.length+1];
		for(int i = 0;i < unit.length;i++){
			if(i == unit.length - 1){
				double[] uoeA = {0.0,1.0};		//�I�[�Ȃ̂ŏ�ɑ؍�
				double[] uoeB = {0.0,0.0,0.0};	//�I�[�Ȃ̂ŋL���͐��܂Ȃ�
				unit[i] = new State(uoeB,uoeA);
			}else{
				unit[i] = new State(B[i],A[i]);
			}
		}
		this.O = new int[O.length];
		for(int i = 0;i < O.length;i++){
			this.O[i] = O[i];
		}
		alpha = new double[O.length+1][unit.length];
		beta = new double[O.length+1][unit.length];
		gamma = new double[O.length][unit.length][unit.length];
		pass = new int[O.length+1];
	}
	
	public void forward(){
		int t = 0,v = 0;
		int cap;
		//������ԂƂ��ă�[0][0] = 1.0��ݒ�
		alpha[t][v] = 1.0;
		pass[0] = 0;
		cap = alpha.length - unit.length + 1;
		
		for(t = 1;t < alpha.length - 1;t++){
			alpha[t][v] = alpha[t-1][v] * unit[v].getTransitionProbability(false) * unit[v].getInternalState(O[t-1]);
			alpha[t][v+1] = alpha[t-1][v] * unit[v].getTransitionProbability(true) * unit[v].getInternalState(O[t-1]);
			
			if(alpha[t][v] > alpha[t][v+1]){
				if(t + 1 > cap){
					v++;
					cap++;
				}
			}else{
				if(v < unit.length-2){
					v++;
					cap++;
				}
			}
			pass[t] = v;
		}
		pass[t-1] = v;
		likelihood = alpha[t-1][v];
	}
	public void backward(){
		int t = O.length;
		beta[t][pass[t]] = 1.0;
		t--;
		for(;t >= 0;t--){
			if(pass[t] != pass[t+1]){
				beta[t][pass[t]] = beta[t+1][pass[t+1]] * unit[pass[t]].getTransitionProbability(true) * unit[pass[t]].getInternalState(O[t]);
			}else{
				beta[t][pass[t]] = beta[t+1][pass[t+1]] * unit[pass[t]].getTransitionProbability(false) * unit[pass[t]].getInternalState(O[t]);
			}
		}
	}
	public void calGamma(){
		for(int t = 0;t < O.length;t++){
			if(pass[t] == pass[t+1]){
				gamma[t][pass[t]][pass[t+1]] = alpha[t][pass[t]] * unit[pass[t]].getTransitionProbability(false) * unit[pass[t]].getInternalState(O[t]) * beta[t+1][pass[t+1]];
			}else{
				gamma[t][pass[t]][pass[t+1]] = alpha[t][pass[t]] * unit[pass[t]].getTransitionProbability(true) * unit[pass[t]].getInternalState(O[t]) * beta[t+1][pass[t+1]];
			}	
		}
	}
	public void reCalA(){
		double[] a;			//�Čv�Z���ꂽ��ԑJ�ڊm��
		double denominater;	//����
		double numerater;	//���q
		int st = 0,et = 0;
		int cap;
		for(int i = 0;i < unit.length-1;i++){
			a = new double[2];		//�؍݂��J��
			cap = pass[st];
			denominater = 0.0;
			numerater = 0.0;
			while(cap == pass[et]){
				denominater += gamma[et][i][pass[et+1]];
				et++;
			}	
			for(int j = st;j < et-1;j++){	//�؍�
				numerater += gamma[j][i][pass[j+1]];
			}
			a[1] = numerater/denominater;
			a[0] = gamma[et-1][i][pass[et]]/denominater;
			unit[i].setTransitionProbability(a);
			st = et;
		}
	}
	public void reCalB(){
		double[] b;			//�Čv�Z���ꂽ�L���o���m��
		double denominater;	//����
		double numerater;	//���q
		int st = 0,et = 0;
		int cap;
		for(int i = 0;i < unit.length-1;i++){
			b = new double[unit[i].getInternalStateStock()];
			cap = pass[st];
			denominater = 0.0;
			numerater = 0.0;
			while(cap == pass[et]){
				denominater += gamma[et][i][pass[et+1]];
				et++;
			}	
			for(int sign = 0;sign < unit[i].getInternalStateStock();sign++){
				numerater = 0.0;
				for(int k = st;k < et;k++){
					if(sign == O[k]){
						numerater += gamma[k][i][pass[k+1]];
					}
				}
				b[sign] = numerater/denominater;
			}
			unit[i].setInternalState(b);
			st = et;
		}
	}
	public double getEval(){
		double cap = likelihood - prevLikelihood;
		prevLikelihood = likelihood;
		return cap;
	}
	public double[][] getAlpha(){
		return alpha;
	}
	public double[][] getBeta(){
		return beta;
	}
	public double[][][] getGamma(){
		return gamma;
	}
	public void printAlpha(){
		DecimalFormat dform = new DecimalFormat("##0.0####");
		System.out.println("��.length:" + alpha.length);
		System.out.println("��[0].length:" + alpha[0].length);
		for(int v = 0;v < alpha[0].length;v++){
			for(int t = 0;t < alpha.length;t++){
				System.out.print("��" + v + "(" + t + ")" + dform.format(alpha[t][v]) + "\t");
			}
			System.out.println();
		}
	}
	public void printBeta(){
		DecimalFormat dform = new DecimalFormat("##0.0####");
		System.out.println("��.length:" + beta.length);
		System.out.println("��[0].length:" + beta[0].length);
		for(int v = 0;v < beta[0].length;v++){
			for(int t = 0;t < beta.length;t++){
				System.out.print("��" + v + "(" + t + ")" + dform.format(beta[t][v]) + "\t");
			}
			System.out.println();
		}
	}
	public void printGamma(){
		DecimalFormat dform = new DecimalFormat("##0.0######");
		System.out.println("��.length:" + gamma.length);
		System.out.println("��[0].length:" + gamma[0].length);
		System.out.println("��[0][0].length:" + gamma[0][0].length);
		for(int i = 0;i < gamma[0][0].length;i++){
			for(int j = 0;j < gamma[0].length;j++){
				for(int t = 0;t < gamma.length;t++){
					System.out.print("��" + t + "(" + i + "," + j + ")" + dform.format(gamma[t][i][j]) + "\t\t");
				}
				System.out.println();
			}
		}
	}
	public void printPass(){
		System.out.println("t.length:" + pass.length);
		for(int i = 0;i < pass.length;i++){
			System.out.print(pass[i] + "\t");
		}
		System.out.println();
	}
	
	public String toString(){
		String str = "";
		DecimalFormat dform = new DecimalFormat("##0.0##");
		System.out.println("���݂̊e��Ԃ̃X�e�[�^�X");
		for(int i = 0;i < unit.length - 1;i++){
			str += ("���" + i + "�̃X�e�[�^�X�F\n");
			str += ("��ԑJ�ڊm��\n");
			str += ("a" + i + "," + i + ":" + dform.format(unit[i].getTransitionProbability(false)) + "\t");
			str += ("a" + i + "," + (i+1) + ":" + dform.format(unit[i].getTransitionProbability(true)) + "\t");
			str += ("\n�L���o�͊m��\n");
			for(int j = 0;j < unit[i].getInternalStateStock();j++){
				str += ("b" + i + "[" + j + "]:" + dform.format(unit[i].getInternalState(j)) + "\t");
			}
			str += "\n";
		}
		
		
		
		return str;
	}
}

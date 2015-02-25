package ErgodicHMM;

import java.text.DecimalFormat;

public class ErgodicHMM {
	private State[] unit;
	private double[][] alpha;
	private double[][] beta;
	private double[][][] gamma;
	private double[] pi;
	private int[] O;
	private double likelihood;
	private double prevLikelihood;
	
	/**
	 * Constructor
	 * @param O Observation symbol
	 * @param A State transition probability
	 * @param B Symbol appearance probability
	 * @param pi Initial condition probability
	 */
	public ErgodicHMM(double[][] A,double[][] B,double[] pi){
		unit = new State[A.length];
		for(int i = 0;i < unit.length;i++){
			unit[i] = new State(B[i],A[i]);
		}
		this.pi = new double[pi.length];
		for(int i = 0;i < pi.length;i++){
			this.pi[i] = pi[i];
		}
	}
	/**
	 * Forward likelihood calculation
	 */
	public void forward(){
		int t = 0;
		double sum;
		
		//Initialization when t=0
		for(int i = 0;i < unit.length;i++){
			alpha[0][i] = pi[i];
		}
		
		for(t = 1;t < alpha.length;t++){
			for(int i = 0;i < unit.length;i++){
				sum = 0.0;
				for(int j = 0;j < unit.length;j++){
					sum += alpha[t-1][j] * unit[j].getTransitionProbability(i) * unit[j].getInternalState(O[t-1]);
				}
				alpha[t][i] = sum;
			}
		}
		
		//Calculate likelihood
		sum = 0.0;
		for(int i = 0;i < unit.length;i++){
			sum += alpha[O.length][i];
		}
		likelihood = sum;
	}
	/**
	 * Backward likelihood calculation
	 */
	public void backward(){
		int t;
		double sum;
		
		//Initialization when t=O.length
		for(int i = 0;i < unit.length;i++){
			beta[O.length][i] = 1.0;
		}
		
		for(t = O.length - 1;t >= 0;t--){
			for(int i = 0;i < unit.length;i++){
				sum = 0.0;
				for(int j = 0;j < unit.length;j++){
					sum += beta[t+1][j] * unit[i].getTransitionProbability(j) * unit[i].getInternalState(O[t]);
				}
				beta[t][i] = sum;
			}
		}
	}
	/**
	 * Calculated likelihood in each time
	 */
	public void calGamma(){
		for(int t = 0;t < O.length;t++){
			for(int i = 0;i < unit.length;i++){
				for(int j = 0;j < unit.length;j++){
					if(Math.abs(alpha[t][i]) > 0.001 && Math.abs(beta[t+1][j]) > 0.001){
						gamma[t][i][j] = alpha[t][i] * unit[i].getTransitionProbability(j) * unit[i].getInternalState(O[t]) * beta[t+1][j] / likelihood;
					}
				}
			}
			
		}
	}
	/**
	 * Recalculation of a state transition probability
	 */
	public void reCalA(){
		double[] newA;		//New state transition probability
		double denominater;
		for(int i = 0;i < unit.length;i++){
			newA = new double[unit[i].getTransitionProbabilityStock()];
			denominater = 0.0;
			for(int t = 0;t < O.length;t++){
				for(int j = 0;j < unit.length;j++){
					denominater += gamma[t][i][j];
					newA[j] += gamma[t][i][j];
				}
			}
			for(int j = 0;j < unit[i].getTransitionProbabilityStock();j++){
				newA[j] /= denominater;
			}
			unit[i].setTransitionProbability(newA);
		}
	}
	/**
	 * Recalculation of a symbol appearance probability
	 */
	public void reCalB(){
		double[] newB;			//New symbol appearance probability
		double denominater;

		for(int i = 0;i < unit.length;i++){
			newB = new double[unit[i].getInternalStateStock()];
			denominater = 0.0;
			for(int t = 0;t < O.length;t++){
				for(int j = 0;j < unit.length;j++){
					denominater += gamma[t][i][j];
					newB[O[t]] += gamma[t][i][j];
				}
			}
			for(int j = 0;j < unit[i].getInternalStateStock();j++){
				newB[j] /= denominater;
			}
			unit[i].setInternalState(newB);
		}
	}
	public double getEval(){
		double cap = likelihood - prevLikelihood;
		prevLikelihood = likelihood;
		return cap;
	}
	public double learn(int[] O){
		this.O = new int[O.length];
		for(int i = 0;i < O.length;i++){
			this.O[i] = O[i];
		}
		alpha = new double[O.length+1][unit.length];
		beta = new double[O.length+1][unit.length];
		gamma = new double[O.length][unit.length][unit.length];
		forward();
		printAlpha();
		backward();
		printBeta();
		calGamma();
		//printGamma();
		reCalA();
		reCalB();
		return getEval();
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
		System.out.println("α.length:" + alpha.length);
		System.out.println("α[0].length:" + alpha[0].length);
		for(int v = 0;v < alpha[0].length;v++){
			for(int t = 0;t < alpha.length;t++){
				System.out.print("α" + v + "(" + t + ")" + dform.format(alpha[t][v]) + "\t");
			}
			System.out.println();
		}
	}
	public void printBeta(){
		DecimalFormat dform = new DecimalFormat("##0.0####");
		System.out.println("β.length:" + beta.length);
		System.out.println("β[0].length:" + beta[0].length);
		for(int v = 0;v < beta[0].length;v++){
			for(int t = 0;t < beta.length;t++){
				System.out.print("β" + v + "(" + t + ")" + dform.format(beta[t][v]) + "\t");
			}
			System.out.println();
		}
	}
	public void printGamma(){
		DecimalFormat dform = new DecimalFormat("##0.0######");
		System.out.println("Γ.length:" + gamma.length);
		System.out.println("Γ[0].length:" + gamma[0].length);
		System.out.println("Γ[0][0].length:" + gamma[0][0].length);
		for(int i = 0;i < gamma[0][0].length;i++){
			for(int j = 0;j < gamma[0].length;j++){
				for(int t = 0;t < gamma.length;t++){
					System.out.print("Γ" + t + "(" + i + "," + j + ")" + dform.format(gamma[t][i][j]) + "\t\t");
				}
				System.out.println();
			}
		}
	}
	
	public String toString(){
		String str = "";
		DecimalFormat dform = new DecimalFormat("##0.0##");
		System.out.println("現在の各状態のステータス");
		for(int i = 0;i < unit.length;i++){
			str += ("状態" + i + "のステータス：\n");
			str += ("状態遷移確率\n");
			for(int j = 0;j < unit.length;j++){
				str += ("a" + i + "," + j + ":" + dform.format(unit[i].getTransitionProbability(j)) + "\t");
			}
			str += ("\n記号出力確率\n");
			for(int j = 0;j < unit[i].getInternalStateStock();j++){
				str += ("b" + i + "[" + j + "]:" + dform.format(unit[i].getInternalState(j)) + "\t");
			}
			str += "\n";
		}
		return str;
	}
}

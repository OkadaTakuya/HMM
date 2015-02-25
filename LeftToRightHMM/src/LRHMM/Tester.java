/**
 * Left to Right HMMの動作確認用プログラム
 */
package LRHMM;

public class Tester {
	public static void main(String[] args){
		double[][] A = {{ 0.8, 0.2},
						{ 0.5, 0.5},
						{ 0.1, 0.9}};
		double[][] B = {{ 0.2, 0.1, 0.7},
						{ 0.5, 0.2, 0.3},
						{ 0.1, 0.1, 0.8}};
		int[] O = { 0, 0, 1, 2};			//0:Alpha 1:Beta 2:Gamma
		int i = 1;
		LeftToRightHMM HMM = new LeftToRightHMM(O,A,B);
		
		System.out.println("計算前");
		System.out.print(HMM);
		do{
			System.out.println(i++ + "回目の計算開始");
			HMM.forward();
			HMM.backward();			
			HMM.calGamma();
			HMM.reCalA();
			HMM.reCalB();
		}while(HMM.getEval() > 0);
		System.out.println("計算後");
		System.out.print(HMM);
	}
}

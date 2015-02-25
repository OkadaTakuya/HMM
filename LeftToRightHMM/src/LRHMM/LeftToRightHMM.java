/**
 * Left to Right HMM
 */
package LRHMM;

import java.text.DecimalFormat;

public class LeftToRightHMM {
	private State[] unit;			//���
	private double[][] alpha;		//�O�����ޓx
	private double[][] beta;		//�������ޓx
	private double[][][] gamma;		//�e�����ɂ�����e��Ԃ̖ޓx
	private int[] O;				//�ϑ��L����
	private double likelihood;		//�n�S�̖̂ޓx
	private double prevLikelihood;	//�O��̌v�Z���̖ޓx
	
	/**
	 * �R���X�g���N�^
	 * @param O �ϑ��L����
	 * @param A ��ԑJ�ڊm���̏����l
	 * @param B �L���o���m���̏����l
	 */
	public LeftToRightHMM(int[] O,double[][] A,double[][] B){
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
	}
	/**
	 * �{�����������g�����D<br>
	 * learn()���g���ꍇ�́Clearn()���̃��\�b�h�͑S��private�ɂ���D<br>
	 * (����̓f�o�b�O�̊֌W�Ƃ��F�X�Ŏg���ĂȂ�)
	 * @return �]���l
	 */
	public double learn(){
		forward();
		backward();
		calGamma();
		reCalA();
		reCalB();
		return getEval();
	}
	/**
	 * �O�����̖ޓx�v�Z���s���D
	 */
	public void forward(){
		int t = 0,j = 0;
		//������ԂƂ���a[0][0] = 1.0; a[0][i] = 0.0;��ݒ�
		alpha[0][0] = 1.0;
		for(int i = 1;i < alpha[0].length;i++){
			alpha[0][i] = 0.0;
		}
		int cap = O.length - unit.length + 1;
		for(t = 1;t < alpha.length - 1;t++){
			for(j = 0;j < alpha[t].length - 1;j++){
				if(j + cap < t){
					alpha[t][j] = 0.0;
				}else{
					if(j == 0){
						alpha[t][j] = alpha[t-1][j] * unit[j].getTransitionProbability(false) * unit[j].getInternalState(O[t-1]);
					}else{
						alpha[t][j] = alpha[t-1][j] * unit[j].getTransitionProbability(false) * unit[j].getInternalState(O[t-1]) + alpha[t-1][j-1] * unit[j-1].getTransitionProbability(true) * unit[j-1].getInternalState(O[t-1]);
					}
				}
			}
		}
		//���̎��_��t = alpha.length ���� j = alpha[alpha.length].length
		alpha[t][j] = alpha[t-1][j-1] * unit[j-1].getTransitionProbability(true) * unit[j-1].getInternalState(O[t-1]);
		for(int i = 0;i < j;i++){
			alpha[t][i] = 0.0;
		}
		likelihood = alpha[t][j];
	}
	/**
	 * �������̖ޓx�v�Z���s���D
	 */
	public void backward(){
		int t = 0,j = 0;
		beta[beta.length - 1][beta[beta.length - 1].length - 1] = 1.0;
		for(int i = beta[beta.length - 1].length - 2;i >= 0;i--){
			beta[beta.length - 1][i] = 0.0;
		}
		for(int i = beta.length - 2;i >= 0;i--){
			beta[i][unit.length - 1] = 0.0;
		}
		int cap = O.length - unit.length + 1;
		for(t = beta.length - 2;t >= 0;t--){
			for(j = beta[t].length - 2;j >= 0;j--){
				if(j >= t + cap){
					beta[t][j] = 0.0;
				}else{
					beta[t][j] = beta[t+1][j] * unit[j].getTransitionProbability(false) * unit[j].getInternalState(O[t]) + beta[t+1][j+1] * unit[j].getTransitionProbability(true) * unit[j].getInternalState(O[t]);
				}
			}
		}
	}
	/**
	 * �e�����ɂ����Ċe��Ԃɂ���Ƃ��̖ޓx�����߂�D
	 */
	public void calGamma(){
		for(int t = 0;t < gamma.length;t++){
			for(int i = 0;i < gamma[t].length;i++){
				for(int j = 0;j < gamma[t][i].length;j++){
					gamma[t][i][j] = 0.0;
					if(i == j){
						gamma[t][i][j] = (alpha[t][i] * unit[i].getTransitionProbability(false) * unit[i].getInternalState(O[t]) * beta[t+1][j])/likelihood;
					}else if(Math.abs(i - j) < 2){
						gamma[t][i][j] = (alpha[t][i] * unit[i].getTransitionProbability(true) * unit[i].getInternalState(O[t]) * beta[t+1][j])/likelihood;
					}
				}
			}
		}
	}
	/**
	 * �e��Ԃ̏�ԑJ�ڊm���̍Čv�Z���s���D
	 */
	public void reCalA(){
		double[] a;
		double denominator;
		double numerater;
		for(int i = 0;i < unit.length;i++){
			a = new double[unit[i].getTransitionProbabilityStock()];
			denominator = 0.0;
			for(int t = 0;t < O.length;t++){
				for(int j = 0;j < unit.length;j++){
					denominator += gamma[t][i][j];
				}
			}
			for(int j = 0;j < unit[i].getTransitionProbabilityStock();j++){
				numerater = 0.0;
				for(int t = 0;t < O.length;t++){
					if(i != unit.length - 1){
						numerater += gamma[t][i][i+j];
					}
				}
				a[1-j] = numerater/denominator;
			}
			unit[i].setTransitionProbability(a);
			
		}
	}
	/**
	 * �e��Ԃ̋L���o���m���̍Čv�Z���s���D
	 */
	public void reCalB(){
		double[] b;
		double denominator;
		double numerater;
		for(int j = 0;j < unit.length;j++){
			b = new double[unit[j].getInternalStateStock()];
			denominator = 0.0;
			for(int t = 0;t < O.length;t++){
				for(int k = 0;k < unit.length;k++){
					denominator += gamma[t][j][k];
				}
			}
			
			for(int sign = 0;sign < unit[j].getInternalStateStock();sign++){
				numerater = 0.0;
				for(int t = 0;t < O.length;t++){
					for(int k = 0;k < unit.length;k++){
						if(O[t] == sign){
							numerater += gamma[t][j][k];
						}
					}
				}
				b[sign] = numerater/denominator;
			}
			unit[j].setInternalState(b);
		}
	}
	/**
	 * ����̃p�����[�^�Đ���̕]���D
	 * @return �]���l
	 */
	public double getEval(){
		double cap = likelihood - prevLikelihood;
		prevLikelihood = likelihood;
		return cap;
	}
	/**
	 * �O�����ޓx�̒l���擾����D(printf�f�o�b�O�p)
	 * @return �O�����ޓx���i�[���ꂽ�z��
	 */
	public double[][] getAlpha(){
		return alpha;
	}
	/**
	 * �������ޓx�̒l���擾����D(printf�f�o�b�O�p)
	 * @return �������ޓx���i�[���ꂽ�z��
	 */
	public double[][] getBeta(){
		return beta;
	}
	/**
	 * �e�����̖ޓx�̒l���擾����D(printf�f�o�b�O�p)
	 * @return �e�����̖ޓx���i�[���ꂽ�z��
	 */
	public double[][][] getGamma(){
		return gamma;
	}
	/**
	 * HMM�̓�����Ԃ�\������D
	 */
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

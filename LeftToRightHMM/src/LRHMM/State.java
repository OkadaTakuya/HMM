/**
 * 1�̏�Ԃ�\���N���X
 */
package LRHMM;

public class State {
	private double[] internal_state;
	private double[] transition_probability;
	/**
	 * �R���X�g���N�^
	 * @param internal_state �e������Ԃ̋L���o���m���̏����l
	 * @param transition_probability ��ԑJ�ڊm���̏����l
	 */
	public State(double[] internal_state,double[] transition_probability){
		this.internal_state = new double[internal_state.length];
		this.transition_probability = new double[transition_probability.length];
		for(int i = 0;i < internal_state.length;i++){
			this.internal_state[i] = internal_state[i];
		}
		for(int i = 0;i < transition_probability.length;i++){
			this.transition_probability[i] = transition_probability[i];
		}
	}
	/**
	 * i�Ԗڂ̓�����Ԃ̋L���o���m����ԋp����D
	 * @param i ������Ԃ̔ԍ�
	 * @return �L���o���m��
	 */
	public double getInternalState(int i){
		if(i < internal_state.length){
			return internal_state[i];
		}else{
			System.out.println("���݂��Ȃ�������Ԃ�I������܂���");
			return 0.0;
		}
	}
	/**
	 * ������Ԃ̐���Ԃ��D
	 * @return ������Ԃ̐�
	 */
	public int getInternalStateStock(){
		return internal_state.length;
	}
	/**
	 * ��Ԑ�m���̐���Ԃ��D
	 * @return �L���o���m���̐�
	 */
	public int getTransitionProbabilityStock(){
		return transition_probability.length;
	}
	/**
	 * �e������Ԃ̋L���o���m�����X�V����D
	 * @param internal_state �Đݒ肷��L���o���m��
	 */
	public void setInternalState(double[] internal_state){
		for(int i = 0;i < internal_state.length;i++){
			this.internal_state[i] = internal_state[i];
		}
	}
	/**
	 * �e��ԑJ�ڊm�����X�V����D
	 * @param transition_probability �Đݒ肷���ԑJ�ڊm��
	 */
	public void setTransitionProbability(double[] transition_probability){
		for(int i = 0;i < transition_probability.length;i++){
			this.transition_probability[i] = transition_probability[i];
		}
	}
	/**
	 * ��Ԑ�m�����擾����D
	 * @param change true:�J�ڂ���Ƃ��̊m�� false:�؍݂���Ƃ��̊m�� 
	 * @return change�Ŏw�肳�ꂽ�m��
	 */
	public double getTransitionProbability(boolean change){
		if(change){
			return transition_probability[0];
		}else{
			return transition_probability[1];
		}
	}
}

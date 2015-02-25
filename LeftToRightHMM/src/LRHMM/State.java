/**
 * 1つの状態を表すクラス
 */
package LRHMM;

public class State {
	private double[] internal_state;
	private double[] transition_probability;
	/**
	 * コンストラクタ
	 * @param internal_state 各内部状態の記号出現確率の初期値
	 * @param transition_probability 状態遷移確率の初期値
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
	 * i番目の内部状態の記号出現確率を返却する．
	 * @param i 内部状態の番号
	 * @return 記号出現確率
	 */
	public double getInternalState(int i){
		if(i < internal_state.length){
			return internal_state[i];
		}else{
			System.out.println("存在しない内部状態を選択されました");
			return 0.0;
		}
	}
	/**
	 * 内部状態の数を返す．
	 * @return 内部状態の数
	 */
	public int getInternalStateStock(){
		return internal_state.length;
	}
	/**
	 * 状態戦確率の数を返す．
	 * @return 記号出現確率の数
	 */
	public int getTransitionProbabilityStock(){
		return transition_probability.length;
	}
	/**
	 * 各内部状態の記号出現確率を更新する．
	 * @param internal_state 再設定する記号出現確率
	 */
	public void setInternalState(double[] internal_state){
		for(int i = 0;i < internal_state.length;i++){
			this.internal_state[i] = internal_state[i];
		}
	}
	/**
	 * 各状態遷移確率を更新する．
	 * @param transition_probability 再設定する状態遷移確率
	 */
	public void setTransitionProbability(double[] transition_probability){
		for(int i = 0;i < transition_probability.length;i++){
			this.transition_probability[i] = transition_probability[i];
		}
	}
	/**
	 * 状態戦確率を取得する．
	 * @param change true:遷移するときの確率 false:滞在するときの確率 
	 * @return changeで指定された確率
	 */
	public double getTransitionProbability(boolean change){
		if(change){
			return transition_probability[0];
		}else{
			return transition_probability[1];
		}
	}
}

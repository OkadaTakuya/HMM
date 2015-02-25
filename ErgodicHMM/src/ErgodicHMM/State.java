package ErgodicHMM;

public class State {
	private double[] internal_state;
	private double[] transition_probability;
	
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
	public double getInternalState(int i){
		if(i < internal_state.length){
			return internal_state[i];
		}else{
			System.out.println("存在しない内部状態を選択されました");
			return 0.0;
		}
	}
	public int getInternalStateStock(){
		return internal_state.length;
	}
	public int getTransitionProbabilityStock(){
		return transition_probability.length;
	}
	public void setInternalState(double[] internal_state){
		for(int i = 0;i < internal_state.length;i++){
			this.internal_state[i] = internal_state[i];
		}
	}
	public void setTransitionProbability(double[] transition_probability){
		for(int i = 0;i < transition_probability.length;i++){
			this.transition_probability[i] = transition_probability[i];
		}
	}
	public double getTransitionProbability(int i){
		if(i < transition_probability.length){
			return transition_probability[i];
		}else{
			System.out.println("存在しない状態遷移確率が選択されました．");
			return 0.0;
		}
	}
}

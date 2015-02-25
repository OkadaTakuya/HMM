import ErgodicHMM.*;
import IOCSV.*;

public class axis {
	public static void main(String[] args){
		double[][] A = {{ 0.3, 0.3, 0.4},
						{ 0.3, 0.3, 0.4},
						{ 0.3, 0.3, 0.4}};
		double[][] B = {{ 0.3, 0.2, 0.2, 0.1, 0.1, 0.05, 0.05},
						{ 0.3, 0.2, 0.2, 0.1, 0.1, 0.05, 0.05},
						{ 0.3, 0.2, 0.2, 0.1, 0.1, 0.05, 0.05}};
		double[] pi = {0.2,0.3,0.5};
		int[] O;
		int count = 1;
		double eval;
		
		ErgodicHMM HMM = new ErgodicHMM(A,B,pi);
		CSVReader buff = new CSVReader("E:\\voice\\csv\\ohayo\\ws\\reo.csv");
		
		System.out.println("計算前");
		System.out.print(HMM);
		long startTime = System.currentTimeMillis();
		do{
			System.out.println(count++ + "回目の学習");
			eval = 0.0;
			//for(int i = 0;i < 5;i++){
				O = buff.readLine();
				O = parseO(O);
				eval += HMM.learn(O);
			//}
			buff.rewind();
		}while(Math.abs(eval) > 0.00001);
		long stopTime = System.currentTimeMillis();
		System.out.println("計算後");
		System.out.print(HMM);
		
		System.out.println("実行にかかった時間は" + (stopTime - startTime) + "ミリ秒でした．");
	}
	public static void printO(int[] O){
		for(int i = 0;i < O.length;i++){
			System.out.print(O[i] + " ");
		}
		System.out.println();
	}
	private static int[] parseO(int[] src){
		int[] dst = new int[src.length];
		for(int i = 0;i < src.length;i++){	
			dst[i] = Math.abs(src[i])/3000;
			if(dst[i] > 6){
				dst[i] = 6;
			}
		}
		return dst;
	}
	private static int[] removeZero(int[] src){
		int count = 0;
		int i = 0,j = 0;
		for(i = 0;i < src.length;i++){
			if(src[i] == 0)count++;
		}
		int[] dst = new int[src.length - count];
		for(i = 0;i < src.length;i++){
			if(src[i] != 0){
				dst[j] = src[i];
				j++;
			}
		}
		return dst;
	}
}

package utility;

public class ValueCalculator {
	double mean;
	double s;
	double V;
	int n;
	
	//Quantile 95% per T-Student
	double n_15 = 1.753;
	double n_20 = 1.725;
	double n_30 = 1.697;
	double n_40 = 1.684;
	double n_50 = 1.676;
	double n_60 = 1.671;
	double n_100 = 1.660;
	
	
	//Quantili per Normale
	//double quant_95 = 1.96;
	//double quant_99 = 2.58;
	
	public static ValueCalculator[] analizeMatrixData(double[][] matrix) {
		if(matrix.length<=0) {
			return null;
		}
		
		
		int column = matrix[0].length;
		
		ValueCalculator[] ret = new ValueCalculator[column];
		
		for(int i = 0; i < column;i++) {
			ret[i] = new ValueCalculator();
			ret[i].analizzaColonna(matrix, i);
		}
		return ret;
	}
	
	public void analizzaColonna(double[][] matrix, int clmIndex) {
		n = matrix.length;
		double temp = 0.0;
		for(int i = 0; i<n; i++) {
			temp +=matrix[i][clmIndex];
		}
		mean = temp/n;
		
		temp = 0.0;
		
		for(int i = 0; i <n;i++) {
			temp += Math.pow((matrix[i][clmIndex] - mean),2);
		}
		
		s = Math.sqrt(temp/(n-1));
		V = temp/n;
	}
	
	public String getInterval() {
		double quant;
		if(n<=17) {
			quant = n_15;
		}else if(n<=25) {
			quant = n_20;
		}else if(n<=35) {
			quant = n_30;
		}else if(n<=45) {
			quant = n_40;
		}else if(n<=55) {
			quant = n_50;
		}else if(n<=65) {
			quant = n_60;
		}else {
			quant = n_100;
		}
		double temp = mean-(quant*(s/Math.sqrt(n)));
		double temp2 = mean+(quant*(s/Math.sqrt(n)));
		
		return String.format("[%.5f , %.5f]",temp,temp2);
	}
}

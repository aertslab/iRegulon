package resultsview.renderers;

import java.text.DecimalFormat;

public class MyFormat extends DecimalFormat{

	public MyFormat(){
		super();
	}
	
	public MyFormat(String pattern){
		super(pattern);
	}
	
	public String formatFloat (float number){
		String aNumber = "" + number;
		if(aNumber.contains(",")){
			aNumber = aNumber.replace(',', '.');
		}
		double dnumber = Double.parseDouble(aNumber);
		aNumber = super.format(dnumber);
		if(aNumber.contains(",")){
			aNumber = aNumber.replace(',', '.');
		}
		return aNumber;
	}
	
	
	
}

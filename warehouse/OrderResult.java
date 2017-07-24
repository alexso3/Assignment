package warehouse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* 
* This represents the result after processing of a product order.
* It is also used by the main program class to print out the outlines when the inventory becomes all zeros
* 
* @author  Alex So
* @version 1.00 2017-07-23
* 
*/
public class OrderResult {

	public enum Result {SUCCESS, FAIL};
	protected Result result;
	protected String errorMessage;
	protected Map<String, List<Integer>> allocated;
	protected Map<String, List<Integer>> backordered;
	
	public OrderResult() {
		allocated = new HashMap<>();
		backordered = new HashMap<>();
	}

	public Map<String, List<Integer>> getAllocated() {
		return allocated;
	}

	public void setAllocated(Map<String, List<Integer>> allocated) {
		this.allocated = allocated;
	}

	public Map<String, List<Integer>> getBackordered() {
		return backordered;
	}

	public void setBackordered(Map<String, List<Integer>> backordered) {
		this.backordered = backordered;
	}

	public Result getResult() {
		return result;
	}

	protected void setResult(Result result) {
		this.result = result;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	protected void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
}

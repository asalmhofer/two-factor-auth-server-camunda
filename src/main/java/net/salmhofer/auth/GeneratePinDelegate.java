package net.salmhofer.auth;

import java.util.HashMap;
import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class GeneratePinDelegate implements JavaDelegate{
	
	private final static Logger logger = Logger.getLogger("LOAN-REQUESTS");
	
	public void execute(DelegateExecution execution) throws Exception {
		
		Long generatedPin = System.currentTimeMillis() % 10000;
		execution.setVariable("generatedPin", generatedPin.intValue());
	}

}

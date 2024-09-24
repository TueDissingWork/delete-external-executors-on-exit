package io.jenkins.plugins.queue;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import hudson.model.Node;
import jenkins.model.Jenkins;

public class HandleExecutorShutdown {
	
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	private static final Logger LOGGER = Logger.getLogger(HandleExecutorShutdown.class.getName());
	
	public Future<Boolean> shutdownExternalExecuters() {        
        return executorService.submit(() -> {
        	Jenkins jenkinsInstance = Jenkins.getInstanceOrNull();
    		List<Node> nodes = jenkinsInstance.getNodes();
    		
    		for (Node node : nodes) {
    			// wait for all jobs to drain, then terminate the each executor (not of type Kubernetes pod)
    			LOGGER.fine("Waiting for node '" + node.getDisplayName() + "' to drain");
    			
    			while (!node.toComputer().isLaunchSupported() && !node.toComputer().isIdle()) {
    				try {
    					Thread.sleep(10000); // 10s
    				} catch (InterruptedException interruptedException) {
    					// do nothing
    				}
    			}
    			node.toComputer().doDoDelete();
    		}
    		return Boolean.TRUE;
        });
    }

}

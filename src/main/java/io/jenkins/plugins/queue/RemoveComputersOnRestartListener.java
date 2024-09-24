package io.jenkins.plugins.queue;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import hudson.Extension;
import hudson.model.Node;
import hudson.model.RestartListener;
import jenkins.model.Jenkins;

@Extension
public class RemoveComputersOnRestartListener extends RestartListener {

	private AtomicBoolean isInitialized = new AtomicBoolean(false);
	private static final Logger LOGGER = Logger.getLogger(ControlBuildQueueDispatcher.class.getName());
	
	Future<Boolean> executorsShutdownComplete;
	
	@Override
	public boolean isReadyToRestart() throws IOException, InterruptedException {
		LOGGER.fine("Ready to restart?");
		return isAllComputersDrained();
	}

	private Boolean isAllComputersDrained() {
		Boolean allShutdown = Boolean.FALSE;
		
		if (isInitialized.compareAndSet(false, true)) {
			executorsShutdownComplete = new HandleExecutorShutdown().shutdownExternalExecuters();
		} else if (executorsShutdownComplete.isDone()) {
			try {
				allShutdown = executorsShutdownComplete.get();
			} catch (InterruptedException | ExecutionException ie) {
				allShutdown = Boolean.FALSE;
			}
		} else if (isExecutorsBusy()) {
			allShutdown = Boolean.FALSE;
		}
		
		return allShutdown;
	}
	
	private boolean isExecutorsBusy() {
		Jenkins jenkinsInstance = Jenkins.getInstanceOrNull();
		List<Node> nodes = jenkinsInstance.getNodes();
		
		boolean isBusy = false;
		
		for (Node node : nodes) {
			if (!node.toComputer().isLaunchSupported() && !node.toComputer().isIdle()) {
				isBusy = true;
				break;
			}
		}
		return isBusy;
	}
}

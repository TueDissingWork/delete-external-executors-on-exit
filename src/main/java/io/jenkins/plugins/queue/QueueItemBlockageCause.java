package io.jenkins.plugins.queue;

import hudson.model.queue.CauseOfBlockage;

public class QueueItemBlockageCause extends CauseOfBlockage {
	
	private static final String DEFAULT_BLOCKAGE_MSG = "Reconcile loop in progress.";

	@Override
	public String getShortDescription() {
		return DEFAULT_BLOCKAGE_MSG;
	}

	
}

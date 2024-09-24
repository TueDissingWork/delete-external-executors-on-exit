package io.jenkins.plugins.queue;

import java.util.logging.Logger;

import javax.annotation.CheckForNull;

import hudson.Extension;
import hudson.model.Queue;
import hudson.model.queue.QueueTaskDispatcher;

@Extension
public class ControlBuildQueueDispatcher extends QueueTaskDispatcher {
	
	private Boolean blockQueue = Boolean.FALSE;
	private static final Logger LOGGER = Logger.getLogger(ControlBuildQueueDispatcher.class.getName());

	public ControlBuildQueueDispatcher() {}
	
	public void blockQueue() {
		this.blockQueue = Boolean.TRUE;
	}
	
	public void unBlockQueue() {
		this.blockQueue = Boolean.FALSE;
	}
	
	@Override
	public @CheckForNull QueueItemBlockageCause canRun(Queue.Item item) {
		LOGGER.info("Check to see if we can run");
		if (this.blockQueue) {
			QueueItemBlockageCause blocked = new QueueItemBlockageCause();
			LOGGER.info("returning blocked reason");
			return blocked;
		}
		return null;
	}
	
}

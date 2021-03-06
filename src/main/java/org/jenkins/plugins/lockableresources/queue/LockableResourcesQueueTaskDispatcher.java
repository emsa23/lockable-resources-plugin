/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 2013, 6WIND S.A. All rights reserved.                 *
 *                                                                     *
 * This file is part of the Jenkins Lockable Resources Plugin and is   *
 * published under the MIT license.                                    *
 *                                                                     *
 * See the "LICENSE.txt" file for more information.                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package org.jenkins.plugins.lockableresources.queue;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Node;
import hudson.model.Queue.BuildableItem;
import hudson.model.queue.QueueTaskDispatcher;
import hudson.model.queue.CauseOfBlockage;

import java.util.List;
import java.util.logging.Logger;

import org.jenkins.plugins.lockableresources.LockableResourcesManager;
import org.jenkins.plugins.lockableresources.LockableResource;

@Extension
public class LockableResourcesQueueTaskDispatcher extends QueueTaskDispatcher {

	static final Logger LOGGER = Logger
			.getLogger(LockableResourcesQueueTaskDispatcher.class.getName());

	@Override
	public CauseOfBlockage canTake(Node node, BuildableItem item) {
		AbstractProject<?, ?> project = Utils.getProject(item);
		if (project == null)
			return null;

		List<LockableResource> required = Utils.requiredResources(project);
		if (required == null || required.isEmpty())
			return null;

		LOGGER.finest(project.getName() + " trying to reserve resources "
				+ required);
		if (LockableResourcesManager.get().queue(required, item.id)) {
			LOGGER.finest(project.getName() + " reserved resources " + required);
			return null;
		} else {
			LOGGER.finest(project.getName() + " waiting for resources "
					+ required);
			return new BecauseResourcesLocked(required);
		}
	}

	public static class BecauseResourcesLocked extends CauseOfBlockage {

		private final List<LockableResource> resources;

		public BecauseResourcesLocked(List<LockableResource> resources) {
			this.resources = resources;
		}

		public List<LockableResource> getResources() {
			return resources;
		}

		@Override
		public String getShortDescription() {
			return "Waiting for resources " + resources.toString();
		}
	}

}

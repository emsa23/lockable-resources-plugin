/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 2013, 6WIND S.A. All rights reserved.                 *
 *                                                                     *
 * This file is part of the Jenkins Lockable Resources Plugin and is   *
 * published under the MIT license.                                    *
 *                                                                     *
 * See the "LICENSE.txt" file for more information.                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package org.jenkins.plugins.lockableresources.queue;

import hudson.matrix.MatrixConfiguration;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Queue;

import java.util.ArrayList;
import java.util.List;

import org.jenkins.plugins.lockableresources.LockableResourcesManager;
import org.jenkins.plugins.lockableresources.RequiredResourcesProperty;
import org.jenkins.plugins.lockableresources.LockableResource;

public class Utils {

	public static AbstractProject<?, ?> getProject(Queue.Item item) {
		if (item.task instanceof AbstractProject) {
			AbstractProject<?, ?> proj = (AbstractProject<?, ?>) item.task;
			if (proj instanceof MatrixConfiguration) {
				proj = (AbstractProject<?, ?>) ((MatrixConfiguration) proj)
						.getParent();
			}
			return proj;
		}
		return null;
	}

	public static AbstractProject<?, ?> getProject(AbstractBuild<?, ?> build) {
		Object p = build.getParent();
		if (p instanceof AbstractProject) {
			AbstractProject<?, ?> proj = (AbstractProject<?, ?>) p;
			if (proj instanceof MatrixConfiguration) {
				proj = (AbstractProject<?, ?>) ((MatrixConfiguration) proj)
						.getParent();
			}
			return proj;
		}
		return null;
	}

	public static List<LockableResource> requiredResources(
			AbstractProject<?, ?> project) {
		RequiredResourcesProperty property = project
				.getProperty(RequiredResourcesProperty.class);
		if (property != null) {
			List<LockableResource> required = new ArrayList<LockableResource>();
			for (String name : property.getResources()) {
				LockableResource r = LockableResourcesManager.get().fromName(
						name);
				if (r != null)
					required.add(r);
			}
			return required;
		}
		return null;
	}
}

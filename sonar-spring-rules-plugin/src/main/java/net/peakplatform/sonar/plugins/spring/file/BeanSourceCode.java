/*
 * Sonar Spring Rules Plugin
 * Copyright (C) 2011 LeanDo Technologies
 * mis@leandotech.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package net.peakplatform.sonar.plugins.spring.file;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Violation;
import org.springframework.beans.factory.config.BeanDefinition;

public class BeanSourceCode {

	private BeanDefinition beanDefinition;

	private final Resource<?> resource;
	private final List<Violation> violations = new ArrayList<Violation>();

	public BeanSourceCode(Resource<?> resource, BeanDefinition beanDefinition) {
		this.resource = resource;
		this.beanDefinition = beanDefinition;
	}

	public BeanDefinition getBeanDefinition() {
		return beanDefinition;
	}

	public void setBeanDefinition(BeanDefinition beanDefinition) {
		this.beanDefinition = beanDefinition;
	}

	public void addViolation(Violation violation) {
		this.violations.add(violation);
	}

	public Resource<?> getResource() {
		return resource;
	}

	public List<Violation> getViolations() {
		return violations;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BeanSourceCode [beanDefinition=");
		builder.append(beanDefinition);
		builder.append(", resource=");
		builder.append(resource);
		builder.append(", violations=");
		builder.append(violations);
		builder.append("]");
		return builder.toString();
	}
}

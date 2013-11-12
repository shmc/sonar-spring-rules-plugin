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

package net.peakplatform.sonar.plugins.spring.checks;

import net.peakplatform.sonar.plugins.spring.file.BeanSourceCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.Violation;

public abstract class BeanCheck {
	
	private Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private Rule rule;
	private BeanSourceCode beanSourceCode;
    
    public void setRule(final Rule rule) {
        this.rule = rule;
    }

    protected BeanSourceCode getBeanSourceCode() {
      return this.beanSourceCode;
    }

    protected void setBeanSourceCode(BeanSourceCode beanSourceCode) {
      this.beanSourceCode = beanSourceCode;
    }

	public abstract void validate(final BeanSourceCode beanSourceCode, final Project project);

	protected final void createViolation(Integer linePosition, String message) {
		Violation violation = Violation.create(rule, this.beanSourceCode.getResource());
		violation.setMessage(message);
		violation.setLineId(linePosition);
    	LOGGER.info("BeanCheck createViolation creating violation for: " + this.beanSourceCode.toString());
		this.beanSourceCode.addViolation(violation);
	}
}

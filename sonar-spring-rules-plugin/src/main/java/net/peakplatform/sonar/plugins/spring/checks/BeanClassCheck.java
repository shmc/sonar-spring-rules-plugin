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

import javassist.Modifier;
import net.peakplatform.sonar.plugins.spring.file.BeanSourceCode;
import net.peakplatform.sonar.plugins.spring.file.SpringProjectFileSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.resources.Resource;
import org.sonar.check.Cardinality;
import org.sonar.check.Priority;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.util.StringUtils;

@org.sonar.check.Rule(key = "BeanClassCheck", name = "Bean Class Check", description = "Description here...", priority = Priority.MAJOR,
    cardinality = Cardinality.SINGLE)
public class BeanClassCheck extends BeanCheck {
	
	private Logger LOGGER = LoggerFactory.getLogger(getClass());

	/** New placeholder string for Spring 3 EL support */
	public static final String EL_PLACEHOLDER_PREFIX = "#{";
	public static final String PLACEHOLDER_PREFIX = "${";
	public static final String PLACEHOLDER_SUFFIX = "}";

	public static final String CLASS_NOT_FOUND = "The defined bean class does not exist.";
	public static final String CLASS_NOT_CLASS = "The defined bean is not a class.";

    public void validate(final BeanSourceCode beanSourceCode, final Project project) {
    	LOGGER.info("BeanClassCheck validate start");
    	try {
    		setBeanSourceCode(beanSourceCode);
    		Resource<?> xmlFile = beanSourceCode.getResource();
    		String qualifier = xmlFile.getQualifier();
	    	LOGGER.info("BeanClassCheck validate qualifier: " + qualifier);
	    	BeanDefinition beanDefinition = beanSourceCode.getBeanDefinition();
	        String className = beanSourceCode.getBeanDefinition().getBeanClassName();
	    	LOGGER.info("BeanClassCheck validate className: " + className);

	    	SpringProjectFileSystem fileSystem = new SpringProjectFileSystem(project);
	
			// Validate bean class and constructor arguments - skip child beans and
			// class names with placeholders
			if (className != null && !hasPlaceHolder(className)) {
				String shortClassName = className.substring(className.lastIndexOf('.')+1) + ".java";
		    	LOGGER.info("BeanClassCheck validate shortClassName: " + shortClassName);
		    	org.sonar.api.resources.File classResource = fileSystem.findClass(shortClassName);
		    	LOGGER.info("BeanClassCheck validate classResource: " + classResource);
				int linePosition = 2;
				if (classResource != null) {	
			    	LOGGER.info("BeanClassCheck validate classResource.getName(): " + classResource.getName());
			    	LOGGER.info("BeanClassCheck validate classResource.getLongName(): " + classResource.getLongName());
			    	LOGGER.info("BeanClassCheck validate classResource.getQualifier(): " + classResource.getQualifier());
			    	// TODO detect interface
			    	//LOGGER.info("BeanClassCheck validate CLASS_NOT_CLASS caught for: " + className);
			    	//createViolation(linePosition+5, CLASS_NOT_CLASS);
				} else {
			    	LOGGER.info("BeanClassCheck validate CLASS_NOT_FOUND caught for: " + className);
			    	createViolation(linePosition, CLASS_NOT_FOUND);
				}
			}
    	} catch (Exception e) {
    		LOGGER.error("BeanClassCheck validate exception", e);
    	}
    	LOGGER.info("BeanClassCheck validate exit");
    }
    
	private boolean hasPlaceHolder(String text) {
		if (text == null || !StringUtils.hasText(text)) {
			return false;
		}
		int pos = text.indexOf(PLACEHOLDER_PREFIX);
		int elPos = text.indexOf(EL_PLACEHOLDER_PREFIX);
		return ((pos != -1 || elPos != -1) && text.indexOf(PLACEHOLDER_SUFFIX, pos) != -1);
	}
}

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

package net.peakplatform.sonar.plugins.spring;

import java.io.File;
import java.util.List;

import net.peakplatform.sonar.plugins.spring.checks.BeanCheck;
import net.peakplatform.sonar.plugins.spring.file.BeanSourceCode;
import net.peakplatform.sonar.plugins.spring.file.SpringProjectFileSystem;
import net.peakplatform.sonar.plugins.spring.language.Spring;
import net.peakplatform.sonar.plugins.spring.rules.SpringRulesRepository;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.Violation;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.FileSystemResource;

public class SpringSensor implements Sensor {
	
	private Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final RulesProfile profile;
    private final ProjectFileSystem projectFileSystem;

    private final GenericApplicationContext ctx = new GenericApplicationContext();
    private final XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(ctx);
    private List<BeanCheck> springChecks;

    public SpringSensor(final RulesProfile profile, final ProjectFileSystem projectFileSystem) {
        this.profile = profile;
        this.projectFileSystem = projectFileSystem;
    }

    public boolean shouldExecuteOnProject(final Project project) {
        return StringUtils.equals(Spring.KEY, project.getLanguageKey()) && hasActiveRules(this.profile);
    }

    public void analyse(final Project project, final SensorContext sensorContext) {
    	
		LOGGER.info("SpringSensor analyse start");
		springChecks = SpringRulesRepository.createChecks(profile);
		LOGGER.info("SpringSensor analyse checks.size(): " + springChecks.size());
		for (InputFile inputfile : SpringPlugin.getFiles(project)) {

			try {
				org.sonar.api.resources.File resource = SpringProjectFileSystem.fromIOFile(inputfile, project);
				if(resource != null && resource.getName().endsWith(".xml")) {
					checkXmlFile(project, sensorContext, resource);
				} else if(resource != null && resource.getName().endsWith(".java")) {
					checkJavaFile();
				}
		        
	    	} catch (Exception e) {
	    		LOGGER.error("SpringSensor analyse exception", e);
	    	}
    	}
    	LOGGER.info("SpringSensor analyse end");
    }

    private void checkXmlFile(final Project project, final SensorContext sensorContext, final org.sonar.api.resources.File beanFile) {
		String sourceDirectory = (String) project.getProperty(SpringPlugin.SOURCE_DIRECTORY);
		LOGGER.info("SpringSensor analyse sourceDirectory: " + sourceDirectory);
    	File springBeanDefinition = this.projectFileSystem.resolvePath(sourceDirectory + "/" + beanFile.getLongName());
	    
    	org.springframework.core.io.Resource beanDefinitionsXML = new FileSystemResource(springBeanDefinition.getAbsolutePath());
        int beanCount = beanDefinitionReader.loadBeanDefinitions(beanDefinitionsXML);
    	LOGGER.info("BeanClassCheck validate " + beanCount + " beans loaded.");
        BeanDefinitionRegistry beanDefinitionRegistry = beanDefinitionReader.getRegistry();
        String[] beanDefinitionNames = beanDefinitionRegistry.getBeanDefinitionNames();
        for(String beanDefinitionName : beanDefinitionNames) {
	    	LOGGER.info("BeanClassCheck validate beanDefinitionName: " + beanDefinitionName);
        	BeanDefinition beanDefinition = beanDefinitionRegistry.getBeanDefinition(beanDefinitionName);
        	BeanSourceCode beanSourceCode = new BeanSourceCode(beanFile, beanDefinition);
        	
	        for(BeanCheck beanCheck : springChecks) {
	        	LOGGER.info("SpringSensor analyse BeanCheck: " + beanCheck.getClass().getName());
	        	beanCheck.validate(beanSourceCode, project);
			}

        	LOGGER.info("SpringSensor analyse beanSourceCode.getViolations().size(): " + beanSourceCode.getViolations().size());
			for (Violation violation : beanSourceCode.getViolations()) {
	        	LOGGER.info("SpringSensor analyse violation.getMessage(): " + violation.getMessage());
				sensorContext.saveViolation(violation);
			}
		}        
    }

    private void checkJavaFile() {
    	
    }
        
    private boolean hasActiveRules(final RulesProfile profile) {
        for (ActiveRule activeRule : profile.getActiveRules()) {
            if (SpringRulesRepository.REPOSITORY_KEY.equals(activeRule.getRepositoryKey())) {
                return true;
            }
        }
        return false;
    }
}

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

import java.util.Arrays;
import java.util.List;

import net.peakplatform.sonar.plugins.spring.file.SpringProjectFileSystem;
import net.peakplatform.sonar.plugins.spring.file.SpringSourceImporter;
import net.peakplatform.sonar.plugins.spring.language.Spring;
import net.peakplatform.sonar.plugins.spring.rules.DefaultSpringProfile;
import net.peakplatform.sonar.plugins.spring.rules.SpringRulesRepository;

import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.SonarPlugin;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Project;

@Properties({ 
	@Property(
		key = SpringPlugin.FILE_PATTERNS, 
		name = "File patterns", 
		description = "List of file patterns that will be scanned.", 
		defaultValue = "java,xml", 
		global = true, 
		project = true
	),
	@Property(
		key = SpringPlugin.SOURCE_DIRECTORY, 
		name = "Source directory", 
		description = "Source directory that will be scanned.", 
		defaultValue = "src/main", 
		global = false, 
		project = true
	),
	@Property(
		key = SpringPlugin.INCLUDE_FILE_FILTER, 
		name = "Files to include", 
		description = "List of file inclusion filters, separated by komma.", 
		defaultValue = "", 
		global = false, 
		project = true
	) 
})
public class SpringPlugin extends SonarPlugin {

    public static final String FILE_PATTERNS = "sonar.spring.filePatterns";
    public static final String SOURCE_DIRECTORY = "sonar.spring.sourceDirectory";
    public static final String INCLUDE_FILE_FILTER = "sonar.spring.includeFileFilter";

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List getExtensions() {
        return Arrays.asList(
        		Spring.class,
        		SpringSourceImporter.class,
        		DefaultSpringProfile.class,
        		SpringRulesRepository.class,
        		SpringSensor.class
        		);
    }

    public static List<InputFile> getFiles(Project project) {
    	SpringProjectFileSystem fileSystem = new SpringProjectFileSystem(project);
      return fileSystem.getFiles();
    }

}

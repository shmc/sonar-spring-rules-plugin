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

import java.util.List;

import net.peakplatform.sonar.plugins.spring.SpringPlugin;
import net.peakplatform.sonar.plugins.spring.language.Spring;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.batch.AbstractSourceImporter;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Project;

import com.google.common.collect.Lists;

public class SpringSourceImporter extends AbstractSourceImporter {

	public SpringSourceImporter(final Spring spring) {
		super(spring);
	}

	/**
	 * Conversion from InputFile to File. Allows to provide backward
	 * compatibility.
	 */
	private static List<java.io.File> toFiles(List<InputFile> files) {
		List<java.io.File> result = Lists.newArrayList();
		for (InputFile file : files) {
			result.add(file.getFile());
		}
		return result;
	}

    @Override
    public void analyse(Project project, SensorContext context) {
      parseDirs(context, toFiles(SpringPlugin.getFiles(project)), SpringProjectFileSystem.getSourceDirs(project), false, project.getFileSystem()
          .getSourceCharset());
    }

    @Override
    public boolean shouldExecuteOnProject(Project project) {
      return isEnabled(project) && StringUtils.equals(Spring.KEY, project.getLanguageKey());
    }

    @Override
    public String toString() {
      return getClass().getSimpleName();
    }
}

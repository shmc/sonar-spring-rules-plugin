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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.peakplatform.sonar.plugins.spring.SpringPlugin;
import net.peakplatform.sonar.plugins.spring.language.Spring;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.resources.DefaultProjectFileSystem;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.WildcardPattern;

import com.google.common.collect.Lists;

public class SpringProjectFileSystem {
	
	private Logger LOGGER = LoggerFactory.getLogger(getClass());

	private final Project project;
	private List<IOFileFilter> filters = Lists.newArrayList();

	public SpringProjectFileSystem(Project project) {
		this.project = project;
	}

	public static org.sonar.api.resources.File fromIOFile(InputFile inputfile, Project project) {
		return org.sonar.api.resources.File.fromIOFile(inputfile.getFile(), getSourceDirs(project));
	}

	public org.sonar.api.resources.File findClass(String className) {
		if (getSourceDirs() == null) {
			return null;
		}
    	LOGGER.info("SpringProjectFileSystem findClass getSourceDirs().size(): " + getSourceDirs().size());
		for (File dir : getSourceDirs()) {
			if (dir.exists()) {
				// create DefaultInputFile for each file.
		    	LOGGER.info("SpringProjectFileSystem findClass dir.getName(): " + dir.getName());
		    	LOGGER.info("SpringProjectFileSystem findClass dir.getAbsolutePath(): " + dir.getAbsolutePath());
				List<File> files = (List<File>) FileUtils.listFiles(dir, null, true);
		    	LOGGER.info("SpringProjectFileSystem findClass files.size(): " + files.size());
				for (File file : files) {
			    	LOGGER.info("SpringProjectFileSystem findClass file.getName(): " + file.getName());
					if(file.getName().equals(className)) {
						String relativePath = DefaultProjectFileSystem.getRelativePath(file, dir);
				    	LOGGER.info("SpringProjectFileSystem findClass relativePath: " + relativePath);
						return SpringProjectFileSystem.fromIOFile(new DefaultInputFile(dir, relativePath), project);
					}
				}
			}	
		}
		return null;
	}
		
	public List<InputFile> getFiles() {
		List<InputFile> result = Lists.newArrayList();
		if (getSourceDirs() == null) {
			return result;
		}

		IOFileFilter suffixFilter = getFileSuffixFilter();
		WildcardPattern[] exclusionPatterns = getExclusionPatterns(true);
		IOFileFilter visibleFileFilter = HiddenFileFilter.VISIBLE;

		for (File dir : getSourceDirs()) {
			if (dir.exists()) {
				// exclusion filter
				IOFileFilter exclusionFilter = new ExclusionFilter(dir,exclusionPatterns);
				// visible filter
				List<IOFileFilter> fileFilters = Lists.newArrayList(visibleFileFilter, suffixFilter, exclusionFilter);
				// inclusion filter
				String inclusionPattern = (String) project.getProperty(SpringPlugin.INCLUDE_FILE_FILTER);
				if (inclusionPattern != null) {
					fileFilters.add(new InclusionFilter(dir, inclusionPattern));
				}
				fileFilters.addAll(this.filters);

				// create DefaultInputFile for each file.
				List<File> files = (List<File>) FileUtils.listFiles(dir, new AndFileFilter(fileFilters), HiddenFileFilter.VISIBLE);
				for (File file : files) {
					String relativePath = DefaultProjectFileSystem.getRelativePath(file, dir);
					result.add(new DefaultInputFile(dir, relativePath));
				}
			}
		}
		return result;
	}

	public static List<File> getSourceDirs(Project project) {
		String sourceDir = (String) project.getProperty(SpringPlugin.SOURCE_DIRECTORY);
		if (sourceDir != null) {
			List<File> sourceDirs = new ArrayList<File>();
			sourceDirs.add(project.getFileSystem().resolvePath(sourceDir));
			return sourceDirs;
		} else {
			return project.getFileSystem().getSourceDirs();
		}
	}

	private List<File> getSourceDirs() {
		return getSourceDirs(project);
	}

	private String[] getFileSuffixes(Project project) {
		List<?> extensions = project.getConfiguration().getList(SpringPlugin.FILE_PATTERNS);

		if (extensions != null && !extensions.isEmpty() && !StringUtils.isEmpty((String) extensions.get(0))) {
			String[] fileSuffixes = new String[extensions.size()];
			for (int i = 0; i < extensions.size(); i++) {
				fileSuffixes[i] = extensions.get(i).toString().trim();
			}
			return fileSuffixes;
		} else {
			return Spring.INSTANCE.getFileSuffixes();
		}
	}

	private IOFileFilter getFileSuffixFilter() {
		IOFileFilter suffixFilter = FileFilterUtils.trueFileFilter();

		List<String> suffixes = Arrays.asList(getFileSuffixes(project));
		if (!suffixes.isEmpty()) {
			suffixFilter = new SuffixFileFilter(suffixes);
		}

		return suffixFilter;
	}

	private WildcardPattern[] getExclusionPatterns(boolean applyExclusionPatterns) {
		WildcardPattern[] exclusionPatterns;
		if (applyExclusionPatterns) {
			exclusionPatterns = WildcardPattern.create(project.getExclusionPatterns());
		} else {
			exclusionPatterns = new WildcardPattern[0];
		}
		return exclusionPatterns;
	}

	private static final class DefaultInputFile implements InputFile {

		private File basedir;
		private String relativePath;

		DefaultInputFile(File basedir, String relativePath) {
			this.basedir = basedir;
			this.relativePath = relativePath;
		}

		public File getFile() {
			return new File(basedir, relativePath);
		}

		public File getFileBaseDir() {
			return basedir;
		}

		public String getRelativePath() {
			return relativePath;
		}
	}

	private static class ExclusionFilter implements IOFileFilter {

		private File sourceDir;
		private WildcardPattern[] patterns;

		ExclusionFilter(File sourceDir, WildcardPattern[] patterns) {
			this.sourceDir = sourceDir;
			this.patterns = patterns;
		}

		public boolean accept(File file) {
			String relativePath = DefaultProjectFileSystem.getRelativePath(file, sourceDir);
			if (relativePath == null) {
				return false;
			}
			for (WildcardPattern pattern : patterns) {
				if (pattern.match(relativePath)) {
					return false;
				}
			}
			return true;
		}

		public boolean accept(File file, String name) {
			return accept(file);
		}
	}

	private static class InclusionFilter implements IOFileFilter {

		private String inclusionPattern;
		private File sourceDir;

		public InclusionFilter(File sourceDir, String inclusionPattern) {
			this.sourceDir = sourceDir;
			this.inclusionPattern = inclusionPattern;
		}

		public boolean accept(File file) {
			String relativePath = DefaultProjectFileSystem.getRelativePath(file, sourceDir);
			if (relativePath == null) {
				return false;
			}

			// one of the inclusionpatterns must match.
			for (String filter : inclusionPattern.split(",")) {
				WildcardPattern matcher = WildcardPattern.create(filter);
				if (matcher.match(relativePath)) {
					return true;
				}
			}
			return false;
		}

		public boolean accept(File file, String name) {
			return accept(file);
		}
	}
}

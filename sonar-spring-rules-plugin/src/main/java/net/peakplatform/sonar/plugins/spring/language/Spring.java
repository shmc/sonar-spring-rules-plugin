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

package net.peakplatform.sonar.plugins.spring.language;

import org.sonar.api.resources.AbstractLanguage;

public class Spring extends AbstractLanguage {

    /** All the valid spring files suffixes. */
    private static final String[] DEFAULT_SUFFIXES = { "xml", "java", "jav" };

    /** A spring instance. */
    public static final Spring INSTANCE = new Spring();

    /** The spring language key. */
    public static final String KEY = "sprng";

    /** The spring language name */
    private static final String SPRING_LANGUAGE_NAME = "Sprng";

    /**
     * Default constructor.
     */
    public Spring() {
        super(KEY, SPRING_LANGUAGE_NAME);
    }

    /**
     * Gets the file suffixes.
     * 
     * @return the file suffixes
     * @see org.sonar.api.resources.Language#getFileSuffixes()
     */
    public String[] getFileSuffixes() {
        return DEFAULT_SUFFIXES;
    }

}

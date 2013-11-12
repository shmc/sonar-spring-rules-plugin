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

package net.peakplatform.sonar.plugins.spring.rules;

import java.util.Arrays;
import java.util.List;

import net.peakplatform.sonar.plugins.spring.checks.BeanClassCheck;

final class CheckClasses {

    @SuppressWarnings("rawtypes")
    private static final Class[] CLASSES = new Class[] {
    // TODO: add checks
    	BeanClassCheck.class
    };

    /**
     * Gets the list of Spring checks.
     */
    @SuppressWarnings("rawtypes")
    public static List<Class> getCheckClasses() {
        return Arrays.asList(CLASSES);
    }

    private CheckClasses() {

    }
}

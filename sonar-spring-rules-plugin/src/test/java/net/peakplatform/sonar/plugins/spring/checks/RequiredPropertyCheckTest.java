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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.DefaultLogger;
import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.Configuration;

public class RequiredPropertyCheckTest extends BeanValidatorBaseTestCase {

    protected final ByteArrayOutputStream mBAOS = new ByteArrayOutputStream();
    protected final PrintStream mStream = new PrintStream(this.mBAOS);

    @Test
    public void testError() throws IOException, Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(RequiredPropertyCheck.class);
        final String[] expected = { "29:5: Bean property setter does not have @Required annotation" };
        verify(checkConfig, getPath("net/peakplatform/sonar/plugins/spring/checks/InputSimpleRequiredPropertyError.java"), expected);
    }

    @Test
    public void testNoError() throws IOException, Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(RequiredPropertyCheck.class);
        final String[] expected = {};
        verify(checkConfig, getPath("net/peakplatform/sonar/plugins/spring/checks/InputSimpleRequiredPropertyNoError.java"), expected);
    }
    
    protected static DefaultConfiguration createCheckConfig(final Class<?> aClazz) {
        final DefaultConfiguration checkConfig = new DefaultConfiguration(aClazz.getName());
        return checkConfig;
    }
    
    protected void verify(final Configuration aConfig, final String aFileName, final String[] aExpected) throws Exception {
        verify(createChecker(aConfig), aFileName, aFileName, aExpected);
    }
    
    protected void verify(final Checker aC, final String aProcessedFilename, final String aMessageFileName, final String[] aExpected) throws Exception {
        verify(aC, new File[] { new File(aProcessedFilename) }, aMessageFileName, aExpected);
    }

    protected void verify(final Checker aC, final File[] aProcessedFiles, final String aMessageFileName, final String[] aExpected) throws Exception {
        this.mStream.flush();
        final List<File> theFiles = Lists.newArrayList();
        Collections.addAll(theFiles, aProcessedFiles);
        final int errs = aC.process(theFiles);

        // process each of the lines
        final ByteArrayInputStream bais = new ByteArrayInputStream(this.mBAOS.toByteArray());
        final LineNumberReader lnr = new LineNumberReader(new InputStreamReader(bais));

        for (int i = 0; i < aExpected.length; i++) {
            final String expected = aMessageFileName + ":" + aExpected[i];
            final String actual = lnr.readLine();
            Assert.assertEquals("error message " + i, expected, actual);
        }

        Assert.assertEquals("unexpected output: " + lnr.readLine(), aExpected.length, errs);
        aC.destroy();
    }

    protected Checker createChecker(final Configuration aCheckConfig) throws Exception {
        final DefaultConfiguration dc = createCheckerConfig(aCheckConfig);
        final Checker c = new Checker();
        // make sure the tests always run with english error messages
        // so the tests don't fail in supported locales like german
        final Locale locale = Locale.ENGLISH;
        c.setLocaleCountry(locale.getCountry());
        c.setLocaleLanguage(locale.getLanguage());
        c.setModuleClassLoader(Thread.currentThread().getContextClassLoader());
        c.configure(dc);
        c.addListener(new BriefLogger(this.mStream));
        return c;
    }

    protected DefaultConfiguration createCheckerConfig(final Configuration aConfig) {
        final DefaultConfiguration dc = new DefaultConfiguration("configuration");
        final DefaultConfiguration twConf = createCheckConfig(TreeWalker.class);
        // make sure that the tests always run with this charset
        dc.addAttribute("charset", "iso-8859-1");
        dc.addChild(twConf);
        twConf.addChild(aConfig);
        return dc;
    }
    
    protected static class BriefLogger extends DefaultLogger {
        public BriefLogger(final OutputStream out) {
            super(out, true);
        }

        @Override
        public void auditStarted(final AuditEvent evt) {
        }

        @Override
        public void fileFinished(final AuditEvent evt) {
        }

        @Override
        public void fileStarted(final AuditEvent evt) {
        }
    }
}

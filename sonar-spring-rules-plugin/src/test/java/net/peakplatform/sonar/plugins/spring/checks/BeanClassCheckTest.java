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

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class BeanClassCheckTest extends BeanValidatorBaseTestCase {

    //private BeanClassCheck beanClassCheck = new BeanClassCheck();
    //private BeanDefinitionReader beanDefinitionReader;

    @Before
    public void setUp() throws IOException {
    	/*GenericApplicationContext ctx = new GenericApplicationContext();
        beanDefinitionReader = new XmlBeanDefinitionReader(ctx);
        beanDefinitionReader.loadBeanDefinitions("classpath:spring/spring-test-infrastructure.xml");*/
    }

    @Test
    public void testInterfaceError() throws IOException, Exception {
    	//List<Violation> violations = beanClassCheck.validate(beanDefinitionReader.getRegistry().getBeanDefinition("testBeanInterfaceError"), new DefaultSensorContext());
    	//Assert.assertEquals(1, violations.size());
    	//Assert.assertEquals(BeanClassCheck.CLASS_NOT_CLASS, violations.get(0).getMessage());
    }
    
    @Test
    public void testClassNotFoundError() throws IOException, Exception {
    	//List<Violation> violations = beanClassCheck.validate(beanDefinitionReader.getRegistry().getBeanDefinition("testBeanClassNotFound"), new DefaultSensorContext());
    	//Assert.assertEquals(1, violations.size());
    	//Assert.assertEquals(BeanClassCheck.CLASS_NOT_FOUND, violations.get(0).getMessage());
    }

    @Test
    public void testNoError() throws IOException, Exception {
    	//List<Violation> violations = beanClassCheck.validate(beanDefinitionReader.getRegistry().getBeanDefinition("testBeanOK"), new DefaultSensorContext());
    	//Assert.assertNull(violations);
    }
}

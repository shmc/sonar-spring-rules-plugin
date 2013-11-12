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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.peakplatform.sonar.plugins.spring.checks.BeanCheck;
import net.peakplatform.sonar.plugins.spring.language.Spring;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.ActiveRuleParam;
import org.sonar.api.rules.AnnotationRuleParser;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleRepository;
import org.sonar.api.utils.AnnotationUtils;
import org.sonar.api.utils.SonarException;
import org.sonar.check.Cardinality;

public class SpringRulesRepository extends RuleRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringRulesRepository.class);

    public static final String REPOSITORY_KEY = "Spring";
    public static final String REPOSITORY_NAME = "Spring";

    private final AnnotationRuleParser annotationRuleParser;

    public SpringRulesRepository(final AnnotationRuleParser annotationRuleParser) {
        super(REPOSITORY_KEY, Spring.KEY);
        setName(REPOSITORY_NAME);        
        this.annotationRuleParser = annotationRuleParser;
    }

    @Override
    public List<Rule> createRules() {
        List<Rule> rules = this.annotationRuleParser.parse(REPOSITORY_KEY, CheckClasses.getCheckClasses());
        for (Rule rule : rules) {
            rule.setCardinality(Cardinality.MULTIPLE);
        }
        return rules;
    }

    /**
     * Instantiate checks as defined in the RulesProfile.
     * 
     * @param profile
     */
    public static List<BeanCheck> createChecks(final RulesProfile profile) {
        LOGGER.debug("Loading checks for profile " + profile.getName());

        List<BeanCheck> checks = new ArrayList<BeanCheck>();
        LOGGER.debug("SpringSensor analyse profile.getActiveRules().size(): " + profile.getActiveRules().size());

        for (ActiveRule activeRule : profile.getActiveRules()) {
        	LOGGER.debug("SpringRulesRepository createChecks activeRule: " + activeRule.toString());
            if (REPOSITORY_KEY.equals(activeRule.getRepositoryKey())) {
                Class<BeanCheck> checkClass = getCheckClass(activeRule);
            	LOGGER.debug("SpringRulesRepository createChecks checkClass.getName(): " + checkClass.getName());
                if (checkClass != null) {
                    checks.add(createCheck(checkClass, activeRule));
                }
            }
        }

        return checks;
    }

    private static BeanCheck createCheck(final Class<BeanCheck> checkClass, final ActiveRule activeRule) {
        try {
            BeanCheck check = checkClass.newInstance();
            check.setRule(activeRule.getRule());
            if (activeRule.getActiveRuleParams() != null) {
                for (ActiveRuleParam param : activeRule.getActiveRuleParams()) {
                    if (!StringUtils.isEmpty(param.getValue())) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Rule param " + param.getKey() + " = " + param.getValue());
                        }
                        
                        BeanUtils.setProperty(check, param.getRuleParam().getKey(), param.getValue());
                    }
                }
            }

            return check;
        } catch (IllegalAccessException e) {
            throw new SonarException(e);
        } catch (InvocationTargetException e) {
            throw new SonarException(e);
        } catch (InstantiationException e) {
            throw new SonarException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Class<BeanCheck> getCheckClass(final ActiveRule activeRule) {
        for (Class<?> checkClass : CheckClasses.getCheckClasses()) {

            org.sonar.check.Rule ruleAnnotation = AnnotationUtils.getClassAnnotation(checkClass, org.sonar.check.Rule.class);
            if (ruleAnnotation.key().equals(activeRule.getConfigKey())) {
                return (Class<BeanCheck>) checkClass;
            }
        }
        LOGGER.error("Could not find check class for config key " + activeRule.getConfigKey());
        return null;
    }
}

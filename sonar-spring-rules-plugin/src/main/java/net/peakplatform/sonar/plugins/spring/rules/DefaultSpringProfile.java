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

import java.util.List;

import net.peakplatform.sonar.plugins.spring.language.Spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.Rule;
import org.sonar.api.utils.ValidationMessages;

public class DefaultSpringProfile extends ProfileDefinition {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSpringProfile.class);
    
    private final SpringRulesRepository springRulesRepository;

    public DefaultSpringProfile(final SpringRulesRepository springRulesRepository) {
        this.springRulesRepository = springRulesRepository;
    }

    @Override
    public RulesProfile createProfile(final ValidationMessages validation) {
        LOGGER.debug("DefaultSpringProfile createProfile start");
        List<Rule> rules = this.springRulesRepository.createRules();
        RulesProfile rulesProfile = RulesProfile.create("Default Spring Profile", Spring.KEY);
        for (Rule rule : rules) {
            LOGGER.debug("DefaultSpringProfile createProfile activating rule: " + rule.toString());
            rulesProfile.activateRule(rule, null);
        }
        rulesProfile.setDefaultProfile(true);
        LOGGER.debug("DefaultSpringProfile createProfile end");
        return rulesProfile;
    }
}

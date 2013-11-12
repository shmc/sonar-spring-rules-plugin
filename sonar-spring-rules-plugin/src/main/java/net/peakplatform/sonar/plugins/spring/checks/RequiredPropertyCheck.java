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

import com.puppycrawl.tools.checkstyle.api.AnnotationUtility;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class RequiredPropertyCheck extends Check {

    private static final String COMPONENT_ANNOTATION = "Component";
    private static final String REQUIRED_ANNOTATION = "Required";
    private static final String SETTER_PREFIX = "set";

    private boolean isSpringBean = false;

    public RequiredPropertyCheck() {
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] { TokenTypes.CLASS_DEF, TokenTypes.METHOD_DEF };
    }

    @Override
    public void beginTree(final DetailAST rootAST) {
        this.isSpringBean = false;
        super.beginTree(rootAST);
    }

    @Override
    public void visitToken(final DetailAST ast) {
        if (ast.getType() == TokenTypes.CLASS_DEF) {
            if (AnnotationUtility.containsAnnotation(ast, COMPONENT_ANNOTATION)) {
                this.isSpringBean = true;
            }
        } else if (ast.getType() == TokenTypes.METHOD_DEF) {
            DetailAST method = ast.findFirstToken(TokenTypes.IDENT);

            if (this.isSpringBean && method.getText().startsWith(SETTER_PREFIX) && !AnnotationUtility.containsAnnotation(ast, REQUIRED_ANNOTATION)) {
                log(ast.getLineNo(), ast.getColumnNo(), "Bean property setter does not have @Required annotation");
            }
        }
    }
}

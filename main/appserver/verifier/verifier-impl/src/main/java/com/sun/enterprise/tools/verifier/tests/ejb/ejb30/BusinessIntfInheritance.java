/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2012 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.enterprise.tools.verifier.tests.ejb.ejb30;

import com.sun.enterprise.tools.verifier.Result;
import com.sun.enterprise.tools.verifier.Verifier;
import com.sun.enterprise.tools.verifier.tests.ComponentNameConstructor;
import com.sun.enterprise.tools.verifier.tests.ejb.EjbTest;
import org.glassfish.ejb.deployment.descriptor.EjbDescriptor;

import java.util.Set;

/**
 * A business interface must not extend javax.ejb.EJBObject or 
 * javax.ejb.EJBLocalObject.
 * 
 * @author Vikas Awasthi
 */
public class BusinessIntfInheritance extends EjbTest {
    
    public Result check(EjbDescriptor descriptor) {
        Result result = getInitializedResult();
        ComponentNameConstructor compName = 
                getVerifierContext().getComponentNameConstructor();

        Set<String> remoteAndLocalIntfs = descriptor.getRemoteBusinessClassNames();
        remoteAndLocalIntfs.addAll(descriptor.getLocalBusinessClassNames());
        
        for (String remoteOrLocalIntf : remoteAndLocalIntfs) {
            try {
                Class c = Class.forName(remoteOrLocalIntf, 
                                        false, 
                                        getVerifierContext().getClassLoader());
                if(javax.ejb.EJBObject.class.isAssignableFrom(c) ||
                        javax.ejb.EJBLocalObject.class.isAssignableFrom(c)) {
                    addErrorDetails(result, compName);
                    result.failed(smh.getLocalString
                                    (getClass().getName() + ".failed",
                                    "[ {0} ] extends either javax.ejb.EJBObject " +
                                    "or javax.ejb.EJBLocalObject.",
                                    new Object[] {remoteOrLocalIntf}));
                }
            } catch (ClassNotFoundException e) {
                Verifier.debug(e);
                addErrorDetails(result, compName);
                result.failed(smh.getLocalString
                                (getClass().getName() + ".failed1",
                                "Business Interface class [ {0} ] not found.",
                                new Object[] {remoteOrLocalIntf}));
            }
        }
        if(result.getStatus()!=Result.FAILED) {
            addGoodDetails(result, compName);
            result.passed(smh.getLocalString
                            (getClass().getName() + ".passed",
                            "Business Interface(s) are valid."));
        }
        
        return result;
    }
}

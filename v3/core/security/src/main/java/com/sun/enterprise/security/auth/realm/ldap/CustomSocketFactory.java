/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
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

package com.sun.enterprise.security.auth.realm.ldap;

import com.sun.enterprise.security.SSLUtils;
import com.sun.enterprise.util.i18n.StringManager;
import com.sun.logging.LogDomains;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.InetAddress;


/**
 * Custom socket factory for ldaps (SSL).
 *
 * The comparator only works in JDK 1.6 onwards. Due to a bug in JDK 1.6
 * compare method invocation fails with a classcast exception. The caller is 
 * trying to pass java.lang.String when it should have passed 
 * javax.net.SocketFactory
 * 
 * @see com.sun.enterprise.security.auth.realm.ldap.LDAPRealm
 *
 */
public class CustomSocketFactory extends SocketFactory implements Comparator<SocketFactory>{
    private static SocketFactory socketFactory = null;
    public static final String SSL = "SSL";
    protected static final Logger _logger =
        LogDomains.getLogger(LogDomains.SECURITY_LOGGER);
    //TODO:V3, make sure second argument is correct
    protected static final StringManager sm =
        StringManager.getManager("com.sun.enterprise.security.auth.realm", Thread.currentThread().getContextClassLoader());
    
    static {
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance(SSL);
            sc.init(SSLUtils.getKeyManagers(), SSLUtils.getTrustManagers(), new java.security.SecureRandom());
        } catch (Exception ex) {
            _logger.log(Level.WARNING, "security.exception", ex);
        }        
        socketFactory = sc.getSocketFactory();
    }
    
    /**
     * @see javax.net.SocketFactory#getDefault()
     */
    public static SocketFactory getDefault() {
        return new CustomSocketFactory();
    }
    
    
    /**
     * @see javax.net.SocketFactory#createSocket(java.lang.String, int)
     */
    public Socket createSocket(String arg0, int arg1) throws IOException,
            UnknownHostException {
        return socketFactory.createSocket(arg0, arg1);
    }
    
    /**
     * @see javax.net.SocketFactory#createSocket(java.net.InetAddress, int)
     */
    public Socket createSocket(InetAddress arg0, int arg1) throws IOException {
        return socketFactory.createSocket(arg0, arg1);
    }
    
    /**
     * @see javax.net.SocketFactory#createSocket(java.lang.String, int,
     *      java.net.InetAddress, int)
     */
    public Socket createSocket(String arg0, int arg1, InetAddress arg2, int arg3)
    throws IOException, UnknownHostException {
        return socketFactory.createSocket(arg0, arg1, arg2, arg3);
    }
    
    /**
     * @see javax.net.SocketFactory#createSocket(java.net.InetAddress, int,
     *      java.net.InetAddress, int)
     */
    public Socket createSocket(InetAddress arg0, int arg1, InetAddress arg2,
            int arg3) throws IOException {
        return socketFactory.createSocket(arg0, arg1, arg2, arg3);
    }

    public int compare(SocketFactory s1, SocketFactory s2) {
        return s1.getClass().toString().compareTo(s2.getClass().toString());
    }
    
    
}

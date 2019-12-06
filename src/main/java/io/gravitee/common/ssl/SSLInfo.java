/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.common.ssl;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import java.util.Arrays;
import java.util.Objects;

public class SSLInfo {
    public static final String UNVERIFIED = "unverified";

    private CertificateInfo[] localCertificates;
    private CertificateInfo[] peerCertificates;
    private String localPrincipal;
    private String peerPrincipal;
    private String cipherSuite;
    private String protocol;

    public SSLInfo() {
    }

    public SSLInfo(SSLSession sslSession, boolean fillCertificates) {
        if (fillCertificates) {
            this.localCertificates = Arrays.stream(sslSession.getLocalCertificates()).map(CertificateInfo::new).toArray(CertificateInfo[]::new);
            try {
                this.peerCertificates = Arrays.stream(sslSession.getPeerCertificates()).map(CertificateInfo::new).toArray(CertificateInfo[]::new);
            } catch (SSLPeerUnverifiedException e) {
                // Ignore
            }
        }

        this.localPrincipal = sslSession.getLocalPrincipal().getName();
        try{
            this.peerPrincipal = sslSession.getPeerPrincipal().getName();
        } catch (SSLPeerUnverifiedException e) {
            this.peerPrincipal = UNVERIFIED;
        }
        this.cipherSuite = sslSession.getProtocol();
        this.protocol = sslSession.getProtocol();
    }

    public SSLInfo(SSLSession sslSession) {
        this(sslSession, false);
    }

    public CertificateInfo[] getLocalCertificates() {
        return localCertificates;
    }

    public void setLocalCertificates(CertificateInfo[] localCertificates) {
        this.localCertificates = localCertificates;
    }

    public CertificateInfo[] getPeerCertificates() {
        return peerCertificates;
    }

    public void setPeerCertificates(CertificateInfo[] peerCertificates) {
        this.peerCertificates = peerCertificates;
    }

    public String getLocalPrincipal() {
        return localPrincipal;
    }

    public void setLocalPrincipal(String localPrincipal) {
        this.localPrincipal = localPrincipal;
    }

    public String getPeerPrincipal() {
        return peerPrincipal;
    }

    public void setPeerPrincipal(String peerPrincipal) {
        this.peerPrincipal = peerPrincipal;
    }

    public String getCipherSuite() {
        return cipherSuite;
    }

    public void setCipherSuite(String cipherSuite) {
        this.cipherSuite = cipherSuite;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SSLInfo sslInfo = (SSLInfo) o;
        return Arrays.equals(localCertificates, sslInfo.localCertificates) &&
                Arrays.equals(peerCertificates, sslInfo.peerCertificates) &&
                Objects.equals(localPrincipal, sslInfo.localPrincipal) &&
                Objects.equals(peerPrincipal, sslInfo.peerPrincipal) &&
                Objects.equals(cipherSuite, sslInfo.cipherSuite) &&
                Objects.equals(protocol, sslInfo.protocol);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(localPrincipal, peerPrincipal, cipherSuite, protocol);
        result = 31 * result + Arrays.hashCode(localCertificates);
        result = 31 * result + Arrays.hashCode(peerCertificates);
        return result;
    }

    @Override
    public String toString() {
        return "SSLInfo{" +
                "localCertificates=" + Arrays.toString(localCertificates) +
                ", peerCertificates=" + Arrays.toString(peerCertificates) +
                ", localPrincipal='" + localPrincipal + '\'' +
                ", peerPrincipal='" + peerPrincipal + '\'' +
                ", cipherSuite='" + cipherSuite + '\'' +
                ", protocol='" + protocol + '\'' +
                '}';
    }
}

package io.alfabattle.amaksim.bankomat.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;

@Slf4j
@Configuration
public class SSLRestTemplateConfig {

    @Autowired
    private Environment env;

    @Value("${client.ssl.trust-store}")
    private String certTrustStore;

    @Value("${client.ssl.trust-store-password}")
    private String certTrustStorePwd;

    @Value("${client.ssl.key-store}")
    private String keyStore;

    @Value("${client.ssl.key-store-password}")
    private String keyStorePwd;

    private static final String JAVA_KEYSTORE = "jks";

    protected KeyStore getStore(final String storeFileName, final char[] password) throws
            KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {

        final KeyStore store = KeyStore.getInstance(JAVA_KEYSTORE);
        URL url = getClass().getClassLoader().getResource(storeFileName);

        try(InputStream inputStream = url.openStream()) {
            store.load(inputStream, password);
        }
        return store;
    }

    @Bean
    public RestTemplate getRestTemplate(RestTemplateBuilder restTemplateBuilder) throws Exception{

        SSLContext sslContext = SSLContextBuilder
                    .create()
                    .loadKeyMaterial(
                            getStore(keyStore, keyStorePwd.toCharArray()),
                            keyStorePwd.toCharArray())
                    .loadTrustMaterial(
                            getStore(certTrustStore, certTrustStorePwd.toCharArray()),
                            new TrustSelfSignedStrategy())
                    .build();

        CloseableHttpClient client = HttpClientBuilder
                .create()
                .setSSLContext(sslContext)
                .build();

        return restTemplateBuilder
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(client))
                .build();

    }

}

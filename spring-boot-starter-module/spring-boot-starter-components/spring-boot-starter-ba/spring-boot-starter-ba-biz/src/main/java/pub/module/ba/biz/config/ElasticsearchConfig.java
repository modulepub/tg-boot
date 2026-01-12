package pub.module.ba.biz.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;

/**
 * creste by itheima.itcast
 */
@Configuration
public class ElasticsearchConfig {

    // TODO 后面需要改成配置
    //    @Value("${elasticsearch.url}")
    private static final String esUrl = "192.168.111.4";
    //    @Value("${elasticsearch.account}")
    private static final String esAccount = "elastic";
    //    @Value("${elasticsearch.password}")
    private static final String esPassword = "crpNmhReCKbOHc_6D_TY";

    private static final int PORT = 9200;
    private static final String SCHEME = "http";

    @Bean(destroyMethod = "close")
    public RestHighLevelClient restHighLevelClient(){
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost(esUrl, PORT, SCHEME))
                        .setHttpClientConfigCallback(httpClientBuilder -> {

                            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(esAccount, esPassword));

                            return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                        })
        );
    }

}

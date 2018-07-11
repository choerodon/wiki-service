package io.choerodon.wiki.infra.config;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.wiki.infra.feign.WikiClient;

@Configuration
public class RetrofitConfig {

    private static final Logger logger = LoggerFactory.getLogger(RetrofitConfig.class);

    @Value("${wiki.url}")
    private String wikiUrl;

    @Value("${wiki.token}")
    private String wikiToken;

    /**
     * Retrofit 设置
     *
     * @return Harbor 平台连接
     */
    @Bean
    public WikiClient wikiClientService() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.interceptors().add((Interceptor.Chain chain) -> {
            Request original = chain.request();
            CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
            logger.info("username:  " + customUserDetails.getUsername());
            Request.Builder requestBuilder = original.newBuilder()
                    .header("username", customUserDetails.getUsername())
                    .header("wikitoken", wikiToken);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
        OkHttpClient okHttpClient = okHttpClientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(wikiUrl)
                .client(okHttpClient)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        return retrofit.create(WikiClient.class);
    }
}

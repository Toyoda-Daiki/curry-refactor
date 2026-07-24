package com.example.config;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Spring AI（OpenAI API呼び出し）のHTTPクライアント設定クラス
 *
 * 【変更理由】
 * RestClientCustomizer は Spring Boot の自動設定にしか適用されない。
 * Spring AI 1.0.3 は独自に RestClient.Builder を使うため、
 * @Primary な RestClient.Builder Bean を定義して Spring AI に注入させる。
 */
@Configuration
public class AiConfig {

  @Bean
  @Primary
  public RestClient.Builder restClientBuilder() {
    return RestClient.builder().requestFactory(new BufferingClientHttpRequestFactory(
                    new HttpComponentsClientHttpRequestFactory(
                      HttpClients.createDefault()
                    )
            ));
  }
}

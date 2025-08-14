package com.loopers.config.redis;

import io.lettuce.core.ReadFrom;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStaticMasterReplicaConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.function.Consumer;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig {
    private static final String CONNECTION_MASTER = "redisConnectionMaster";
    public static final String REDIS_TEMPLATE_MASTER = "redisTemplateMaster";
    private static final StringRedisSerializer STRING_SERIALIZER = new StringRedisSerializer();

    private final RedisProperties redisProperties;

    public RedisConfig(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Primary
    @Bean
    public LettuceConnectionFactory defaultRedisConnectionFactory() {
        int database = redisProperties.getDatabase();
        RedisNodeInfo master = redisProperties.getMaster();
        List<RedisNodeInfo> replicas = redisProperties.getReplicas();

        return lettuceConnectionFactory(
            database, master, replicas,
            b -> b.readFrom(ReadFrom.REPLICA_PREFERRED) // 리플리카 우선 읽기
        );
    }

    @Bean
    @Qualifier(CONNECTION_MASTER)
    public LettuceConnectionFactory masterRedisConnectionFactory() {
        int database = redisProperties.getDatabase();
        RedisNodeInfo master = redisProperties.getMaster();
        List<RedisNodeInfo> replicas = redisProperties.getReplicas();

        return lettuceConnectionFactory(
            database, master, replicas,
            b -> b.readFrom(ReadFrom.MASTER) // 마스터 전용 읽기/쓰기
        );
    }

    @Primary
    @Bean
    public RedisTemplate<String, String> defaultRedisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        return configureRedisTemplate(template, lettuceConnectionFactory);
    }

    @Bean
    @Qualifier(REDIS_TEMPLATE_MASTER)
    public RedisTemplate<String, String> masterRedisTemplate(
        @Qualifier(CONNECTION_MASTER) LettuceConnectionFactory lettuceConnectionFactory
    ) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        return configureRedisTemplate(template, lettuceConnectionFactory);
    }

    private LettuceConnectionFactory lettuceConnectionFactory(
        int database,
        RedisNodeInfo master,
        List<RedisNodeInfo> replicas,
        Consumer<LettuceClientConfiguration.LettuceClientConfigurationBuilder> customizer
    ) {
        // 1. Lettuce 클라이언트 설정 빌더 생성
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder =
            LettuceClientConfiguration.builder();

        // 2. 커스터마이저 적용 (ReadFrom 전략 등)
        if (customizer != null) {
            customizer.accept(builder);
        }
        LettuceClientConfiguration clientConfig = builder.build();

        // 3. Master-Replica 구성 설정
        RedisStaticMasterReplicaConfiguration masterReplica =
            new RedisStaticMasterReplicaConfiguration(master.getHost(), master.getPort());
        masterReplica.setDatabase(database);

        // 4. 리플리카 노드들 추가
        if (replicas != null) {
            for (RedisNodeInfo r : replicas) {
                masterReplica.addNode(r.getHost(), r.getPort());
            }
        }
        return new LettuceConnectionFactory(masterReplica, clientConfig);
    }

    private <K, V> RedisTemplate<K, V> configureRedisTemplate(
        RedisTemplate<K, V> template,
        LettuceConnectionFactory connectionFactory
    ) {
        // 모든 시리얼라이저를 String으로 통일
        template.setKeySerializer(STRING_SERIALIZER);
        template.setValueSerializer(STRING_SERIALIZER);
        template.setHashKeySerializer(STRING_SERIALIZER);
        template.setHashValueSerializer(STRING_SERIALIZER);

        // 연결 팩토리 설정
        template.setConnectionFactory(connectionFactory);
        template.afterPropertiesSet();
        return template;
    }
}

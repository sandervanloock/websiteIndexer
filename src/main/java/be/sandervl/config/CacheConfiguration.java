package be.sandervl.config;

import io.github.jhipster.config.JHipsterProperties;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@AutoConfigureAfter(value = { MetricsConfiguration.class })
@AutoConfigureBefore(value = { WebConfigurer.class, DatabaseConfiguration.class })
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Ehcache ehcache =
            jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries()))
                .withExpiry(Expirations.timeToLiveExpiration(Duration.of(ehcache.getTimeToLiveSeconds(), TimeUnit.SECONDS)))
                .build());
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            cm.createCache(be.sandervl.domain.User.class.getName(), jcacheConfiguration);
            cm.createCache(be.sandervl.domain.Authority.class.getName(), jcacheConfiguration);
            cm.createCache(be.sandervl.domain.User.class.getName() + ".authorities", jcacheConfiguration);
            cm.createCache(be.sandervl.domain.Attribute.class.getName(), jcacheConfiguration);
            cm.createCache(be.sandervl.domain.Attribute.class.getName() + ".relatives", jcacheConfiguration);
            cm.createCache(be.sandervl.domain.Document.class.getName(), jcacheConfiguration);
            cm.createCache(be.sandervl.domain.Document.class.getName() + ".matches", jcacheConfiguration);
            cm.createCache(be.sandervl.domain.Document.class.getName() + ".attributes", jcacheConfiguration);
            cm.createCache(be.sandervl.domain.FeedItem.class.getName(), jcacheConfiguration);
            cm.createCache(be.sandervl.domain.Selector.class.getName(), jcacheConfiguration);
            cm.createCache(be.sandervl.domain.Selector.class.getName() + ".children", jcacheConfiguration);
            cm.createCache(be.sandervl.domain.Site.class.getName(), jcacheConfiguration);
            cm.createCache(be.sandervl.domain.Site.class.getName() + ".pages", jcacheConfiguration);
            cm.createCache(be.sandervl.domain.Site.class.getName() + ".selectors", jcacheConfiguration);
            // jhipster-needle-ehcache-add-entry
        };
    }
}

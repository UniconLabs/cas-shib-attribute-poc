package net.unicon.iam.cas.config

import net.shibboleth.ext.spring.service.ReloadableSpringService
import net.shibboleth.ext.spring.util.SchemaTypeAwareXMLBeanDefinitionReader
import net.shibboleth.ext.spring.util.SpringSupport
import net.shibboleth.idp.attribute.resolver.AttributeDefinition
import net.shibboleth.idp.attribute.resolver.AttributeResolver
import net.shibboleth.idp.attribute.resolver.DataConnector
import net.shibboleth.idp.attribute.resolver.impl.AttributeResolverImpl
import net.shibboleth.idp.attribute.resolver.spring.impl.AttributeResolverServiceStrategy
import net.shibboleth.idp.saml.attribute.principalconnector.impl.PrincipalConnector
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Scope
import org.springframework.context.support.GenericApplicationContext
import org.springframework.ui.velocity.VelocityEngineFactoryBean
import org.springframework.web.context.support.ServletContextResource

import javax.servlet.ServletContext

@org.springframework.context.annotation.Configuration
class Configuration {
    @Autowired(required = false)
    ApplicationContext applicationContext

    @Autowired(required = false)
    Set<PrincipalConnector> principalConnectors

    @Autowired(required = false)
    Set<AttributeDefinition> attributeDefinitions

    @Autowired(required = false)
    Set<DataConnector> dataConnectors

    @Autowired
    ServletContext servletContext

    @Autowired
    PlaceholderConfigurerSupport propertyPlaceholderConfigurer

    @Bean
    @Scope("singleton")
    AttributeResolver attributeResolver() {
        // TODO: probably can do this better
        def appCtx = SpringSupport.newContext(
                "test",
                [new ServletContextResource(servletContext, '/WEB-INF/shibboleth/attribute-resolver.xml')],
                [propertyPlaceholderConfigurer],
                [],
                [],
                applicationContext
        )

        final AttributeResolver resolver = new AttributeResolverImpl(
                "ShibbolethAttributeResolver",
                appCtx.getBeansOfType(AttributeDefinition).values(),
                appCtx.getBeansOfType(DataConnector).values(),
                null)

        // TODO: figure out initialization. as of now, has to be a post construct on DAO
        return resolver
    }

    @Bean(name = "shibboleth.VelocityEngine")
    VelocityEngineFactoryBean velocityEngineFactoryBean() {
        return new VelocityEngineFactoryBean().with {
            it.velocityProperties = new Properties([
                    'input.encoding': 'UTF-8',
                    'output.encoding': 'UTF-8',
                    'resource.loader': 'file, classpath, string',
                    'classpath.resource.loader.class': 'org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader',
                    'string.resource.loader.class': 'org.apache.velocity.runtime.resource.loader.StringResourceLoader',
                    'file.resource.loader.class': 'org.apache.velocity.runtime.resource.loader.FileResourceLoader',
                    'file.resource.loader.path': '/tmp',
                    'file.resource.loader.cache': false
            ])
            return it
        }
    }
}

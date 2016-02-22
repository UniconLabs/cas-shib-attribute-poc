package net.unicon.iam.cas.persondir.support

import net.shibboleth.idp.attribute.resolver.AttributeResolver
import net.shibboleth.idp.attribute.resolver.context.AttributeResolutionContext
import org.jasig.services.persondir.IPersonAttributeDao
import org.jasig.services.persondir.IPersonAttributes
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Component("shibbolethPersonAttributeDao")
class ShibbolethPersonAttributeDao implements IPersonAttributeDao {
    @Autowired
    AttributeResolver attributeResolver

    @PostConstruct
    public void init() {
        if (!attributeResolver.isInitialized()) {
            attributeResolver.initialize()
        }
    }

    @Override
    IPersonAttributes getPerson(String uid) {
        def attributeResolutionContext = new AttributeResolutionContext().with {
            principal = uid
            return it
        }
        attributeResolver.resolveAttributes(attributeResolutionContext)
        def attributes = attributeResolutionContext.resolvedIdPAttributes.collectEntries { key, value -> [key, value.values.collect {it.value}]}
        def person = new ShibbolethPersonAttributes(uid, attributes)
        return person
    }

    @Override
    Set<IPersonAttributes> getPeople(Map<String, Object> query) {
        return null
    }

    @Override
    Set<IPersonAttributes> getPeopleWithMultivaluedAttributes(Map<String, List<Object>> query) {
        return null
    }

    @Override
    Set<String> getPossibleUserAttributeNames() {
        return null
    }

    @Override
    Set<String> getAvailableQueryAttributes() {
        return null
    }

    @Override
    Map<String, List<Object>> getMultivaluedUserAttributes(Map<String, List<Object>> seed) {
        return null
    }

    @Override
    Map<String, List<Object>> getMultivaluedUserAttributes(String uid) {
        return null
    }

    @Override
    Map<String, Object> getUserAttributes(Map<String, Object> seed) {
        return null
    }

    @Override
    Map<String, Object> getUserAttributes(String uid) {
        return null
    }
}

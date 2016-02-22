package net.unicon.iam.cas.persondir.support

import org.jasig.services.persondir.IPersonAttributes

class ShibbolethPersonAttributes implements IPersonAttributes {
    final String name
    final Map<String, List<Object>> attributes

    public ShibbolethPersonAttributes(String name, Map<String, List<Object>> attributes) {
        this.name = name
        this.attributes = attributes
    }

    @Override
    Object getAttributeValue(String name) {
        if (this.attributes.containsKey(name) && this.attributes.get(name)) {
            return this.attributes.get(name)[0]
        }
        return null
    }

    @Override
    List<Object> getAttributeValues(String name) {
        return this.attributes.get(name)
    }
}

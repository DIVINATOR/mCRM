@XmlSchema(
        namespace = "http://xml.avaya.com/endpointAPI",
        elementFormDefault = XmlNsForm.QUALIFIED,
        xmlns = {@XmlNs(
                prefix = "",
                namespaceURI = "http://xml.avaya.com/endpointAPI"
        )}
)
package io.github.divinator.history;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
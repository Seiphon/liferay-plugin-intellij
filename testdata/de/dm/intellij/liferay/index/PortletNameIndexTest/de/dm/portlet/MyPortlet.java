package de.dm.portlet;

import org.osgi.service.component.annotations.Component;
import javax.portlet.Portlet;

@Component(
    property = {
        "javax.portlet.display-name=My Portlet",
        "javax.portlet.name=de_dm_portlet_MyPortletName"
    },
    service = Portlet.class
)
public class MyPortlet implements Portlet {

}
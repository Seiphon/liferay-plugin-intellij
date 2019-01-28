package de.dm.portlet;

import org.osgi.service.component.annotations.Component;
import javax.portlet.Portlet;
import javax.portlet.ProcessAction;

@Component(
    property = {
        "javax.portlet.display-name=My Portlet",
        "javax.portlet.name=de_dm_portlet_MyJspPortletName",
        "javax.portlet.init-param.view-template=/html/view.jsp"
    },
    service = Portlet.class
)
public class MyJspPortlet implements Portlet {

    @ProcessAction(name="/my/jspaction")
    public void handleActionCommand() {

    }

}
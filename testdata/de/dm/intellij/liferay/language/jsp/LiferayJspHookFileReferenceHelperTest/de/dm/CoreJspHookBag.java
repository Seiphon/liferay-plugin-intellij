package de.dm;

import com.liferay.portal.deploy.hot.CustomJspBag;
import com.liferay.portal.kernel.url.URLContainer;

import org.osgi.service.component.annotations.Component;

import java.util.List;

@Component(
    immediate = true,
    property = {
        "context.id=CoreJspHookBag", "context.name=Custom Core JSP Hook Bag",
        "service.ranking:Integer=100"
    }
)
public class CoreJspHookBag implements CustomJspBag {

    @Override
    public String getCustomJspDir() {
        return "META-INF/custom_jsps/";
    }

    @Override
    public List<String> getCustomJsps() {
        return null;
    }

    @Override
    public URLContainer getURLContainer() {
        return null;
    }

    @Override
    public boolean isCustomJspGlobal() {
        return true;
    }

}
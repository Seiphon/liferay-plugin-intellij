package de.dm.intellij.liferay.theme;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.NanoXmlUtil;
import de.dm.intellij.liferay.module.LiferayModuleComponent;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Class to parse liferay-look-and-feel.xml (if present) and to save information into LiferayModuleComponent
 */
public class LiferayLookAndFeelXmlParser {

    public static final String TEMPLATE_EXTENSION = "template-extension";
    public static final String ROOT_PATH = "root-path";
    public static final String CSS_PATH = "css-path";
    public static final String IMAGES_PATH = "images-path";
    public static final String JAVASCRIPT_PATH = "javascript-path";
    public static final String TEMPLATES_PATH = "templates-path";

    public static void handleChange(Project project, VirtualFileEvent event) {
        if ("liferay-look-and-feel.xml".equals(event.getFileName())) {
            VirtualFile virtualFile = event.getFile();
            final Module module = ModuleUtil.findModuleForFile(virtualFile, project);
            if (module != null) {
                LiferayModuleComponent component = module.getComponent(LiferayModuleComponent.class);
                if (component != null) {
                    component.setLiferayLookAndFeelXml(virtualFile.getUrl());

                    if (virtualFile.getLength() > 0) {
                        try {
                            NanoXmlUtil.parse(virtualFile.getInputStream(), new NanoXmlUtil.BaseXmlBuilder() {
                                @Override
                                public void addPCData(Reader reader, String systemID, int lineNr) throws Exception {
                                    String location = getLocation();

                                    switch (location) {
                                        case ".look-and-feel.theme." + ROOT_PATH:
                                            component.getThemeSettings().put(ROOT_PATH, readText(reader).trim());
                                            break;
                                        case ".look-and-feel.theme." + TEMPLATE_EXTENSION:
                                            component.getThemeSettings().put(TEMPLATE_EXTENSION, readText(reader).trim());
                                            break;
                                        case ".look-and-feel.theme." + CSS_PATH:
                                            component.getThemeSettings().put(CSS_PATH, readText(reader).trim());
                                            break;
                                        case ".look-and-feel.theme." + IMAGES_PATH:
                                            component.getThemeSettings().put(IMAGES_PATH, readText(reader).trim());
                                            break;
                                        case ".look-and-feel.theme." + JAVASCRIPT_PATH:
                                            component.getThemeSettings().put(JAVASCRIPT_PATH, readText(reader).trim());
                                            break;
                                        case ".look-and-feel.theme." + TEMPLATES_PATH:
                                            component.getThemeSettings().put(TEMPLATES_PATH, readText(reader).trim());
                                            break;
                                    }
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public static Collection<Setting> parseSettings(XmlFile xmlFile) {
        Collection<Setting> result = new ArrayList<Setting>();

        if (xmlFile != null) {
            if (xmlFile.isValid()) {
                XmlTag rootTag = xmlFile.getRootTag();
                if ("look-and-feel".equals(rootTag.getLocalName())) {
                    XmlTag themeTag = rootTag.findFirstSubTag("theme");
                    if (themeTag != null) {
                        XmlTag settingsTag = themeTag.findFirstSubTag("settings");
                        if (settingsTag != null) {
                            for (XmlTag xmlTag : settingsTag.findSubTags("setting")) {
                                String key = xmlTag.getAttributeValue("key");
                                String type = xmlTag.getAttributeValue("type");

                                result.add(new Setting(xmlTag, key, type));
                                //checkbox=boolean, other=string
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    private static String getTagValue(XmlTag parent, String qname, String defaultValue) {
        String result = defaultValue;

        XmlTag child = parent.findFirstSubTag(qname);
        if (child != null) {
            if (child.getValue() != null) {
                String text = child.getValue().getText();
                if ( (text != null) && (text.trim().length() > 0) ) {
                    result = text;
                }
            }
        }

        return result;
    }

    public static class Setting {
        public PsiElement psiElement;
        public String key;
        public String type;

        public Setting(PsiElement psiElement, String key, String type) {
            this.psiElement = psiElement;
            this.key = key;
            this.type = type;
        }
    }
}

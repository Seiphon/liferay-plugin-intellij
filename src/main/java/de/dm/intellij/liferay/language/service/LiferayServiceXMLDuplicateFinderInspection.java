package de.dm.intellij.liferay.language.service;

import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlText;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LiferayServiceXMLDuplicateFinderInspection extends AbstractLiferayServiceXMLDuplicateEntryInspection {

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "check for duplicate finders";
    }

    @Nullable
    @Override
    public String getStaticDescription() {
        return "Check for duplicate finders for entities in service.xml.";
    }

    @Override
    protected boolean isSuitableXmlText(XmlText xmlText) {
        return false;
    }

    @Override
    protected boolean isSuitableXmlAttributeValue(XmlAttributeValue xmlAttributeValue) {
        return LiferayServiceXMLUtil.isFinderNameAttribute(xmlAttributeValue);
    }
}

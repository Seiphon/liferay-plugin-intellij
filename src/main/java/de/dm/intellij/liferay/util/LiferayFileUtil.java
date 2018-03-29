package de.dm.intellij.liferay.util;

import com.intellij.javaee.web.WebRoot;
import com.intellij.javaee.web.facet.WebFacet;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceHelper;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.Processor;
import de.dm.intellij.liferay.module.LiferayModuleComponent;
import de.dm.intellij.liferay.theme.LiferayLookAndFeelXmlParser;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LiferayFileUtil {

    private static final String TEMPLATES = "templates";
    private static final String JOURNAL = "journal";
    private static final String STRUCTURES = "structures";
    private static final String APPLICATION_DISPLAY = "application_display";

    public static boolean isJournalTemplateFile(PsiFile psiFile) {
        //Journal Template files in a resource importer directory structure is detected in a path like "/journal/templates/StructureName/TemplateName" or "/journal/templates/TemplateName"
        if (psiFile != null) {
            if (psiFile.getParent() != null) {
                VirtualFile parent = psiFile.getVirtualFile().getParent();
                //Template with a parent folder for the Structure
                if (! (TEMPLATES.equals(parent.getName())) ) {
                    parent = parent.getParent();
                }
                if (parent != null) {
                    String templatesFolderName = parent.getName();
                    if (TEMPLATES.equals(templatesFolderName)) {
                        VirtualFile grandGrandParent = parent.getParent();
                        if (grandGrandParent != null) {
                            String journalFolderName = grandGrandParent.getName();
                            if (JOURNAL.equals(journalFolderName)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean isJournalStructureFile(PsiFile psiFile) {
        //Journal Structure files in a resource importer directory structure is detected in a path like "/journal/structures/Structure.xml"
        if (psiFile instanceof XmlFile) {
            if (psiFile.getParent() != null) {
                VirtualFile parent = psiFile.getVirtualFile().getParent();
                if (parent != null) {
                    String structureFolderName = parent.getName();
                    if (STRUCTURES.equals(structureFolderName)) {
                        VirtualFile grandGrandParent = parent.getParent();
                        if (grandGrandParent != null) {
                            String journalFolderName = grandGrandParent.getName();
                            if (JOURNAL.equals(journalFolderName)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean isApplicationDisplayTemplateFile(PsiFile psiFile) {
        //Application Display Template files in a resource importer directory structure is detected in a path like "/templates/application_display/type/TemplateName"
        if (psiFile != null) {
            if (psiFile.getParent() != null) {
                VirtualFile parent = psiFile.getVirtualFile().getParent();
                if (parent.getParent() != null) {
                    VirtualFile grandParent = parent.getParent();
                    String grandParentName = grandParent.getName();
                    if (APPLICATION_DISPLAY.equals(grandParentName)) {
                        VirtualFile grandGrandParent = grandParent.getParent();
                        if (grandGrandParent != null) {
                            String grandGrandParentName = grandGrandParent.getName();
                            if (TEMPLATES.equals(grandGrandParentName)) {
                                return true;
                            }
                        }
                    }

                }
            }
        }
        return false;
    }

    public static String getApplicationDisplayTemplateType(PsiFile psiFile) {
        if (psiFile != null) {
            if (psiFile.getParent() != null) {
                VirtualFile parent = psiFile.getVirtualFile().getParent();
                return parent.getName();
            }
        }
        return null;
    }


    /**
     * Check if the given file is a theme template file.
     *
     * The file is a theme template file if it is in the directory hierarchy of the template-path folder which has been defined in liferay-look-and-feel.xml
     *
     * @param psiFile
     * @return
     */
    public static boolean isThemeTemplateFile(PsiFile psiFile) {
        final Module module = ModuleUtil.findModuleForPsiElement(psiFile);
        if ( (module != null) && (psiFile.getVirtualFile() != null) ) {
            LiferayModuleComponent component = module.getComponent(LiferayModuleComponent.class);
            if (component != null) {
                String templatesPath = component.getThemeSettings().get(LiferayLookAndFeelXmlParser.TEMPLATES_PATH);
                if ((templatesPath != null) && (templatesPath.trim().length() > 0)) {
                    VirtualFile webRootFile = getFileInWebRoot(module, templatesPath);
                    VirtualFile sourceRootFile = getFileInSourceRoot(module, templatesPath);
                    return
                            (
                                    ((webRootFile != null) && (isParent(psiFile.getVirtualFile(), webRootFile))) ||
                                            ((sourceRootFile != null) && (isParent(psiFile.getVirtualFile(), sourceRootFile)))
                            );
                }
            }
        }

        return false;
    }

    /**
     * Check if given file is a layout template file.
     *
     * A layout template file is detected by the extension ".tpl"
     *
     * @param psiFile
     * @return
     */
    public static boolean isLayoutTemplateFile(PsiFile psiFile) {
        if (psiFile.getVirtualFile() != null) {
            return "tpl".equals(psiFile.getVirtualFile().getExtension());
        }

        return false;
    }

    public static boolean isParent(@NotNull VirtualFile child, @NotNull VirtualFile parent) {
        VirtualFile ancestor = child;
        while ( ( ancestor = ancestor.getParent()) != null) {
            if (ancestor.equals(parent)) {
                return true;
            }
        }

        return false;
    }

    public static VirtualFile getJournalStructureFile(PsiFile journalTemplateFile) {
        if (isJournalTemplateFile(journalTemplateFile)) {
            VirtualFile parent = journalTemplateFile.getVirtualFile().getParent();

            String templateName = journalTemplateFile.getVirtualFile().getNameWithoutExtension();

            //Structure file in same directory
            VirtualFile structureFile = getChild(parent, templateName + ".xml");
            if (structureFile != null) {
                return structureFile;
            }
            structureFile = getChild(parent, templateName + ".json");
            if (structureFile != null) {
                return structureFile;
            }

            //Template with a parent folder for the Structure
            if (! (TEMPLATES.equals(parent.getName())) ) {
                templateName = parent.getName();

                parent = parent.getParent();
            }
            VirtualFile grandParent = parent.getParent();
            VirtualFile structures = getChild(grandParent, STRUCTURES);
            if (structures != null) {
                VirtualFile result = getChild(structures, templateName + ".xml");
                if (result == null) {
                    result = getChild(structures, templateName + ".json"); //JSON-Format for Liferay 7
                }
                return result;
            }
        }
        return null;
    }

    public static VirtualFile getFileInSourceRoot(Module module, String path) {
        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
        for (VirtualFile sourceRoot : moduleRootManager.getSourceRoots()) {
            VirtualFile pathFile = sourceRoot.findFileByRelativePath(path);
            if (pathFile != null) {
                return pathFile;
            }
        }

        return null;
    }

    public static VirtualFile getFileInWebRoot(Module module, String path) {
        Collection<WebFacet> webFacets = WebFacet.getInstances(module);
        for (WebFacet webFacet : webFacets) {
            //Iterate all WebFacets and examine their Web Root directories
            List<WebRoot> webRoots = webFacet.getWebRoots();
            for (WebRoot webRoot : webRoots) {
                if ("/".equals(webRoot.getRelativePath())) {
                    if (webRoot.getFile() != null) {
                        VirtualFile pathFile = webRoot.getFile().findFileByRelativePath(path);
                        if (pathFile != null) {
                            return pathFile;
                        }
                    }
                }
            }
        }

        return null;
    }

    public static Collection<String> getWebRootsRelativePaths(Module module, VirtualFile virtualFile) {
        Collection<String> result = new ArrayList<String>();

        Collection<WebFacet> webFacets = WebFacet.getInstances(module);
        for (WebFacet webFacet : webFacets) {
            List<WebRoot> webRoots = webFacet.getWebRoots();
            for (WebRoot webRoot : webRoots) {
                VirtualFile webRootFile = webRoot.getFile();
                result.add(VfsUtilCore.getRelativePath(virtualFile, webRootFile));
            }
        }

        return result;
    }

    public static VirtualFile getChild(VirtualFile parent, String name) {
        int index = name.indexOf('/');

        String pathElement = name;

        if (index > -1) {
            pathElement = name.substring(0, index);
        }

        if (parent != null) {
            for (VirtualFile virtualFile : parent.getChildren()) {
                if (pathElement.equals(virtualFile.getName())) {
                    if (index == -1) {
                        return virtualFile;
                    } else {
                        return getChild(virtualFile, name.substring(index+1));
                    }
                }
            }
        }

        return null;
    }

    public static VirtualFile getParent(VirtualFile file, String name) {
        if (file != null) {
            VirtualFile parent = file.getParent();
            if (parent != null) {
                if (name.equals(parent.getName())) {
                    return parent;
                }
            }
            return getParent(parent, name);
        }

        return null;
    }

    public static VirtualFile getThemeSettingsDirectory(Module module, String themeSetting) {
        String themeSettingValue = LiferayModuleComponent.getThemeSetting(module, themeSetting);
        if ((themeSettingValue != null) && (themeSettingValue.trim().length() > 0)) {
            VirtualFile themeSettingsFile = LiferayFileUtil.getFileInSourceRoot(module, themeSettingValue);
            if (themeSettingsFile == null) {
                themeSettingsFile = LiferayFileUtil.getFileInWebRoot(module, themeSettingValue);
            }

            return themeSettingsFile;
        }
        return null;
    }

    public static void addLibraryRoot(final Collection<PsiFileSystemItem> result, final FileReferenceHelper fileReferenceHelper, final Module module, final String libraryName, final String path) {

        ModuleRootManager.getInstance(module).orderEntries().forEachLibrary(
                new Processor<Library>() {
                    @Override
                    public boolean process(Library library) {

                        if (library.getName().contains(libraryName)) {
                            VirtualFile[] files = library.getFiles(OrderRootType.CLASSES);
                            for (VirtualFile file : files) {
                                if ( (path == null) || (path.trim().length() == 0) ) {
                                    result.add(fileReferenceHelper.getPsiFileSystemItem(module.getProject(), file));
                                } else {
                                    VirtualFile child = getChild(file, path);
                                    if (child != null) {
                                        result.add(fileReferenceHelper.getPsiFileSystemItem(module.getProject(), child));
                                    }
                                }
                            }
                        }

                        return true;
                    }
                }
        );

    }

    public static String getCustomJspDir(Module module) {
        String liferayHookXml = LiferayModuleComponent.getLiferayHookXml(module);
        if ( (liferayHookXml != null) && (liferayHookXml.trim().length() > 0) ) {
            VirtualFile virtualFile = VfsUtilCore.findRelativeFile(liferayHookXml, null);
            XmlFile xmlFile = (XmlFile) PsiManager.getInstance(module.getProject()).findFile(virtualFile);
            if ( (xmlFile != null) && (xmlFile.isValid()) ) {
                XmlTag rootTag = xmlFile.getRootTag();
                if ("hook".equals(rootTag.getLocalName())) {
                    XmlTag customJspDirTag = rootTag.findFirstSubTag("custom-jsp-dir");
                    if ((customJspDirTag != null) && (customJspDirTag.getValue() != null)) {
                        return customJspDirTag.getValue().getTrimmedText();
                    }
                }
            }
        }

        return null;
    }

}

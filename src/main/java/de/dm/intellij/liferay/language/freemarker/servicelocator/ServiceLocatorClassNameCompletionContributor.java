package de.dm.intellij.liferay.language.freemarker.servicelocator;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.freemarker.psi.FtlArgumentList;
import com.intellij.freemarker.psi.FtlMethodCallExpression;
import com.intellij.freemarker.psi.FtlStringLiteral;
import com.intellij.freemarker.psi.variables.FtlCallableType;
import com.intellij.freemarker.psi.variables.FtlMethodType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.intellij.util.Query;
import de.dm.intellij.liferay.util.Icons;
import de.dm.intellij.liferay.util.ProjectUtils;
import org.jetbrains.annotations.NotNull;

public class ServiceLocatorClassNameCompletionContributor extends CompletionContributor {

    private static final PsiElementPattern.Capture<PsiElement> ELEMENT_FILTER =
            PlatformPatterns.psiElement()
                    .withParent(PlatformPatterns.psiElement(FtlStringLiteral.class))
                    .with(new PatternCondition<PsiElement>("pattern") {

                        @Override
                        public boolean accepts(@NotNull PsiElement psiElement, ProcessingContext context) {
                            return isServiceLocatorCall(psiElement);
                        }
                    });

    public ServiceLocatorClassNameCompletionContributor() {
        extend(
            CompletionType.BASIC,
            ELEMENT_FILTER,
            new CompletionProvider<CompletionParameters>() {
                @Override
                protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                    PsiElement originalPosition = parameters.getOriginalPosition();
                    if (originalPosition != null) {
                        PsiFile psiFile = originalPosition.getContainingFile();
                        psiFile = psiFile.getOriginalFile();

                        Module module = ModuleUtil.findModuleForFile(psiFile);

                        if (isServiceLocatorCall(originalPosition)) {
                            PsiClass baseLocalServiceClass = ProjectUtils.getClassByName(originalPosition.getProject(), "com.liferay.portal.kernel.service.BaseLocalService", originalPosition);
                            PsiClass baseServiceClass = ProjectUtils.getClassByName(originalPosition.getProject(), "com.liferay.portal.kernel.service.BaseService", originalPosition);

                            addClassInheritorsLookup(baseLocalServiceClass, result, module);
                            addClassInheritorsLookup(baseServiceClass, result, module);

                            result.stopHere();
                        }
                    }
                }
            }
        );
    }

    public static boolean isServiceLocatorCall(PsiElement element) {
        FtlArgumentList ftlArgumentList = PsiTreeUtil.getParentOfType(element, FtlArgumentList.class);
        if (ftlArgumentList != null) {
            FtlMethodCallExpression ftlMethodCallExpression = PsiTreeUtil.getParentOfType(ftlArgumentList, FtlMethodCallExpression.class);
            if (ftlMethodCallExpression != null) {
                FtlCallableType[] callableCandidates = ftlMethodCallExpression.getCallableCandidates();
                for (FtlCallableType callableCandidate : callableCandidates) {
                    if (callableCandidate instanceof FtlMethodType) {
                        FtlMethodType ftlMethodType = (FtlMethodType)callableCandidate;
                        PsiMethod method = ftlMethodType.getMethod();
                        if (method != null) {
                            String methodName = method.getName();

                            PsiClass containingClass = method.getContainingClass();

                            if (containingClass != null) {
                                String qualifiedClassName = containingClass.getQualifiedName();

                                String signature = qualifiedClassName + "." + methodName;

                                if ( (ServiceLocatorFtlVariable.SERVICE_LOCATOR_CLASS_NAME + ".findService").equals(signature)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private static void addClassInheritorsLookup(PsiClass baseClass, CompletionResultSet result, Module module) {
        if (baseClass != null) {
            SearchScope scope = GlobalSearchScope.allScope(module.getProject());
            Query<PsiClass> query = ClassInheritorsSearch.search(baseClass, scope, false);

            query.forEach(psiClass -> {
                String qualifiedName = psiClass.getQualifiedName();
                if (qualifiedName != null) {
                    if (!(qualifiedName.endsWith("Wrapper"))) {
                        result.addElement(LookupElementBuilder.create(qualifiedName).withIcon(Icons.LIFERAY_ICON));
                    }
                }
            });
        }
    }

}

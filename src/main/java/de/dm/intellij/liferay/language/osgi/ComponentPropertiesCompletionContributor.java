package de.dm.intellij.liferay.language.osgi;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiAnnotationParameterList;
import com.intellij.psi.PsiArrayInitializerMemberValue;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.impl.compiled.ClsElementImpl;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import de.dm.intellij.liferay.util.Icons;
import de.dm.intellij.liferay.util.ProjectUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ComponentPropertiesCompletionContributor extends CompletionContributor {

    //see https://dev.liferay.com/develop/reference/-/knowledge_base/7-0/portlet-descriptor-to-osgi-service-property-map
    public static Map<String, String[][]> COMPONENT_PROPERTIES = new HashMap<String, String[][]>();
    static {

        COMPONENT_PROPERTIES.put("com.liferay.adaptive.media.handler.AMRequestHandler",
                new String[][]{
                        {"adaptive.media.handler.pattern", "String"}
                });

        COMPONENT_PROPERTIES.put("com.liferay.adaptive.media.image.counter.AMImageCounter",
                new String[][]{
                        {"adaptive.media.key", "String"}
                });

        COMPONENT_PROPERTIES.put("com.liferay.adaptive.media.image.optimizer.AMImageOptimizer",
                new String[][]{
                        {"adaptive.media.key", "String"}
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/changing-adaptive-medias-image-scaling
        COMPONENT_PROPERTIES.put("com.liferay.adaptive.media.image.scaler.AMImageScaler",
                new String[][]{
                        {"mime.type", "String"},
                        {"service.ranking", "Integer"},
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/customizing-the-product-menu
        COMPONENT_PROPERTIES.put("com.liferay.application.list.PanelApp",
                new String[][]{
                        {"panel.app.order", "Integer"},
                        {"panel.category.key", "String"},
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/customizing-the-product-menu
        COMPONENT_PROPERTIES.put("com.liferay.application.list.PanelCategory",
                new String[][]{
                        {"panel.category.key", "String"},
                        {"panel.category.order", "Integer"},
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/rendering-an-asset
        COMPONENT_PROPERTIES.put("com.liferay.asset.kernel.model.AssetRendererFactory",
                new String[][]{
                        {"javax.portlet.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.asset.kernel.util.AssetEntryQueryProcessor",
                new String[][]{
                        {"javax.portlet.name", "String"}
                });

        COMPONENT_PROPERTIES.put("com.liferay.asset.kernel.validator.AssetEntryValidator",
                new String[][]{
                        {"model.class.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.asset.kernel.validator.AssetEntryValidatorExclusionRule",
                new String[][]{
                        {"model.class.name", "String"},
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/creating-form-field-types
        COMPONENT_PROPERTIES.put("com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldType",
                new String[][]{
                        {"ddm.form.field.type.display.order", "Integer"},
                        {"ddm.form.field.type.icon", "String"},
                        {"ddm.form.field.type.js.class", "String"},
                        {"ddm.form.field.type.js.module", "String"},
                        {"ddm.form.field.type.label", "String"},
                        {"ddm.form.field.type.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueAccessor",
                new String[][]{
                        {"ddm.form.field.type.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueRenderer",
                new String[][]{
                        {"ddm.form.field.type.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueRequestParameterRetriever",
                new String[][]{
                        {"ddm.form.field.type.name", "String"},
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/creating-form-field-types
        COMPONENT_PROPERTIES.put("com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderer",
                new String[][]{
                        {"ddm.form.field.type.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.dynamic.data.mapping.util.DDMDisplay",
                new String[][]{
                        {"javax.portlet.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.dynamic.data.mapping.util.DDMStructurePermissionSupport",
                new String[][]{
                        {"add.structure.action.id", "String"},
                        {"default.model.resource.name", "boolean"},
                        {"model.class.name", "String"}
                });

        COMPONENT_PROPERTIES.put("com.liferay.dynamic.data.mapping.util.DDMTemplatePermissionSupport",
                new String[][]{
                        {"add.template.action.id", "String"},
                        {"default.model.resource.name", "boolean"},
                        {"model.class.name", "String"}
                });

        COMPONENT_PROPERTIES.put("com.liferay.expando.kernel.model.CustomAttributesDisplay",
                new String[][]{
                        {"javax.portlet.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.exportimport.content.processor.ExportImportContentProcessor",
                new String[][]{
                        {"model.class.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.exportimport.kernel.controller.ExportImportController",
                new String[][]{
                        {"model.class.name", "String"},
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/data-handlers
        COMPONENT_PROPERTIES.put("com.liferay.exportimport.kernel.lar.PortletDataHandler",
                new String[][]{
                        {"javax.portlet.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.exportimport.kernel.lar.StagedModelDataHandler",
                new String[][]{
                        {"javax.portlet.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor",
                new String[][]{
                        {"javax.portlet.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.exportimport.resources.importer.portlet.preferences.PortletPreferencesTranslator",
                new String[][]{
                        {"portlet.preferences.translator.portlet.id", "String"},
                });


        COMPONENT_PROPERTIES.put("com.liferay.frontend.image.editor.capability.ImageEditorCapability",
                new String[][]{
                        {"com.liferay.frontend.image.editor.capability.category", "String"},
                        {"com.liferay.frontend.image.editor.capability.controls", "String"},
                        {"com.liferay.frontend.image.editor.capability.icon", "String"},
                        {"com.liferay.frontend.image.editor.capability.name", "String"},
                        {"com.liferay.frontend.image.editor.capability.type", "String"},
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/creating-form-navigator-contexts
        COMPONENT_PROPERTIES.put("com.liferay.frontend.taglib.form.navigator.context.FormNavigatorContextProvider",
                new String[][]{
                        {"formNavigatorId", "String"},
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/creating-custom-item-selector-views
        COMPONENT_PROPERTIES.put("com.liferay.item.selector.ItemSelectorView",
                new String[][]{
                        {"item.selector.view.order", "Integer"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.knowledge.base.web.internal.selector.KBArticleSelector",
                new String[][]{
                        {"model.class.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.mentions.matcher.MentionsMatcher",
                new String[][]{
                        {"model.class.name", "String"}
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener",
                new String[][]{
                        {"model.class.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.deploy.hot.CustomJspBag",
                new String[][]{
                        {"context.id", "String"},
                        {"context.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.editor.configuration.EditorOptionsProvider",
                new String[][]{
                        {"editor.config.key", "String"},
                        {"editor.name", "String"},
                        {"javax.portlet.name", "String"},
                        {"service.ranking", "Integer"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.atom.AtomCollectionAdapter",
                new String[][]{
                        {"model.class.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor",
                new String[][]{
                        {"background.task.executor.class.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.cache.configurator.PortalCacheConfiguratorSettings",
                new String[][]{
                        {"portal.cache.manager.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.captcha.Captcha",
                new String[][]{
                        {"captcha.engine.impl", "String"},
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/modifying-an-editors-configuration
        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.editor.configuration.EditorConfigContributor",
                new String[][]{
                        {"editor.config.key", "String"},
                        {"editor.name", "String"},
                        {"javax.portlet.name", "String"},
                        {"service.ranking", "Integer"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.events.LifecycleAction",
                new String[][]{
                        {"key", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.messaging.Destination",
                new String[][]{
                        {"destination.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.messaging.MessageListener",
                new String[][]{
                        {"destination.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.model.ModelListener",
                new String[][]{
                        {"service.ranking", "Integer"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.model.LayoutTypeController",
                new String[][]{
                        {"layout.type", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.notifications.UserNotificationDefinition",
                new String[][]{
                        {"javax.portlet.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.notifications.UserNotificationHandler",
                new String[][]{
                        {"javax.portlet.name", "String"},
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/providing-the-user-personal-bar
        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.portlet.AddPortletProvider",
                new String[][]{
                        {"model.class.name", "String"},
                        {"service.ranking", "Integer"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.portlet.BrowsePortletProvider",
                new String[][]{
                        {"model.class.name", "String"},
                        {"service.ranking", "Integer"},
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/implementing-configuration-actions
        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.portlet.ConfigurationAction",
                new String[][]{
                        {"javax.portlet.name", "String"},
                        {"valid.url.prefixes", "String"},
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/providing-the-user-personal-bar
        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.portlet.EditPortletProvider",
                new String[][]{
                        {"model.class.name", "String"},
                        {"service.ranking", "Integer"},
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/making-urls-friendlier
        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.portlet.FriendlyURLMapper",
                new String[][]{
                        {"com.liferay.portlet.friendly-url-routes", "String"},
                        {"javax.portlet.name", "String"}
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.portlet.ManagePortletProvider",
                new String[][]{
                        {"model.class.name", "String"},
                        {"service.ranking", "Integer"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.portlet.PortletLayoutFinder",
                new String[][]{
                        {"model.class.name", "String"},
                });


        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.portlet.PortletLayoutListener",
                new String[][]{
                        {"javax.portlet.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.portlet.PreviewPortletProvider",
                new String[][]{
                        {"model.class.name", "String"},
                        {"service.ranking", "Integer"},
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/providing-the-user-personal-bar
        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.portlet.ViewPortletProvider",
                new String[][]{
                        {"model.class.name", "String"},
                        {"service.ranking", "Integer"},
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/mvc-action-command
        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand",
                new String[][]{
                        {"javax.portlet.name", "String"},
                        {"mvc.command.name", "String"}
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/mvc-render-command
        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand",
                new String[][]{
                        {"javax.portlet.name", "String"},
                        {"mvc.command.name", "String"}
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/mvc-resource-command
        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand",
                new String[][]{
                        {"javax.portlet.name", "String"},
                        {"mvc.command.name", "String"}
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/configuring-your-admin-apps-actions-menu
        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon",
                new String[][]{
                        {"javax.portlet.name", "String"},
                        {"path", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.portlet.toolbar.contributor.PortletToolbarContributor",
                new String[][]{
                        {"javax.portlet.name", "String"},
                        {"mvc.path", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.poller.PollerProcessor",
                new String[][]{
                        {"javax.portlet.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.repository.RepositoryFactory",
                new String[][]{
                        {"repository.target.class.name", "String"}
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.scheduler.messaging.SchedulerEventMessageListener",
                new String[][]{
                        {"destination.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.search.Indexer",
                new String[][]{
                        {"index.on.startup", "boolean"}
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/introduction-to-liferay-search
        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.search.IndexerPostProcessor",
                new String[][]{
                        {"indexer.class.name", "String"},
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/introduction-to-liferay-search
        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.search.IndexSearcher",
                new String[][]{
                        {"search.engine.impl", "String"},
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/introduction-to-liferay-search
        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.search.IndexWriter",
                new String[][]{
                        {"search.engine.impl", "String"},
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/introduction-to-liferay-search
        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.search.hits.HitsProcessor",
                new String[][]{
                        {"sort.order", "int"},
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/introduction-to-liferay-search
        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.search.suggest.QuerySuggester",
                new String[][]{
                        {"search.engine.impl", "String"},
                        {"distance.threshold", "float"},
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/password-based-authentication-pipelines
        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.security.auth.Authenticator",
                new String[][]{
                        {"key", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.security.auth.AuthFailure",
                new String[][]{
                        {"key", "String"},
                });


        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.security.permission.BaseModelPermissionChecker",
                new String[][]{
                        {"model.class.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.security.permission.PermissionUpdateHandler",
                new String[][]{
                        {"model.class.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.security.permission.ResourcePermissionChecker",
                new String[][]{
                        {"resource.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.servlet.taglib.DynamicInclude",
                new String[][]{
                        {"portal.settings.authentication.tabs.name", "String"}
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.servlet.taglib.TagDynamicIdFactory",
                new String[][]{
                        {"tagClassName", "String"},
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/form-navigator
        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.servlet.taglib.ui.FormNavigatorCategory",
                new String[][]{
                        {"form.navigator.category.order", "Integer"},
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/form-navigator
        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.servlet.taglib.ui.FormNavigatorEntry",
                new String[][]{
                        {"form.navigator.entry.order", "Integer"},
                        {"service.ranking", "Integer"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.social.SocialActivityManager",
                new String[][]{
                        {"model.class.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.struts.StrutsAction",
                new String[][]{
                        {"path", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.struts.StrutsPortletAction",
                new String[][]{
                        {"path", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.template.TemplateContextContributor",
                new String[][]{
                        {"type", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.template.TemplateHandler",
                new String[][]{
                        {"javax.portlet.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.templateparser.TransformerListener",
                new String[][]{
                        {"javax.portlet.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.trash.TrashHandler",
                new String[][]{
                        {"model.class.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.workflow.WorkflowEngineManager",
                new String[][]{
                        {"proxy.bean", "boolean"},
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/liferays-workflow-framework
        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.workflow.WorkflowHandler",
                new String[][]{
                        {"model.class.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.upgrade.UpgradeStep",
                new String[][]{
                        {"upgrade.bundle.symbolic.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.util.ResourceBundleLoader",
                new String[][]{
                        {"bundle.symbolic.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.kernel.webdav.WebDAVStorage",
                new String[][]{
                        {"javax.portlet.name", "String"},
                        {"webdav.storage.token", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.language.LanguageResources",
                new String[][]{
                        {"language.id", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.output.stream.container.OutputStreamContainerFactory",
                new String[][]{
                        {"name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.search.analysis.FieldQueryBuilderFactory",
                new String[][]{
                        {"description.fields", "String"},
                        {"title.fields", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.search.buffer.IndexerRequestBufferExecutor",
                new String[][]{
                        {"buffered.execution.mode", "String"},
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/customizing-liferay-search
        COMPONENT_PROPERTIES.put("com.liferay.portal.search.buffer.IndexerRequestBufferOverflowHandler",
                new String[][]{
                        {"mode", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchConnection",
                new String[][]{
                        {"operation.mode", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.security.ldap.configuration.ConfigurationProvider",
                new String[][]{
                        {"factoryPid", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.security.sso.openid.OpenIdProviderRegistry",
                new String[][]{
                        {"open.id.ax.schema", "String"},
                        {"open.id.ax.type", "String"},
                        {"open.id.url", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.struts.FindActionHelper",
                new String[][]{
                        {"model.class.name", "String"}
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.template.TemplateResourceParser",
                new String[][]{
                        {"lang.type", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.template.TemplateManager",
                new String[][]{
                        {"language.type", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.verify.VerifyProcess",
                new String[][]{
                        {"verify.process.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.workflow.kaleo.definition.parser.NodeValidator",
                new String[][]{
                        {"node.type", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.workflow.kaleo.runtime.assignment.TaskAssignmentSelector",
                new String[][]{
                        {"assignee.class.name", "String"},
                        {"scripting.language", "String"}
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.workflow.kaleo.runtime.notification.NotificationSender",
                new String[][]{
                        {"fromName", "String"},
                        {"notification.type", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portal.workflow.kaleo.runtime.notification.recipient.NotificationRecipientBuilder",
                new String[][]{
                        {"recipient.type", "String"}
                });

        COMPONENT_PROPERTIES.put("com.liferay.portlet.documentlibrary.store.Store",
                new String[][]{
                        {"store.type", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.portlet.documentlibrary.store.StoreWrapper",
                new String[][]{
                        {"store.type", "String"},
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/customizing-the-control-menu
        COMPONENT_PROPERTIES.put("com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry",
                new String[][]{
                        {"product.navigation.control.menu.category.key", "String"},
                        {"product.navigation.control.menu.category.order", "Integer"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.push.notifications.sender.PushNotificationsSender",
                new String[][]{
                        {"platform", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.social.kernel.model.SocialActivityInterpreter",
                new String[][]{
                        {"javax.portlet.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.social.kernel.model.SocialRequestInterpreter",
                new String[][]{
                        {"javax.portlet.name", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.sync.internal.jsonws.SyncDLObject",
                new String[][]{
                        {"json.web.service.context.name", "String"},
                        {"json.web.service.context.path", "String"},
                });

        COMPONENT_PROPERTIES.put("com.liferay.wiki.importer.WikiImporter",
                new String[][]{
                        {"importer", "String"},
                        {"page", "String"},
                });

        COMPONENT_PROPERTIES.put("java.lang.Object",
                new String[][]{
                        {"auth.public.path", "String"},
                        {"osgi.command.function", "String"},
                        {"osgi.command.scope", "String"}
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/overriding-language-keys
        COMPONENT_PROPERTIES.put("java.util.ResourceBundle",
                new String[][]{
                        {"language.id", "String"},
                });

        COMPONENT_PROPERTIES.put("javax.management.DynamicMBean",
                new String[][]{
                        {"jmx.objectname", "String"},
                        {"jmx.objectname.cache.key", "String"},
                });

        COMPONENT_PROPERTIES.put("javax.portlet.Portlet",
                new String[][] {
                        {"com.liferay.portlet.action-timeout", "int"},
                        {"com.liferay.portlet.action-url-redirect", "boolean"},
                        {"com.liferay.portlet.active", "boolean"},
                        {"com.liferay.portlet.add-default-resource", "boolean"},
                        {"com.liferay.portlet.ajaxable", "boolean"},
                        {"com.liferay.portlet.autopropagated-parameters", "String"},
                        {"com.liferay.portlet.configuration-path", "String"},
                        {"com.liferay.portlet.control-panel-entry-category", "String"},
                        {"com.liferay.portlet.control-panel-entry-weight", "double"},
                        {"com.liferay.portlet.css-class-wrapper", "String"},
                        {"com.liferay.portlet.display-category", "String"},
                        {"com.liferay.portlet.facebook-integration", "String"},
                        {"com.liferay.portlet.footer-portal-css", "String"},
                        {"com.liferay.portlet.footer-portal-javascript", "String"},
                        {"com.liferay.portlet.footer-portlet-css", "String"},
                        {"com.liferay.portlet.footer-portlet-javascript", "String"},
                        {"com.liferay.portlet.friendly-url-mapping", "String"},
                        {"com.liferay.portlet.friendly-url-routes", "String"},
                        {"com.liferay.portlet.header-portal-css", "String"},
                        {"com.liferay.portlet.header-portal-javascript", "String"},
                        {"com.liferay.portlet.header-portlet-css", "String"},
                        {"com.liferay.portlet.header-portlet-javascript", "String"},
                        {"com.liferay.portlet.icon", "String"},
                        {"com.liferay.portlet.instanceable", "boolean"},
                        {"com.liferay.portlet.layout-cacheable", "boolean"},
                        {"com.liferay.portlet.maximize-edit", "boolean"},
                        {"com.liferay.portlet.maximize-help", "boolean"},
                        {"com.liferay.portlet.parent-struts-path", "String"},
                        {"com.liferay.portlet.pop-up-print", "boolean"},
                        {"com.liferay.portlet.preferences-company-wide", "boolean"},
                        {"com.liferay.portlet.preferences-owned-by-group", "boolean"},
                        {"com.liferay.portlet.preferences-unique-per-layout", "boolean"},
                        {"com.liferay.portlet.private-request-attributes", "boolean"},
                        {"com.liferay.portlet.private-session-attributes", "boolean"},
                        {"com.liferay.portlet.remoteable", "boolean"},
                        {"com.liferay.portlet.render-timeout", "int"},
                        {"com.liferay.portlet.render-weight", "int"},
                        {"com.liferay.portlet.requires-namespaced-parameters", "boolean"},
                        {"com.liferay.portlet.restore-current-view", "boolean"},
                        {"com.liferay.portlet.scopeable", "boolean"},
                        {"com.liferay.portlet.show-portlet-access-denied", "boolean"},
                        {"com.liferay.portlet.show-portlet-inactive", "boolean"},
                        {"com.liferay.portlet.single-page-application", "boolean"},
                        {"com.liferay.portlet.struts-path", "String"},
                        {"com.liferay.portlet.system", "boolean"},
                        {"com.liferay.portlet.use-default-template", "boolean"},
                        {"com.liferay.portlet.user-principal-strategy", "String"},
                        {"com.liferay.portlet.virtual-path", "String"},
                        {"javax.portlet.description", "String"},
                        {"javax.portlet.display-name", "String"},
                        {"javax.portlet.expiration-cache", "int"},
                        {"javax.portlet.info.keywords", "String"},
                        {"javax.portlet.info.short-title", "String"},
                        {"javax.portlet.info.title", "String"},
                        {"javax.portlet.init-param", ""},
                        {"javax.portlet.mime-type", "String"},
                        {"javax.portlet.name", "String"},
                        {"javax.portlet.portlet-mode", "String"},
                        {"javax.portlet.preferences", "String"},
                        {"javax.portlet.resource-bundle", "String"},
                        {"javax.portlet.security-role-ref", "String"},
                        {"javax.portlet.supported-processing-event", "String"},
                        {"javax.portlet.supported-public-render-parameter", "String"},
                        {"javax.portlet.supported-publishing-event", "String"},
                        {"javax.portlet.window-state", "String"},
                        {"xml.doctype.declaration.allowed", "String"},
                        {"xml.external.general.entities.allowed", "String"},
                        {"xml.external.parameter.entities.allowed", "String"},
                        {"xsl.secure.processing.enabled", "String"}

                }
        );

        COMPONENT_PROPERTIES.put("javax.portlet.filter.PortletFilter",
                new String[][]{
                        {"javax.portlet.name", "String"},
                        {"preinitialized.filter", "boolean"},
                        {"service.id", "String"},
                });

        COMPONENT_PROPERTIES.put("javax.servlet.Filter",
                new String[][]{
                        {"after-filter", "String"},
                        {"before-filter", "String"},
                        {"dispatcher", "String"},
                        {"filter.init.", "String"},
                        {"filter.init.basic_auth", "String"},
                        {"filter.init.filter-class", "String"},
                        {"filter.init.portal_property_prefix", "String"},
                        {"init.param", "String"},
                        {"osgi.http.whiteboard.context.select", "String"},
                        {"osgi.http.whiteboard.filter.asyncSupported", "boolean"},
                        {"osgi.http.whiteboard.filter.dispatcher", "String"},
                        {"osgi.http.whiteboard.filter.name", "String"},
                        {"osgi.http.whiteboard.filter.pattern", "String"},
                        {"osgi.http.whiteboard.filter.regex", "String"},
                        {"osgi.http.whiteboard.filter.servlet", "String"},
                        {"osgi.http.whiteboard.target", "String"},
                        {"servlet-context-name", "String"},
                        {"servlet-filter-name", "String"},
                        {"url-pattern", "String"}
                });

        COMPONENT_PROPERTIES.put("javax.servlet.Servlet",
                new String[][]{
                        {"osgi.http.whiteboard.context.select", "String"},
                        {"osgi.http.whiteboard.servlet.asyncSupported", "boolean"},
                        {"osgi.http.whiteboard.servlet.errorPage", "String"},
                        {"osgi.http.whiteboard.servlet.name", "String"},
                        {"osgi.http.whiteboard.servlet.pattern", "String"},
                        {"osgi.http.whiteboard.target", "String"},
                        {"servlet.init.", "String"}
                });

        //see https://dev.liferay.com/de/develop/tutorials/-/knowledge_base/7-0/liferay-websocket-whiteboard
        COMPONENT_PROPERTIES.put("javax.websocket.Endpoint",
                new String[][]{
                        {"org.osgi.http.websocket.endpoint.path", "String"},
                });

        COMPONENT_PROPERTIES.put("org.eclipse.osgi.service.urlconversion.URLConverter",
                new String[][]{
                        {"protocol", "String"},
                });

        //OSGi http whiteboard specification, see https://osgi.org/download/r6/osgi.cmpn-6.0.0.pdf
        COMPONENT_PROPERTIES.put("org.osgi.service.http.context.ServletContextHelper",
                new String[][]{
                        {"context.init.", "String"},
                        {"osgi.http.whiteboard.context.name", "String"},
                        {"osgi.http.whiteboard.context.path", "String"}
                });

    }
    private Map<String, List<LookupElementBuilder>> KEYWORD_LOOKUPS = new HashMap<String, List<LookupElementBuilder>>();

    public ComponentPropertiesCompletionContributor() {
        for (Map.Entry<String, String[][]> entry : COMPONENT_PROPERTIES.entrySet()) {
            List<LookupElementBuilder> lookups = new ArrayList<LookupElementBuilder>();

            for (String[] keyword : entry.getValue()) {
                lookups.add(LookupElementBuilder.create(keyword[0]).withTypeText(keyword[1]).withIcon(Icons.LIFERAY_ICON));
            };
            KEYWORD_LOOKUPS.put(entry.getKey(), lookups);
        }

        extend(
                CompletionType.BASIC,
                ComponentPropertiesPsiElementPatternCapture.instance,
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                        List<String> serviceClassNames = getServiceClassNames(parameters.getOriginalPosition());

                        if (! (serviceClassNames.isEmpty()) ) {
                            Set<LookupElementBuilder> lookups = new TreeSet<>(Comparator.comparing(LookupElementBuilder::getLookupString));

                            for (String serviceClassName : serviceClassNames) {
                                List<LookupElementBuilder> elements = KEYWORD_LOOKUPS.get(serviceClassName);
                                if (elements != null) {
                                    lookups.addAll(elements);
                                }
                            }

                            result.addAllElements(lookups);
                            result.stopHere();
                        }
                    }
                }

        );
    }

    @NotNull
    public static List<String> getServiceClassNames(PsiElement originalPsiElement) {
        PsiAnnotationParameterList annotationParameterList = PsiTreeUtil.getParentOfType(originalPsiElement, PsiAnnotationParameterList.class);

        return getServiceClassNames(annotationParameterList);
    }

    @NotNull
    public static List<String> getServiceClassNames(PsiAnnotationParameterList annotationParameterList) {
        List<String> result = new ArrayList<>();

        if (annotationParameterList != null) {
            PsiNameValuePair[] psiNameValuePairs = PsiTreeUtil.getChildrenOfType(annotationParameterList, PsiNameValuePair.class);
            if (psiNameValuePairs != null) {
                for (PsiNameValuePair psiNameValuePair : psiNameValuePairs) {
                    String name = psiNameValuePair.getName();

                    if ("service".equals(name)) {
                        PsiAnnotationMemberValue value = psiNameValuePair.getValue();

                        if (value instanceof PsiArrayInitializerMemberValue) {
                            PsiArrayInitializerMemberValue psiArrayInitializerMemberValue = (PsiArrayInitializerMemberValue)value;

                            PsiAnnotationMemberValue[] initializers = psiArrayInitializerMemberValue.getInitializers();

                            for (PsiAnnotationMemberValue initializer : initializers) {
                                processInitializer(result, initializer);
                            }
                        } else {
                            processInitializer(result, value);
                        }
                    }
                }
            }
        }

        return result;
    }

    private static void processInitializer(List<String> result, PsiAnnotationMemberValue initializer) {
        if (initializer instanceof PsiClassObjectAccessExpression) {
            PsiClassObjectAccessExpression psiClassObjectAccessExpression = (PsiClassObjectAccessExpression) initializer;

            String serviceClassName = getServiceClassName(psiClassObjectAccessExpression);
            if (serviceClassName != null) {
                result.add(serviceClassName);
            }
        }
    }

    private static String getServiceClassName(PsiClassObjectAccessExpression psiClassObjectAccessExpression) {
        PsiTypeElement operand = psiClassObjectAccessExpression.getOperand();

        PsiJavaCodeReferenceElement referenceElement = null;

        if (operand instanceof ClsElementImpl) {
            PsiType psiType = operand.getType();
            if (psiType instanceof PsiClassReferenceType) {
                PsiClassReferenceType psiClassReferenceType = (PsiClassReferenceType)psiType;

                referenceElement = psiClassReferenceType.getReference();
            }
        } else {
            referenceElement = operand.getInnermostComponentReferenceElement();
        }

        if (referenceElement != null) {
            String serviceClassName = ProjectUtils.getQualifiedNameWithoutResolve(referenceElement, false);

            return serviceClassName;
        }

        return null;
    }

}

package org.nuxeo.automation.scripting.operation;

import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationDocumentation;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.OperationType;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.impl.InvokableMethod;
import org.nuxeo.ecm.automation.core.util.BlobList;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.DocumentRefList;

public class ScriptingTypeImpl implements OperationType {

    protected final AutomationService service;

    protected final ScriptingOperationDescriptor desc;

    public ScriptingTypeImpl(AutomationService service, ScriptingOperationDescriptor desc) {
        this.service = service;
        this.desc = desc;
    }

    protected InvokableMethod[] methods = new InvokableMethod[] { runMethod() };

    @Override
    public String getContributingComponent() {
        return null;
    }

    @Override
    public OperationDocumentation getDocumentation() throws OperationException {
        OperationDocumentation doc = new OperationDocumentation(getId());
        doc.label = getId();
        doc.category = desc.getCategory();
        doc.description = desc.getDescription();
        doc.params = desc.getParams();
        doc.signature = new String[] { desc.getInputType(), desc.getOutputType() };
        return doc;
    }

    protected String getParamDocumentationType(Class<?> type, boolean isIterable) {
        String t;
        if (DocumentModel.class.isAssignableFrom(type) || DocumentRef.class.isAssignableFrom(type)) {
            t = isIterable ? Constants.T_DOCUMENTS : Constants.T_DOCUMENT;
        } else if (DocumentModelList.class.isAssignableFrom(type) || DocumentRefList.class.isAssignableFrom(type)) {
            t = Constants.T_DOCUMENTS;
        } else if (BlobList.class.isAssignableFrom(type)) {
            t = Constants.T_BLOBS;
        } else if (Blob.class.isAssignableFrom(type)) {
            t = isIterable ? Constants.T_BLOBS : Constants.T_BLOB;
        } else if (URL.class.isAssignableFrom(type)) {
            t = Constants.T_RESOURCE;
        } else if (Calendar.class.isAssignableFrom(type)) {
            t = Constants.T_DATE;
        } else {
            t = type.getSimpleName().toLowerCase();
        }
        return t;
    }

    @Override
    public String getId() {
        return desc.getId();
    }

    @Override
    public List<InvokableMethod> getMethods() {
        return Arrays.asList(methods);
    }

    @Override
    public InvokableMethod[] getMethodsMatchingInput(Class<?> in) {
        return methods;
    }

    protected InvokableMethod runMethod() {
        try {
            return new InvokableMethod(this, ScriptingOperationImpl.class.getMethod("run", Object.class));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new UnsupportedOperationException("Cannot use reflection for run method", e);
        }
    }

    @Override
    public AutomationService getService() {
        return service;
    }

    @Override
    public Class<?> getType() {
        return ScriptingOperationImpl.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object newInstance(OperationContext ctx, Map<String, Object> args) throws Exception {

        // XXX cache the ScriptingOperationImpl to avoid create new Engine instance ?
        // => would be interesting to share the engine across diffrent op inside the same chain ?

        if (ctx.getVars().containsKey(Constants.VAR_RUNTIME_CHAIN)) {
            // WTF !!!
            args.putAll((Map<String, Object>) ctx.getVars().get(Constants.VAR_RUNTIME_CHAIN));
        }
        ScriptingOperationImpl impl = new ScriptingOperationImpl(desc.getScript(), ctx, args);
        return impl;
    }

}

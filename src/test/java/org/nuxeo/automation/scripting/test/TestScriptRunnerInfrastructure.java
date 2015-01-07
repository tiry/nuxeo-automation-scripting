package org.nuxeo.automation.scripting.test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.automation.scripting.AutomationScriptingService;
import org.nuxeo.automation.scripting.ScriptRunner;
import org.nuxeo.automation.scripting.operation.ScriptingTypeImpl;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationDocumentation.Param;
import org.nuxeo.ecm.automation.OperationType;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.TransactionalFeature;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import com.google.inject.Inject;


@RunWith(FeaturesRunner.class)
@Features({ TransactionalFeature.class, CoreFeature.class })
@RepositoryConfig(cleanup = Granularity.METHOD)
@Deploy({ "org.nuxeo.ecm.automation.core", "org.nuxeo.ecm.automation.features", "org.nuxeo.ecm.platform.query.api", "org.nuxeo.ecm.automation.scripting"})
@LocalDeploy({"org.nuxeo.ecm.automation.scripting.tests:automation-scripting-contrib.xml"})
public class TestScriptRunnerInfrastructure {

    @Inject
    CoreSession session;
    
    @Inject
    AutomationService as;
    
    @Test
    public void serviceShouldBeDeclared() {     
        AutomationScriptingService ass = Framework.getService(AutomationScriptingService.class);
        Assert.assertNotNull(ass);
    }
        
    //@Test
    public void shouldExecuteSimpleScript() throws Exception {
            
        AutomationScriptingService ass = Framework.getService(AutomationScriptingService.class);
        Assert.assertNotNull(ass);

        long t0 = System.currentTimeMillis();
        String js = ass.getJSWrapper(true);   
        System.out.println("wrapper generation time : " + (System.currentTimeMillis()-t0) + " ms") ;
        
        ScriptRunner runner = ass.getRunner(session);
        Assert.assertNotNull(runner); 

        System.out.println("wrapper compile time : " + runner.initialize() + " ms") ;
        
        InputStream stream = this.getClass().getResourceAsStream("/simpleAutomationScript.js");
        Assert.assertNotNull(stream);
        
        t0 = System.currentTimeMillis();
        runner.run(stream);
        System.out.println("script exec time : " + (System.currentTimeMillis()-t0) + " ms") ;       
    }
    
    
    @Test
    public void simpleScriptingOperationShouldBeAvailable() throws Exception {     
        
        OperationType type = as.getOperation("Scripting.HelloWorld");
        Assert.assertNotNull(type);        
        Assert.assertTrue(type instanceof ScriptingTypeImpl);
        
        Param[] paramDefs = type.getDocumentation().getParams();
        Assert.assertEquals(1, paramDefs.length);
        
        OperationContext ctx = new OperationContext(session);
        Map<String, Object> params = new HashMap<>();        
        
        params.put("lang", "fr");                
        ctx.setInput("John");                              
        Object result = as.run(ctx,"Scripting.HelloWorld" , params);        
        Assert.assertEquals("Bonjour John", result);
        
        params.put("lang", "en");
        ctx.setInput("John");         
        result = as.run(ctx,"Scripting.HelloWorld" , params);        
        Assert.assertEquals("Hello John", result);        
        
    }

    
    @Test
    public void runOperationOnSubTree() throws Exception {     
        
        DocumentModel root = session.getRootDocument();
        
        for (int i = 0; i < 5 ; i++) {
            DocumentModel doc = session.createDocumentModel("/","new"+i , "File");
            doc = session.createDocument(doc);            
        }
        
        session.save();
        DocumentModelList res = session.query("select * from File where  ecm:mixinType = 'HiddenInNavigation'");
        Assert.assertEquals(0, res.size());
                       
        OperationContext ctx = new OperationContext(session);
        Map<String, Object> params = new HashMap<>();        
        
        params.put("facet", "HiddenInNavigation");
        params.put("type", "File");
        ctx.setInput(root);                              
        Object result = as.run(ctx,"Scripting.AddFacetInSubTree" , params);        
        Assert.assertTrue(result instanceof DocumentModelList);
        
    }

}

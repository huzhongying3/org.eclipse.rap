/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.rwt;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import javax.servlet.http.HttpSession;
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.internal.lifecycle.*;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.lifecycle.IEntryPoint;
import org.eclipse.rap.rwt.resources.IResourceManagerFactory;
import org.eclipse.rap.rwt.resources.ResourceManager;
import org.eclipse.rap.rwt.widgets.Display;
import org.eclipse.rap.rwt.widgets.Widget;
import com.w4t.*;
import com.w4t.Fixture.TestRequest;
import com.w4t.Fixture.TestResponse;
import com.w4t.engine.lifecycle.PhaseEvent;
import com.w4t.engine.lifecycle.PhaseId;
import com.w4t.engine.service.*;
import com.w4t.util.browser.Ie6;


public final class RWTFixture {
  
  public static final class TestResourceManagerFactory
    implements IResourceManagerFactory
  {
    public IResourceManager create() {
      return new TestResourceManager();
    }
  }

  public final static class TestResourceManager implements IResourceManager {

    public String getCharset( final String name ) {
      return null;
    }

    public ClassLoader getContextLoader() {
      return null;
    }

    public String getLocation( final String name ) {
      return null;
    }

    public URL getResource( final String name ) {
      return null;
    }

    public InputStream getResourceAsStream( final String name ) {
      return null;
    }

    public Enumeration getResources( final String name ) throws IOException {
      return null;
    }

    public boolean isRegistered( final String name ) {
      return false;
    }

    public void register( final String name ) {
    }

    public void register( final String name, final String charset ) {
    }

    public void register( final String name, 
                          final String charset, 
                          final RegisterOptions options )
    {
    }

    public void setContextLoader( final ClassLoader classLoader ) {
    }
  }

  public static class TestEntryPoint implements IEntryPoint {
    public Display createUI() {
      return new Display();
    }
  }

  private static LifeCycleAdapterFactory lifeCycleAdapterFactory;
  private static WidgetAdapterFactory widgetAdapterFactory;

  private RWTFixture() {
    // prevent instantiation
  }
  
  public static void setUp() {
    // standard setup
    Fixture.setUp();
    
    registerAdapterFactories();
    
    // registration of mockup resource manager
    registerResourceManager();
  }
  
  public static void setUpWithoutResourceManager() {
    // standard setup
    Fixture.setUp();
    
    // registration of adapter factories
    registerAdapterFactories();
  }

  public static void tearDown() {
    // deregistration of mockup resource manager
    deregisterResourceManager();
    
    // deregistration of adapter factories
    deregisterAdapterFactories();
    
    // standard teardown
    Fixture.tearDown();
  }

  public static void registerAdapterFactories() {
    AdapterManager manager = W4TContext.getAdapterManager();
    lifeCycleAdapterFactory = new LifeCycleAdapterFactory();
    manager.registerAdapters( lifeCycleAdapterFactory, Display.class );
    manager.registerAdapters( lifeCycleAdapterFactory, Widget.class );
    widgetAdapterFactory = new WidgetAdapterFactory();
    manager.registerAdapters( widgetAdapterFactory, Display.class );
    manager.registerAdapters( widgetAdapterFactory, Widget.class );
  }
  
  public static void deregisterAdapterFactories() {
    AdapterManager manager = W4TContext.getAdapterManager();
    manager.deregisterAdapters( widgetAdapterFactory, Display.class );
    manager.deregisterAdapters( widgetAdapterFactory, Widget.class );
    manager.deregisterAdapters( lifeCycleAdapterFactory, Display.class );
    manager.deregisterAdapters( lifeCycleAdapterFactory, Widget.class );
  }

  public static void registerResourceManager() {
    ResourceManager.register( new TestResourceManagerFactory() );
    Image.clear();
  }
  
  public static void deregisterResourceManager() {
    Fixture.setPrivateField( ResourceManager.class, null, "_instance", null );
    Fixture.setPrivateField( ResourceManager.class, null, "factory", null );
  }
  
  public static void preserveWidgets() {
    PreserveWidgetsPhaseListener listener = new PreserveWidgetsPhaseListener();
    PhaseEvent event = new PhaseEvent( new RWTLifeCycle(), PhaseId.READ_DATA );
    listener.beforePhase( event );
  }

  public static void clearPreserved() {
    PreserveWidgetsPhaseListener listener = new PreserveWidgetsPhaseListener();
    PhaseEvent event = new PhaseEvent( new RWTLifeCycle(), PhaseId.RENDER );
    listener.afterPhase( event );
  }
  
  public static void markInitialized( final Widget widget ) {
    Object adapter = widget.getAdapter( IWidgetAdapter.class );
    WidgetAdapter widgetAdapter = ( WidgetAdapter )adapter;
    widgetAdapter.setInitialized( true );
  }

  public static void fakeNewRequest() {
    HttpSession session = ContextProvider.getSession();
    TestRequest request = new TestRequest();
    request.setSession( session );
    TestResponse response = new TestResponse();
    ServiceContext serviceContext = new ServiceContext( request, response );
    serviceContext.setStateInfo( new ServiceStateInfo() );
    ContextProvider.disposeContext();
    ContextProvider.setContext( serviceContext );
    Fixture.fakeResponseWriter();
    Fixture.fakeBrowser( new Ie6( true, true ) );
  }
}

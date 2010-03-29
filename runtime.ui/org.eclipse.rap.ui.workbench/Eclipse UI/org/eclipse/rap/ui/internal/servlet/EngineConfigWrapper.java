/*******************************************************************************
 * Copyright (c) 2006, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.ui.internal.servlet;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.runtime.*;
import org.eclipse.rap.ui.internal.preferences.WorkbenchFileSettingStoreFactory;
import org.eclipse.rwt.internal.*;
import org.eclipse.rwt.internal.engine.RWTServletContextListener;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.internal.resources.ResourceRegistry;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.theme.ResourceLoader;
import org.eclipse.rwt.internal.theme.Theme;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.internal.theme.css.CssFileReader;
import org.eclipse.rwt.internal.theme.css.StyleSheet;
import org.eclipse.rwt.lifecycle.PhaseListener;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.ISettingStoreFactory;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.osgi.framework.Bundle;


/**
 * The underlying W4Toolkit runtime engine expects some configuration
 * infos read by the IEngineConfig implementation. We abuse the
 * <code>EngineConfigWrapper</code> to fake an appropriate environment
 * for the library.
 */
// TODO: [fappel] clean replacement mechanism that is anchored in W4Toolkit core
final class EngineConfigWrapper implements IEngineConfig {

  private final static String FOLDER
    = EngineConfigWrapper.class.getPackage().getName().replace( '.', '/' );
  // path to a w4toolkit configuration file on the classpath
  private final static String CONFIG = FOLDER +"/config.xml";
  //  extension point id for adapter factory registration
  private static final String ID_ADAPTER_FACTORY
    = "org.eclipse.rap.ui.adapterfactory";
  //  extension point id for entry point registration
  private static final String ID_ENTRY_POINT
    = "org.eclipse.rap.ui.entrypoint";
  //  extension point id for custom theme registration
  private static final String ID_THEMES
    = "org.eclipse.rap.ui.themes";
  private static final String ID_THEME_CONTRIBUTIONS
    = "org.eclipse.rap.ui.themeContributions";
  //  extension point id for custom themeable widget registration
  private static final String ID_THEMEABLE_WIDGETS
    = "org.eclipse.rap.ui.themeableWidgets";
  //  extension point id for phase listener registration
  private static final String ID_PHASE_LISTENER
    = "org.eclipse.rap.ui.phaselistener";
  //  extension point id for service handler registration
  private static final String ID_SERVICE_HANDLER
    = "org.eclipse.rap.ui.serviceHandler";
  //  extension point id for registration of resources (i.e. javascript)
  //  which needed to be loaded at page startup
  private static final String ID_RESOURCES
    = "org.eclipse.rap.ui.resources";
  // extension point id for registration of a setting store factories
  private static final String ID_SETTING_STORES
    = "org.eclipse.rap.ui.settingstores";

  private final EngineConfig engineConfig;

  EngineConfigWrapper() {
    engineConfig = new EngineConfig( findContextPath().toString() );
    registerPhaseListener();
    registerRWTLifeCycle();
    registerResourceManagerFactory();
    registerSettingStoreFactory();
    registerWorkbenchEntryPoint();
    registerThemeableWidgets();
    registerThemes();
    registerThemeContributions();
    registerFactories();
    registerResources();    
    registerUICallBackServiceHandler();
    registerJSLibraryServiceHandler();
    registerCustomServiceHandlers();
  }


  public File getClassDir() {
    return engineConfig.getClassDir();
  }

  public File getConfigFile() {
    File result = engineConfig.getConfigFile();
    if( !result.exists() ) {
      result.getParentFile().mkdirs();
      try {
        result.createNewFile();
        createConfiguration( result );
      } catch( final IOException shouldNotHappen ) {
        throw new RuntimeException( shouldNotHappen );
      }
    }
    return result;
  }

  public File getLibDir() {
    return engineConfig.getLibDir();
  }

  public File getServerContextDir() {
    return engineConfig.getServerContextDir();
  }

  public File getSourceDir() {
    return engineConfig.getSourceDir();
  }


  //////////////////
  // helping methods

  private static void registerPhaseListener() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint point = registry.getExtensionPoint( ID_PHASE_LISTENER );
    IConfigurationElement[] elements = point.getConfigurationElements();
    for( int i = 0; i < elements.length; i++ ) {
      try {
        PhaseListener listener
          = ( PhaseListener )elements[ i ].createExecutableExtension( "class" );
        PhaseListenerRegistry.add( listener );
      } catch( final CoreException ce ) {
        WorkbenchPlugin.getDefault().getLog().log( ce.getStatus() );
      }
    }
  }

  private static void registerResourceManagerFactory() {
    ResourceManager.register( new ResourceManagerFactory() );
  }

  private static void registerSettingStoreFactory() {
    // determine which factory to use via an environment setting / config.ini
    ISettingStoreFactory result = null;
    String factoryId
      = System.getProperty( RWTServletContextListener.SETTING_STORE_FACTORY_PARAM );
    if( factoryId != null ) {
      IExtensionRegistry registry = Platform.getExtensionRegistry();
      IExtensionPoint point = registry.getExtensionPoint( ID_SETTING_STORES );
      IConfigurationElement[] elements = point.getConfigurationElements();
      for( int i = 0; i < elements.length; i++ ) {
        String id = elements[ i ].getAttribute( "id" );
        if( factoryId.equals( id ) ) {
          try {
            Object obj = elements[ i ].createExecutableExtension( "class" );
            if( obj instanceof ISettingStoreFactory ) {
              result = ( ISettingStoreFactory )obj;
            }
          } catch( CoreException cex ) {
            WorkbenchPlugin.log( cex.getStatus() );
          }
        }
      }
      if( result == null ) {
        String msg =   "Warning: could not find the factory with id '"
                     + factoryId
                     + "' in org.eclipse.rap.ui.settingstores";
        WorkbenchPlugin.log( WorkbenchPlugin.getStatus( new Throwable( msg ) ) );
      }
    }
    if( result == null ) {
      result = new WorkbenchFileSettingStoreFactory(); // default
    }
    SettingStoreManager.register( result );
  }

  private static void registerFactories() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint point = registry.getExtensionPoint( ID_ADAPTER_FACTORY );
    IConfigurationElement[] elements = point.getConfigurationElements();
    for( int i = 0; i < elements.length; i++ ) {
      String contributorName = elements[ i ].getContributor().getName();
      String factoryName = elements[ i ].getAttribute( "factoryClass" );
      String adaptableName = elements[ i ].getAttribute( "adaptableClass" );
      try {
        Bundle bundle = Platform.getBundle( contributorName );
        Class factoryClass = bundle.loadClass( factoryName );
        Class adaptableClass = bundle.loadClass( adaptableName );
        AdapterFactoryRegistry.add( factoryClass, adaptableClass );
      } catch( final Throwable thr ) {
        String text =   "Could not register adapter factory ''{0}'' "
                      + "for the adapter type ''{1}''.";
        Object[] param = new Object[] { factoryName, adaptableName};
        String msg = MessageFormat.format( text, param );
        Status status = new Status( IStatus.ERROR,
                                    contributorName,
                                    IStatus.OK,
                                    msg,
                                    thr );
        WorkbenchPlugin.getDefault().getLog().log( status );
      }
    }
  }

  private static void registerWorkbenchEntryPoint() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint point = registry.getExtensionPoint( ID_ENTRY_POINT );
    IConfigurationElement[] elements = point.getConfigurationElements();
    for( int i = 0; i < elements.length; i++ ) {
      String contributorName = elements[ i ].getContributor().getName();
      String className = elements[ i ].getAttribute( "class" );
      String parameter = elements[ i ].getAttribute( "parameter" );
      String id = elements[ i ].getAttribute( "id" );
      try {
        Bundle bundle = Platform.getBundle( contributorName );
        Class clazz = bundle.loadClass( className );
        EntryPointManager.register( parameter, clazz );
        EntryPointExtension.bind( id, parameter );
      } catch( final Throwable thr ) {
        String text =   "Could not register entry point ''{0}'' "
                      + "with request startup parameter ''{1}''.";
        Object[] param = new Object[] { className, parameter };
        String msg = MessageFormat.format( text, param );
        IStatus status = new Status( IStatus.ERROR,
                                     contributorName,
                                     IStatus.OK,
                                     msg,
                                     thr );
        WorkbenchPlugin.getDefault().getLog().log( status );
      }
    }
  }

  private static void registerThemeableWidgets() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint ep = registry.getExtensionPoint( ID_THEMEABLE_WIDGETS );
    IConfigurationElement[] widgetExts = ep.getConfigurationElements();
    for( int i = 0; i < widgetExts.length; i++ ) {
      String contributorName = widgetExts[ i ].getContributor().getName();
      String widgetClass = widgetExts[ i ].getAttribute( "class" );
      try {
        final Bundle bundle = Platform.getBundle( contributorName );
        ResourceLoader resLoader = createResourceLoader( bundle );
        Class widget = bundle.loadClass( widgetClass );
        ThemeManager.getInstance().addThemeableWidget( widget, resLoader );
      } catch( final Throwable thr ) {
        String text = "Could not register themeable widget ''{0}''.";
        Object[] param = new Object[] { widgetClass };
        String message = MessageFormat.format( text, param );
        IStatus status
          = new Status( IStatus.ERROR, contributorName, message, thr );
        WorkbenchPlugin.getDefault().getLog().log( status );
      }
    }
    ThemeManager.getInstance().initializeThemeableWidgets();
  }


  private static void registerThemes() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint ep = registry.getExtensionPoint( ID_THEMES );
    IConfigurationElement[] elements = ep.getConfigurationElements();
    for( int i = 0; i < elements.length; i++ ) {
      String contributorName = elements[ i ].getContributor().getName();
      String themeId = elements[ i ].getAttribute( "id" );
      String themeName = elements[ i ].getAttribute( "name" );
      String themeFile = elements[ i ].getAttribute( "file" );
      if( !ThemeManager.DEFAULT_THEME_ID.equals( themeId ) ) {
        try {
          Bundle bundle = Platform.getBundle( contributorName );
          ResourceLoader loader = createResourceLoader( bundle );
          StyleSheet styleSheet = null;
          if( themeFile != null ) {
            styleSheet = CssFileReader.readStyleSheet( themeFile, loader );
          }
          Theme theme = new Theme( themeId, themeName, styleSheet );
          ThemeManager.getInstance().registerTheme( theme );
        } catch( final Exception e ) {
          String text = "Could not register custom theme ''{0}'' "
                        + "from file ''{1}''.";
          Object[] param = new Object[]{ themeId, themeFile };
          String msg = MessageFormat.format( text, param );
          IStatus status = new Status( IStatus.ERROR, contributorName, msg, e );
          WorkbenchPlugin.getDefault().getLog().log( status );
        }
      }
    }
  }

  private static void registerThemeContributions() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint ep = registry.getExtensionPoint( ID_THEME_CONTRIBUTIONS );
    IConfigurationElement[] elements = ep.getConfigurationElements();
    for( int i = 0; i < elements.length; i++ ) {
      String contributorName = elements[ i ].getContributor().getName();
      String themeId = elements[ i ].getAttribute( "themeId" );
      String themeFile = elements[ i ].getAttribute( "file" );
      try {
        Bundle bundle = Platform.getBundle( contributorName );
        ResourceLoader loader = createResourceLoader( bundle );
        StyleSheet styleSheet = CssFileReader.readStyleSheet( themeFile,
                                                              loader );
        Theme theme = ThemeManager.getInstance().getTheme( themeId );
        if( theme == null ) {
          throw new IllegalArgumentException( "No such theme defined: "
                                              + themeId );
        }
        theme.addStyleSheet( styleSheet );
      } catch( final Exception e ) {
        String text =   "Could not register theme contribution for theme ''{0}'' "
                      + "from file ''{1}''.";
        Object[] param = new Object[] { themeId, themeFile };
        String msg = MessageFormat.format( text, param );
        IStatus status = new Status( IStatus.ERROR, contributorName, msg, e );
        WorkbenchPlugin.getDefault().getLog().log( status );
      }
    }
    ThemeManager.getInstance().initialize();
  }

  private static ResourceLoader createResourceLoader( final Bundle bundle ) {
    ResourceLoader result = new ResourceLoader() {

      public InputStream getResourceAsStream( final String resourceName )
        throws IOException
      {
        InputStream result = null;
        IPath path = new Path( resourceName );
        URL url = FileLocator.find( bundle, path, null );
        if( url != null ) {
          result = url.openStream();
        }
        return result;
      }
    };
    return result;
  }

  private static void registerRWTLifeCycle() {
    // TODO: [fappel] ugly, ugly, ugly - replace this.
    //                Create the only valid lifecycle for RAP
    try {
      Class clazz = LifeCycleFactory.class;
      Field field = clazz.getDeclaredField( "globalLifeCycle" );
      field.setAccessible( true );
      field.set( null, new RWTLifeCycle() );
    } catch( final Throwable shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
  }

  // determine a faked context directory
  private static IPath findContextPath() {
    Bundle bundle = Platform.getBundle( PlatformUI.PLUGIN_ID );
    IPath stateLocation = Platform.getStateLocation( bundle );
    return stateLocation.append( "context" );
  }

  private static void createConfiguration( final File destination )
    throws FileNotFoundException, IOException
  {
    ClassLoader loader = EngineConfigWrapper.class.getClassLoader();
    InputStream is = loader.getResourceAsStream( CONFIG );
    try {
      OutputStream out = new FileOutputStream( destination );
      try {
        int character = is.read();
        while( character != -1 ) {
          out.write( character );
          character = is.read();
        }
      } finally {
        out.close();
      }
    } finally {
      is.close();
    }
  }

  private static void registerResources() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint point = registry.getExtensionPoint( ID_RESOURCES );
    IConfigurationElement[] elements = point.getConfigurationElements();
    for( int i = 0; i < elements.length; i++ ) {
      try {
        IResource resource
          = ( IResource )elements[ i ].createExecutableExtension( "class" );
        ResourceRegistry.add( resource );
      } catch( final CoreException ce ) {
        WorkbenchPlugin.getDefault().getLog().log( ce.getStatus() );
      }
    }
  }

  private static void registerUICallBackServiceHandler() {
    ServiceManager.registerServiceHandler( UICallBackServiceHandler.HANDLER_ID,
                                           new UICallBackServiceHandler() );
  }

  private void registerJSLibraryServiceHandler() {
    ServiceManager.registerServiceHandler( JSLibraryServiceHandler.HANDLER_ID,
                                           new JSLibraryServiceHandler() );
  }
  
  private void registerCustomServiceHandlers() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint point = registry.getExtensionPoint( ID_SERVICE_HANDLER );
    IConfigurationElement[] elements = point.getConfigurationElements();
    for( int i = 0; i < elements.length; i++ ) {
      try {
        String id = elements[ i ].getAttribute( "id" );
        if( id != null ) {
          Object extObject = elements[ i ].createExecutableExtension( "class" );
          IServiceHandler handler = ( IServiceHandler )extObject;
          ServiceManager.registerServiceHandler( id, handler );
        }
      } catch( final CoreException ce ) {
        WorkbenchPlugin.getDefault().getLog().log( ce.getStatus() );
      }
    }
  }

}

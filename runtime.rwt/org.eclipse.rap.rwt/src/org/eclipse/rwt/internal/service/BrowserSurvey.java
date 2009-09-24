/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.branding.BrandingUtil;
import org.eclipse.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rwt.internal.lifecycle.HtmlResponseWriter;
import org.eclipse.rwt.internal.theme.*;
import org.eclipse.rwt.internal.util.*;


/** 
 * <p>A helping class that loads a special html page in order to
 * determine which browser has originated the request.</p>
 */
public final class BrowserSurvey {

  /** 
   * <p>Writes a special html page into the passed HtmlResponseWriter,
   * in order to  determine which browser has originated the request.</p> 
   */
  static void sendBrowserSurvey() throws ServletException {
    if( LifeCycleServiceHandler.configurer.isStartupPageModifiedSince() ) {
      HttpServletRequest request = ContextProvider.getRequest();
      // first check whether a survey has already been sent
      String survey = request.getParameter( RequestParams.SURVEY );
      if( survey != null && survey.equals( "true" ) ) {
        String msg = "Initialization fault. Browser survey failed.";
        throw new ServletException( msg );
      }
      // send out the survey
      try {
        if( isAjaxRequest() ) {
          renderAjax();
        } else {
          renderScript();
        }
      } catch( IOException e ) {
        String txt = "Failed to load Browser Survey HTML Page (Reason: {0})";
        Object[] param = new Object[] { e.getMessage() };
        String msg = MessageFormat.format( txt, param );
        throw new ServletException( msg, e );
      }
    } else {
      AbstractBranding branding = BrandingUtil.findBranding();
      if( branding.getThemeId() != null ) {
        ThemeUtil.setCurrentThemeId( branding.getThemeId() );
      }
    }
  }

  private static String getBgImage() {
    String result = "";
    QxType cssValue = ThemeUtil.getCssValue( "Display", 
                                             "background-image", 
                                             SimpleSelector.DEFAULT );
    if( cssValue != null && cssValue instanceof QxImage ) {
      QxImage image = ( QxImage )cssValue;
      // path is null if non-existing image was specified in css file
      if( image.path != null ) {
        result = RWT.getResourceManager().getLocation( image.path );
      }
    }
    return result;
  }

  public static String getSerlvetName() {
    String result = ContextProvider.getRequest().getServletPath();
    if( result.startsWith( "/" ) ) {
      result = result.substring( 1 );
    }
    return result;
  }

  public static void replacePlaceholder( final StringBuffer buffer, 
                                         final String placeHolder, 
                                         final String replacement ) 
  {
    int index;
    index = buffer.indexOf( placeHolder );
    while( index != -1 ) {
      buffer.replace( index, index + placeHolder.length(), replacement );
      index = buffer.indexOf( placeHolder );
    }
  }
  
  private static void renderScript() throws IOException {
    ContextProvider.getResponse().setContentType( HTML.CONTENT_TEXT_HTML );
    TemplateHolder template
      = LifeCycleServiceHandler.configurer.getTemplateOfStartupPage();
    template.replace( TemplateHolder.VAR_BACKGROUND_IMAGE, getBgImage() );
    // TODO [fappel]: check whether servletName has to be url encoded
    //                in case the client has switched of cookies
    template.replace( TemplateHolder.VAR_SERVLET, getSerlvetName() );
    template.replace( TemplateHolder.VAR_FALLBACK_URL, createURL() );
    template.replace( TemplateHolder.VAR_ADMIN_OR_STARTUP, adminOrStartup() );
    template.replace( TemplateHolder.VAR_ENTRY_POINT,
                      EntitiesUtil.encodeHTMLEntities( getEntryPoint() ) );
    String[] tokens = template.getTokens();
    for( int i = 0; i < tokens.length; i++ ) {
      if( tokens[ i ] != null ) {
        getResponseWriter().append( tokens[ i ] );
      }
    }
  }

  private static void renderAjax() {
    ContextProvider.getResponse().setContentType( HTML.CONTENT_TEXT_XML );
    HtmlResponseWriter writer = getResponseWriter();
    writer.append( HTMLUtil.createXmlProcessingInstruction() );
    writer.append( HTML.START_AJAX_RESPONSE );
    StringBuffer code = new StringBuffer();
    code.append( "alert('" );
    code.append( "Your request was sent to an expired session.\\n" ); 
    code.append( "A new session will be initiated." );
    code.append( "');");
    code.append( "window.open( '");
    code.append( URLHelper.getURLString( false ) );
    code.append( "', '_self' );");
    writer.append( HTMLUtil.createJavaScriptInline( code.toString() ) );
    writer.append( HTML.END_AJAX_RESPONSE );
  }

  static String getResourceName() {
    String name = BrowserSurvey.class.getPackage().getName();
    return name.replace( '.', '/' ) + "/index.html";
  }
  
  private static String createURL() {
    StringBuffer url = new StringBuffer();
    url.append( URLHelper.getURLString( false ) );
    URLHelper.appendFirstParam( url, adminOrStartup(), getEntryPoint() );
    URLHelper.appendParam( url, RequestParams.SCRIPT, "false" );
    URLHelper.appendParam( url, RequestParams.AJAX_ENABLED, "false" );
    return ContextProvider.getResponse().encodeURL( url.toString() );
  }

  private static String getEntryPoint() {
    HttpServletRequest request = ContextProvider.getRequest();
    String result = request.getParameter( RequestParams.STARTUP );
    if( result == null ) {
      result = EntryPointManager.DEFAULT;
    }
    return result;
  }

  private static String adminOrStartup() {
    HttpServletRequest request = ContextProvider.getRequest();
    String parameter = request.getParameter( RequestParams.ADMIN );
    boolean admin = "true".equals( parameter );
    return admin ? RequestParams.ADMIN : RequestParams.STARTUP;
  }

  private static HtmlResponseWriter getResponseWriter() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    return stateInfo.getResponseWriter();
  }
  
  private static boolean isAjaxRequest() {
    HttpServletRequest request = ContextProvider.getRequest();
    String paramValue = request.getParameter( RequestParams.IS_AJAX_REQUEST );
    return "true".equals( paramValue );
  }
}
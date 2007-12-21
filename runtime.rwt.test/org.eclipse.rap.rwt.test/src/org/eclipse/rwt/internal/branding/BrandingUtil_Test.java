/*******************************************************************************
 * Copyright (c) 2002-2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.branding;

import junit.framework.TestCase;

import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.branding.Header;
import org.eclipse.swt.RWTFixture;


public class BrandingUtil_Test extends TestCase {
  
  private static class TestBranding extends AbstractBranding {
    String favIcon;
    Header[] headers;
    String exitMessage;
    public String getBody() {
      return null;
    }
    public String getExitMessage() {
      return exitMessage;
    }
    public String getFavIcon() {
      return favIcon;
    }
    public Header[] getHeaders() {
      return headers;
    }
  }
  
  public void testReplacePlaceholder() {
    StringBuffer buffer = new StringBuffer();
    buffer.append( "placeholder" );
    BrandingUtil.replacePlaceholder( buffer, "placeholder", "replacement" );
    assertEquals( "replacement", buffer.toString() );
    buffer.setLength( 0 );
    buffer.append( "null" );
    BrandingUtil.replacePlaceholder( buffer, "null", null );
    assertEquals( "", buffer.toString() );
  }
  
  public void testHeaderMarkup() {
    String expected;
    String markup;
    TestBranding branding = new TestBranding();
    // No fav icon
    branding.favIcon = null;
    markup = BrandingUtil.headerMarkup( branding );
    assertEquals( "", markup );
    branding.favIcon = "";
    markup = BrandingUtil.headerMarkup( branding );
    assertEquals( "", markup );
    // Ordinary fav icon
    branding.favIcon = "my/fav/icon.ico";
    markup = BrandingUtil.headerMarkup( branding );
    expected 
      = "<link rel=\"shortcut icon\" " 
      + "type=\"image/x-icon\" " 
      + "href=\"my/fav/icon.ico\" />\n"; 
    assertEquals( expected, markup );
    // Some header without attributes
    branding.favIcon = null;
    branding.headers = new Header[] {
      new Header( "meta", new String[ 0 ], new String[ 0 ] )
    };
    markup = BrandingUtil.headerMarkup( branding );
    assertEquals( "<meta />\n", markup );
    // Some header with attributes
    branding.favIcon = null;
    branding.headers = new Header[] {
      new Header( "meta", new String[] { "name" }, new String[] { "value" } )
    };
    markup = BrandingUtil.headerMarkup( branding );
    assertEquals( "<meta name=\"value\" />\n", markup );
    // Header with attributes that have a null-name or-value: will be ignored
    branding.favIcon = null;
    branding.headers = new Header[] {
      new Header( "meta", 
                  new String[] { null, "name", null }, 
                  new String[] { null, null, "value" } )
    };
    markup = BrandingUtil.headerMarkup( branding );
    assertEquals( "<meta />\n", markup );
    // fav icon and header
    branding.favIcon = "my/fav/icon.ico";
    branding.headers = new Header[] {
      new Header( "meta", new String[] { "name" }, new String[] { "value" } )
    };
    markup = BrandingUtil.headerMarkup( branding );
    expected 
      = "<link href=\"my/fav/icon.ico\" " 
      + "type=\"image/x-icon\" " 
      + "rel=\"shortcut icon\" />\n"
      + "<meta name=\"value\" />\n";
  }
  
  public void testExitMessageScript() {
    String script;
    TestBranding branding = new TestBranding();
    assertEquals( "", BrandingUtil.exitMessageScript( branding ) );
    branding.exitMessage = "";
    assertEquals( "", BrandingUtil.exitMessageScript( branding ) );
    branding.exitMessage = "want to exit?";
    script = BrandingUtil.exitMessageScript( branding );
    assertTrue( script.indexOf( "want to exit?" ) != -1 );
    branding.exitMessage = "\"\n";
    script = BrandingUtil.exitMessageScript( branding );
    assertEquals( "app.setExitConfirmation( \"\\\"\\n\" );", script );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}

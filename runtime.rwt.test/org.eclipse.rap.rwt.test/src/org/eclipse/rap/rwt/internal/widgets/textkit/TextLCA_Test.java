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

package org.eclipse.rap.rwt.internal.widgets.textkit;

import java.io.IOException;
import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.events.ModifyEvent;
import org.eclipse.rap.rwt.events.ModifyListener;
import org.eclipse.rap.rwt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.Fixture;
import com.w4t.engine.requests.RequestParams;

public class TextLCA_Test extends TestCase {

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    Text text = new Text( shell, RWT.NONE );
    text.setText( "abc" );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
    assertEquals( text.getText(), adapter.getPreserved( Props.TEXT ) );
    display.dispose();
  }

  public void testReadData() {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    Text text = new Text( shell, RWT.NONE );
    Fixture.fakeRequestParam( WidgetUtil.getId( text ) + ".text", "abc" );
    WidgetUtil.getLCA( text ).readData( text );
    assertEquals( "abc", text.getText() );
  }

  public void testRenderChanges() throws IOException {
    Fixture.fakeResponseWriter();
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Text text = new Text( shell, RWT.NONE );
    text.setText( "hello" );
    shell.open();
    RWTFixture.markInitialized( text );
    TextLCA textLCA = new TextLCA();
    textLCA.renderChanges( text );
    assertTrue( Fixture.getAllMarkup().endsWith( "setValue( \"hello\" );" ) );
    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    textLCA.renderChanges( text );
    assertEquals( "", Fixture.getAllMarkup() );
  }

  public void testModifyEvent() throws IOException {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    final Text text = new Text( shell, RWT.NONE );
    text.addModifyListener( new ModifyListener() {
      public void modifyText( final ModifyEvent event ) {
        assertEquals( text, event.getSource() );
        log.append( "modifyText" );
      }
    } );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    String textId = WidgetUtil.getId( text );
    
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, textId );
    new RWTLifeCycle().execute();
    assertEquals( "modifyText", log.toString() );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}

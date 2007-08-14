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
package com.w4t.webtextareakit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.HtmlResponseWriter;
import org.eclipse.rwt.internal.util.HTML;

import com.w4t.*;


/** <p>The default renderer for org.eclipse.rap.WebTextArea.</p>
  * <p>The default renderer is non-browser-specific and implements 
  * functionality in a way that runs on the most commonly used browsers.</p>
  */
public class WebTextAreaRenderer_Default_Script extends Renderer {
 
  public void readData( final WebComponent component ) {
    ReadDataUtil.applyValue( component );
  }
  
  public void processAction( final WebComponent component ) {
    ProcessActionUtil.processFocusGained( component );
    ProcessActionUtil.processItemStateChangedScript( component );
  }
  
  public void render( final WebComponent component ) throws IOException {
    WebTextArea textArea = ( WebTextArea )component;
    HtmlResponseWriter out = getResponseWriter();
    out.startElement( HTML.TEXTAREA, null );
    out.writeAttribute( HTML.ID, textArea.getUniqueID(), null );
    out.writeAttribute( HTML.NAME, textArea.getUniqueID(), null );
    out.writeAttribute( HTML.ROWS, String.valueOf( textArea.getRows() ), null );
    out.writeAttribute( HTML.COLS, String.valueOf( textArea.getCols() ), null );
    out.writeAttribute( HTML.WRAP, textArea.getWrap(), null );
    RenderUtil.writeUniversalAttributes( textArea );
    RenderUtil.writeReadOnly( textArea );
    RenderUtil.writeDisabled( textArea );
    EventUtil.createItemAndFocusHandler( textArea ); 
    out.writeText( WebTextAreaUtil.getValue( textArea ), null );
    out.endElement( HTML.TEXTAREA );
  }
}

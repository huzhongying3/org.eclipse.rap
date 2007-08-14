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
package com.w4t.dhtml.menubarkit;

import java.io.IOException;
import com.w4t.WebComponent;
import com.w4t.ajax.AjaxStatusUtil;
import com.w4t.dhtml.MenuBar;


/** <p>The renderer for org.eclipse.rap.dhtml.MenuBar on Opera 8 
 * and higher with AJaX support.</p>
 */
public class MenuBarRenderer_Opera8up_Ajax 
  extends MenuBarRenderer_Mozilla1_6up_Script
{
  
  public void render( final WebComponent component ) throws IOException {
    if( AjaxStatusUtil.mustRender( component ) ) {
      super.render( component );
    } else {
      MenuBarUtil.renderPopupMenu( ( MenuBar )component );
    }
  }
}
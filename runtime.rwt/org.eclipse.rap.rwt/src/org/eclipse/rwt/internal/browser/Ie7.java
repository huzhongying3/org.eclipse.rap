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
package org.eclipse.rwt.internal.browser;



/** <p>the implementation for Microsoft Internet Explorer 7.</p>
  */
public class Ie7 extends Ie6up {
  
  public Ie7( final boolean scriptEnabled ) {
    super( scriptEnabled );
  }
  
  public Ie7( final boolean scriptEnabled, final boolean ajaxEnabled ) {
    super( scriptEnabled, ajaxEnabled );
  }

  public Ie7( final Browser browser ) {
    super( browser );
  }
}

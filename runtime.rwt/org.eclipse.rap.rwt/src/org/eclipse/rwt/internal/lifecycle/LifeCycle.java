/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.io.IOException;
import javax.servlet.ServletException;

import org.eclipse.rwt.lifecycle.ILifeCycle;
import org.eclipse.rwt.lifecycle.PhaseListener;


/** <p>The superclass for all implementations of the lifecycle of a request.</p>
  *
  * <p>Implementations can be provided for different compatibility modes. 
  * LifeCycles are loaded depending on compatibility mode in the 
  * W4TModelCore using the org.eclipse.rap.engine.lifecycle.LifeCycleFactory.</p>
  */
public abstract class LifeCycle implements ILifeCycle {
  
  /** 
   * <p>Executes the lifecycle defined in this LifeCycle. Implementing 
   * subclasses use this as entry point to the processing of their phases.</p> 
   */
  public abstract void execute() throws ServletException, IOException;

  public abstract void addPhaseListener( PhaseListener listener );

  public abstract void removePhaseListener( PhaseListener listener );
  
  public abstract Scope getScope();
}

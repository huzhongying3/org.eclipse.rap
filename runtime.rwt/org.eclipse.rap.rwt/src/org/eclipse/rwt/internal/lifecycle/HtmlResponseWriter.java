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
package org.eclipse.rwt.internal.lifecycle;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

import org.eclipse.rwt.internal.util.*;



/** 
 * <p>This class assists in writing markup to the response stream.</p>
 * <p><b>Note:</b> Dont't use any of the <code>write</code>-Methods to start 
 * or end an <em>element</em>. Doing so will confuse 'outer' rendering code
 * and may lead to orphan closing angle brackets (&gt;). To start and end 
 * <em>elements</em> only use the semantic-aware methods
 * <code>startElement</code>, <code>endElement</code> and 
 * <code>closeElementIfStarted</code>.</p>
 */
public class HtmlResponseWriter extends Writer {

  /** <p>used for rendering internally.</p>
   * 
   * <p>Styles with exactly the same settings are collected while rendering
   * and a css class with these settings and prefixed CLASS_PREFIX is
   * rendered and assigned instead of rendering the same style settings
   * inline multiple times. This happens only if no class attribute
   * (see {@link SimpleComponent#getCssClass() SimpleComponent.getCssClass()})
   * is set; else style settings are inlined into the HTML. Note that in the
   * latter case the rendering and style interprettion behaviour of the
   * browser may cause some or all of either the style or the class settings
   * to take no effect.</p> 
   */
  // TODO [w4t] moved from Style, move back when dependency is resolved
  public static final String CLASS_PREFIX = "w4tCss";


  private List body = new ArrayList();
  
  private String elementStarted;
  private boolean closed;
  private boolean avoidEscape;


  /** contains the names of the javascript libraries that are needed for
    * the content which was rendered into this HtmlResponseWriter. */
  private List jsLibraries = new ArrayList();
  
  
  /** 
   * <p>Append a token to the token list of the body's token</p>
   * <p>This method is not inteded to be used by clients.</p>
   * */
  public void append( final String token ) {
    body.add( token );
  }

  /**
   * <p>Append a HtmlResponseWriter's body token list to the
   * token list of the body's token.</p>
   * <p>This method is not inteded to be used by clients.</p>
   */
  public void append( final HtmlResponseWriter responseWriter ) {
    for( int i = 0; i < responseWriter.getBodySize(); i++ ) {
      body.add( responseWriter.getBodyToken( i ) );
    }
  }

  /**
   * <p>Removes all of the tokens from the body list. The body list will be
   * empty after this call returns</p>
   * <p>This method is not inteded to be used by clients.</p>
   */
  public void clearBody() {
    body.clear();
  }

 /**
   * <p>Returns the number of tokens in the body list.</p>
   * <p>This method is not inteded to be used by clients.</p>
   */
  public int getBodySize() {
    return body.size();
  }

  /**
   * <p>Returns the token at the specified position in the body list.</p>
   * <p>This method is not inteded to be used by clients.</p>
   */
  public String getBodyToken( final int index ) {
    return body.get( index ).toString();
  }

  /**
   * <p>Returns an iterator to loop over all body tokens.</p>
   * <p>This method is not inteded to be used by clients.</p>
   */
  public Iterator bodyTokens() {
    return body.iterator();
  }

  ///////////////////////////////////////////////////
  // control methods for javascript library rendering

  /** <p>Returns the names of the JavaScript libraries that the components
    * which were rendered into this HtmlResponseWriter need.</p> */
  public String[] getJSLibraries() {
    String[] result = new String[ jsLibraries.size() ];
    jsLibraries.toArray( result );
    return result;
  }
  
  /**
   * <p>Informs the HtmlResponseWriter that the given library is needed. This
   * will cause a &lt;script&gt;-tag referencing the library to be rendered at
   * the adequate place.</p>
   * <p>Prior to calling this methid, the given <code>libraryName</code> must
   * be registered with the {@link org.eclipse.rwt.resources.IResourceManager 
   * IResourceManager} using one of these two register-methods: 
   * {@link org.eclipse.rwt.resources.IResourceManager#register(String, String) 
   * register(String, String)}, 
   * {@link org.eclipse.rwt.resources.IResourceManager#register(String, String, 
   * org.eclipse.rwt.resources.IResourceManager.RegisterOptions)
   * register(String, String, RegisterOptions)}</p>
   * <p>Calling this method for an already registered <code>libraryName</code>
   * has no effect.</p>
   * @param libraryName the name of the library, must not be <code>null</code>.
   */
  public void useJSLibrary( final String libraryName ) {
    ParamCheck.notNull( libraryName, "libraryName" );
    if( !jsLibraries.contains( libraryName ) ) {
      jsLibraries.add( libraryName );
    }
  }
  
  /**
   * <p>Removes the given <code>libraryName</code> from the list of JavaScript 
   * libraries.</p>
   * <p>An attempt to remove a not previously registered 
   * (using <code>useJSLibrary(String)</code>) library will be ignored silently.
   * </p>
   * @param libraryName the name of the library to be removed
   */
  public void removeJSLibraries( final String libraryName ) {
    jsLibraries.remove( libraryName );
  }
    
  /**
   * <p>Returns the number of JavaScript libraries that were registered by
   * calls to <code>useJSLibrary(String)</code>.</p>
   */
  public int getJSLibrariesCount() {
    return jsLibraries.size();
  }


  //////////////////
  // response writer
  
  public void close() throws IOException {
    checkIfWriterClosed();
    closeElementIfStarted();
    closed = true;
  }

  public void flush() throws IOException {
    checkIfWriterClosed();
    closeElementIfStarted();
  }
  
  public void write( final char[] cbuf, 
                     final int off, 
                     final int len )
    throws IOException
  {
    checkIfWriterClosed();
    closeElementIfStarted();
    doWrite( cbuf, off, len );
  }
  
  /**
   * <p>Writes the given <code>character</code> to the response stream.</p> 
   * @param character the single char to be written
   * @throws IOException if an I/O error occurs
   */
  public void write( final char character ) throws IOException {
    checkIfWriterClosed();
    // TODO [rh] replace with doWrite(String) ?
    append( new String( new char[]{ character } ) );
  }

  public void write( final int c ) throws IOException {
    checkIfWriterClosed();
    // TODO [rh] replace with doWrite(String) ?
    append( new String( new char[] { ( char )c } ) );
  }

  public void write( final String content ) throws IOException {
    checkIfWriterClosed();
    closeElementIfStarted();
    doWrite( content );
  }
  
  public void write( final String str, 
                     final int off, 
                     final int len ) 
    throws IOException
  {
    checkIfWriterClosed();
    // TODO [rh] replace with doWrite(String) ?
    append( str.substring( off, off + len ) );
  }
  
  /**
   * <p>Starts the element given by <code>name</code>. An eventually still
   * opened element will be closed first.</p>
   * @param name the elements name, must not be <code>null</code>.
   * @param component <em>currently not used, must be <code>null</code></em>.
   * @throws IOException if an I/O error occurs
   */
  public void startElement( final String name, final Object component ) 
    throws IOException
  {
    checkIfWriterClosed();
    ParamCheck.notNull( name, "name" );
    closeElementIfStarted();
    // checking first with charAt for performance, since equalsIgnoreCase check
    // takes twice as long
    char firstChar = name.charAt( 0 );
    if( ( firstChar == 's' ) || ( firstChar == 'S' ) ) {
      if(    "script".equalsIgnoreCase( name ) 
          || "style".equalsIgnoreCase( name ) ) 
      {
        avoidEscape = true;
      }
    }

    doWrite( "<" );
    doWrite( name );
    elementStarted = name;
  }

  /**
   * <p>Writes an attribute to the currently started element. Characters
   * not allowed in (X)HTML will be encoded.</p>
   * <p>Example:
   * <pre>
   * writer.startElement("input",null);
   * writer.writeAttribute("hidden",null,null);
   * writer.endElement("input");
   * // results in &lt;input hidden="hidden" /&gt; 
   * </pre>
   * </p>
   * @param name the attribtues name, must not be <code>null</code>.
   * @param value the attributes value. If <code>null</code> it is considered
   * a 'minimized' attribute.
   * @param property <em>currently not used, must be <code>null</code></em>.
   * @throws IOException if an I/O error occurs
   * @throws IllegalStateException when there is no started element
   */
  public void writeAttribute( final String name, 
                              final Object value, 
                              final String property )
    throws IOException
  {
    checkIfWriterClosed();
    ParamCheck.notNull( name, "name" );
    if( elementStarted == null ) {
      String msg = "There is no started element to add an attribute.";
      throw new IllegalStateException( msg );
    }
    if( value == null ) {
      doWrite( " " );
      doWrite( name );
      doWrite( "=\"" );
      doWrite( name );
      doWrite( "\"" );        
    } else {
      doWrite( " " );
      doWrite( name );
      doWrite( "=\"" );
      doWrite( HtmlResponseWriterUtil.encode( value.toString() ) );
      doWrite( "\"" );
    }
  }

  /**
   * <p>Writes the closing tag for element <code>name</code> to the response 
   * stream.</p>
   * @param name the elements name, must not be <code>null</code>. 
   * @throws IOException if an I/O error occurs
   */
  public void endElement( final String name ) throws IOException {
    checkIfWriterClosed();
    ParamCheck.notNull( name, "name" );
    closeElementIfStarted();
    avoidEscape = false;
    if( !HtmlResponseWriterUtil.isEmptyTag( name ) ) {
      doWrite( "</" );
      doWrite( name );
      doWrite( ">" );
    }
  }
  
  /**
   * <p>Ends the document. An eventually opened element is closed.</p>
   * @throws IOException if an I/O error occurs
   */
  public void endDocument() throws IOException {
    checkIfWriterClosed();
    closeElementIfStarted();
  }
  
  /**
   * <p>Writes the <em>toString</em> of the given <code>text</code> to the 
   * response stream, characters not allowed in HTML will be encoded. Use 
   * this method only to write 'inside' an element.</p>
   * <p>Example:
   * <pre>
   * writer.startElement("p",null);
   * <strong>writer.writeText("Hello World");</strong>
   * writer.endElement("p");
   * </pre></p>
   * @param text the text to write, must not be <code>null</code>.
   * @param property <em>currently not used, must be <code>null</code></em> 
   * @throws IOException if an I/O error occurs
   */
  // TODO [rh] surround content of script tags with [CDATA[ section 
  public void writeText( final Object text, final String property )
    throws IOException
  {
    checkIfWriterClosed();
    ParamCheck.notNull( text, "text" );
    closeElementIfStarted();
    if( avoidEscape ) {
      doWrite( text.toString() );
    } else {
      doWrite( HtmlResponseWriterUtil.encode( text.toString() ) );
    }
  }

  /**
   * <p>Writes a portion of an array of characters.</p>
   * @param text array of characters, must not be <code>null</code>
   * @param off offset from which to start writing characters
   * @param len number of characters to write
   * @throws IOException if an I/O error occurs
   */
  public void writeText( final char[] text, final int off, final int len )
    throws IOException 
  {
    checkIfWriterClosed();
    ParamCheck.notNull( text, "text" );
    closeElementIfStarted();
    if( avoidEscape ) {
      doWrite( text, off, len );
    } else {
      char[] content = new char[ len ];
      System.arraycopy( text, off, content, 0, len );
      StringBuffer buffer = new StringBuffer();
      buffer.append( HtmlResponseWriterUtil.encode( new String( content ) ) );
      char[] encoded = buffer.toString().toCharArray();
      write( encoded );
    }
  }
  
  /**
   * <p>Writes the given <code>comment</code>, surrounded by opening and
   * closing comment tags (&lt;!-- --&gt;), to the response stream.</p>
   * @param comment the comment to be written, must not be <code>null</code>.
   * @throws IOException if an I/O error occurs
   */
  // TODO [rh] We could check whether 'elementStarted' is null, since comments 
  //      are not allowed inside element tags in XHTML 'mode'
  public void writeComment( final Object comment ) throws IOException {
    checkIfWriterClosed();
    ParamCheck.notNull( comment, "comment" ); 
    closeElementIfStarted();
    doWrite( "<!-- " );
    doWrite( HtmlResponseWriterUtil.encode( comment.toString() ) );
    doWrite( " -->" );
  }

  /**
   * <p>Writes a non-breaking space (&amp;nbsp;) to the response stream.</p>
   * @throws IOException if an I/O error occurs
   */
  public void writeNBSP() throws IOException { 
    checkIfWriterClosed();
    closeElementIfStarted();
    doWrite( "&nbsp;" );
  }
  
  /**
   * <p>Starts the document</p>
   * @throws IOException if an I/O error occurs
   */
  public void startDocument() throws IOException {
    checkIfWriterClosed();
  }

  /**
   * <p>Writes appropriate markup to close an eventually started element (see
   * {@link #startElement(String, Object) startElement(String,Object)}). Does 
   * nothing if no element was started.</p>
   * <p>Usually it is not necessary to call this method explicitly, since
   * the semantic-aware methods like <code>writeText</code> and 
   * <code>closeElement</code> take care about not yet closed elements.</p>
   * <p>Example:
   * <pre>
   * writer.startElement("div",null);
   * writer.writeAttribute("id","x",null);
   * writer.closeElementIfStarted();
   * // results in &lt;div id="x" /&gt; 
   * </pre></p>
   * @throws IOException if an I/O error occurs
   */
  public void closeElementIfStarted() {
    if( elementStarted != null ) {
      if( HtmlResponseWriterUtil.isEmptyTag( elementStarted ) ) {
        doWrite( " />" );
      } else {
        doWrite( ">" );
      }
      elementStarted = null;
    }
  }

  protected void doWrite( final String content ) {
    append( content );    
  }

  private void doWrite( final char[] cbuf, 
                        final int off, 
                        final int len )
  {
    // TODO [rh] replace with doWrite(String) ?
    append( String.valueOf( cbuf, off, len ) );    
  }

  // helping methods
  //////////////////

  private void checkIfWriterClosed() {
    if( closed ) {
      String msg = "Operation is not allowed since the writer was closed.";
      throw new IllegalStateException( msg );
    }
  }
}
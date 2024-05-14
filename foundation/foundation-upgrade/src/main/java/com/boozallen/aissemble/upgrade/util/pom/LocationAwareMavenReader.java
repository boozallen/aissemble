package com.boozallen.aissemble.upgrade.util.pom;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.DateFormat;
import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationFile;
import org.apache.maven.model.ActivationOS;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.ConfigurationContainer;
import org.apache.maven.model.Contributor;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.Developer;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Extension;
import org.apache.maven.model.FileSet;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.InputLocationTracker;
import org.apache.maven.model.InputSource;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.License;
import org.apache.maven.model.MailingList;
import org.apache.maven.model.Model;
import org.apache.maven.model.ModelBase;
import org.apache.maven.model.Notifier;
import org.apache.maven.model.Organization;
import org.apache.maven.model.Parent;
import org.apache.maven.model.PatternSet;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginConfiguration;
import org.apache.maven.model.PluginContainer;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Prerequisites;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Relocation;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.ReportSet;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.Repository;
import org.apache.maven.model.RepositoryBase;
import org.apache.maven.model.RepositoryPolicy;
import org.apache.maven.model.Resource;
import org.apache.maven.model.Scm;
import org.apache.maven.model.Site;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.xml.pull.EntityReplacementMap;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * This class is nearly an exact copy of {@link org.apache.maven.model.io.xpp3.MavenXpp3ReaderEx} (which tracks the
 * location of model elements in the original source POM).  It extends the location tracking to attach the start and end
 * character of the entire block of XML for each element.  This is useful for editing the file in place and preserving
 * the original formatting.  In order to fully support this, some elements which previously did not have location
 * tracking have been updated to include it.
 */
@SuppressWarnings( "all" )
public class LocationAwareMavenReader
{

      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * If set the parser will be loaded with all single characters
     * from the XHTML specification.
     * The entities used:
     * <ul>
     * <li>http://www.w3.org/TR/xhtml1/DTD/xhtml-lat1.ent</li>
     * <li>http://www.w3.org/TR/xhtml1/DTD/xhtml-special.ent</li>
     * <li>http://www.w3.org/TR/xhtml1/DTD/xhtml-symbol.ent</li>
     * </ul>
     */
    private boolean addDefaultEntities = true;

    /**
     * Field contentTransformer.
     */
    public final LocationAwareMavenReader.ContentTransformer contentTransformer;


      //----------------/
     //- Constructors -/
    //----------------/

    public LocationAwareMavenReader()
    {
        this( new LocationAwareMavenReader.ContentTransformer()
        {
            public String transform( String source, String fieldName )
            {
                return source;
            }
        } );
    } //-- org.apache.maven.model.io.xpp3.MavenXpp3ReaderEx()

    public LocationAwareMavenReader(LocationAwareMavenReader.ContentTransformer contentTransformer)
    {
        this.contentTransformer = contentTransformer;
    } //-- org.apache.maven.model.io.xpp3.MavenXpp3ReaderEx(ContentTransformer)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method checkFieldWithDuplicate.
     *
     * @param parser
     * @param parsed
     * @param alias
     * @param tagName
     * @throws XmlPullParserException
     * @return boolean
     */
    private boolean checkFieldWithDuplicate( XmlPullParser parser, String tagName, String alias, java.util.Set parsed )
        throws XmlPullParserException
    {
        if ( !( parser.getName().equals( tagName ) || parser.getName().equals( alias ) ) )
        {
            return false;
        }
        if ( !parsed.add( tagName ) )
        {
            throw new XmlPullParserException( "Duplicated tag: '" + tagName + "'", parser, null );
        }
        return true;
    } //-- boolean checkFieldWithDuplicate( XmlPullParser, String, String, java.util.Set )

    /**
     * Method checkUnknownAttribute.
     *
     * @param parser
     * @param strict
     * @param tagName
     * @param attribute
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void checkUnknownAttribute( XmlPullParser parser, String attribute, String tagName, boolean strict )
        throws XmlPullParserException, IOException
    {
        // strictXmlAttributes = true for model: if strict == true, not only elements are checked but attributes too
        if ( strict )
        {
            throw new XmlPullParserException( "Unknown attribute '" + attribute + "' for tag '" + tagName + "'", parser, null );
        }
    } //-- void checkUnknownAttribute( XmlPullParser, String, String, boolean )

    /**
     * Method checkUnknownElement.
     *
     * @param parser
     * @param strict
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void checkUnknownElement( XmlPullParser parser, boolean strict )
        throws XmlPullParserException, IOException
    {
        if ( strict )
        {
            throw new XmlPullParserException( "Unrecognised tag: '" + parser.getName() + "'", parser, null );
        }

        for ( int unrecognizedTagCount = 1; unrecognizedTagCount > 0; )
        {
            int eventType = parser.next();
            if ( eventType == XmlPullParser.START_TAG )
            {
                unrecognizedTagCount++;
            }
            else if ( eventType == XmlPullParser.END_TAG )
            {
                unrecognizedTagCount--;
            }
        }
    } //-- void checkUnknownElement( XmlPullParser, boolean )

    /**
     * Returns the state of the "add default entities" flag.
     *
     * @return boolean
     */
    public boolean getAddDefaultEntities()
    {
        return addDefaultEntities;
    } //-- boolean getAddDefaultEntities()

    /**
     * Method getBooleanValue.
     *
     * @param s
     * @param parser
     * @param attribute
     * @throws XmlPullParserException
     * @return boolean
     */
    private boolean getBooleanValue( String s, String attribute, XmlPullParser parser )
        throws XmlPullParserException
    {
        return getBooleanValue( s, attribute, parser, null );
    } //-- boolean getBooleanValue( String, String, XmlPullParser )

    /**
     * Method getBooleanValue.
     *
     * @param s
     * @param defaultValue
     * @param parser
     * @param attribute
     * @throws XmlPullParserException
     * @return boolean
     */
    private boolean getBooleanValue( String s, String attribute, XmlPullParser parser, String defaultValue )
        throws XmlPullParserException
    {
        if ( s != null && s.length() != 0 )
        {
            return Boolean.valueOf( s ).booleanValue();
        }
        if ( defaultValue != null )
        {
            return Boolean.valueOf( defaultValue ).booleanValue();
        }
        return false;
    } //-- boolean getBooleanValue( String, String, XmlPullParser, String )

    /**
     * Method getByteValue.
     *
     * @param s
     * @param strict
     * @param parser
     * @param attribute
     * @throws XmlPullParserException
     * @return byte
     */
    private byte getByteValue( String s, String attribute, XmlPullParser parser, boolean strict )
        throws XmlPullParserException
    {
        if ( s != null )
        {
            try
            {
                return Byte.valueOf( s ).byteValue();
            }
            catch ( NumberFormatException nfe )
            {
                if ( strict )
                {
                    throw new XmlPullParserException( "Unable to parse element '" + attribute + "', must be a byte", parser, nfe );
                }
            }
        }
        return 0;
    } //-- byte getByteValue( String, String, XmlPullParser, boolean )

    /**
     * Method getCharacterValue.
     *
     * @param s
     * @param parser
     * @param attribute
     * @throws XmlPullParserException
     * @return char
     */
    private char getCharacterValue( String s, String attribute, XmlPullParser parser )
        throws XmlPullParserException
    {
        if ( s != null )
        {
            return s.charAt( 0 );
        }
        return 0;
    } //-- char getCharacterValue( String, String, XmlPullParser )

    /**
     * Method getDateValue.
     *
     * @param s
     * @param parser
     * @param attribute
     * @throws XmlPullParserException
     * @return Date
     */
    private java.util.Date getDateValue( String s, String attribute, XmlPullParser parser )
        throws XmlPullParserException
    {
        return getDateValue( s, attribute, null, parser );
    } //-- java.util.Date getDateValue( String, String, XmlPullParser )

    /**
     * Method getDateValue.
     *
     * @param s
     * @param parser
     * @param dateFormat
     * @param attribute
     * @throws XmlPullParserException
     * @return Date
     */
    private java.util.Date getDateValue( String s, String attribute, String dateFormat, XmlPullParser parser )
        throws XmlPullParserException
    {
        if ( s != null )
        {
            String effectiveDateFormat = dateFormat;
            if ( dateFormat == null )
            {
                effectiveDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";
            }
            if ( "long".equals( effectiveDateFormat ) )
            {
                try
                {
                    return new java.util.Date( Long.parseLong( s ) );
                }
                catch ( NumberFormatException e )
                {
                    throw new XmlPullParserException( e.getMessage(), parser, e );
                }
            }
            else
            {
                try
                {
                    DateFormat dateParser = new java.text.SimpleDateFormat( effectiveDateFormat, java.util.Locale.US );
                    return dateParser.parse( s );
                }
                catch ( java.text.ParseException e )
                {
                    throw new XmlPullParserException( e.getMessage(), parser, e );
                }
            }
        }
        return null;
    } //-- java.util.Date getDateValue( String, String, String, XmlPullParser )

    /**
     * Method getDoubleValue.
     *
     * @param s
     * @param strict
     * @param parser
     * @param attribute
     * @throws XmlPullParserException
     * @return double
     */
    private double getDoubleValue( String s, String attribute, XmlPullParser parser, boolean strict )
        throws XmlPullParserException
    {
        if ( s != null )
        {
            try
            {
                return Double.valueOf( s ).doubleValue();
            }
            catch ( NumberFormatException nfe )
            {
                if ( strict )
                {
                    throw new XmlPullParserException( "Unable to parse element '" + attribute + "', must be a floating point number", parser, nfe );
                }
            }
        }
        return 0;
    } //-- double getDoubleValue( String, String, XmlPullParser, boolean )

    /**
     * Method getFloatValue.
     *
     * @param s
     * @param strict
     * @param parser
     * @param attribute
     * @throws XmlPullParserException
     * @return float
     */
    private float getFloatValue( String s, String attribute, XmlPullParser parser, boolean strict )
        throws XmlPullParserException
    {
        if ( s != null )
        {
            try
            {
                return Float.valueOf( s ).floatValue();
            }
            catch ( NumberFormatException nfe )
            {
                if ( strict )
                {
                    throw new XmlPullParserException( "Unable to parse element '" + attribute + "', must be a floating point number", parser, nfe );
                }
            }
        }
        return 0;
    } //-- float getFloatValue( String, String, XmlPullParser, boolean )

    /**
     * Method getIntegerValue.
     *
     * @param s
     * @param strict
     * @param parser
     * @param attribute
     * @throws XmlPullParserException
     * @return int
     */
    private int getIntegerValue( String s, String attribute, XmlPullParser parser, boolean strict )
        throws XmlPullParserException
    {
        if ( s != null )
        {
            try
            {
                return Integer.valueOf( s ).intValue();
            }
            catch ( NumberFormatException nfe )
            {
                if ( strict )
                {
                    throw new XmlPullParserException( "Unable to parse element '" + attribute + "', must be an integer", parser, nfe );
                }
            }
        }
        return 0;
    } //-- int getIntegerValue( String, String, XmlPullParser, boolean )

    /**
     * Method getLongValue.
     *
     * @param s
     * @param strict
     * @param parser
     * @param attribute
     * @throws XmlPullParserException
     * @return long
     */
    private long getLongValue( String s, String attribute, XmlPullParser parser, boolean strict )
        throws XmlPullParserException
    {
        if ( s != null )
        {
            try
            {
                return Long.valueOf( s ).longValue();
            }
            catch ( NumberFormatException nfe )
            {
                if ( strict )
                {
                    throw new XmlPullParserException( "Unable to parse element '" + attribute + "', must be a long integer", parser, nfe );
                }
            }
        }
        return 0;
    } //-- long getLongValue( String, String, XmlPullParser, boolean )

    /**
     * Method getRequiredAttributeValue.
     *
     * @param s
     * @param strict
     * @param parser
     * @param attribute
     * @throws XmlPullParserException
     * @return String
     */
    private String getRequiredAttributeValue( String s, String attribute, XmlPullParser parser, boolean strict )
        throws XmlPullParserException
    {
        if ( s == null )
        {
            if ( strict )
            {
                throw new XmlPullParserException( "Missing required value for attribute '" + attribute + "'", parser, null );
            }
        }
        return s;
    } //-- String getRequiredAttributeValue( String, String, XmlPullParser, boolean )

    /**
     * Method getShortValue.
     *
     * @param s
     * @param strict
     * @param parser
     * @param attribute
     * @throws XmlPullParserException
     * @return short
     */
    private short getShortValue( String s, String attribute, XmlPullParser parser, boolean strict )
        throws XmlPullParserException
    {
        if ( s != null )
        {
            try
            {
                return Short.valueOf( s ).shortValue();
            }
            catch ( NumberFormatException nfe )
            {
                if ( strict )
                {
                    throw new XmlPullParserException( "Unable to parse element '" + attribute + "', must be a short integer", parser, nfe );
                }
            }
        }
        return 0;
    } //-- short getShortValue( String, String, XmlPullParser, boolean )

    /**
     * Method getTrimmedValue.
     *
     * @param s
     * @return String
     */
    private String getTrimmedValue( String s )
    {
        if ( s != null )
        {
            s = s.trim();
        }
        return s;
    } //-- String getTrimmedValue( String )

    /**
     * Method interpolatedTrimmed.
     *
     * @param value
     * @param context
     * @return String
     */
    private String interpolatedTrimmed( String value, String context )
    {
        return getTrimmedValue( contentTransformer.transform( value, context ) );
    } //-- String interpolatedTrimmed( String, String )

    /**
     * Method nextTag.
     *
     * @param parser
     * @throws IOException
     * @throws XmlPullParserException
     * @return int
     */
    private int nextTag( XmlPullParser parser )
        throws IOException, XmlPullParserException
    {
        int eventType = parser.next();
        if ( eventType == XmlPullParser.TEXT )
        {
            eventType = parser.next();
        }
        if ( eventType != XmlPullParser.START_TAG && eventType != XmlPullParser.END_TAG )
        {
            throw new XmlPullParserException( "expected START_TAG or END_TAG not " + XmlPullParser.TYPES[eventType], parser, null );
        }
        return eventType;
    } //-- int nextTag( XmlPullParser )

    /**
     * @see ReaderFactory#newXmlReader
     *
     * @param reader
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return Model
     */
    public Model read( Reader reader, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        XmlPullParser parser = addDefaultEntities ? new MXParser(EntityReplacementMap.defaultEntityReplacementMap) : new MXParser( );

        parser.setInput( reader );


        return read( parser, strict, source );
    } //-- Model read( Reader, boolean, InputSource )

    /**
     * Method read.
     *
     * @param in
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return Model
     */
    public Model read( InputStream in, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        return read( ReaderFactory.newXmlReader( in ), strict, source );
    } //-- Model read( InputStream, boolean, InputSource )

    /**
     * Method parseActivation.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return Activation
     */
    private Activation parseActivation( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        Activation activation = new Activation();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        activation.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "activeByDefault", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                activation.setLocation( "activeByDefault", _location );
                activation.setActiveByDefault( getBooleanValue( interpolatedTrimmed( parser.nextText(), "activeByDefault" ), "activeByDefault", parser, "false" ) );
            }
            else if ( checkFieldWithDuplicate( parser, "jdk", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                activation.setLocation( "jdk", _location );
                activation.setJdk( interpolatedTrimmed( parser.nextText(), "jdk" ) );
                setWrapLocation( activation, parser, "jdk" );
            }
            else if ( checkFieldWithDuplicate( parser, "os", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                activation.setLocation( "os", _location );
                activation.setOs( parseActivationOS( parser, strict, source ) );
                setWrapLocation( activation, parser, "os" );
            }
            else if ( checkFieldWithDuplicate( parser, "property", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                activation.setLocation( "property", _location );
                activation.setProperty( parseActivationProperty( parser, strict, source ) );
                setWrapLocation( activation, parser, "property" );
            }
            else if ( checkFieldWithDuplicate( parser, "file", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                activation.setLocation( "file", _location );
                activation.setFile( parseActivationFile( parser, strict, source ) );
                setWrapLocation( activation, parser, "file" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( activation, parser );
        return activation;
    } //-- Activation parseActivation( XmlPullParser, boolean, InputSource )

    /**
     * Method parseActivationFile.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return ActivationFile
     */
    private ActivationFile parseActivationFile( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        ActivationFile activationFile = new ActivationFile();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        activationFile.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "missing", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                activationFile.setLocation( "missing", _location );
                activationFile.setMissing( interpolatedTrimmed( parser.nextText(), "missing" ) );
                setWrapLocation( activationFile, parser, "missing" );
            }
            else if ( checkFieldWithDuplicate( parser, "exists", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                activationFile.setLocation( "exists", _location );
                activationFile.setExists( interpolatedTrimmed( parser.nextText(), "exists" ) );
                setWrapLocation( activationFile, parser, "exists" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( activationFile, parser );
        return activationFile;
    } //-- ActivationFile parseActivationFile( XmlPullParser, boolean, InputSource )

    /**
     * Method parseActivationOS.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return ActivationOS
     */
    private ActivationOS parseActivationOS( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        ActivationOS activationOS = new ActivationOS();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        activationOS.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "name", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                activationOS.setLocation( "name", _location );
                activationOS.setName( interpolatedTrimmed( parser.nextText(), "name" ) );
                setWrapLocation( activationOS, parser, "name" );
            }
            else if ( checkFieldWithDuplicate( parser, "family", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                activationOS.setLocation( "family", _location );
                activationOS.setFamily( interpolatedTrimmed( parser.nextText(), "family" ) );
                setWrapLocation( activationOS, parser, "family" );
            }
            else if ( checkFieldWithDuplicate( parser, "arch", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                activationOS.setLocation( "arch", _location );
                activationOS.setArch( interpolatedTrimmed( parser.nextText(), "arch" ) );
                setWrapLocation( activationOS, parser, "arch" );
            }
            else if ( checkFieldWithDuplicate( parser, "version", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                activationOS.setLocation( "version", _location );
                activationOS.setVersion( interpolatedTrimmed( parser.nextText(), "version" ) );
                setWrapLocation( activationOS, parser, "version" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( activationOS, parser );
        return activationOS;
    } //-- ActivationOS parseActivationOS( XmlPullParser, boolean, InputSource )

    /**
     * Method parseActivationProperty.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return ActivationProperty
     */
    private ActivationProperty parseActivationProperty( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        ActivationProperty activationProperty = new ActivationProperty();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        activationProperty.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "name", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                activationProperty.setLocation( "name", _location );
                activationProperty.setName( interpolatedTrimmed( parser.nextText(), "name" ) );
                setWrapLocation( activationProperty, parser, "name" );
            }
            else if ( checkFieldWithDuplicate( parser, "value", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                activationProperty.setLocation( "value", _location );
                activationProperty.setValue( interpolatedTrimmed( parser.nextText(), "value" ) );
                setWrapLocation( activationProperty, parser, "value" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }

        setWrapLocation( activationProperty, parser );
        return activationProperty;
    } //-- ActivationProperty parseActivationProperty( XmlPullParser, boolean, InputSource )

    /**
     * Method parseBuild.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return Build
     */
    private Build parseBuild( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        Build build = new Build();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        build.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "sourceDirectory", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                build.setLocation( "sourceDirectory", _location );
                build.setSourceDirectory( interpolatedTrimmed( parser.nextText(), "sourceDirectory" ) );
                setWrapLocation( build, parser, "sourceDirectory" );
            }
            else if ( checkFieldWithDuplicate( parser, "scriptSourceDirectory", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                build.setLocation( "scriptSourceDirectory", _location );
                build.setScriptSourceDirectory( interpolatedTrimmed( parser.nextText(), "scriptSourceDirectory" ) );
                setWrapLocation( build, parser, "scriptSourceDirectory" );
            }
            else if ( checkFieldWithDuplicate( parser, "testSourceDirectory", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                build.setLocation( "testSourceDirectory", _location );
                build.setTestSourceDirectory( interpolatedTrimmed( parser.nextText(), "testSourceDirectory" ) );
                setWrapLocation( build, parser, "testSourceDirectory" );
            }
            else if ( checkFieldWithDuplicate( parser, "outputDirectory", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                build.setLocation( "outputDirectory", _location );
                build.setOutputDirectory( interpolatedTrimmed( parser.nextText(), "outputDirectory" ) );
                setWrapLocation( build, parser, "outputDirectory" );
            }
            else if ( checkFieldWithDuplicate( parser, "testOutputDirectory", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                build.setLocation( "testOutputDirectory", _location );
                build.setTestOutputDirectory( interpolatedTrimmed( parser.nextText(), "testOutputDirectory" ) );
                setWrapLocation( build, parser, "testOutputDirectory" );
            }
            else if ( checkFieldWithDuplicate( parser, "extensions", null, parsed ) )
            {
                java.util.List<Extension> extensions = new java.util.ArrayList<Extension>();
                build.setExtensions( extensions );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                build.setLocation( "extensions", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "extension".equals( parser.getName() ) )
                    {
                        extensions.add( parseExtension( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( build, parser, "extensions" );
            }
            else if ( checkFieldWithDuplicate( parser, "defaultGoal", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                build.setLocation( "defaultGoal", _location );
                build.setDefaultGoal( interpolatedTrimmed( parser.nextText(), "defaultGoal" ) );
                setWrapLocation( build, parser, "defaultGoal" );
            }
            else if ( checkFieldWithDuplicate( parser, "resources", null, parsed ) )
            {
                java.util.List<Resource> resources = new java.util.ArrayList<Resource>();
                build.setResources( resources );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                build.setLocation( "resources", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "resource".equals( parser.getName() ) )
                    {
                        resources.add( parseResource( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( build, parser, "resources" );
            }
            else if ( checkFieldWithDuplicate( parser, "testResources", null, parsed ) )
            {
                java.util.List<Resource> testResources = new java.util.ArrayList<Resource>();
                build.setTestResources( testResources );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                build.setLocation( "testResources", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "testResource".equals( parser.getName() ) )
                    {
                        testResources.add( parseResource( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( build, parser, "testResources" );
            }
            else if ( checkFieldWithDuplicate( parser, "directory", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                build.setLocation( "directory", _location );
                build.setDirectory( interpolatedTrimmed( parser.nextText(), "directory" ) );
                setWrapLocation( build, parser, "directory" );
            }
            else if ( checkFieldWithDuplicate( parser, "finalName", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                build.setLocation( "finalName", _location );
                build.setFinalName( interpolatedTrimmed( parser.nextText(), "finalName" ) );
                setWrapLocation( build, parser, "finalName" );
            }
            else if ( checkFieldWithDuplicate( parser, "filters", null, parsed ) )
            {
                java.util.List<String> filters = new java.util.ArrayList<String>();
                build.setFilters( filters );
                InputLocation _locations;
                _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                build.setLocation( "filters", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "filter".equals( parser.getName() ) )
                    {
                        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                        _locations.setLocation( Integer.valueOf( filters.size() ), _location );
                        filters.add( interpolatedTrimmed( parser.nextText(), "filters" ) );
                        setWrapLocation( _locations, parser, filters.size()-1 );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( build, parser, "filters" );
            }
            else if ( checkFieldWithDuplicate( parser, "pluginManagement", null, parsed ) )
            {
                build.setPluginManagement( parsePluginManagement( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "plugins", null, parsed ) )
            {
                java.util.List<Plugin> plugins = new java.util.ArrayList<Plugin>();
                build.setPlugins( plugins );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                build.setLocation( "plugins", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "plugin".equals( parser.getName() ) )
                    {
                        plugins.add( parsePlugin( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( build, parser, "plugins" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( build, parser );
        return build;
    } //-- Build parseBuild( XmlPullParser, boolean, InputSource )

    /**
     * Method parseBuildBase.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return BuildBase
     */
    private BuildBase parseBuildBase( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        BuildBase buildBase = new BuildBase();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        buildBase.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "defaultGoal", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                buildBase.setLocation( "defaultGoal", _location );
                buildBase.setDefaultGoal( interpolatedTrimmed( parser.nextText(), "defaultGoal" ) );
                setWrapLocation( buildBase, parser, "defaultGoal" );
            }
            else if ( checkFieldWithDuplicate( parser, "resources", null, parsed ) )
            {
                java.util.List<Resource> resources = new java.util.ArrayList<Resource>();
                buildBase.setResources( resources );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                buildBase.setLocation( "resources", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "resource".equals( parser.getName() ) )
                    {
                        resources.add( parseResource( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( buildBase, parser, "resources" );
            }
            else if ( checkFieldWithDuplicate( parser, "testResources", null, parsed ) )
            {
                java.util.List<Resource> testResources = new java.util.ArrayList<Resource>();
                buildBase.setTestResources( testResources );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                buildBase.setLocation( "testResources", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "testResource".equals( parser.getName() ) )
                    {
                        testResources.add( parseResource( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( buildBase, parser, "testResources" );
            }
            else if ( checkFieldWithDuplicate( parser, "directory", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                buildBase.setLocation( "directory", _location );
                buildBase.setDirectory( interpolatedTrimmed( parser.nextText(), "directory" ) );
                setWrapLocation( buildBase, parser, "directory" );
            }
            else if ( checkFieldWithDuplicate( parser, "finalName", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                buildBase.setLocation( "finalName", _location );
                buildBase.setFinalName( interpolatedTrimmed( parser.nextText(), "finalName" ) );
                setWrapLocation( buildBase, parser, "finalName" );
            }
            else if ( checkFieldWithDuplicate( parser, "filters", null, parsed ) )
            {
                java.util.List<String> filters = new java.util.ArrayList<String>();
                buildBase.setFilters( filters );
                InputLocation _locations;
                _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                buildBase.setLocation( "filters", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "filter".equals( parser.getName() ) )
                    {
                        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                        _locations.setLocation( Integer.valueOf( filters.size() ), _location );
                        filters.add( interpolatedTrimmed( parser.nextText(), "filters" ) );
                        setWrapLocation( _locations, parser, filters.size()-1 );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( buildBase, parser, "filters" );
            }
            else if ( checkFieldWithDuplicate( parser, "pluginManagement", null, parsed ) )
            {
                buildBase.setPluginManagement( parsePluginManagement( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "plugins", null, parsed ) )
            {
                java.util.List<Plugin> plugins = new java.util.ArrayList<Plugin>();
                buildBase.setPlugins( plugins );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                buildBase.setLocation( "plugins", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "plugin".equals( parser.getName() ) )
                    {
                        plugins.add( parsePlugin( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( buildBase, parser, "plugins" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( buildBase, parser );
        return buildBase;
    } //-- BuildBase parseBuildBase( XmlPullParser, boolean, InputSource )

    /**
     * Method parseCiManagement.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return CiManagement
     */
    private CiManagement parseCiManagement( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        CiManagement ciManagement = new CiManagement();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        ciManagement.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "system", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                ciManagement.setLocation( "system", _location );
                ciManagement.setSystem( interpolatedTrimmed( parser.nextText(), "system" ) );
                setWrapLocation( ciManagement, parser, "system" );
            }
            else if ( checkFieldWithDuplicate( parser, "url", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                ciManagement.setLocation( "url", _location );
                ciManagement.setUrl( interpolatedTrimmed( parser.nextText(), "url" ) );
                setWrapLocation( ciManagement, parser, "url" );
            }
            else if ( checkFieldWithDuplicate( parser, "notifiers", null, parsed ) )
            {
                java.util.List<Notifier> notifiers = new java.util.ArrayList<Notifier>();
                ciManagement.setNotifiers( notifiers );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                ciManagement.setLocation( "notifiers", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "notifier".equals( parser.getName() ) )
                    {
                        notifiers.add( parseNotifier( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( ciManagement, parser, "notifiers" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( ciManagement, parser );
        return ciManagement;
    } //-- CiManagement parseCiManagement( XmlPullParser, boolean, InputSource )

    /**
     * Method parseConfigurationContainer.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return ConfigurationContainer
     */
    private ConfigurationContainer parseConfigurationContainer( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        ConfigurationContainer configurationContainer = new ConfigurationContainer();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        configurationContainer.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "inherited", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                configurationContainer.setLocation( "inherited", _location );
                configurationContainer.setInherited( interpolatedTrimmed( parser.nextText(), "inherited" ) );
                setWrapLocation( configurationContainer, parser, "inherited" );
            }
            else if ( checkFieldWithDuplicate( parser, "configuration", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                configurationContainer.setLocation( "configuration", _location );
                configurationContainer.setConfiguration( org.codehaus.plexus.util.xml.Xpp3DomBuilder.build( parser, true, new Xpp3DomBuilderInputLocationBuilder( _location ) ) );
                setWrapLocation( configurationContainer, parser, "configuration" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( configurationContainer, parser );
        return configurationContainer;
    } //-- ConfigurationContainer parseConfigurationContainer( XmlPullParser, boolean, InputSource )

    /**
     * Method parseContributor.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return Contributor
     */
    private Contributor parseContributor( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        Contributor contributor = new Contributor();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        contributor.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "name", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                contributor.setLocation( "name", _location );
                contributor.setName( interpolatedTrimmed( parser.nextText(), "name" ) );
                setWrapLocation( contributor, parser, "name" );
            }
            else if ( checkFieldWithDuplicate( parser, "email", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                contributor.setLocation( "email", _location );
                contributor.setEmail( interpolatedTrimmed( parser.nextText(), "email" ) );
                setWrapLocation( contributor, parser, "email" );
            }
            else if ( checkFieldWithDuplicate( parser, "url", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                contributor.setLocation( "url", _location );
                contributor.setUrl( interpolatedTrimmed( parser.nextText(), "url" ) );
                setWrapLocation( contributor, parser, "url" );
            }
            else if ( checkFieldWithDuplicate( parser, "organization", "organisation", parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                contributor.setLocation( "organization", _location );
                contributor.setOrganization( interpolatedTrimmed( parser.nextText(), "organization" ) );
                setWrapLocation( contributor, parser, "organization" );
            }
            else if ( checkFieldWithDuplicate( parser, "organizationUrl", "organisationUrl", parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                contributor.setLocation( "organizationUrl", _location );
                contributor.setOrganizationUrl( interpolatedTrimmed( parser.nextText(), "organizationUrl" ) );
                setWrapLocation( contributor, parser, "organizationUrl" );
            }
            else if ( checkFieldWithDuplicate( parser, "roles", null, parsed ) )
            {
                java.util.List<String> roles = new java.util.ArrayList<String>();
                contributor.setRoles( roles );
                InputLocation _locations;
                _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                contributor.setLocation( "roles", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "role".equals( parser.getName() ) )
                    {
                        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                        _locations.setLocation( Integer.valueOf( roles.size() ), _location );
                        roles.add( interpolatedTrimmed( parser.nextText(), "roles" ) );
                        setWrapLocation( _locations, parser, roles.size()-1 );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( contributor, parser, "roles" );
            }
            else if ( checkFieldWithDuplicate( parser, "timezone", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                contributor.setLocation( "timezone", _location );
                contributor.setTimezone( interpolatedTrimmed( parser.nextText(), "timezone" ) );
                setWrapLocation( contributor, parser, "timezone" );
            }
            else if ( checkFieldWithDuplicate( parser, "properties", null, parsed ) )
            {
                InputLocation _locations;
                _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                contributor.setLocation( "properties", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    String key = parser.getName();
                    _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                    _locations.setLocation( key, _location );
                    String value = parser.nextText().trim();
                    contributor.addProperty( key, value );
                    setWrapLocation( _locations, parser, key );
                }
                setWrapLocation( contributor, parser, "properties" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( contributor, parser );
        return contributor;
    } //-- Contributor parseContributor( XmlPullParser, boolean, InputSource )

    /**
     * Method parseDependency.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return Dependency
     */
    private Dependency parseDependency( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        Dependency dependency = new Dependency();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        dependency.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "groupId", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                dependency.setLocation( "groupId", _location );
                dependency.setGroupId( interpolatedTrimmed( parser.nextText(), "groupId" ) );
                setWrapLocation( dependency, parser, "groupId" );
            }
            else if ( checkFieldWithDuplicate( parser, "artifactId", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                dependency.setLocation( "artifactId", _location );
                dependency.setArtifactId( interpolatedTrimmed( parser.nextText(), "artifactId" ) );
                setWrapLocation( dependency, parser, "artifactId" );
            }
            else if ( checkFieldWithDuplicate( parser, "version", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                dependency.setLocation( "version", _location );
                dependency.setVersion( interpolatedTrimmed( parser.nextText(), "version" ) );
                setWrapLocation( dependency, parser, "version" );
            }
            else if ( checkFieldWithDuplicate( parser, "type", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                dependency.setLocation( "type", _location );
                dependency.setType( interpolatedTrimmed( parser.nextText(), "type" ) );
                setWrapLocation( dependency, parser, "type" );
            }
            else if ( checkFieldWithDuplicate( parser, "classifier", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                dependency.setLocation( "classifier", _location );
                dependency.setClassifier( interpolatedTrimmed( parser.nextText(), "classifier" ) );
                setWrapLocation( dependency, parser, "classifier" );
            }
            else if ( checkFieldWithDuplicate( parser, "scope", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                dependency.setLocation( "scope", _location );
                dependency.setScope( interpolatedTrimmed( parser.nextText(), "scope" ) );
                setWrapLocation( dependency, parser, "scope" );
            }
            else if ( checkFieldWithDuplicate( parser, "systemPath", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                dependency.setLocation( "systemPath", _location );
                dependency.setSystemPath( interpolatedTrimmed( parser.nextText(), "systemPath" ) );
                setWrapLocation( dependency, parser, "systemPath" );
            }
            else if ( checkFieldWithDuplicate( parser, "exclusions", null, parsed ) )
            {
                java.util.List<Exclusion> exclusions = new java.util.ArrayList<Exclusion>();
                dependency.setExclusions( exclusions );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                dependency.setLocation( "exclusions", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "exclusion".equals( parser.getName() ) )
                    {
                        exclusions.add( parseExclusion( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( dependency, parser, "exclusions" );
            }
            else if ( checkFieldWithDuplicate( parser, "optional", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                dependency.setLocation( "optional", _location );
                dependency.setOptional( interpolatedTrimmed( parser.nextText(), "optional" ) );
                setWrapLocation( dependency, parser, "optional" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( dependency, parser );
        return dependency;
    } //-- Dependency parseDependency( XmlPullParser, boolean, InputSource )

    /**
     * Method parseDependencyManagement.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return DependencyManagement
     */
    private DependencyManagement parseDependencyManagement( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        DependencyManagement dependencyManagement = new DependencyManagement();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        dependencyManagement.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "dependencies", null, parsed ) )
            {
                java.util.List<Dependency> dependencies = new java.util.ArrayList<Dependency>();
                dependencyManagement.setDependencies( dependencies );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                dependencyManagement.setLocation( "dependencies", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "dependency".equals( parser.getName() ) )
                    {
                        dependencies.add( parseDependency( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( dependencyManagement, parser, "dependencies" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( dependencyManagement, parser );
        return dependencyManagement;
    } //-- DependencyManagement parseDependencyManagement( XmlPullParser, boolean, InputSource )

    /**
     * Method parseDeploymentRepository.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return DeploymentRepository
     */
    private DeploymentRepository parseDeploymentRepository( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        DeploymentRepository deploymentRepository = new DeploymentRepository();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        deploymentRepository.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "uniqueVersion", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                deploymentRepository.setLocation( "uniqueVersion", _location );
                deploymentRepository.setUniqueVersion( getBooleanValue( interpolatedTrimmed( parser.nextText(), "uniqueVersion" ), "uniqueVersion", parser, "true" ) );
                setWrapLocation( deploymentRepository, parser, "uniqueVersion" );
            }
            else if ( checkFieldWithDuplicate( parser, "releases", null, parsed ) )
            {
                deploymentRepository.setReleases( parseRepositoryPolicy( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "snapshots", null, parsed ) )
            {
                deploymentRepository.setSnapshots( parseRepositoryPolicy( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "id", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                deploymentRepository.setLocation( "id", _location );
                deploymentRepository.setId( interpolatedTrimmed( parser.nextText(), "id" ) );
                setWrapLocation( deploymentRepository, parser, "id" );
            }
            else if ( checkFieldWithDuplicate( parser, "name", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                deploymentRepository.setLocation( "name", _location );
                deploymentRepository.setName( interpolatedTrimmed( parser.nextText(), "name" ) );
                setWrapLocation( deploymentRepository, parser, "name" );
            }
            else if ( checkFieldWithDuplicate( parser, "url", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                deploymentRepository.setLocation( "url", _location );
                deploymentRepository.setUrl( interpolatedTrimmed( parser.nextText(), "url" ) );
                setWrapLocation( deploymentRepository, parser, "url" );
            }
            else if ( checkFieldWithDuplicate( parser, "layout", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                deploymentRepository.setLocation( "layout", _location );
                deploymentRepository.setLayout( interpolatedTrimmed( parser.nextText(), "layout" ) );
                setWrapLocation( deploymentRepository, parser, "layout" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        return deploymentRepository;
    } //-- DeploymentRepository parseDeploymentRepository( XmlPullParser, boolean, InputSource )

    /**
     * Method parseDeveloper.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return Developer
     */
    private Developer parseDeveloper( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        Developer developer = new Developer();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        developer.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "id", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                developer.setLocation( "id", _location );
                developer.setId( interpolatedTrimmed( parser.nextText(), "id" ) );
                setWrapLocation( developer, parser, "id" );
            }
            else if ( checkFieldWithDuplicate( parser, "name", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                developer.setLocation( "name", _location );
                developer.setName( interpolatedTrimmed( parser.nextText(), "name" ) );
                setWrapLocation( developer, parser, "name" );
            }
            else if ( checkFieldWithDuplicate( parser, "email", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                developer.setLocation( "email", _location );
                developer.setEmail( interpolatedTrimmed( parser.nextText(), "email" ) );
                setWrapLocation( developer, parser, "email" );
            }
            else if ( checkFieldWithDuplicate( parser, "url", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                developer.setLocation( "url", _location );
                developer.setUrl( interpolatedTrimmed( parser.nextText(), "url" ) );
                setWrapLocation( developer, parser, "url" );
            }
            else if ( checkFieldWithDuplicate( parser, "organization", "organisation", parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                developer.setLocation( "organization", _location );
                developer.setOrganization( interpolatedTrimmed( parser.nextText(), "organization" ) );
                setWrapLocation( developer, parser, "organization" );
            }
            else if ( checkFieldWithDuplicate( parser, "organizationUrl", "organisationUrl", parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                developer.setLocation( "organizationUrl", _location );
                developer.setOrganizationUrl( interpolatedTrimmed( parser.nextText(), "organizationUrl" ) );
                setWrapLocation( developer, parser, "organizationUrl" );
            }
            else if ( checkFieldWithDuplicate( parser, "roles", null, parsed ) )
            {
                java.util.List<String> roles = new java.util.ArrayList<String>();
                developer.setRoles( roles );
                InputLocation _locations;
                _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                developer.setLocation( "roles", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "role".equals( parser.getName() ) )
                    {
                        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                        _locations.setLocation( Integer.valueOf( roles.size() ), _location );
                        roles.add( interpolatedTrimmed( parser.nextText(), "roles" ) );
                        setWrapLocation( _locations, parser, roles.size()-1 );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( developer, parser, "roles" );
            }
            else if ( checkFieldWithDuplicate( parser, "timezone", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                developer.setLocation( "timezone", _location );
                developer.setTimezone( interpolatedTrimmed( parser.nextText(), "timezone" ) );
                setWrapLocation( developer, parser, "timezone" );
            }
            else if ( checkFieldWithDuplicate( parser, "properties", null, parsed ) )
            {
                InputLocation _locations;
                _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                developer.setLocation( "properties", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    String key = parser.getName();
                    _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                    _locations.setLocation( key, _location );
                    String value = parser.nextText().trim();
                    developer.addProperty( key, value );
                    setWrapLocation( _locations, parser, key );
                }
                setWrapLocation( developer, parser, "properties" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( developer, parser );
        return developer;
    } //-- Developer parseDeveloper( XmlPullParser, boolean, InputSource )

    /**
     * Method parseDistributionManagement.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return DistributionManagement
     */
    private DistributionManagement parseDistributionManagement( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        DistributionManagement distributionManagement = new DistributionManagement();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        distributionManagement.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "repository", null, parsed ) )
            {
                distributionManagement.setRepository( parseDeploymentRepository( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "snapshotRepository", null, parsed ) )
            {
                distributionManagement.setSnapshotRepository( parseDeploymentRepository( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "site", null, parsed ) )
            {
                distributionManagement.setSite( parseSite( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "downloadUrl", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                distributionManagement.setLocation( "downloadUrl", _location );
                distributionManagement.setDownloadUrl( interpolatedTrimmed( parser.nextText(), "downloadUrl" ) );
                setWrapLocation( distributionManagement, parser, "downloadUrl" );
            }
            else if ( checkFieldWithDuplicate( parser, "relocation", null, parsed ) )
            {
                distributionManagement.setRelocation( parseRelocation( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "status", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                distributionManagement.setLocation( "status", _location );
                distributionManagement.setStatus( interpolatedTrimmed( parser.nextText(), "status" ) );
                setWrapLocation( distributionManagement, parser, "status" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( distributionManagement, parser );
        return distributionManagement;
    } //-- DistributionManagement parseDistributionManagement( XmlPullParser, boolean, InputSource )

    /**
     * Method parseExclusion.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return Exclusion
     */
    private Exclusion parseExclusion( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        Exclusion exclusion = new Exclusion();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        exclusion.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "groupId", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                exclusion.setLocation( "groupId", _location );
                exclusion.setGroupId( interpolatedTrimmed( parser.nextText(), "groupId" ) );
                setWrapLocation( exclusion, parser, "groupId" );
            }
            else if ( checkFieldWithDuplicate( parser, "artifactId", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                exclusion.setLocation( "artifactId", _location );
                exclusion.setArtifactId( interpolatedTrimmed( parser.nextText(), "artifactId" ) );
                setWrapLocation( exclusion, parser, "artifactId" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( exclusion, parser );
        return exclusion;
    } //-- Exclusion parseExclusion( XmlPullParser, boolean, InputSource )

    /**
     * Method parseExtension.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return Extension
     */
    private Extension parseExtension( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        Extension extension = new Extension();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        extension.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "groupId", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                extension.setLocation( "groupId", _location );
                extension.setGroupId( interpolatedTrimmed( parser.nextText(), "groupId" ) );
                setWrapLocation( extension, parser, "groupId" );
            }
            else if ( checkFieldWithDuplicate( parser, "artifactId", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                extension.setLocation( "artifactId", _location );
                extension.setArtifactId( interpolatedTrimmed( parser.nextText(), "artifactId" ) );
                setWrapLocation( extension, parser, "artifactId" );
            }
            else if ( checkFieldWithDuplicate( parser, "version", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                extension.setLocation( "version", _location );
                extension.setVersion( interpolatedTrimmed( parser.nextText(), "version" ) );
                setWrapLocation( extension, parser, "version" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( extension, parser );
        return extension;
    } //-- Extension parseExtension( XmlPullParser, boolean, InputSource )

    /**
     * Method parseFileSet.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return FileSet
     */
    private FileSet parseFileSet( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        FileSet fileSet = new FileSet();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        fileSet.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "directory", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                fileSet.setLocation( "directory", _location );
                fileSet.setDirectory( interpolatedTrimmed( parser.nextText(), "directory" ) );
                setWrapLocation( fileSet, parser, "directory" );
            }
            else if ( checkFieldWithDuplicate( parser, "includes", null, parsed ) )
            {
                java.util.List<String> includes = new java.util.ArrayList<String>();
                fileSet.setIncludes( includes );
                InputLocation _locations;
                _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                fileSet.setLocation( "includes", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "include".equals( parser.getName() ) )
                    {
                        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                        _locations.setLocation( Integer.valueOf( includes.size() ), _location );
                        includes.add( interpolatedTrimmed( parser.nextText(), "includes" ) );
                        setWrapLocation( _locations, parser, includes.size()-1 );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( fileSet, parser, "includes" );
            }
            else if ( checkFieldWithDuplicate( parser, "excludes", null, parsed ) )
            {
                java.util.List<String> excludes = new java.util.ArrayList<String>();
                fileSet.setExcludes( excludes );
                InputLocation _locations;
                _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                fileSet.setLocation( "excludes", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "exclude".equals( parser.getName() ) )
                    {
                        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                        _locations.setLocation( Integer.valueOf( excludes.size() ), _location );
                        excludes.add( interpolatedTrimmed( parser.nextText(), "excludes" ) );
                        setWrapLocation( _locations, parser, excludes.size()-1 );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( fileSet, parser, "excludes" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( fileSet, parser );
        return fileSet;
    } //-- FileSet parseFileSet( XmlPullParser, boolean, InputSource )

    /**
     * Method parseIssueManagement.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return IssueManagement
     */
    private IssueManagement parseIssueManagement( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        IssueManagement issueManagement = new IssueManagement();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        issueManagement.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "system", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                issueManagement.setLocation( "system", _location );
                issueManagement.setSystem( interpolatedTrimmed( parser.nextText(), "system" ) );
                setWrapLocation( issueManagement, parser, "system" );
            }
            else if ( checkFieldWithDuplicate( parser, "url", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                issueManagement.setLocation( "url", _location );
                issueManagement.setUrl( interpolatedTrimmed( parser.nextText(), "url" ) );
                setWrapLocation( issueManagement, parser, "url" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( issueManagement, parser );
        return issueManagement;
    } //-- IssueManagement parseIssueManagement( XmlPullParser, boolean, InputSource )

    /**
     * Method parseLicense.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return License
     */
    private License parseLicense( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        License license = new License();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        license.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "name", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                license.setLocation( "name", _location );
                license.setName( interpolatedTrimmed( parser.nextText(), "name" ) );
                setWrapLocation( license, parser, "name" );
            }
            else if ( checkFieldWithDuplicate( parser, "url", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                license.setLocation( "url", _location );
                license.setUrl( interpolatedTrimmed( parser.nextText(), "url" ) );
                setWrapLocation( license, parser, "url" );
            }
            else if ( checkFieldWithDuplicate( parser, "distribution", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                license.setLocation( "distribution", _location );
                license.setDistribution( interpolatedTrimmed( parser.nextText(), "distribution" ) );
                setWrapLocation( license, parser, "distribution" );
            }
            else if ( checkFieldWithDuplicate( parser, "comments", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                license.setLocation( "comments", _location );
                license.setComments( interpolatedTrimmed( parser.nextText(), "comments" ) );
                setWrapLocation( license, parser, "comments" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( license, parser );
        return license;
    } //-- License parseLicense( XmlPullParser, boolean, InputSource )

    /**
     * Method parseMailingList.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return MailingList
     */
    private MailingList parseMailingList( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        MailingList mailingList = new MailingList();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        mailingList.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "name", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                mailingList.setLocation( "name", _location );
                mailingList.setName( interpolatedTrimmed( parser.nextText(), "name" ) );
                setWrapLocation( mailingList, parser, "name" );
            }
            else if ( checkFieldWithDuplicate( parser, "subscribe", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                mailingList.setLocation( "subscribe", _location );
                mailingList.setSubscribe( interpolatedTrimmed( parser.nextText(), "subscribe" ) );
                setWrapLocation( mailingList, parser, "subscribe" );
            }
            else if ( checkFieldWithDuplicate( parser, "unsubscribe", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                mailingList.setLocation( "unsubscribe", _location );
                mailingList.setUnsubscribe( interpolatedTrimmed( parser.nextText(), "unsubscribe" ) );
                setWrapLocation( mailingList, parser, "unsubscribe" );
            }
            else if ( checkFieldWithDuplicate( parser, "post", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                mailingList.setLocation( "post", _location );
                mailingList.setPost( interpolatedTrimmed( parser.nextText(), "post" ) );
                setWrapLocation( mailingList, parser, "post" );
            }
            else if ( checkFieldWithDuplicate( parser, "archive", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                mailingList.setLocation( "archive", _location );
                mailingList.setArchive( interpolatedTrimmed( parser.nextText(), "archive" ) );
                setWrapLocation( mailingList, parser, "archive" );
            }
            else if ( checkFieldWithDuplicate( parser, "otherArchives", null, parsed ) )
            {
                java.util.List<String> otherArchives = new java.util.ArrayList<String>();
                mailingList.setOtherArchives( otherArchives );
                InputLocation _locations;
                _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                mailingList.setLocation( "otherArchives", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "otherArchive".equals( parser.getName() ) )
                    {
                        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                        _locations.setLocation( Integer.valueOf( otherArchives.size() ), _location );
                        otherArchives.add( interpolatedTrimmed( parser.nextText(), "otherArchives" ) );
                        setWrapLocation( _locations, parser, otherArchives.size()-1 );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( mailingList, parser, "otherArchives" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( mailingList, parser );
        return mailingList;
    } //-- MailingList parseMailingList( XmlPullParser, boolean, InputSource )

    /**
     * Method parseModel.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return Model
     */
    private Model parseModel( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        Model model = new Model();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        model.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else if ( "xmlns".equals( name ) )
            {
                // ignore xmlns attribute in root class, which is a reserved attribute name
            }
            else if ( "child.project.url.inherit.append.path".equals( name ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                model.setLocation( "childProjectUrlInheritAppendPath", _location );
                model.setChildProjectUrlInheritAppendPath( interpolatedTrimmed( value, "child.project.url.inherit.append.path" ) );
                setWrapLocation( model, parser, "childProjectUrlInheritAppendPath" );
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "modelVersion", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                model.setLocation( "modelVersion", _location );
                model.setModelVersion( interpolatedTrimmed( parser.nextText(), "modelVersion" ) );
                setWrapLocation( model, parser, "modelVersion" );
            }
            else if ( checkFieldWithDuplicate( parser, "parent", null, parsed ) )
            {
                model.setParent( parseParent( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "groupId", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                model.setLocation( "groupId", _location );
                model.setGroupId( interpolatedTrimmed( parser.nextText(), "groupId" ) );
                setWrapLocation( model, parser, "groupId" );
            }
            else if ( checkFieldWithDuplicate( parser, "artifactId", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                model.setLocation( "artifactId", _location );
                model.setArtifactId( interpolatedTrimmed( parser.nextText(), "artifactId" ) );
                setWrapLocation( model, parser, "artifactId" );
            }
            else if ( checkFieldWithDuplicate( parser, "version", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                model.setLocation( "version", _location );
                model.setVersion( interpolatedTrimmed( parser.nextText(), "version" ) );
                setWrapLocation( model, parser, "version" );
            }
            else if ( checkFieldWithDuplicate( parser, "packaging", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                model.setLocation( "packaging", _location );
                model.setPackaging( interpolatedTrimmed( parser.nextText(), "packaging" ) );
                setWrapLocation( model, parser, "packaging" );
            }
            else if ( checkFieldWithDuplicate( parser, "name", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                model.setLocation( "name", _location );
                model.setName( interpolatedTrimmed( parser.nextText(), "name" ) );
                setWrapLocation( model, parser, "name" );
            }
            else if ( checkFieldWithDuplicate( parser, "description", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                model.setLocation( "description", _location );
                model.setDescription( interpolatedTrimmed( parser.nextText(), "description" ) );
                setWrapLocation( model, parser, "description" );
            }
            else if ( checkFieldWithDuplicate( parser, "url", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                model.setLocation( "url", _location );
                model.setUrl( interpolatedTrimmed( parser.nextText(), "url" ) );
                setWrapLocation( model, parser, "url" );
            }
            else if ( checkFieldWithDuplicate( parser, "inceptionYear", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                model.setLocation( "inceptionYear", _location );
                model.setInceptionYear( interpolatedTrimmed( parser.nextText(), "inceptionYear" ) );
                setWrapLocation( model, parser, "inceptionYear" );
            }
            else if ( checkFieldWithDuplicate( parser, "organization", "organisation", parsed ) )
            {
                model.setOrganization( parseOrganization( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "licenses", null, parsed ) )
            {
                java.util.List<License> licenses = new java.util.ArrayList<License>();
                model.setLicenses( licenses );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                model.setLocation( "licenses", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "license".equals( parser.getName() ) )
                    {
                        licenses.add( parseLicense( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( model, parser, "licenses" );
            }
            else if ( checkFieldWithDuplicate( parser, "developers", null, parsed ) )
            {
                java.util.List<Developer> developers = new java.util.ArrayList<Developer>();
                model.setDevelopers( developers );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                model.setLocation( "developers", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "developer".equals( parser.getName() ) )
                    {
                        developers.add( parseDeveloper( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( model, parser, "developers" );
            }
            else if ( checkFieldWithDuplicate( parser, "contributors", null, parsed ) )
            {
                java.util.List<Contributor> contributors = new java.util.ArrayList<Contributor>();
                model.setContributors( contributors );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                model.setLocation( "contributors", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "contributor".equals( parser.getName() ) )
                    {
                        contributors.add( parseContributor( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( model, parser, "contributors" );
            }
            else if ( checkFieldWithDuplicate( parser, "mailingLists", null, parsed ) )
            {
                java.util.List<MailingList> mailingLists = new java.util.ArrayList<MailingList>();
                model.setMailingLists( mailingLists );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                model.setLocation( "mailingLists", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "mailingList".equals( parser.getName() ) )
                    {
                        mailingLists.add( parseMailingList( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( model, parser, "mailingLists" );
            }
            else if ( checkFieldWithDuplicate( parser, "prerequisites", null, parsed ) )
            {
                model.setPrerequisites( parsePrerequisites( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "modules", null, parsed ) )
            {
                java.util.List<String> modules = new java.util.ArrayList<String>();
                model.setModules( modules );
                InputLocation _locations;
                _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                model.setLocation( "modules", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "module".equals( parser.getName() ) )
                    {
                        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                        _locations.setLocation( Integer.valueOf( modules.size() ), _location );
                        modules.add( interpolatedTrimmed( parser.nextText(), "modules" ) );
                        setWrapLocation( _locations, parser, modules.size()-1 );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( model, parser, "modules" );
            }
            else if ( checkFieldWithDuplicate( parser, "scm", null, parsed ) )
            {
                model.setScm( parseScm( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "issueManagement", null, parsed ) )
            {
                model.setIssueManagement( parseIssueManagement( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "ciManagement", null, parsed ) )
            {
                model.setCiManagement( parseCiManagement( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "distributionManagement", null, parsed ) )
            {
                model.setDistributionManagement( parseDistributionManagement( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "properties", null, parsed ) )
            {
                InputLocation _locations;
                _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                model.setLocation( "properties", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    String key = parser.getName();
                    _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                    _locations.setLocation( key, _location );
                    String value = parser.nextText().trim();
                    model.addProperty( key, value );
                    setWrapLocation( _locations, parser, key );
                }
                setWrapLocation( model, parser, "properties" );
            }
            else if ( checkFieldWithDuplicate( parser, "dependencyManagement", null, parsed ) )
            {
                model.setDependencyManagement( parseDependencyManagement( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "dependencies", null, parsed ) )
            {
                java.util.List<Dependency> dependencies = new java.util.ArrayList<Dependency>();
                model.setDependencies( dependencies );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                model.setLocation( "dependencies", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "dependency".equals( parser.getName() ) )
                    {
                        dependencies.add( parseDependency( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( model, parser, "dependencies" );
            }
            else if ( checkFieldWithDuplicate( parser, "repositories", null, parsed ) )
            {
                java.util.List<Repository> repositories = new java.util.ArrayList<Repository>();
                model.setRepositories( repositories );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                model.setLocation( "repositories", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "repository".equals( parser.getName() ) )
                    {
                        repositories.add( parseRepository( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( model, parser, "repositories" );
            }
            else if ( checkFieldWithDuplicate( parser, "pluginRepositories", null, parsed ) )
            {
                java.util.List<Repository> pluginRepositories = new java.util.ArrayList<Repository>();
                model.setPluginRepositories( pluginRepositories );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                model.setLocation( "pluginRepositories", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "pluginRepository".equals( parser.getName() ) )
                    {
                        pluginRepositories.add( parseRepository( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( model, parser, "pluginRepositories" );
            }
            else if ( checkFieldWithDuplicate( parser, "build", null, parsed ) )
            {
                model.setBuild( parseBuild( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "reports", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                model.setLocation( "reports", _location );
                model.setReports( org.codehaus.plexus.util.xml.Xpp3DomBuilder.build( parser, true, new Xpp3DomBuilderInputLocationBuilder( _location ) ) );
                setWrapLocation( model, parser, "reports" );
            }
            else if ( checkFieldWithDuplicate( parser, "reporting", null, parsed ) )
            {
                model.setReporting( parseReporting( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "profiles", null, parsed ) )
            {
                java.util.List<Profile> profiles = new java.util.ArrayList<Profile>();
                model.setProfiles( profiles );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                model.setLocation( "profiles", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "profile".equals( parser.getName() ) )
                    {
                        profiles.add( parseProfile( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( model, parser, "profiles" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( model, parser );
        return model;
    } //-- Model parseModel( XmlPullParser, boolean, InputSource )

    /**
     * Method parseModelBase.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return ModelBase
     */
    private ModelBase parseModelBase( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        ModelBase modelBase = new ModelBase();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        modelBase.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "modules", null, parsed ) )
            {
                java.util.List<String> modules = new java.util.ArrayList<String>();
                modelBase.setModules( modules );
                InputLocation _locations;
                _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                modelBase.setLocation( "modules", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "module".equals( parser.getName() ) )
                    {
                        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                        _locations.setLocation( Integer.valueOf( modules.size() ), _location );
                        modules.add( interpolatedTrimmed( parser.nextText(), "modules" ) );
                        setWrapLocation( _locations, parser, modules.size()-1 );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( modelBase, parser, "modules" );
            }
            else if ( checkFieldWithDuplicate( parser, "distributionManagement", null, parsed ) )
            {
                modelBase.setDistributionManagement( parseDistributionManagement( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "properties", null, parsed ) )
            {
                InputLocation _locations;
                _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                modelBase.setLocation( "properties", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    String key = parser.getName();
                    _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                    _locations.setLocation( key, _location );
                    String value = parser.nextText().trim();
                    modelBase.addProperty( key, value );
                    setWrapLocation( _locations, parser, key );
                }
                setWrapLocation( modelBase, parser, "properties" );
            }
            else if ( checkFieldWithDuplicate( parser, "dependencyManagement", null, parsed ) )
            {
                modelBase.setDependencyManagement( parseDependencyManagement( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "dependencies", null, parsed ) )
            {
                java.util.List<Dependency> dependencies = new java.util.ArrayList<Dependency>();
                modelBase.setDependencies( dependencies );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                modelBase.setLocation( "dependencies", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "dependency".equals( parser.getName() ) )
                    {
                        dependencies.add( parseDependency( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( modelBase, parser, "dependencies" );
            }
            else if ( checkFieldWithDuplicate( parser, "repositories", null, parsed ) )
            {
                java.util.List<Repository> repositories = new java.util.ArrayList<Repository>();
                modelBase.setRepositories( repositories );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                modelBase.setLocation( "repositories", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "repository".equals( parser.getName() ) )
                    {
                        repositories.add( parseRepository( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( modelBase, parser, "repositories" );
            }
            else if ( checkFieldWithDuplicate( parser, "pluginRepositories", null, parsed ) )
            {
                java.util.List<Repository> pluginRepositories = new java.util.ArrayList<Repository>();
                modelBase.setPluginRepositories( pluginRepositories );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                modelBase.setLocation( "pluginRepositories", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "pluginRepository".equals( parser.getName() ) )
                    {
                        pluginRepositories.add( parseRepository( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( modelBase, parser, "pluginRepositories" );
            }
            else if ( checkFieldWithDuplicate( parser, "reports", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                modelBase.setLocation( "reports", _location );
                modelBase.setReports( org.codehaus.plexus.util.xml.Xpp3DomBuilder.build( parser, true, new Xpp3DomBuilderInputLocationBuilder( _location ) ) );
                setWrapLocation( modelBase, parser, "reports" );
            }
            else if ( checkFieldWithDuplicate( parser, "reporting", null, parsed ) )
            {
                modelBase.setReporting( parseReporting( parser, strict, source ) );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( modelBase, parser );
        return modelBase;
    } //-- ModelBase parseModelBase( XmlPullParser, boolean, InputSource )

    /**
     * Method parseNotifier.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return Notifier
     */
    private Notifier parseNotifier( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        Notifier notifier = new Notifier();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        notifier.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "type", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                notifier.setLocation( "type", _location );
                notifier.setType( interpolatedTrimmed( parser.nextText(), "type" ) );
                setWrapLocation( notifier, parser, "type" );
            }
            else if ( checkFieldWithDuplicate( parser, "sendOnError", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                notifier.setLocation( "sendOnError", _location );
                notifier.setSendOnError( getBooleanValue( interpolatedTrimmed( parser.nextText(), "sendOnError" ), "sendOnError", parser, "true" ) );
                setWrapLocation( notifier, parser, "sendOnError" );
            }
            else if ( checkFieldWithDuplicate( parser, "sendOnFailure", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                notifier.setLocation( "sendOnFailure", _location );
                notifier.setSendOnFailure( getBooleanValue( interpolatedTrimmed( parser.nextText(), "sendOnFailure" ), "sendOnFailure", parser, "true" ) );
                setWrapLocation( notifier, parser, "sendOnFailure" );
            }
            else if ( checkFieldWithDuplicate( parser, "sendOnSuccess", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                notifier.setLocation( "sendOnSuccess", _location );
                notifier.setSendOnSuccess( getBooleanValue( interpolatedTrimmed( parser.nextText(), "sendOnSuccess" ), "sendOnSuccess", parser, "true" ) );
                setWrapLocation( notifier, parser, "sendOnSuccess" );
            }
            else if ( checkFieldWithDuplicate( parser, "sendOnWarning", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                notifier.setLocation( "sendOnWarning", _location );
                notifier.setSendOnWarning( getBooleanValue( interpolatedTrimmed( parser.nextText(), "sendOnWarning" ), "sendOnWarning", parser, "true" ) );
                setWrapLocation( notifier, parser, "sendOnWarning" );
            }
            else if ( checkFieldWithDuplicate( parser, "address", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                notifier.setLocation( "address", _location );
                notifier.setAddress( interpolatedTrimmed( parser.nextText(), "address" ) );
                setWrapLocation( notifier, parser, "address" );
            }
            else if ( checkFieldWithDuplicate( parser, "configuration", null, parsed ) )
            {
                InputLocation _locations;
                _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                notifier.setLocation( "configuration", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    String key = parser.getName();
                    _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                    _locations.setLocation( key, _location );
                    String value = parser.nextText().trim();
                    notifier.addConfiguration( key, value );
                    setWrapLocation( _locations, parser, key );
                }
                setWrapLocation( notifier, parser, "configuration" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( notifier, parser );
        return notifier;
    } //-- Notifier parseNotifier( XmlPullParser, boolean, InputSource )

    /**
     * Method parseOrganization.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return Organization
     */
    private Organization parseOrganization( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        Organization organization = new Organization();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        organization.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "name", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                organization.setLocation( "name", _location );
                organization.setName( interpolatedTrimmed( parser.nextText(), "name" ) );
                setWrapLocation( organization, parser, "name" );
            }
            else if ( checkFieldWithDuplicate( parser, "url", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                organization.setLocation( "url", _location );
                organization.setUrl( interpolatedTrimmed( parser.nextText(), "url" ) );
                setWrapLocation( organization, parser, "url" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( organization, parser );
        return organization;
    } //-- Organization parseOrganization( XmlPullParser, boolean, InputSource )

    /**
     * Method parseParent.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return Parent
     */
    private Parent parseParent( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        Parent parent = new Parent();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        parent.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "groupId", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                parent.setLocation( "groupId", _location );
                parent.setGroupId( interpolatedTrimmed( parser.nextText(), "groupId" ) );
                setWrapLocation( parent, parser, "groupId" );
            }
            else if ( checkFieldWithDuplicate( parser, "artifactId", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                parent.setLocation( "artifactId", _location );
                parent.setArtifactId( interpolatedTrimmed( parser.nextText(), "artifactId" ) );
                setWrapLocation( parent, parser, "artifactId" );
            }
            else if ( checkFieldWithDuplicate( parser, "version", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                parent.setLocation( "version", _location );
                parent.setVersion( interpolatedTrimmed( parser.nextText(), "version" ) );
                setWrapLocation( parent, parser, "version" );
            }
            else if ( checkFieldWithDuplicate( parser, "relativePath", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                parent.setLocation( "relativePath", _location );
                parent.setRelativePath( interpolatedTrimmed( parser.nextText(), "relativePath" ) );
                setWrapLocation( parent, parser, "relativePath" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( parent, parser );
        return parent;
    } //-- Parent parseParent( XmlPullParser, boolean, InputSource )

    /**
     * Method parsePatternSet.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return PatternSet
     */
    private PatternSet parsePatternSet( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        PatternSet patternSet = new PatternSet();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        patternSet.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "includes", null, parsed ) )
            {
                java.util.List<String> includes = new java.util.ArrayList<String>();
                patternSet.setIncludes( includes );
                InputLocation _locations;
                _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                patternSet.setLocation( "includes", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "include".equals( parser.getName() ) )
                    {
                        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                        _locations.setLocation( Integer.valueOf( includes.size() ), _location );
                        includes.add( interpolatedTrimmed( parser.nextText(), "includes" ) );
                        setWrapLocation( _locations, parser, includes.size()-1 );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( patternSet, parser, "includes" );
            }
            else if ( checkFieldWithDuplicate( parser, "excludes", null, parsed ) )
            {
                java.util.List<String> excludes = new java.util.ArrayList<String>();
                patternSet.setExcludes( excludes );
                InputLocation _locations;
                _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                patternSet.setLocation( "excludes", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "exclude".equals( parser.getName() ) )
                    {
                        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                        _locations.setLocation( Integer.valueOf( excludes.size() ), _location );
                        excludes.add( interpolatedTrimmed( parser.nextText(), "excludes" ) );
                        setWrapLocation( _locations, parser, excludes.size()-1 );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( patternSet, parser, "excludes" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( patternSet, parser );
        return patternSet;
    } //-- PatternSet parsePatternSet( XmlPullParser, boolean, InputSource )

    /**
     * Method parsePlugin.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return Plugin
     */
    private Plugin parsePlugin( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        Plugin plugin = new Plugin();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        plugin.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "groupId", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                plugin.setLocation( "groupId", _location );
                plugin.setGroupId( interpolatedTrimmed( parser.nextText(), "groupId" ) );
                setWrapLocation( plugin, parser, "groupId" );
            }
            else if ( checkFieldWithDuplicate( parser, "artifactId", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                plugin.setLocation( "artifactId", _location );
                plugin.setArtifactId( interpolatedTrimmed( parser.nextText(), "artifactId" ) );
                setWrapLocation( plugin, parser, "artifactId" );
            }
            else if ( checkFieldWithDuplicate( parser, "version", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                plugin.setLocation( "version", _location );
                plugin.setVersion( interpolatedTrimmed( parser.nextText(), "version" ) );
                setWrapLocation( plugin, parser, "version" );
            }
            else if ( checkFieldWithDuplicate( parser, "extensions", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                plugin.setLocation( "extensions", _location );
                plugin.setExtensions( interpolatedTrimmed( parser.nextText(), "extensions" ) );
                setWrapLocation( plugin, parser, "extensions" );
            }
            else if ( checkFieldWithDuplicate( parser, "executions", null, parsed ) )
            {
                java.util.List<PluginExecution> executions = new java.util.ArrayList<PluginExecution>();
                plugin.setExecutions( executions );
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                plugin.setLocation( "executions", _location );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "execution".equals( parser.getName() ) )
                    {
                        executions.add( parsePluginExecution( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( plugin, parser, "executions" );
            }
            else if ( checkFieldWithDuplicate( parser, "dependencies", null, parsed ) )
            {
                java.util.List<Dependency> dependencies = new java.util.ArrayList<Dependency>();
                plugin.setDependencies( dependencies );
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                plugin.setLocation( "dependencies", _location );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "dependency".equals( parser.getName() ) )
                    {
                        dependencies.add( parseDependency( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( plugin, parser, "dependencies" );
            }
            else if ( checkFieldWithDuplicate( parser, "goals", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                plugin.setLocation( "goals", _location );
                plugin.setGoals( org.codehaus.plexus.util.xml.Xpp3DomBuilder.build( parser, true, new Xpp3DomBuilderInputLocationBuilder( _location ) ) );
                setWrapLocation( plugin, parser, "goals" );
            }
            else if ( checkFieldWithDuplicate( parser, "inherited", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                plugin.setLocation( "inherited", _location );
                plugin.setInherited( interpolatedTrimmed( parser.nextText(), "inherited" ) );
                setWrapLocation( plugin, parser, "inherited" );
            }
            else if ( checkFieldWithDuplicate( parser, "configuration", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                plugin.setLocation( "configuration", _location );
                plugin.setConfiguration( org.codehaus.plexus.util.xml.Xpp3DomBuilder.build( parser, true, new Xpp3DomBuilderInputLocationBuilder( _location ) ) );
                setWrapLocation( plugin, parser, "configuration" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( plugin, parser );
        return plugin;
    } //-- Plugin parsePlugin( XmlPullParser, boolean, InputSource )

    /**
     * Method parsePluginConfiguration.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return PluginConfiguration
     */
    private PluginConfiguration parsePluginConfiguration( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        pluginConfiguration.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "pluginManagement", null, parsed ) )
            {
                pluginConfiguration.setPluginManagement( parsePluginManagement( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "plugins", null, parsed ) )
            {
                java.util.List<Plugin> plugins = new java.util.ArrayList<Plugin>();
                pluginConfiguration.setPlugins( plugins );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                pluginConfiguration.setLocation( "plugins", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "plugin".equals( parser.getName() ) )
                    {
                        plugins.add( parsePlugin( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( pluginConfiguration, parser, "plugins" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( pluginConfiguration, parser );
        return pluginConfiguration;
    } //-- PluginConfiguration parsePluginConfiguration( XmlPullParser, boolean, InputSource )

    /**
     * Method parsePluginContainer.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return PluginContainer
     */
    private PluginContainer parsePluginContainer( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        PluginContainer pluginContainer = new PluginContainer();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        pluginContainer.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "plugins", null, parsed ) )
            {
                java.util.List<Plugin> plugins = new java.util.ArrayList<Plugin>();
                pluginContainer.setPlugins( plugins );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                pluginContainer.setLocation( "plugins", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "plugin".equals( parser.getName() ) )
                    {
                        plugins.add( parsePlugin( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( pluginContainer, parser, "plugins" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( pluginContainer, parser );
        return pluginContainer;
    } //-- PluginContainer parsePluginContainer( XmlPullParser, boolean, InputSource )

    /**
     * Method parsePluginExecution.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return PluginExecution
     */
    private PluginExecution parsePluginExecution( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        PluginExecution pluginExecution = new PluginExecution();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        pluginExecution.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "id", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                pluginExecution.setLocation( "id", _location );
                pluginExecution.setId( interpolatedTrimmed( parser.nextText(), "id" ) );
                setWrapLocation( pluginExecution, parser, "id" );
            }
            else if ( checkFieldWithDuplicate( parser, "phase", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                pluginExecution.setLocation( "phase", _location );
                pluginExecution.setPhase( interpolatedTrimmed( parser.nextText(), "phase" ) );
                setWrapLocation( pluginExecution, parser, "phase" );
            }
            else if ( checkFieldWithDuplicate( parser, "goals", null, parsed ) )
            {
                java.util.List<String> goals = new java.util.ArrayList<String>();
                pluginExecution.setGoals( goals );
                InputLocation _locations;
                _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                pluginExecution.setLocation( "goals", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "goal".equals( parser.getName() ) )
                    {
                        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                        _locations.setLocation( Integer.valueOf( goals.size() ), _location );
                        goals.add( interpolatedTrimmed( parser.nextText(), "goals" ) );
                        setWrapLocation( _locations, parser, goals.size()-1 );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( pluginExecution, parser, "goals" );
            }
            else if ( checkFieldWithDuplicate( parser, "inherited", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                pluginExecution.setLocation( "inherited", _location );
                pluginExecution.setInherited( interpolatedTrimmed( parser.nextText(), "inherited" ) );
                setWrapLocation( pluginExecution, parser, "inherited" );
            }
            else if ( checkFieldWithDuplicate( parser, "configuration", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                pluginExecution.setLocation( "configuration", _location );
                pluginExecution.setConfiguration( org.codehaus.plexus.util.xml.Xpp3DomBuilder.build( parser, true, new Xpp3DomBuilderInputLocationBuilder( _location ) ) );
                setWrapLocation( pluginExecution, parser, "configuration" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( pluginExecution, parser );
        return pluginExecution;
    } //-- PluginExecution parsePluginExecution( XmlPullParser, boolean, InputSource )

    /**
     * Method parsePluginManagement.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return PluginManagement
     */
    private PluginManagement parsePluginManagement( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        PluginManagement pluginManagement = new PluginManagement();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        pluginManagement.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "plugins", null, parsed ) )
            {
                java.util.List<Plugin> plugins = new java.util.ArrayList<Plugin>();
                pluginManagement.setPlugins( plugins );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                pluginManagement.setLocation( "plugins", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "plugin".equals( parser.getName() ) )
                    {
                        plugins.add( parsePlugin( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( pluginManagement, parser, "plugins" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( pluginManagement, parser );
        return pluginManagement;
    } //-- PluginManagement parsePluginManagement( XmlPullParser, boolean, InputSource )

    /**
     * Method parsePrerequisites.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return Prerequisites
     */
    private Prerequisites parsePrerequisites( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        Prerequisites prerequisites = new Prerequisites();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        prerequisites.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "maven", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                prerequisites.setLocation( "maven", _location );
                prerequisites.setMaven( interpolatedTrimmed( parser.nextText(), "maven" ) );
                setWrapLocation( prerequisites, parser, "maven" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( prerequisites, parser );
        return prerequisites;
    } //-- Prerequisites parsePrerequisites( XmlPullParser, boolean, InputSource )

    /**
     * Method parseProfile.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return Profile
     */
    private Profile parseProfile( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        Profile profile = new Profile();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        profile.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "id", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                profile.setLocation( "id", _location );
                profile.setId( interpolatedTrimmed( parser.nextText(), "id" ) );
                setWrapLocation( profile, parser, "id" );
            }
            else if ( checkFieldWithDuplicate( parser, "activation", null, parsed ) )
            {
                profile.setActivation( parseActivation( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "build", null, parsed ) )
            {
                profile.setBuild( parseBuildBase( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "modules", null, parsed ) )
            {
                java.util.List<String> modules = new java.util.ArrayList<String>();
                profile.setModules( modules );
                InputLocation _locations;
                _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                profile.setLocation( "modules", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "module".equals( parser.getName() ) )
                    {
                        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                        _locations.setLocation( Integer.valueOf( modules.size() ), _location );
                        modules.add( interpolatedTrimmed( parser.nextText(), "modules" ) );
                        setWrapLocation( _locations, parser, modules.size()-1 );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( profile, parser, "modules" );
            }
            else if ( checkFieldWithDuplicate( parser, "distributionManagement", null, parsed ) )
            {
                profile.setDistributionManagement( parseDistributionManagement( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "properties", null, parsed ) )
            {
                InputLocation _locations;
                _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                profile.setLocation( "properties", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    String key = parser.getName();
                    _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                    _locations.setLocation( key, _location );
                    String value = parser.nextText().trim();
                    profile.addProperty( key, value );
                    setWrapLocation( _locations, parser, key );
                }
                setWrapLocation( profile, parser, "properties" );
            }
            else if ( checkFieldWithDuplicate( parser, "dependencyManagement", null, parsed ) )
            {
                profile.setDependencyManagement( parseDependencyManagement( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "dependencies", null, parsed ) )
            {
                java.util.List<Dependency> dependencies = new java.util.ArrayList<Dependency>();
                profile.setDependencies( dependencies );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                profile.setLocation( "dependencies", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "dependency".equals( parser.getName() ) )
                    {
                        dependencies.add( parseDependency( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( profile, parser, "dependencies" );
            }
            else if ( checkFieldWithDuplicate( parser, "repositories", null, parsed ) )
            {
                java.util.List<Repository> repositories = new java.util.ArrayList<Repository>();
                profile.setRepositories( repositories );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                profile.setLocation( "repositories", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "repository".equals( parser.getName() ) )
                    {
                        repositories.add( parseRepository( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( profile, parser, "repositories" );
            }
            else if ( checkFieldWithDuplicate( parser, "pluginRepositories", null, parsed ) )
            {
                java.util.List<Repository> pluginRepositories = new java.util.ArrayList<Repository>();
                profile.setPluginRepositories( pluginRepositories );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                profile.setLocation( "pluginRepositories", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "pluginRepository".equals( parser.getName() ) )
                    {
                        pluginRepositories.add( parseRepository( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( profile, parser, "pluginRepositories" );
            }
            else if ( checkFieldWithDuplicate( parser, "reports", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                profile.setLocation( "reports", _location );
                profile.setReports( org.codehaus.plexus.util.xml.Xpp3DomBuilder.build( parser, true, new Xpp3DomBuilderInputLocationBuilder( _location ) ) );
                setWrapLocation( profile, parser, "reports" );
            }
            else if ( checkFieldWithDuplicate( parser, "reporting", null, parsed ) )
            {
                profile.setReporting( parseReporting( parser, strict, source ) );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( profile, parser );
        return profile;
    } //-- Profile parseProfile( XmlPullParser, boolean, InputSource )

    /**
     * Method parseRelocation.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return Relocation
     */
    private Relocation parseRelocation( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        Relocation relocation = new Relocation();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        relocation.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "groupId", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                relocation.setLocation( "groupId", _location );
                relocation.setGroupId( interpolatedTrimmed( parser.nextText(), "groupId" ) );
                setWrapLocation( relocation, parser, "groupId" );
            }
            else if ( checkFieldWithDuplicate( parser, "artifactId", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                relocation.setLocation( "artifactId", _location );
                relocation.setArtifactId( interpolatedTrimmed( parser.nextText(), "artifactId" ) );
                setWrapLocation( relocation, parser, "artifactId" );
            }
            else if ( checkFieldWithDuplicate( parser, "version", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                relocation.setLocation( "version", _location );
                relocation.setVersion( interpolatedTrimmed( parser.nextText(), "version" ) );
                setWrapLocation( relocation, parser, "version" );
            }
            else if ( checkFieldWithDuplicate( parser, "message", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                relocation.setLocation( "message", _location );
                relocation.setMessage( interpolatedTrimmed( parser.nextText(), "message" ) );
                setWrapLocation( relocation, parser, "message" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( relocation, parser );
        return relocation;
    } //-- Relocation parseRelocation( XmlPullParser, boolean, InputSource )

    /**
     * Method parseReportPlugin.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return ReportPlugin
     */
    private ReportPlugin parseReportPlugin( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        ReportPlugin reportPlugin = new ReportPlugin();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        reportPlugin.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "groupId", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                reportPlugin.setLocation( "groupId", _location );
                reportPlugin.setGroupId( interpolatedTrimmed( parser.nextText(), "groupId" ) );
                setWrapLocation( reportPlugin, parser, "groupId" );
            }
            else if ( checkFieldWithDuplicate( parser, "artifactId", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                reportPlugin.setLocation( "artifactId", _location );
                reportPlugin.setArtifactId( interpolatedTrimmed( parser.nextText(), "artifactId" ) );
                setWrapLocation( reportPlugin, parser, "artifactId" );
            }
            else if ( checkFieldWithDuplicate( parser, "version", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                reportPlugin.setLocation( "version", _location );
                reportPlugin.setVersion( interpolatedTrimmed( parser.nextText(), "version" ) );
                setWrapLocation( reportPlugin, parser, "version" );
            }
            else if ( checkFieldWithDuplicate( parser, "reportSets", null, parsed ) )
            {
                java.util.List<ReportSet> reportSets = new java.util.ArrayList<ReportSet>();
                reportPlugin.setReportSets( reportSets );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                reportPlugin.setLocation( "reportSets", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "reportSet".equals( parser.getName() ) )
                    {
                        reportSets.add( parseReportSet( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( reportPlugin, parser, "reportSets" );
            }
            else if ( checkFieldWithDuplicate( parser, "inherited", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                reportPlugin.setLocation( "inherited", _location );
                reportPlugin.setInherited( interpolatedTrimmed( parser.nextText(), "inherited" ) );
                setWrapLocation( reportPlugin, parser, "inherited" );
            }
            else if ( checkFieldWithDuplicate( parser, "configuration", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                reportPlugin.setLocation( "configuration", _location );
                reportPlugin.setConfiguration( org.codehaus.plexus.util.xml.Xpp3DomBuilder.build( parser, true, new Xpp3DomBuilderInputLocationBuilder( _location ) ) );
                setWrapLocation( reportPlugin, parser, "configuration" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( reportPlugin, parser );
        return reportPlugin;
    } //-- ReportPlugin parseReportPlugin( XmlPullParser, boolean, InputSource )

    /**
     * Method parseReportSet.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return ReportSet
     */
    private ReportSet parseReportSet( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        ReportSet reportSet = new ReportSet();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        reportSet.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "id", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                reportSet.setLocation( "id", _location );
                reportSet.setId( interpolatedTrimmed( parser.nextText(), "id" ) );
                setWrapLocation( reportSet, parser, "id" );
            }
            else if ( checkFieldWithDuplicate( parser, "reports", null, parsed ) )
            {
                java.util.List<String> reports = new java.util.ArrayList<String>();
                reportSet.setReports( reports );
                InputLocation _locations;
                _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                reportSet.setLocation( "reports", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "report".equals( parser.getName() ) )
                    {
                        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                        _locations.setLocation( Integer.valueOf( reports.size() ), _location );
                        reports.add( interpolatedTrimmed( parser.nextText(), "reports" ) );
                        setWrapLocation( _locations, parser, reports.size()-1 );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( reportSet, parser, "reports" );
            }
            else if ( checkFieldWithDuplicate( parser, "inherited", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                reportSet.setLocation( "inherited", _location );
                reportSet.setInherited( interpolatedTrimmed( parser.nextText(), "inherited" ) );
                setWrapLocation( reportSet, parser, "inherited" );
            }
            else if ( checkFieldWithDuplicate( parser, "configuration", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                reportSet.setLocation( "configuration", _location );
                reportSet.setConfiguration( org.codehaus.plexus.util.xml.Xpp3DomBuilder.build( parser, true, new Xpp3DomBuilderInputLocationBuilder( _location ) ) );
                setWrapLocation( reportSet, parser, "configuration" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( reportSet, parser );
        return reportSet;
    } //-- ReportSet parseReportSet( XmlPullParser, boolean, InputSource )

    /**
     * Method parseReporting.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return Reporting
     */
    private Reporting parseReporting( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        Reporting reporting = new Reporting();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        reporting.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "excludeDefaults", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                reporting.setLocation( "excludeDefaults", _location );
                reporting.setExcludeDefaults( interpolatedTrimmed( parser.nextText(), "excludeDefaults" ) );
                setWrapLocation( reporting, parser, "excludeDefaults" );
            }
            else if ( checkFieldWithDuplicate( parser, "outputDirectory", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                reporting.setLocation( "outputDirectory", _location );
                reporting.setOutputDirectory( interpolatedTrimmed( parser.nextText(), "outputDirectory" ) );
                setWrapLocation( reporting, parser, "outputDirectory" );
            }
            else if ( checkFieldWithDuplicate( parser, "plugins", null, parsed ) )
            {
                java.util.List<ReportPlugin> plugins = new java.util.ArrayList<ReportPlugin>();
                reporting.setPlugins( plugins );
                InputLocation _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                reporting.setLocation( "plugins", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "plugin".equals( parser.getName() ) )
                    {
                        plugins.add( parseReportPlugin( parser, strict, source ) );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( reporting, parser, "plugins" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( reporting, parser );
        return reporting;
    } //-- Reporting parseReporting( XmlPullParser, boolean, InputSource )

    /**
     * Method parseRepository.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return Repository
     */
    private Repository parseRepository( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        Repository repository = new Repository();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        repository.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "releases", null, parsed ) )
            {
                repository.setReleases( parseRepositoryPolicy( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "snapshots", null, parsed ) )
            {
                repository.setSnapshots( parseRepositoryPolicy( parser, strict, source ) );
            }
            else if ( checkFieldWithDuplicate( parser, "id", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                repository.setLocation( "id", _location );
                repository.setId( interpolatedTrimmed( parser.nextText(), "id" ) );
                setWrapLocation( repository, parser, "id" );
            }
            else if ( checkFieldWithDuplicate( parser, "name", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                repository.setLocation( "name", _location );
                repository.setName( interpolatedTrimmed( parser.nextText(), "name" ) );
                setWrapLocation( repository, parser, "name" );
            }
            else if ( checkFieldWithDuplicate( parser, "url", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                repository.setLocation( "url", _location );
                repository.setUrl( interpolatedTrimmed( parser.nextText(), "url" ) );
                setWrapLocation( repository, parser, "url" );
            }
            else if ( checkFieldWithDuplicate( parser, "layout", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                repository.setLocation( "layout", _location );
                repository.setLayout( interpolatedTrimmed( parser.nextText(), "layout" ) );
                setWrapLocation( repository, parser, "layout" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( repository, parser );
        return repository;
    } //-- Repository parseRepository( XmlPullParser, boolean, InputSource )

    /**
     * Method parseRepositoryBase.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return RepositoryBase
     */
    private RepositoryBase parseRepositoryBase( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        RepositoryBase repositoryBase = new RepositoryBase();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        repositoryBase.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "id", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                repositoryBase.setLocation( "id", _location );
                repositoryBase.setId( interpolatedTrimmed( parser.nextText(), "id" ) );
                setWrapLocation( repositoryBase, parser, "id" );
            }
            else if ( checkFieldWithDuplicate( parser, "name", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                repositoryBase.setLocation( "name", _location );
                repositoryBase.setName( interpolatedTrimmed( parser.nextText(), "name" ) );
                setWrapLocation( repositoryBase, parser, "name" );
            }
            else if ( checkFieldWithDuplicate( parser, "url", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                repositoryBase.setLocation( "url", _location );
                repositoryBase.setUrl( interpolatedTrimmed( parser.nextText(), "url" ) );
                setWrapLocation( repositoryBase, parser, "url" );
            }
            else if ( checkFieldWithDuplicate( parser, "layout", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                repositoryBase.setLocation( "layout", _location );
                repositoryBase.setLayout( interpolatedTrimmed( parser.nextText(), "layout" ) );
                setWrapLocation( repositoryBase, parser, "layout" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( repositoryBase, parser );
        return repositoryBase;
    } //-- RepositoryBase parseRepositoryBase( XmlPullParser, boolean, InputSource )

    /**
     * Method parseRepositoryPolicy.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return RepositoryPolicy
     */
    private RepositoryPolicy parseRepositoryPolicy( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        RepositoryPolicy repositoryPolicy = new RepositoryPolicy();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        repositoryPolicy.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "enabled", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                repositoryPolicy.setLocation( "enabled", _location );
                repositoryPolicy.setEnabled( interpolatedTrimmed( parser.nextText(), "enabled" ) );
                setWrapLocation( repositoryPolicy, parser, "enabled" );
            }
            else if ( checkFieldWithDuplicate( parser, "updatePolicy", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                repositoryPolicy.setLocation( "updatePolicy", _location );
                repositoryPolicy.setUpdatePolicy( interpolatedTrimmed( parser.nextText(), "updatePolicy" ) );
                setWrapLocation( repositoryPolicy, parser, "updatePolicy" );
            }
            else if ( checkFieldWithDuplicate( parser, "checksumPolicy", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                repositoryPolicy.setLocation( "checksumPolicy", _location );
                repositoryPolicy.setChecksumPolicy( interpolatedTrimmed( parser.nextText(), "checksumPolicy" ) );
                setWrapLocation( repositoryPolicy, parser, "checksumPolicy" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( repositoryPolicy, parser );
        return repositoryPolicy;
    } //-- RepositoryPolicy parseRepositoryPolicy( XmlPullParser, boolean, InputSource )

    /**
     * Method parseResource.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return Resource
     */
    private Resource parseResource( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        Resource resource = new Resource();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        resource.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "targetPath", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                resource.setLocation( "targetPath", _location );
                resource.setTargetPath( interpolatedTrimmed( parser.nextText(), "targetPath" ) );
                setWrapLocation( resource, parser, "targetPath" );
            }
            else if ( checkFieldWithDuplicate( parser, "filtering", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                resource.setLocation( "filtering", _location );
                resource.setFiltering( interpolatedTrimmed( parser.nextText(), "filtering" ) );
                setWrapLocation( resource, parser, "filtering" );
            }
            else if ( checkFieldWithDuplicate( parser, "directory", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                resource.setLocation( "directory", _location );
                resource.setDirectory( interpolatedTrimmed( parser.nextText(), "directory" ) );
                setWrapLocation( resource, parser, "directory" );
            }
            else if ( checkFieldWithDuplicate( parser, "includes", null, parsed ) )
            {
                java.util.List<String> includes = new java.util.ArrayList<String>();
                resource.setIncludes( includes );
                InputLocation _locations;
                _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                resource.setLocation( "includes", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "include".equals( parser.getName() ) )
                    {
                        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                        _locations.setLocation( Integer.valueOf( includes.size() ), _location );
                        includes.add( interpolatedTrimmed( parser.nextText(), "includes" ) );
                        setWrapLocation( _locations, parser, includes.size()-1 );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( resource, parser, "includes" );
            }
            else if ( checkFieldWithDuplicate( parser, "excludes", null, parsed ) )
            {
                java.util.List<String> excludes = new java.util.ArrayList<String>();
                resource.setExcludes( excludes );
                InputLocation _locations;
                _locations = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                resource.setLocation( "excludes", _locations );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( "exclude".equals( parser.getName() ) )
                    {
                        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                        _locations.setLocation( Integer.valueOf( excludes.size() ), _location );
                        excludes.add( interpolatedTrimmed( parser.nextText(), "excludes" ) );
                        setWrapLocation( _locations, parser, excludes.size()-1 );
                    }
                    else
                    {
                        checkUnknownElement( parser, strict );
                    }
                }
                setWrapLocation( resource, parser, "excludes" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( resource, parser );
        return resource;
    } //-- Resource parseResource( XmlPullParser, boolean, InputSource )

    /**
     * Method parseScm.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return Scm
     */
    private Scm parseScm( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        Scm scm = new Scm();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        scm.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else if ( "child.scm.connection.inherit.append.path".equals( name ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                scm.setLocation( "childScmConnectionInheritAppendPath", _location );
                scm.setChildScmConnectionInheritAppendPath( interpolatedTrimmed( value, "child.scm.connection.inherit.append.path" ) );
                setWrapLocation( scm, parser, "childScmConnectionInheritAppendPath" );
            }
            else if ( "child.scm.developerConnection.inherit.append.path".equals( name ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                scm.setLocation( "childScmDeveloperConnectionInheritAppendPath", _location );
                scm.setChildScmDeveloperConnectionInheritAppendPath( interpolatedTrimmed( value, "child.scm.developerConnection.inherit.append.path" ) );
                setWrapLocation( scm, parser, "childScmDeveloperConnectionInheritAppendPath" );
            }
            else if ( "child.scm.url.inherit.append.path".equals( name ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                scm.setLocation( "childScmUrlInheritAppendPath", _location );
                scm.setChildScmUrlInheritAppendPath( interpolatedTrimmed( value, "child.scm.url.inherit.append.path" ) );
                setWrapLocation( scm, parser, "childScmUrlInheritAppendPath" );
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "connection", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                scm.setLocation( "connection", _location );
                scm.setConnection( interpolatedTrimmed( parser.nextText(), "connection" ) );
                setWrapLocation( scm, parser, "connection" );
            }
            else if ( checkFieldWithDuplicate( parser, "developerConnection", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                scm.setLocation( "developerConnection", _location );
                scm.setDeveloperConnection( interpolatedTrimmed( parser.nextText(), "developerConnection" ) );
                setWrapLocation( scm, parser, "developerConnection" );
            }
            else if ( checkFieldWithDuplicate( parser, "tag", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                scm.setLocation( "tag", _location );
                scm.setTag( interpolatedTrimmed( parser.nextText(), "tag" ) );
                setWrapLocation( scm, parser, "tag" );
            }
            else if ( checkFieldWithDuplicate( parser, "url", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                scm.setLocation( "url", _location );
                scm.setUrl( interpolatedTrimmed( parser.nextText(), "url" ) );
                setWrapLocation( scm, parser, "url" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( scm, parser );
        return scm;
    } //-- Scm parseScm( XmlPullParser, boolean, InputSource )

    /**
     * Method parseSite.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return Site
     */
    private Site parseSite( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        String tagName = parser.getName();
        Site site = new Site();
        InputLocation _location;
        _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
        site.setLocation( "", _location );
        for ( int i = parser.getAttributeCount() - 1; i >= 0; i-- )
        {
            String name = parser.getAttributeName( i );
            String value = parser.getAttributeValue( i );

            if ( name.indexOf( ':' ) >= 0 )
            {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            }
            else if ( "child.site.url.inherit.append.path".equals( name ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                site.setLocation( "childSiteUrlInheritAppendPath", _location );
                site.setChildSiteUrlInheritAppendPath( interpolatedTrimmed( value, "child.site.url.inherit.append.path" ) );
                setWrapLocation( site, parser, "childSiteUrlInheritAppendPath" );
            }
            else
            {
                checkUnknownAttribute( parser, name, tagName, strict );
            }
        }
        java.util.Set parsed = new java.util.HashSet();
        while ( ( strict ? parser.nextTag() : nextTag( parser ) ) == XmlPullParser.START_TAG )
        {
            if ( checkFieldWithDuplicate( parser, "id", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                site.setLocation( "id", _location );
                site.setId( interpolatedTrimmed( parser.nextText(), "id" ) );
                setWrapLocation( site, parser, "id" );
            }
            else if ( checkFieldWithDuplicate( parser, "name", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                site.setLocation( "name", _location );
                site.setName( interpolatedTrimmed( parser.nextText(), "name" ) );
                setWrapLocation( site, parser, "name" );
            }
            else if ( checkFieldWithDuplicate( parser, "url", null, parsed ) )
            {
                _location = new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), source );
                site.setLocation( "url", _location );
                site.setUrl( interpolatedTrimmed( parser.nextText(), "url" ) );
                setWrapLocation( site, parser, "url" );
            }
            else
            {
                checkUnknownElement( parser, strict );
            }
        }
        setWrapLocation( site, parser );
        return site;
    } //-- Site parseSite( XmlPullParser, boolean, InputSource )

    /**
     * Method read.
     *
     * @param parser
     * @param source
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return Model
     */
    private Model read( XmlPullParser parser, boolean strict, InputSource source )
        throws IOException, XmlPullParserException
    {
        Model model = null;
        int eventType = parser.getEventType();
        boolean parsed = false;
        while ( eventType != XmlPullParser.END_DOCUMENT )
        {
            if ( eventType == XmlPullParser.START_TAG )
            {
                if ( strict && ! "project".equals( parser.getName() ) )
                {
                    throw new XmlPullParserException( "Expected root element 'project' but found '" + parser.getName() + "'", parser, null );
                }
                else if ( parsed )
                {
                    // fallback, already expected a XmlPullParserException due to invalid XML
                    throw new XmlPullParserException( "Duplicated tag: 'project'", parser, null );
                }
                model = parseModel( parser, strict, source );
                model.setModelEncoding( parser.getInputEncoding() );
                parsed = true;
            }
            eventType = parser.next();
        }
        if ( parsed )
        {
            return model;
        }
        throw new XmlPullParserException( "Expected root element 'project' but found no element at all: invalid XML document", parser, null );
    } //-- Model read( XmlPullParser, boolean, InputSource )

    /**
     * Sets the state of the "add default entities" flag.
     *
     * @param addDefaultEntities
     */
    public void setAddDefaultEntities( boolean addDefaultEntities )
    {
        this.addDefaultEntities = addDefaultEntities;
    } //-- void setAddDefaultEntities( boolean )


      //-----------------/
     //- Inner Classes -/
    //-----------------/

    /**
     * Class Xpp3DomBuilderInputLocationBuilder.
     *
     * @version $Revision$ $Date$
     */
    private static class Xpp3DomBuilderInputLocationBuilder
        implements org.codehaus.plexus.util.xml.Xpp3DomBuilder.InputLocationBuilder
    {

          //--------------------------/
         //- Class/Member Variables -/
        //--------------------------/

        /**
         * Field rootLocation.
         */
        private final InputLocation rootLocation;


          //----------------/
         //- Constructors -/
        //----------------/

        public Xpp3DomBuilderInputLocationBuilder(InputLocation rootLocation)
        {
            this.rootLocation = rootLocation;
        } //-- org.apache.maven.model.io.xpp3.Xpp3DomBuilderInputLocationBuilder(InputLocation)


          //-----------/
         //- Methods -/
        //-----------/

        /**
         * Method toInputLocation.
         *
         * @param parser
         * @return Object
         */
        public Object toInputLocation( XmlPullParser parser )
        {
            return new InputLocation( parser.getLineNumber(), parser.getColumnNumber(), rootLocation.getSource() );
        } //-- Object toInputLocation( XmlPullParser )

    }

    public static interface ContentTransformer
{
    /**
     * Interpolate the value read from the xpp3 document
     * @param source The source value
     * @param fieldName A description of the field being interpolated. The implementation may use this to
     *                           log stuff.
     * @return The interpolated value.
     */
    String transform( String source, String fieldName );
}


    /**
     * Adds the location of the first character of the opening tag and the last character of the closing tag to the
     * given model element.  This method is one of the few customizations that differ from the standard
     * {@link org.apache.maven.model.io.xpp3.MavenXpp3ReaderEx}.
     *
     * @param element the model element to update.
     * @param parser  the parser object.
     * @param key     the key of the sub-element to record (empty if element == sub-element).
     */
    private void setWrapLocation(InputLocationTracker element, XmlPullParser parser, Object key) {
        int line = parser.getLineNumber();
        int col = parser.getColumnNumber(); // column is the last char of the tag (`>`)
        element.setLocation(key + END, new InputLocation(line, col));
        InputLocation endOfStart = element.getLocation(key);
        line = endOfStart.getLineNumber();
        col = endOfStart.getColumnNumber() - parser.getName().length() - 2; // column is the last char of the tag (`>`)
        element.setLocation(key + START, new InputLocation(line, col));
    }

    private void setWrapLocation(InputLocationTracker element, XmlPullParser parser) {
        setWrapLocation(element, parser, "");
    }

    public static final String START = new Object().toString();
    public static final String END = new Object().toString();
}

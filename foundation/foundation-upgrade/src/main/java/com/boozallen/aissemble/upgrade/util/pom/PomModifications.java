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

import com.boozallen.aissemble.upgrade.util.FileUtils;
import org.apache.maven.model.InputLocation;
import org.codehaus.plexus.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.function.Function;

/**
 * Tracks and applies modifications to a Maven POM file "in-place" to preserve formatting.
 */
public class PomModifications extends TreeSet<PomModifications.Modification> {

    public Final finalizeMods() {
        return new Final(iterator());
    }

    public static class Final {
        private Iterator<Modification> iterator;
        private Modification next;

        private Final(Iterator<Modification> iterator) {
            this.iterator = iterator;
            next = iterator.next();
        }


        public boolean appliesTo(int lineNumber) {
            return next != null && next.getStart().getLineNumber() == lineNumber;
        }

        /**
         * Applies the next modification to the input reader, writing the result to the output writer.
         *
         * @param in the input reader
         * @param out the output writer
         * @param line the current line of the input reader
         * @return the line number of `in` after the modification has been applied (changes iff the input reader was advanced)
         * @throws IOException if an I/O error occurs
         */
        public int apply(BufferedReader in, Writer out, String line) throws IOException {
            int newInputLine = next.apply(in, out, line);
            if (iterator.hasNext()) {
                next = iterator.next();
            } else {
                next = null;
            }
            return newInputLine;
        }
    }

    public static abstract class Modification implements Comparable<Modification> {
        private final InputLocation start;

        public Modification(InputLocation start) {
            this.start = start;
        }

        public InputLocation getStart() {
            return start;
        }
        
        public abstract int apply(BufferedReader in, Writer out, String currentLine) throws IOException;

        @Override
        public int compareTo(Modification o) {
            return Comparator.comparingInt(InputLocation::getLineNumber)
                    .thenComparingInt(InputLocation::getColumnNumber)
                    .compare(this.getStart(), o.getStart());
        }
    }

    /**
     * Advances the input reader to the line at the specified end location, writing only the content that is not between
     * the start location and the end location to the output writer. I.e., any content on the first line before the
     * start of the location, and any content on the last line after the end of the location, is written to the output
     * writer.
     */
    public static class Deletion extends Modification {
        private final InputLocation end;

        public Deletion(InputLocation start, InputLocation end) {
            super(start);
            this.end = end;
        }

        public InputLocation getEnd() {
            return end;
        }

        @Override
        public int apply(BufferedReader in, Writer out, String line) throws IOException {
            int current = getStart().getLineNumber();
            // NB: clashes with other modifications on same line
            int startColumn = getStart().getColumnNumber();
            String substring = startColumn == 0 ? "" : line.substring(0, startColumn - 1);
            if (StringUtils.isNotBlank(substring)) {
                out.write(substring);
                out.write("\n");
            }
            while (current < getEnd().getLineNumber()) {
                line = in.readLine();
                current++;
            }
            if( getEnd().getColumnNumber() <= line.length() ) {
                out.write(line.substring(getEnd().getColumnNumber()-1));
                out.write("\n");
            }
            return current;
        }
    }

    /**
     * Inserts the produced content at the specified start line and before the existing content on that line.  Does NOT
     * support adding content to the middle of a line.
     */
    public static class Insertion extends Modification {
        private final Function<String,String> contentProducer;
        private final int currentIndent;

        /**
         * @param start the location to insert the content (will insert before the existing content on that line)
         * @param currentIndent the indent level of the current content on the line
         * @param contentProducer a function that produces the content to insert, given a one-level indent string
         */
        public Insertion(InputLocation start, int currentIndent, Function<String,String> contentProducer) {
            super(start);
            this.contentProducer = contentProducer;
            this.currentIndent = currentIndent;
        }

        @Override
        public int apply(BufferedReader in, Writer out, String line) throws IOException {
            String indent = FileUtils.getIndent(line, currentIndent);
            out.write(contentProducer.apply(indent));
            out.write(line);
            out.write("\n");
            return getStart().getLineNumber();
        }
    }

    /**
     * Replaces the content between the start and end locations with the produced content.
     */
    public static class Replacement extends Modification {
        private final InputLocation end;
        private final Function<String,String> contentProducer;
        private final int indentLvl;

        /**
         * Constructor for replacing content within a single line.
         *
         * @param start the location to insert the new content
         * @param end the location to skip to, existing content between start and end will be deleted
         * @param content the new content
         */
        public Replacement(InputLocation start, InputLocation end, String content) {
            this(start, end, 0, l -> content);
        }

        /**
         * Constructor for multi-line replacements.
         *
         * @param start the location to insert the new content
         * @param end the location to skip to, existing content between start and end will be deleted
         * @param indentLvl the indent level of the current content on the line
         * @param contentProducer a function that produces the content to insert, given a one-level indent string
         */
        public Replacement(InputLocation start, InputLocation end, int indentLvl, Function<String,String> contentProducer) {
            super(start);
            this.end = end;
            this.contentProducer = contentProducer;
            this.indentLvl = indentLvl;
        }

        public InputLocation getEnd() {
            return end;
        }

        public Function<String, String> getContentProducer() {
            return contentProducer;
        }

        public int getIndentLvl() {
            return indentLvl;
        }

        @Override
        public int apply(BufferedReader in, Writer out, String line) throws IOException {
            int current = getStart().getLineNumber();
            // NB: clashes with other modifications on same line
            String substring = line.substring(0, getStart().getColumnNumber() - 1);
            if (StringUtils.isNotBlank(substring)) {
                out.write(substring);
            }
            String indent = FileUtils.getIndent(line, getIndentLvl());
            out.write(getContentProducer().apply(indent));
            while (current < getEnd().getLineNumber()) {
                line = in.readLine();
                current++;
            }
            if( getEnd().getColumnNumber() <= line.length() ) {
                out.write(line.substring(getEnd().getColumnNumber()-1));
                out.write("\n");
            }
            return current;
        }

    }
}

/*
 * B3P Commons Core is a library with commonly used classes for webapps.
 * Included are clieop3, oai, security, struts, taglibs and other
 * general helper classes and extensions.
 *
 * Copyright 2000 - 2008 B3Partners BV
 *
 * This file is part of B3P Commons Core.
 *
 * B3P Commons Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * B3P Commons Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with B3P Commons Core.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * $Id: CsvInputStream.java 2988 2006-03-24 15:57:41Z Matthijs $
 */
package nl.b3p.commons.csv;

import java.io.Reader;
import java.io.LineNumberReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.text.StringCharacterIterator;
import java.text.CharacterIterator;

/* Leest CSV bestanden volgens RFC4180 (http://www.rfc-editor.org/rfc/rfc4180.txt)
 * Quoting met ", in een veld wordt een " geescaped door er nog een " voor te
 * zetten. Ondersteunt ook velden met een nieuwe regel (uiteraard alleen herkend
 * indien deze gequote is).
 *
 * TODO automatisch detecteren separator? (is soms niet mogelijk)
 * XXX check op maximale regellengte?
 * XXX tests toevoegen
 */
public class CsvInputStream {

    private static final char QUOTE = '"';
    /* field read, more fields in record */
    private static final int OK_HAVE_MORE_FIELDS = 0;
    /* field read, end of record/file */
    private static final int OK_END_OF_RECORD = 1;
    /* end of file, no record read */
    private static final int EOF = 2;
    private LineNumberReader input;
    private int recordNumber;
    private char separator = ',';
    private int columnCount = -1;
    private boolean checkColumnCount;
    private String inputDescription = "csv file";

    /** Maakt een nieuwe CsvInputStream, default separator ',' en checkColumnCount
     * false.
     */
    public CsvInputStream(Reader input) {
        this.input = new LineNumberReader(input);
    }

    /** Leest het volgende csv record en retourneert de velden als elementen in
     * een String array. Indien er geen record meer is retourneert deze methode
     * null.
     *
     * @throws CsvFormatExcpetion Indien het csv bestand ongeldig is, of indien
     * checkColumnCount enabled en het record niet evenveel kolommen heeft als
     * het eerste record.
     */
    public List readRecordAsList() throws IOException, CsvFormatException {

        List fields = new ArrayList(columnCount == -1 ? 10 : columnCount);
        int columns = 0;
        int recordState;
        do {
            recordState = getNextField();

            if (recordState != EOF) {
                fields.add(field.toString());
                columns++;
            } else {
                if (columns == 0) {
                    /* end of file */
                    return null;
                }
            }
        } while ((recordState == OK_HAVE_MORE_FIELDS) && ((columnCount == -1) ? true : (columns < columnCount)));

        if (checkColumnCount) {
            if (columnCount != -1) {
                if (columns < columnCount) {
                    throw new CsvFormatException(getExceptionMessage("More columns expected"));
                } else if (recordState == OK_HAVE_MORE_FIELDS) {
                    throw new CsvFormatException(getExceptionMessage("More columns than expected (" + columnCount + ") at pos " + currentLine.getIndex() + 1));
                }
            } else {
                columnCount = columns;
            }
        }
        recordNumber++;
        return fields;
    }

    public String[] readRecord() throws IOException, CsvFormatException {
        List fields = readRecordAsList();
        if(fields == null) {
			return null;
		} else {
        	return (String[]) fields.toArray(new String[]{});
		}
    }

    private String getExceptionMessage(String s) {
        return inputDescription + " at line " + input.getLineNumber() + ": " + s;
    }
    private StringCharacterIterator currentLine;
    private StringBuffer field;

    private int getNextField() throws IOException, CsvFormatException {

        if (currentLine == null) {
            /* nieuw record */

            String s = input.readLine();

            if (s == null) {
                return EOF;
            }
            currentLine = new StringCharacterIterator(s);
        }

        boolean inQuote = false;
        int fieldBeginPos = currentLine.getIndex();

        field = new StringBuffer();
        do {
            char c = currentLine.current();
            int pos = currentLine.getIndex();

            if (c == CharacterIterator.DONE) {
                if (inQuote) {
                    String s = input.readLine();
                    if (s == null) {
                        throw new CsvFormatException("EOF before end of multi-line field");
                    }
                    currentLine = new StringCharacterIterator(s);
                    /* schakel hier mee het stukje uit wat indien het karakter een quote is
                     * en het eerste van het veld dat dan wordt begonnen met een gequote veld,
                     * want dat is niet van toepassing omdat we al midden in een veld zitten
                     * (maar de index van de huidige regel begint wel weer bij 0).
                     */
                    fieldBeginPos = -1;
                    field.append('\n');
                    continue;
                } else {
                    currentLine = null;
                    return OK_END_OF_RECORD;
                }
            }

            if (c == QUOTE) {
                if (inQuote) {
                    c = currentLine.next();

                    if (c == QUOTE) {
                        field.append('"');
                        currentLine.next();
                        continue;
                    }

                    if (c == CharacterIterator.DONE) {
                        currentLine = null;
                        return OK_END_OF_RECORD;
                    }

                    if (c == separator) {
                        currentLine.next();
                        return OK_HAVE_MORE_FIELDS;
                    }

                    throw new CsvFormatException(getExceptionMessage("Expected separator '" + separator + "' but found '" + c + "' at pos " + (currentLine.getIndex() + 1)));
                }

                if (pos == fieldBeginPos) {
                    inQuote = true;
                    currentLine.next();
                    continue;
                }
            }

            if (!inQuote && c == separator) {
                currentLine.next();
                return OK_HAVE_MORE_FIELDS;
            }

            field.append(c);
            currentLine.next();
        } while (true);
    }

    /* geeft het het volgnummer van het laatst gelezen record, beginnend bij 1
     * voor het eerste record. Indien er nog geen record is gelezen geeft 0.
     */
    public int getLastRecordNumber() {
        return recordNumber;
    }

    /* geeft het huidige regelnummer (hoeft niet hetzelfde te zijn als recordNumber-1
     * indien er records zijn die verdeeld zijn over meerdere regels)
     */
    public int getLineNumber() {
        return input.getLineNumber();
    }

    public char getSeparator() {
        return separator;
    }

    public void setSeparator(char separator) {
        this.separator = separator;
    }

    public boolean getCheckColumnCount() {
        return checkColumnCount;
    }

    public void setCheckColumnCount(boolean checkColumnCount) {
        this.checkColumnCount = checkColumnCount;
    }

    public int getColumnCount() {
        if (columnCount == -1) {
            throw new IllegalStateException("No record read yet, column count unknown");
        }
        return columnCount;
    }

    public String getInputDescription() {
        return inputDescription;
    }

    public void setInputDescription(String inputDescription) {
        this.inputDescription = inputDescription;
    }
}

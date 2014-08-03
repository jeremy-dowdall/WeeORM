/*******************************************************************************
 * Copyright (c) 2010 Oobium, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jeremy Dowdall <jeremy@oobium.com> - initial API and implementation
 ******************************************************************************/
package fm.strength.worm.util;

import java.util.HashSet;
import java.util.Set;

public class SqlSafeWords {

	private static final Set<String> reservedWords = new HashSet<String>();
	static {
	    reservedWords.add("ADD".toLowerCase());
	    reservedWords.add("ALL".toLowerCase());
	    reservedWords.add("ALLOCATE".toLowerCase());
	    reservedWords.add("ALTER".toLowerCase());
	    reservedWords.add("AND".toLowerCase());
	    reservedWords.add("ANY".toLowerCase());
	    reservedWords.add("ARE".toLowerCase());
	    reservedWords.add("AS".toLowerCase());
	    reservedWords.add("ASC".toLowerCase());
	    reservedWords.add("ASSERTION".toLowerCase());
	    reservedWords.add("AT".toLowerCase());
	    reservedWords.add("AUTHORIZATION".toLowerCase());
	    reservedWords.add("AVG".toLowerCase());
	    reservedWords.add("BEGIN".toLowerCase());
	    reservedWords.add("BETWEEN".toLowerCase());
	    reservedWords.add("BIT".toLowerCase());
	    reservedWords.add("BOOLEAN".toLowerCase());
	    reservedWords.add("BOTH".toLowerCase());
	    reservedWords.add("BY".toLowerCase());
	    reservedWords.add("CALL".toLowerCase());
	    reservedWords.add("CASCADE".toLowerCase());
	    reservedWords.add("CASCADED".toLowerCase());
	    reservedWords.add("CASE".toLowerCase());
	    reservedWords.add("CAST".toLowerCase());
	    reservedWords.add("CHAR".toLowerCase());
	    reservedWords.add("CHARACTER".toLowerCase());
	    reservedWords.add("CHECK".toLowerCase());
	    reservedWords.add("CLOSE".toLowerCase());
	    reservedWords.add("COLLATE".toLowerCase());
	    reservedWords.add("COLLATION".toLowerCase());
	    reservedWords.add("COLUMN".toLowerCase());
	    reservedWords.add("COMMIT".toLowerCase());
	    reservedWords.add("CONNECT".toLowerCase());
	    reservedWords.add("CONNECTION".toLowerCase());
	    reservedWords.add("CONSTRAINT".toLowerCase());
	    reservedWords.add("CONSTRAINTS".toLowerCase());
	    reservedWords.add("CONTINUE".toLowerCase());
	    reservedWords.add("CONVERT".toLowerCase());
	    reservedWords.add("CORRESPONDING".toLowerCase());
	    reservedWords.add("COUNT".toLowerCase());
	    reservedWords.add("CREATE".toLowerCase());
	    reservedWords.add("CURRENT".toLowerCase());
	    reservedWords.add("CURRENT_DATE".toLowerCase());
	    reservedWords.add("CURRENT_TIME".toLowerCase());
	    reservedWords.add("CURRENT_TIMESTAMP".toLowerCase());
	    reservedWords.add("CURRENT_USER".toLowerCase());
	    reservedWords.add("CURSOR".toLowerCase());
	    reservedWords.add("DEALLOCATE".toLowerCase());
	    reservedWords.add("DEC".toLowerCase());
	    reservedWords.add("DECIMAL".toLowerCase());
	    reservedWords.add("DECLARE".toLowerCase());
	    reservedWords.add("DEFERRABLE".toLowerCase());
	    reservedWords.add("DEFERRED".toLowerCase());
	    reservedWords.add("DELETE".toLowerCase());
	    reservedWords.add("DESC".toLowerCase());
	    reservedWords.add("DESCRIBE".toLowerCase());
	    reservedWords.add("DIAGNOSTICS".toLowerCase());
	    reservedWords.add("DISCONNECT".toLowerCase());
	    reservedWords.add("DISTINCT".toLowerCase());
	    reservedWords.add("DOUBLE".toLowerCase());
	    reservedWords.add("DROP".toLowerCase());
	    reservedWords.add("ELSE".toLowerCase());
	    reservedWords.add("END".toLowerCase());
	    reservedWords.add("ENDEXEC".toLowerCase());
	    reservedWords.add("ESCAPE".toLowerCase());
	    reservedWords.add("EXCEPT".toLowerCase());
	    reservedWords.add("EXCEPTION".toLowerCase());
	    reservedWords.add("EXEC".toLowerCase());
	    reservedWords.add("EXECUTE".toLowerCase());
	    reservedWords.add("EXISTS".toLowerCase());
	    reservedWords.add("EXPLAIN".toLowerCase());
	    reservedWords.add("EXTERNAL".toLowerCase());
	    reservedWords.add("FALSE".toLowerCase());
	    reservedWords.add("FETCH".toLowerCase());
	    reservedWords.add("FIRST".toLowerCase());
	    reservedWords.add("FLOAT".toLowerCase());
	    reservedWords.add("FOR".toLowerCase());
	    reservedWords.add("FOREIGN".toLowerCase());
	    reservedWords.add("FOUND".toLowerCase());
	    reservedWords.add("FROM".toLowerCase());
	    reservedWords.add("FULL".toLowerCase());
	    reservedWords.add("FUNCTION".toLowerCase());
	    reservedWords.add("GET".toLowerCase());
	    reservedWords.add("GET_CURRENT_CONNECTION".toLowerCase());
	    reservedWords.add("GLOBAL".toLowerCase());
	    reservedWords.add("GO".toLowerCase());
	    reservedWords.add("GOTO".toLowerCase());
	    reservedWords.add("GRANT".toLowerCase());
	    reservedWords.add("GROUP".toLowerCase());
	    reservedWords.add("HAVING".toLowerCase());
	    reservedWords.add("HOUR".toLowerCase());
	    reservedWords.add("IDENTITY".toLowerCase());
	    reservedWords.add("IMMEDIATE".toLowerCase());
	    reservedWords.add("IN".toLowerCase());
	    reservedWords.add("INDICATOR".toLowerCase());
	    reservedWords.add("INITIALLY".toLowerCase());
	    reservedWords.add("INNER".toLowerCase());
	    reservedWords.add("INOUT".toLowerCase());
	    reservedWords.add("INPUT".toLowerCase());
	    reservedWords.add("INSENSITIVE".toLowerCase());
	    reservedWords.add("INSERT".toLowerCase());
	    reservedWords.add("INT".toLowerCase());
	    reservedWords.add("INTEGER".toLowerCase());
	    reservedWords.add("INTERSECT".toLowerCase());
	    reservedWords.add("INTERVAL".toLowerCase());
	    reservedWords.add("INTO".toLowerCase());
	    reservedWords.add("IS".toLowerCase());
	    reservedWords.add("ISOLATION".toLowerCase());
	    reservedWords.add("JOIN".toLowerCase());
	    reservedWords.add("KEY".toLowerCase());
	    reservedWords.add("LAST".toLowerCase());
	    reservedWords.add("LEFT".toLowerCase());
	    reservedWords.add("LIKE".toLowerCase());
	    reservedWords.add("LONGINT".toLowerCase());
	    reservedWords.add("LOWER".toLowerCase());
	    reservedWords.add("LTRIM".toLowerCase());
	    reservedWords.add("MATCH".toLowerCase());
	    reservedWords.add("MAX".toLowerCase());
	    reservedWords.add("MIN".toLowerCase());
	    reservedWords.add("MINUTE".toLowerCase());
	    reservedWords.add("NATIONAL".toLowerCase());
	    reservedWords.add("NATURAL".toLowerCase());
	    reservedWords.add("NCHAR".toLowerCase());
	    reservedWords.add("NVARCHAR".toLowerCase());
	    reservedWords.add("NEXT".toLowerCase());
	    reservedWords.add("NO".toLowerCase());
	    reservedWords.add("NOT".toLowerCase());
	    reservedWords.add("NULL".toLowerCase());
	    reservedWords.add("NULLIF".toLowerCase());
	    reservedWords.add("NUMERIC".toLowerCase());
	    reservedWords.add("OF".toLowerCase());
	    reservedWords.add("ON".toLowerCase());
	    reservedWords.add("ONLY".toLowerCase());
	    reservedWords.add("OPEN".toLowerCase());
	    reservedWords.add("OPTION".toLowerCase());
	    reservedWords.add("OR".toLowerCase());
	    reservedWords.add("ORDER".toLowerCase());
	    reservedWords.add("OUT".toLowerCase());
	    reservedWords.add("OUTER".toLowerCase());
	    reservedWords.add("OUTPUT".toLowerCase());
	    reservedWords.add("OVERLAPS".toLowerCase());
	    reservedWords.add("PAD".toLowerCase());
	    reservedWords.add("PARTIAL".toLowerCase());
	    reservedWords.add("PREPARE".toLowerCase());
	    reservedWords.add("PRESERVE".toLowerCase());
	    reservedWords.add("PRIMARY".toLowerCase());
	    reservedWords.add("PRIOR".toLowerCase());
	    reservedWords.add("PRIVILEGES".toLowerCase());
	    reservedWords.add("PROCEDURE".toLowerCase());
	    reservedWords.add("PUBLIC".toLowerCase());
	    reservedWords.add("READ".toLowerCase());
	    reservedWords.add("REAL".toLowerCase());
	    reservedWords.add("REFERENCES".toLowerCase());
	    reservedWords.add("RELATIVE".toLowerCase());
	    reservedWords.add("RESTRICT".toLowerCase());
	    reservedWords.add("REVOKE".toLowerCase());
	    reservedWords.add("RIGHT".toLowerCase());
	    reservedWords.add("ROLLBACK".toLowerCase());
	    reservedWords.add("ROWS".toLowerCase());
	    reservedWords.add("RTRIM".toLowerCase());
	    reservedWords.add("SCHEMA".toLowerCase());
	    reservedWords.add("SCROLL".toLowerCase());
	    reservedWords.add("SECOND".toLowerCase());
	    reservedWords.add("SELECT".toLowerCase());
	    reservedWords.add("SESSION_USER".toLowerCase());
	    reservedWords.add("SET".toLowerCase());
	    reservedWords.add("SMALLINT".toLowerCase());
	    reservedWords.add("SOME".toLowerCase());
	    reservedWords.add("SPACE".toLowerCase());
	    reservedWords.add("SQL".toLowerCase());
	    reservedWords.add("SQLCODE".toLowerCase());
	    reservedWords.add("SQLERROR".toLowerCase());
	    reservedWords.add("SQLSTATE".toLowerCase());
	    reservedWords.add("SUBSTR".toLowerCase());
	    reservedWords.add("SUBSTRING".toLowerCase());
	    reservedWords.add("SUM".toLowerCase());
	    reservedWords.add("SYSTEM_USER".toLowerCase());
	    reservedWords.add("TABLE".toLowerCase());
	    reservedWords.add("TEMPORARY".toLowerCase());
	    reservedWords.add("TIMEZONE_HOUR".toLowerCase());
	    reservedWords.add("TIMEZONE_MINUTE".toLowerCase());
	    reservedWords.add("TO".toLowerCase());
	    reservedWords.add("TRAILING".toLowerCase());
	    reservedWords.add("TRANSACTION".toLowerCase());
	    reservedWords.add("TRANSLATE".toLowerCase());
	    reservedWords.add("TRANSLATION".toLowerCase());
	    reservedWords.add("TRUE".toLowerCase());
	    reservedWords.add("TYPE".toLowerCase());
	    reservedWords.add("UNION".toLowerCase());
	    reservedWords.add("UNIQUE".toLowerCase());
	    reservedWords.add("UNKNOWN".toLowerCase());
	    reservedWords.add("UPDATE".toLowerCase());
	    reservedWords.add("UPPER".toLowerCase());
	    reservedWords.add("USER".toLowerCase());
	    reservedWords.add("USING".toLowerCase());
	    reservedWords.add("VALUES".toLowerCase());
	    reservedWords.add("VARCHAR".toLowerCase());
	    reservedWords.add("VARYING".toLowerCase());
	    reservedWords.add("VIEW".toLowerCase());
	    reservedWords.add("WHENEVER".toLowerCase());
	    reservedWords.add("WHERE".toLowerCase());
	    reservedWords.add("WITH".toLowerCase());
	    reservedWords.add("WORK".toLowerCase());
	    reservedWords.add("WRITE".toLowerCase());
	    reservedWords.add("XML".toLowerCase());
	    reservedWords.add("XMLEXISTS".toLowerCase());
	    reservedWords.add("XMLPARSE".toLowerCase());
	    reservedWords.add("XMLSERIALIZE".toLowerCase());
	    reservedWords.add("YEAR".toLowerCase());
	}


    /**
	 * Escapes the word if it is an SQL reserved word by surrounding it with quotes (").
	 * @param column
	 * @return a word that is safe to use in an SQL query
	 */
	public static String get(String column) {
		return (reservedWords.contains(column)) ? ("\"" + column + "\"") : column;
	}
    
}

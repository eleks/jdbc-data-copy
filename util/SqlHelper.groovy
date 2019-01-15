/**/
import groovy.lang.Closure;
import groovy.lang.Tuple;

import groovy.sql.Sql;
import groovy.sql.SqlWithParams;

import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class SqlHelper {
	static final Pattern goDelim = Pattern.compile('(?i)^GO\\s*$');
	static final Pattern slashDelim = Pattern.compile('(?i)^/\\s*$');
	static final Pattern semicolonDelim = Pattern.compile('(?i)(.*);\\s*$');
	private static Pattern spaces = Pattern.compile('(?i)^\\s*$');


	/** splits resource stream, file or reader to commands and calls closure for each command.
	 * delimiter is a regexp that should fully match one line. the matched line not considered as a command.
	 * if the last command not ended with a delimiter then exception will be thrown.
	 */
	public static void eachCommand(Object batch, Pattern delim, Closure c) throws IOException {
		BufferedReader r = Cast.asReader(batch);
		StringBuilder cmd = new StringBuilder();
		String line;
		while( (line = r.readLine())!=null ){
			Matcher m = delim.matcher(line);
			if( m.matches() ){
				if(m.groupCount()>0){
					//add first group into sql command if there is a group in pattern
					if(cmd.length()>0)cmd.append("\r\n");
					cmd.append(m.group(1));
				}
				//got a delimiter
				c.call(cmd.toString());
				cmd.setLength(0);
			} else {
				if(cmd.length()>0)cmd.append("\r\n");
				cmd.append(line);
			}
		}
		if( !spaces.matcher(cmd).matches() )throw new RuntimeException("The last command was not processed: \n"+cmd);
	}
	
	
	/** splits resource stream, file or reader to commands with `GO` delimiter line and calls closure for each command.
	 */
	public static void eachGoCommand(Object batch, Closure c)  throws IOException {
		eachCommand( batch, goDelim, c );
	}
	
	/** splits resource stream, file or reader to commands with `/` delimiter line and calls closure for each command.
	 */
	public static void eachSlashCommand(Object batch, Closure c) throws IOException {
		eachCommand( batch, slashDelim, c );
	}

	/** splits resource stream, file or reader to commands with `/` delimiter line and calls closure for each command.
	 */
	public static void eachSemicolonCommand(Object batch, Closure c) throws IOException {
		eachCommand( batch, semicolonDelim, c );
	}

}

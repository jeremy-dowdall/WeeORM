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

import android.text.TextUtils;

import fm.strength.sloppyj.Jay;
import fm.strength.sloppyj.SnakeMapper;

public class StringUtils {

	public static String columnName(String variable) {
		return SnakeMapper.toSnake(variable);
	}
	
    public static String format(String msg, Object[] args) {
        for(int i = 0; i < args.length; i++) {
            if(!(args[i] instanceof String)) {
                args[i] = Jay.get(args[i]).asJson();
            }
        }
        return String.format(msg, args);
    }

    public static String path(String...segments) {
        String path = TextUtils.join("/", segments);
        if(path.charAt(0) == '/') path = path.substring(1);
        Log.d("path: %", path);
        return path;
    }

    public static String plural(String singular) {
        if(singular == null || singular.length() == 0) {
            return singular;
        }
        if(singular.equalsIgnoreCase("person")) {
            return singular.charAt(0) + "eople";
        }
        else if(singular.equalsIgnoreCase("child")) {
            return singular.charAt(0) + "hildren";
        }
        else if(singular.equalsIgnoreCase("alumnus")) {
            return singular.charAt(0) + "lumni";
        }
        else if('y' == singular.charAt(singular.length()-1)) {
            if(singular.length() > 1) {
                switch(singular.charAt(singular.length()-2)) {
                    case 'a':
                    case 'e':
                    case 'i':
                    case 'o':
                    case 'u':
                        break;
                    default:
                        return singular.substring(0, singular.length()-1) + "ies";
                }
            }
        }
        else if('s' == singular.charAt(singular.length()-1)){
            return singular + "es";
        }
        else if(singular.endsWith("ch")){
            return singular + "es";
        }
        return singular + "s";
    }

    public static String singular(String plural) {
        if(plural.equalsIgnoreCase("people")) {
            return plural.charAt(0) + "erson";
        } else if(plural.equalsIgnoreCase("alumni")) {
            return plural.charAt(0) + "lumnus";
        } else if(plural.endsWith("ies")) {
            return plural.substring(0, plural.length()-3) + 'y';
        } else if('s' == plural.charAt(plural.length()-1)){
            return plural.substring(0, plural.length() - 1);
        }
        return plural;
    }

}

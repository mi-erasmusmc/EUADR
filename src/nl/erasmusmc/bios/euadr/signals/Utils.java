/**
 * (C) Science and Technology Corporation, Delft, The Netherlands, 2015
 * All Rights Reserved
 * No part of this program or any of its contents may be reproduced, copied, modified or adapted, 
 * without the prior written consent of the author, unless otherwise indicated for stand-alone materials.
 * 
 * For any mode of sharing, please contact the author at the email below.
 * 
 * Commercial use and distribution of the contents of the website is not allowed without express and 
 * prior written consent of the author.
 * 
 * @author Erik van Mulligen, vanmulligen@stcorp.nl
 * @date
 */

package nl.erasmusmc.bios.euadr.signals;

import java.util.ArrayList;
import java.util.List;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/**
 *  
 */
public class Utils {

    public static String getContentElementAsString(JsonElement jsonElement) {
	return (jsonElement == null || jsonElement.isJsonNull()) ? null : jsonElement.getAsString();
    }

    public static Boolean getContentElementAsBoolean(JsonElement jsonElement) {
	return (jsonElement == null || jsonElement.isJsonNull()) ? null : jsonElement.getAsBoolean();
    }

    public static Integer getContentElementAsInteger(JsonElement jsonElement) {
	return (jsonElement == null || jsonElement.isJsonNull()) ? null : jsonElement.getAsInt();
    }

    public static Float getContentElementAsFloat(JsonElement jsonElement) {
	return (jsonElement == null || jsonElement.isJsonNull()) ? null : jsonElement.getAsFloat();
    }

    public static Long getContentElementAsDate(JsonElement jsonElement) {
	return (jsonElement == null || jsonElement.isJsonNull()) ? null : jsonElement.getAsLong();
    }

    public static List<String> getContentElementAsList(JsonElement jsonElement) {
	List<String> result = null;

	if ( jsonElement != null && !jsonElement.isJsonNull() ){
	    result = new ArrayList<String>();
	    JsonArray array = jsonElement.getAsJsonArray();
	    for ( int i = 0 ; i < array.size() ; i++ ){
		result.add( array.get(i).getAsString() );
	    }
	}
	return result;
    }
}

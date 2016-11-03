package nl.erasmusmc.mi.bios.etox.rxnorm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RxNorm {
    static Logger logger = LogManager.getLogger();
    private static Connection connection = null;
    private static String server = null;
    private static String database = null;
    private static String username = null;
    private static String password = null;

    public RxNorm(){
    }

    public RxNorm( String server, String database, String username, String password ){
	RxNorm.server = server;
	RxNorm.database = database;
	RxNorm.username = username;
	RxNorm.password = password;
    }

    public String getPreferredName(String atc) {
	return getPreferredName(atc,false);
    }

    public String getPreferredName(String atc, Boolean force) {
	logger.info( "getPreferredName(" + atc + "," + force +")" );
	Connection connection = getConnection(force);
	String result = null;

	try {
	    PreparedStatement statement = connection.prepareStatement( "SELECT DISTINCT STR FROM RXNCONSO WHERE SAB=\"RXNORM\" AND RXCUI in (SELECT DISTINCT RXCUI FROM `RXNCONSO` where SAB=\"ATC\" and CODE=?)" );
	    statement.setString( 1, atc );
	    ResultSet resultSet = statement.executeQuery();
	    if ( resultSet.next() ){
		result = resultSet.getString("STR");
	    }
	} catch (SQLException e) {
	    if (!force){
		result = getPreferredName(atc,true);
	    }
	    else{
		logger.info( "getPreferredName(1): " + e.getMessage() );
	    }
	}

	return result;
    }

    private static Connection getConnection(boolean force){
	if ( connection == null || force ){
	    try {
		Class.forName("com.mysql.jdbc.Driver");
		/* added UTF8 encoding for searches on special characters */
		connection = DriverManager.getConnection("jdbc:mysql://" + server + "/" + database + "?" + "user=" + username + "&password=" + password + "&connectTimeout=0&socketTimeout=0&autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8");
	    } catch (ClassNotFoundException | SQLException e) {
		connection = null;
	    }
	}
	return connection;
    }

    public List<String> getMeshCodes(String atc) {
	return getCodes( atc, "MSH");
    }

    public List<String> getSnomedCodes(String atc) {
	return getCodes( atc, "SNOMEDCT_US");
    }

    public List<String> getRxNormCodes(String atc) {
	return getCodes( atc, "RXNORM");
    }

    private List<String> getCodes(String atc, String code_system){
	return getCodes(atc, code_system, false);
    }
    
    public String getAtc(String name){
	return getAtc(name, false);
    }
    
    private String getAtc(String name, Boolean force){
	Connection connection = getConnection(force);

	try {
	    PreparedStatement statement = connection.prepareStatement( "SELECT CODE FROM RXNCONSO WHERE SAB=\"ATC\" and STR=?" );
	    statement.setString( 1, name );
	    ResultSet resultSet = statement.executeQuery();
	    if ( resultSet.next() ){
		return resultSet.getString("CODE");
	    }
	} catch (SQLException e) {
	    if (!force){
		return getAtc(name, true);
	    }
	    else{
		logger.error( "getAtc(): " + e.getMessage() );
	    }
	} finally {
	    if (connection != null) {
		try { 
		    connection.close(); 
		} 
		catch (SQLException ignore) {
		}
	    }
	}

	return null;
	
    }

    private List<String> getCodes(String atc, String code_system, Boolean force){
	List<String> result = new ArrayList<String>();
	Connection connection = getConnection(force);

	try {
	    PreparedStatement statement = connection.prepareStatement( "SELECT DISTINCT CODE FROM RXNCONSO WHERE SAB=? AND RXCUI in (SELECT DISTINCT RXCUI FROM `RXNCONSO` where SAB=\"ATC\" and CODE=?)" );
	    statement.setString( 1, code_system );
	    statement.setString( 2, atc );
	    ResultSet resultSet = statement.executeQuery();
	    while ( resultSet.next() ){
		result.add( resultSet.getString("CODE") );
	    }
	} catch (SQLException e) {
	    if (!force){
		result = getCodes(atc, code_system, true);
	    }
	    else{
		logger.error( "getCodes(): " + e.getMessage() );
	    }
	} finally {
	    if (connection != null) {
		try { 
		    connection.close(); 
		} 
		catch (SQLException ignore) {
		}
	    }
	}

	return result;
    }

}

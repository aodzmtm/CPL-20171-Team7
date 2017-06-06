package common.properties;

import java.io.IOException;
import java.util.Properties;

import common.properties.PropertiesManager.FILE_TYPE;

public class Context {/**
	 * Main Setting XML FILE of Acroem Co,. Ltd. 
	 */
	public static final String MAIN_CONFIG_FILE = "context.xml";
	
	/**
	 * ȯ�漳�� ���� ����Ʈ�� ������ �ִ� Properties
	 */
	private static Properties prop;
	
	
	/**
	 * ������
	 * 
	 * @throws IOException 
	 */
	protected Context(){}
	
	
	public static Properties getProp() throws IOException
	{
		if (prop == null)
		{
			prop = PropertiesManager.readProperties(MAIN_CONFIG_FILE, FILE_TYPE.FILE_TYPE_XML);
		}
		
		return prop;
	}
	
	
	
	/**
	 * �־��� key�� �ش��ϴ� property ���� �ǵ�����.
	 * 
	 * @param key Ű ��
	 * @return property ��, ������ null
	 */
	public static String getPropKeyValue(String key) throws IOException
	{
		return getProp().getProperty(key);
	}
	
}

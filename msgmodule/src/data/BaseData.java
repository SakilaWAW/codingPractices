package data;

import java.util.HashMap;
import java.util.Map;

/**
 * 用来描述所有报文数据类的基础功能：
 * 初始化 & 得到信息
 * @author stg
 *
 */
public abstract class BaseData {
	
	/**
	 * 数据源
	 */
	protected Map<String,String> m_valueMap;
	
	/**
	 * 在构造函数中进行init()
	 */
	public BaseData(){
		m_valueMap = new HashMap<String,String>();
	}
	
	/**
	 * 数据初始化
	 */
	public abstract void init();
	
	/**
	 * 得到数据源
	 * @return 数据源
	 */
	public Map<String,String> getData(){
		return m_valueMap;
	}
}

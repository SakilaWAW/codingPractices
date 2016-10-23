package msgbuilder;

import data.BaseData;

public abstract class MsgBuilder {
	
	/**
	 * 数据
	 */
	private BaseData m_data;
	
	/**
	 * 模板类型
	 */
	private int templateType;
	
	/**
	 * 设置数据源
	 * @param m_data
	 */
	public void setM_data(BaseData m_data) {
		this.m_data = m_data;
	}
	
	/**
	 * 得到模板类型
	 * @return 模板类型
	 */
	public int getTemplateType() {
		return templateType;
	}
	
	/**
	 * 设置模板类型
	 * @param templateType 模板类型
	 */
	public void setTemplateType(int templateType) {
		this.templateType = templateType;
	}
	
	protected void init(){
		m_data.init();
	}
	
	/**
	 * 构造方法
	 */
	public MsgBuilder(){
		
	}
	
	public String getMsg(){
		return null;
	}
}

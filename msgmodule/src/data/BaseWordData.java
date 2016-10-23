package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.n22.nci.frame.Path;
import com.n22.nci.pages.policy.StepMgr;
import com.n22.nci.policy.PolicyCustomer;
import com.n22.nci.tool.PolicyService;

public abstract class BaseWordData extends BaseData {

	private Context context;
	private Policy policy;
	private Agent agent;

	/**
	 * 初始化基础信息 不含受益人和产品信息
	 */
	@Override
	public void init() {
		initBase();
	}

	public BaseWordData(Context context, Policy policy, Agent agent) {
		super();
		this.context = context;
		this.policy = policy;
		this.agent = agent;
	}

	/**
	 * 初始化基础信息
	 */
	private void initBase() {
		m_valueMap.put("POLICY", policy);
		m_valueMap.put("AGENT", agent);

		// 如果值不存在默认传值1，如果存在则传已有值
		if (null == policy.getAdditional("renewFlag")
				|| "".equals(policy.getAdditional("renewFlag").toString()
						.trim()))
			policy.setAdditional("renewFlag", "1");

		// 是否银代新政
		if (null == policy.getAdditional("newDeal")
				|| "".equals(policy.getAdditional("newDeal").toString().trim()))
			policy.setAdditional("newDeal", "0");

		PolicyCustomer applicant = policy.getApplicant();
		PolicyCustomer insured = policy.getInsured();

		// 投保人性别 需要转换成核心认识的
		m_valueMap.put("APPLICANT_SEX",
				getCoreSexValue(applicant.getGenderCode()));
		m_valueMap.put("APPLICANT_CARDTYPE",
				getCoreCardTypeValue(applicant.getCardType()));
		m_valueMap.put("APPLICANT_MARRIAGE",
				getCoreMarriagrValue(applicant.getMarriage()));

		m_valueMap.put("INSURED_SEX", getCoreSexValue(insured.getGenderCode()));
		m_valueMap.put("INSURED_CARDTYPE",
				getCoreCardTypeValue(insured.getCardType()));
		m_valueMap.put("INSURED_MARRIAGE",
				getCoreMarriagrValue(insured.getMarriage()));
		m_valueMap.put("NewPayMode", getCorePayTypeValue(null == policy
				.getPay() ? null : policy.getPay().getTransferCode()));

		//
		m_valueMap
				.put("INSURED_CARDSTOPTIME", transCustomerCardIsLong(insured));
		m_valueMap.put("APPLICANT_CARDSTOPTIME",
				transCustomerCardIsLong(applicant));

		long currentTimeMillis = System.currentTimeMillis();
		Date currentDate = new Date(currentTimeMillis);
		SimpleDateFormat sdf = new SimpleDateFormat();

		sdf.applyPattern("yyyy-MM-dd");
		String date = sdf.format(currentDate);

		sdf.applyPattern("HH:mm:ss");
		String time = sdf.format(currentDate);

		m_valueMap.put("DATE", date);
		m_valueMap.put("TIME", time);
		m_valueMap.put("MachineNo", PolicyService.getSn());

		Map<String, Object> mmValueMap = (Map<String, Object>) policy
				.getAdditional("MM_VALUEMAP");
		if (StepMgr.isQuestChange() && null != mmValueMap) {
			String mmSubmitJson = (String) mmValueMap.get("MM_CASEINFO_JSON");
			m_valueMap.put("MagnumSend", mmSubmitJson);
		}

		// 对 全部录入有 姓名的地方 做转换，目前有 投被保人姓名、银行卡持有人姓名、续期银行卡持有人姓名
		// 受益人姓名 不在此做处理，在拼接受益人的地方做处理
		String insuredName = this.repalceStarStr(insured.getName());
		String applicantName = this.repalceStarStr(applicant.getName());
		String bankAccountName = this.repalceStarStr(policy.getPay()
				.getBankAccountName());
		String RollOverBankAccountName = this.repalceStarStr(policy.getPay()
				.getRollOverBankAccountName());
		m_valueMap.put("INSUREDNAME", insuredName);
		m_valueMap.put("APPLICANTNAME", applicantName);
		m_valueMap.put("ACCOUNTNAME", bankAccountName);
		m_valueMap.put("ROACCOUNTNAME", RollOverBankAccountName);
		// 南京医保
		m_valueMap.put("POSTICKER", policy.getAdditional("PosTicker"));
		String paydate = policy.getPayDate() != null ? sdf.format(policy
				.getPayDate()) : "";
		m_valueMap.put("PAYDATE", paydate);
	}
	
	/**
	 * 初始化dic-config映射表到valueMap
	 */
	private void initTransCoreValue() {
		InputStream is = null;
		try {
			if (null == context) {
				String path = Path.getPolicyPath() + getDicConfigXml();
				is = new FileInputStream(new File(path));
			} else {
				is = context.getAssets().open(getDicConfigXml());
			}
			SAXBuilder builder = new SAXBuilder();
			Document read_doc = builder.build(is);
			Element root = read_doc.getRootElement();
			disposeElement(root, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 获得dic-config配置文件
	 */
	protected abstract void getDicConfigXml();
}

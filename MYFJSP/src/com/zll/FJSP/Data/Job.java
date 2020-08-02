package com.zll.FJSP.Data;

/**
 * Description:工件参数
 * 
 * @author zll-hust E-mail:zh20010728@126.com
 * @date 创建时间：2020年5月28日 下午2:29:10
 */
public class Job {
	public int index;// 工件编号 jobNo
	public int opsNr;// 工序数 procedureNo
	public int[] opsIndex;// 工件工序对应的index
	public int[] opsMacNr;// 工序对应备选机器数

	public Job(int index, int opsNr, int[] opsIndex, int[] opsMacNr) {
		this.index = index;
		this.opsNr = opsNr;
		this.opsIndex = opsIndex;
		this.opsMacNr = opsMacNr;
	}
}
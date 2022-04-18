package com.hide_and_fps.business_logic.dto.system;

import java.util.Date;

import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
public class SyAdminDto
{
	/** 관리자 번호
	 * */
	private int adminIdx;

	/** 관리자 이름
	 * */
	private String adminNm;

	/** 관리자 아이디
	 * */
	private String id;

	private String name;
	
	/** 비밀번호
	 */
	private String pwd;

	/** 이메일
	 * */
	private String email;

	/** 핸드폰 번호 1 (첫 번째 자리)
	 * */
	private String cellNo1;

	/** 핸드폰 번호 2 (두 번째 자리)
	 * */
	private String cellNo2;

	/** 핸드폰 번호 3 (세 번째 자리)
	 * */
	private String cellNo3;

	/** 관리자 IP
	 * */
	private String ip;

	/** 관리자 계정 잠김 여부
	 * */
	private String blockYn;

	/** 로그인 실패 횟수
	 * */
	private int failCnt;
	
	/** 사용여부
	 * */
	private String useYn;

	/** 등록자
	 * */
	private int insNo;

	/** 등록일
	 * */
	private Date insDate;

	/** 수정자
	 * */
	private int uptNo;

	/** 수정일
	 * */
	private Date uptDate;
	
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null || obj instanceof SyAdminDto == false)
		{
			return false;
		}

		if(((SyAdminDto)obj).getAdminIdx() == this.getAdminIdx())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
}
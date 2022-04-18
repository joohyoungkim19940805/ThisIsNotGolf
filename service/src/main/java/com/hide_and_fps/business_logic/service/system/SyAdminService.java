package com.hide_and_fps.business_logic.service.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hide_and_fps.business_logic.dao.system.SyAdminDao;


@Service
public class SyAdminService {

	@Autowired
	private SyAdminDao syAdminDao;	

	/*
	public SyAdminDto getAdminById(int id) {
		return this.syAdminDao.getAdminById(id);
	}
	
	
	
	public DecaplusList<SyAdminDto> searchAdminList() {
		return this.syAdminDao.searchAdminList();
	}
	*/
}
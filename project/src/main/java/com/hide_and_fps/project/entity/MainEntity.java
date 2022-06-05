package com.hide_and_fps.project.entity;

public record MainEntity(
		String area_name,
		Long area_size,
		String citi_nama,
		Integer citi_percent,
		Integer non_citi_percent,
		Integer non_citi_size
		) {}

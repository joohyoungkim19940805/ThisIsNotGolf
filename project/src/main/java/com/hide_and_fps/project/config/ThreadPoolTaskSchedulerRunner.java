package com.hide_and_fps.project.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/*
import com.brazeMonitoringAssistant.web.scheduler.BrazeAutoSaveUserData;
import com.brazeMonitoringAssistant.web.scheduler.MonitoringAutoSaveJsonLog;
import com.brazeMonitoringAssistant.web.service.BatchService;
import com.brazeMonitoringAssistant.web.service.LogStreamService;
import com.brazeMonitoringAssistant.web.util.CommonUtil;
*/

@Component
public class ThreadPoolTaskSchedulerRunner {
	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;

	/*
	@Autowired
	private BatchService batchService;
	
	@Autowired
	private LogStreamService logStreamService;
	
	@Autowired
	private CommonUtil util;
	
	@PostConstruct
    public void scheduleRunnableWithCronTrigger() {
		taskScheduler.schedule(new MonitoringAutoSaveJsonLog(batchService, logStreamService, util), new CronTrigger("0 30 11 * * ?"));
		//taskScheduler.schedule(new BrazeAutoSaveUserData(WebClient.builder(), util), new CronTrigger("0 57 16 * * ?")); //zip �ٿ���۱��� 4~8������ �ҿ� ~ zip �ٿ� �Ϸ���� 5~6�� ���� �ҿ� json ��ȯ���� �� 2�� �ҿ�
	}
	*/
}

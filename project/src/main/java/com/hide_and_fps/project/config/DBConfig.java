package com.hide_and_fps.project.config;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
/*
@Configuration
@PropertySources({
	//로컬 환경에서 사용될 파일 경로
	@PropertySource(value = "classpath:/appConfig/datasource.properties", ignoreResourceNotFound = true),
	
	//서버 환경에서 사용될 파일 경로 (서버에서 사용될 datasource.properties 파일은 GIT에 올리지 않도록 주의 필요)
	@PropertySource(value = "file:${appConfigPath}/datasource.properties", ignoreResourceNotFound = true)
})
public class DBConfig {
	
	//datasource.properties의 key-value 프로퍼티에 저장 된 값을 변수에 할당
	@Value("${datasource.driver-class-name}")
	private String driver;
	
	@Value("${datasource.url}")
	private String url;
	
	@Value("${datasource.username}")
	private String username;
	
	@Value("${datasource.password}")
	private String password;
	
	@Value("${datasource.min-idle}")
	private int minIdle;
	
	@Value("${datasource.max-pool-size}")
	private int maxPoolSize;
	
	@Value("${datasource.max-life-time}")
	private long maxLifetime;
	
	@Value("${datasource.connection-test-query}")
	private String connectionTestQuery;
	
	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		
		//DB 연결정보 설정
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setDriverClassName(driver);
		hikariConfig.setJdbcUrl(url);
		hikariConfig.setUsername(username);
		hikariConfig.setPassword(password);
		
		//DBCP 설정
		hikariConfig.setMinimumIdle(this.minIdle);
		hikariConfig.setMaximumPoolSize(this.maxPoolSize);
		hikariConfig.setConnectionTestQuery(connectionTestQuery);
		hikariConfig.setMaxLifetime(this.maxLifetime);
		
		//DataSource 생성 및 반환
		HikariDataSource dataSource = new HikariDataSource(hikariConfig);
		return dataSource;
	}
*/
//	@Bean
//	public SqlSession decaplusSqlSession(@Autowired DataSource dataSource) throws Exception {
//		
//		org.apache.ibatis.session.Configuration mybatisConfiguration = new org.apache.ibatis.session.Configuration();
//		mybatisConfiguration.setObjectFactory(new ObjectFactory());
//		mybatisConfiguration.setMapUnderscoreToCamelCase(true);
//		
//		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
//		sqlSessionFactoryBean.setDataSource(dataSource);
//
//		sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mappers/**/*.xml"));
//
//		sqlSessionFactoryBean.setConfiguration(mybatisConfiguration);
//		
//		SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactoryBean.getObject());
//		SqlSession decaplusSqlSession = new SqlSession(sqlSessionTemplate);
//		
//		return decaplusSqlSession;
//	}

/*	
	@Bean
	public DataSourceTransactionManager transactionManager(@Autowired DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
	
	@Bean
	public TransactionInterceptor transactionAdvice(@Autowired DataSourceTransactionManager transactionManager) {
		NameMatchTransactionAttributeSource txAttributeSource = new NameMatchTransactionAttributeSource();
		
		RuleBasedTransactionAttribute txMethod = null;
		
		//Rollback Rules
		List<RollbackRuleAttribute> rollbackRuls = new ArrayList<RollbackRuleAttribute>();
		rollbackRuls.add(new RollbackRuleAttribute(Exception.class));
		
		//regist
		txMethod = new RuleBasedTransactionAttribute();
		txMethod.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		txMethod.setRollbackRules(rollbackRuls);
		txMethod.setReadOnly(false);
		txAttributeSource.addTransactionalMethod("regist*", txMethod);
		
		//update
		txMethod = new RuleBasedTransactionAttribute();
		txMethod.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		txMethod.setRollbackRules(rollbackRuls);
		txMethod.setReadOnly(false);
		txAttributeSource.addTransactionalMethod("update*", txMethod);
		
		//delete
		txMethod = new RuleBasedTransactionAttribute();
		txMethod.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		txMethod.setRollbackRules(rollbackRuls);
		txMethod.setReadOnly(false);
		txAttributeSource.addTransactionalMethod("delete*", txMethod);
		
		//newTrx
		txMethod = new RuleBasedTransactionAttribute();
		txMethod.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		txMethod.setRollbackRules(rollbackRuls);
		txMethod.setReadOnly(false);
		txAttributeSource.addTransactionalMethod("newTrx*", txMethod);
		
		//send
		txMethod = new RuleBasedTransactionAttribute();
		txMethod.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		txMethod.setRollbackRules(rollbackRuls);
		txMethod.setReadOnly(false);
		txAttributeSource.addTransactionalMethod("send*", txMethod);
		
		TransactionInterceptor transactionInterceptor = new TransactionInterceptor();
		transactionInterceptor.setTransactionManager(transactionManager);
		transactionInterceptor.setTransactionAttributeSource(txAttributeSource);
		
		return transactionInterceptor;
	}
	
	@Bean
	public Advisor transactionAdviceAdvisor(@Autowired TransactionInterceptor transactionAdvice) {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression("execution(* com.hide_and_fps.service.*.*Service.*(..))");
		return new DefaultPointcutAdvisor(pointcut, transactionAdvice);
	}
}
*/

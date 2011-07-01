/*
 * Copyright 2002-2011 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.springframework.integration.flow.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.config.ConsumerEndpointFactoryBean;
import org.springframework.integration.handler.BridgeHandler;

/**
 * @author David Turanski
 *
 */
public class FlowUtils {
	/*
	 * create  a bridge
	 */
	public static void createBridge(MessageChannel inputChannel, MessageChannel outputChannel,
			BeanDefinitionRegistry registry) {

		BeanDefinitionBuilder handlerBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(BridgeHandler.class);

		handlerBuilder.addPropertyValue("outputChannel", outputChannel);

		AbstractBeanDefinition handlerBeanDefinition = handlerBuilder.getBeanDefinition();

		BeanDefinitionBuilder consumerEndpointBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(ConsumerEndpointFactoryBean.class);
		
		String handlerBeanName = registerBeanDefinition(handlerBeanDefinition, registry); 
		consumerEndpointBuilder.addPropertyReference("handler", handlerBeanName);
		consumerEndpointBuilder.addPropertyValue("inputChannel", inputChannel);
		registerBeanDefinition(consumerEndpointBuilder.getBeanDefinition(), registry); 
	}
	
	public static String registerBeanDefinition(BeanDefinition beanDefinition, BeanDefinitionRegistry registry){
		String beanName = BeanDefinitionReaderUtils.generateBeanName(beanDefinition, registry);
		beanName = "flow."+ beanName;
		String strIndex = StringUtils.substringAfter(beanName,"#");
		int index = Integer.valueOf(strIndex);
		while (registry.isBeanNameInUse(beanName)){
			index++;
			beanName = beanName.replaceAll("#\\d$","#"+ (index)); 
		}
		registry.registerBeanDefinition(beanName, beanDefinition);
		return beanName;
	}
}
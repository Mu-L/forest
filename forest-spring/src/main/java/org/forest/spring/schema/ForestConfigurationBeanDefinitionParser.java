package org.forest.spring.schema;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.forest.config.ForestConfiguration;
import org.forest.exceptions.ForestRuntimeException;
import org.forest.spring.ssl.SpringSSLKeyStore;
import org.forest.ssl.SSLKeyStore;
import org.forest.utils.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.ChildBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.*;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-21 14:49
 */
public class ForestConfigurationBeanDefinitionParser implements BeanDefinitionParser {
    private static Log log = LogFactory.getLog(ForestConfigurationBeanDefinitionParser.class);

    private final Class configurationBeanClass = ForestConfiguration.class;
    private final Class sslKeyStoreBeanClass = SpringSSLKeyStore.class;


    public ForestConfigurationBeanDefinitionParser() {
    }

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();

        beanDefinition.setBeanClass(configurationBeanClass);
        beanDefinition.setLazyInit(false);
        beanDefinition.setFactoryMethodName("configuration");
        String id = element.getAttribute("id");
        if (id == null || id.length() == 0) {
            String generatedBeanName = configurationBeanClass.getName();
            id = generatedBeanName;
            int counter = 2;
            while(parserContext.getRegistry().containsBeanDefinition(id)) {
                id = generatedBeanName + (counter ++);
            }
        }
        if (id != null && id.length() > 0) {
            if (parserContext.getRegistry().containsBeanDefinition(id))  {
                throw new IllegalStateException("Duplicate spring bean id " + id);
            }
            parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
        }

        Method[] methods = configurationBeanClass.getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            Class[] paramTypes = method.getParameterTypes();
            if (paramTypes.length == 0 || paramTypes.length > 1) {
                continue;
            }
            Class paramType = paramTypes[0];
            if (Collections.class.isAssignableFrom(paramType) ||
                    Map.class.isAssignableFrom(paramType)) {
                continue;
            }
            if (methodName.length() >= 3 && methodName.startsWith("set")) {
                String attributeName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
                String attributeValue = element.getAttribute(attributeName);
                if (StringUtils.isNotEmpty(attributeValue)) {
                    beanDefinition.getPropertyValues().addPropertyValue(attributeName, attributeValue);
                }
            }
        }
        parseChildren(element.getChildNodes(), beanDefinition);
        log.info("[Forest] Created Forest Configuration Bean: " + beanDefinition);
        return beanDefinition;

    }


    public void parseChildren(NodeList nodeList, RootBeanDefinition beanDefinition) {
        int nodesLength = nodeList.getLength();
        if (nodesLength > 0) {
            ManagedMap<String, Object> varMap = new ManagedMap<String, Object>();
            ManagedMap<String, BeanDefinition> sslKeyStoreMap = new ManagedMap<>();
            for (int i = 0; i < nodesLength; i++) {
                Node node = nodeList.item(i);
                if (node instanceof Element) {
                    Element elem = (Element) node;
                    String elemName = elem.getLocalName();
                    if (elemName.equals("var")) {
                        parseVariable(elem, varMap);
                    }
                    else if (elemName.equals("ssl-keystore")) {
                        parseSSLKeyStore(elem, sslKeyStoreMap);
                    }
                }
            }
            beanDefinition.getPropertyValues().addPropertyValue("variables", varMap);
            beanDefinition.getPropertyValues().addPropertyValue("sslKeyStores", sslKeyStoreMap);
        }
    }


    public void parseVariable(Element elem, ManagedMap<String, Object> varMap) {
        String name = elem.getAttribute("name");
        String value = elem.getAttribute("value");
        varMap.put(name, value);
    }

    public void parseSSLKeyStore(Element elem, ManagedMap<String, BeanDefinition> sslKeyStoreMap) {
        String id = elem.getAttribute("id");
        String filePath = elem.getAttribute("file");
        String keystoreType = elem.getAttribute("type");
        String keystorePass = elem.getAttribute("pass");

        if (StringUtils.isEmpty(keystoreType)) {
            keystoreType = SSLKeyStore.DEFAULT_KEYSTORE_TYPE;
        }
        if (StringUtils.isEmpty(filePath)) {
            throw new ForestRuntimeException(
                    "The file of SSL KeyStore \"" + id + "\" is empty!");
        }

        BeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClassName(sslKeyStoreBeanClass.getName());
        beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(id);
        beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(keystoreType);
        beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(filePath);
        beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(keystorePass);
        sslKeyStoreMap.put(id, beanDefinition);
    }
}

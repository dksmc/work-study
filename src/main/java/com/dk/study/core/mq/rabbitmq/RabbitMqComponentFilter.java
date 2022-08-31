package com.dk.study.core.mq.rabbitmq;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

/**
 * @author dk
 * @date 2022/08/26 15:58
 **/
public class RabbitMqComponentFilter implements TypeFilter {

    /**
     * 过滤掉指定包下的类
     *
     * @param metadataReader 当前正在被扫描的bean
     * @param metadataReaderFactory 工厂,可以获取到其他任何类的信息
     * @return true/false
     */
    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) {
        String className = metadataReader.getClassMetadata().getClassName();
        String excludeClass = "com.dk.study.core.mq.rabbitmq";
        // String resourcePattern = "/**/*.class";
        return className.contains(excludeClass);
        //spring工具类，可以获取指定路径下的全部类
        /*ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        try {
            String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    ClassUtils.convertClassNameToResourcePath(excludeClass) + resourcePattern;
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            for (Resource resource : resources) {
                //用于读取类信息
                MetadataReader reader = metadataReaderFactory.getMetadataReader(resource);
                //扫描到的class
                String classname = reader.getClassMetadata().getClassName();
                System.out.println("当前被获取的类：" + classMetadata.getClassName());
                return StringUtils.hasLength(classname) && classname.contains(excludeClass);
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
        return false;*/
    }
}

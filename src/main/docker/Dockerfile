FROM registry.cn-hangzhou.aliyuncs.com/choerodon-tools/javabase:0.5.0
COPY app.jar /{{service.code}}.jar
ENTRYPOINT [ "java", "-jar", "/{{service.code}}.jar" ]

package com.xxx.rpc.common.bean;

public class RpcRequest {
    private String requestId; //请求参数
    private String InterfaceName; //接口名称
    private String serviceVersion; //服务版本号
    private String methodName; //调用的方法名称
    private Class<?>[] paramterTypes; //方法参数类型
    private Object[] parameters;    //方法参数

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getInterfaceName() {
        return InterfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        InterfaceName = interfaceName;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParamterTypes() {
        return paramterTypes;
    }

    public void setParamterTypes(Class<?>[] paramterTypes) {
        this.paramterTypes = paramterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}

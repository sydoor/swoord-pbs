package com.lizikj.trace.utils;

import java.util.Map;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.RpcContext;

/**
 * RpcContext工具类
 * @auth zone
 * @date 2017-10-14
 */
public class RpcContextUtils {

	public static String getParameter(String name) {
		RpcContext rpcContext = RpcContext.getContext();
		URL url = rpcContext.getUrl();
		Map<String, String> parameters = url.getParameters();
		return url.getParameter(name);
	}

	public static int getPort() {
		RpcContext rpcContext = RpcContext.getContext();
		URL url = rpcContext.getUrl();
		return url.getPort();
	}

	public static String getHost() {
		RpcContext rpcContext = RpcContext.getContext();
		URL url = rpcContext.getUrl();
		return url.getHost();
	}
}

package com.lizikj.log.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.lizikj.common.util.JsonUtils;

/**
 *
 * @auth zone
 * @date 2017-10-19
 */
@Activate(group = { Constants.PROVIDER })
public class LogFilter implements Filter {
	private final static Logger logger = LoggerFactory.getLogger(LogFilter.class);

	private static final String BEFORE_LOG_MSG = "local service[{}.{}][{}].\nReceive request:{}.";
	private static final String AFTER_LOG_MSG = "local service [{}.{}].\nReturn response:[{}]";

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		before(invoker, invocation);

		Result result = invoker.invoke(invocation);

		after(invoker, invocation, result.getValue());
		return result;
	}

	private void before(Invoker<?> invoker, Invocation invocation) {
		try {
			Object[] arguments = invocation.getArguments();
			String json = JsonUtils.toJSONString(arguments);
			logger.info(BEFORE_LOG_MSG, invoker.getInterface().getSimpleName(), invocation.getMethodName(), invoker.getUrl().getAddress(), json);
		} catch (Exception ex) {
		}
	}

	private void after(Invoker<?> invoker, Invocation invocation, Object response) {
		try {
			String json = JsonUtils.toJSONString(response);
			logger.info(AFTER_LOG_MSG, invoker.getInterface().getSimpleName(), invocation.getMethodName(), json);
		} catch (Exception ex) {
		}
	}
}

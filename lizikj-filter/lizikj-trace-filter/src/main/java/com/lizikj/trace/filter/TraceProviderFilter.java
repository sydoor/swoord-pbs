package com.lizikj.trace.filter;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.google.common.base.Stopwatch;
import com.lizikj.trace.context.TraceContext;
import com.lizikj.trace.trace.TraceAgent;
import com.lizikj.trace.trace.config.TraceConfig;
import com.lizikj.trace.utils.IdUtils;
import com.lizikj.trace.utils.NetworkUtils;
import com.lizikj.trace.utils.RpcContextUtils;
import com.twitter.zipkin.gen.Annotation;
import com.twitter.zipkin.gen.Endpoint;
import com.twitter.zipkin.gen.Span;

/**
 * 服务端日志过滤器
 * @auth zone
 * @date 2017-10-14
 */
@Activate(group = { Constants.PROVIDER })
public class TraceProviderFilter implements Filter {

	private Logger logger = LoggerFactory.getLogger(getClass().getName());

	private Span startTrace(Map<String, String> attaches, Invocation invocation) {
//		String host = RpcContextUtils.getHost();
//		int port = RpcContextUtils.getPort();
		String methodName = invocation.getMethodName();// 调用的方法名
		String interfaceName = RpcContextUtils.getParameter("interface");
		interfaceName = interfaceName.substring(interfaceName.lastIndexOf(".") + 1);// facade名
		String applicationName = "provider." + interfaceName + "." + methodName;

		Long traceId = Long.valueOf(attaches.get(TraceContext.TRACE_ID_KEY));
		Long parentSpanId = Long.valueOf(attaches.get(TraceContext.SPAN_ID_KEY));

		TraceContext.start();
		TraceContext.setTraceId(traceId);
		TraceContext.setSpanId(parentSpanId);

		Span providerSpan = new Span();

		long id = IdUtils.get();
		providerSpan.setId(id);
		providerSpan.setParent_id(parentSpanId);
		providerSpan.setTrace_id(traceId);
//        providerSpan.setName(TraceContext.getTraceConfig().getApplicationName());
		providerSpan.setName(applicationName);
		long timestamp = System.currentTimeMillis() * 1000;
		providerSpan.setTimestamp(timestamp);

		providerSpan.addToAnnotations(Annotation.create(timestamp, TraceContext.ANNO_SR,
				Endpoint.create(TraceContext.getTraceConfig().getApplicationName(), 
				NetworkUtils.ip2Num(NetworkUtils.getSiteIp()), 
				TraceContext.getTraceConfig().getServerPort())));

		TraceContext.addSpan(providerSpan);
		return providerSpan;
	}

	private void endTrace(Span span, Stopwatch watch) {

		span.addToAnnotations(Annotation.create(System.currentTimeMillis() * 1000, 
				TraceContext.ANNO_SS,
				Endpoint.create(span.getName(), 
				NetworkUtils.ip2Num(NetworkUtils.getSiteIp()), 
				TraceContext.getTraceConfig().getServerPort())));

		span.setDuration(watch.stop().elapsed(TimeUnit.MICROSECONDS));

		TraceConfig traceConfig = TraceContext.getTraceConfig();
		traceConfig.setServerPort(RpcContextUtils.getPort());
		traceConfig.setApplicationName(RpcContextUtils.getParameter("interface"));
		TraceAgent traceAgent = TraceAgent.getTraceAgent();
		traceAgent.send(TraceContext.getSpans());

	}

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		TraceConfig config = TraceContext.getTraceConfig();
		if (config == null || !config.isEnabled()) {
			return invoker.invoke(invocation);
		}

		Map<String, String> attaches = invocation.getAttachments();
		if (!attaches.containsKey(TraceContext.TRACE_ID_KEY)) {
			return invoker.invoke(invocation);
		}
		try {
			Stopwatch watch = Stopwatch.createStarted();
			Span providerSpan = this.startTrace(attaches, invocation);
			this.endTrace(providerSpan, watch);
		} catch (Exception e) {
		}

		Result result = invoker.invoke(invocation);
		return result;

	}
}

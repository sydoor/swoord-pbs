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
 * 消费端日志过滤器
 * @auth zone
 * @date 2017-10-14
 */
@Activate(group = { Constants.CONSUMER })
public class TraceConsumerFilter implements Filter {

	private Logger logger = LoggerFactory.getLogger(getClass().getName());

	private Span startTrace(Invoker<?> invoker, Invocation invocation) {
//		String host = RpcContextUtils.getHost();
//		int port = RpcContextUtils.getPort();
		String interfaceName = RpcContextUtils.getParameter("interface");
		String methodName = invocation.getMethodName();
		interfaceName = interfaceName.substring(interfaceName.lastIndexOf(".") + 1);// facade名
		String applicationName = "consumer." + interfaceName + "." + methodName;

		Span consumerSpan = new Span();

		Long traceId = null;
		long id = IdUtils.get();
		consumerSpan.setId(id);
		if (null == TraceContext.getTraceId()) {
			TraceContext.start();
			traceId = id;
		} else {
			traceId = TraceContext.getTraceId();
		}

		consumerSpan.setTrace_id(traceId);
		consumerSpan.setParent_id(TraceContext.getSpanId());
//		consumerSpan.setName(TraceContext.getTraceConfig().getApplicationName());
		consumerSpan.setName(applicationName);
		long timestamp = System.currentTimeMillis() * 1000;
		consumerSpan.setTimestamp(timestamp);

		consumerSpan.addToAnnotations(Annotation.create(timestamp, TraceContext.ANNO_CS,
				Endpoint.create(TraceContext.getTraceConfig().getApplicationName(), NetworkUtils.ip2Num(NetworkUtils.getSiteIp()), TraceContext.getTraceConfig().getServerPort())));

		Map<String, String> attaches = invocation.getAttachments();
		attaches.put(TraceContext.TRACE_ID_KEY, String.valueOf(consumerSpan.getTrace_id()));
		attaches.put(TraceContext.SPAN_ID_KEY, String.valueOf(consumerSpan.getId()));
		return consumerSpan;
	}

	private void endTrace(Span span, Stopwatch watch) {

		span.addToAnnotations(Annotation.create(System.currentTimeMillis() * 1000, TraceContext.ANNO_CR,
				Endpoint.create(span.getName(), NetworkUtils.ip2Num(NetworkUtils.getSiteIp()), TraceContext.getTraceConfig().getServerPort())));

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

		Span span = null;
		Stopwatch watch = null;
		try {
			watch = Stopwatch.createStarted();
			span = this.startTrace(invoker, invocation);
			TraceContext.start();
			TraceContext.setTraceId(span.getTrace_id());
			TraceContext.setSpanId(span.getId());
			TraceContext.addSpan(span);
		} catch (Exception e) {
		}

		Result result = invoker.invoke(invocation);
		try {
			if (span != null && watch != null) {
				this.endTrace(span, watch);
			}
		} catch (Exception e) {
		}
		return result;
	}
}

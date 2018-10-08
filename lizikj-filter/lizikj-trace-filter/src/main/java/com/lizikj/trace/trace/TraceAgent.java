package com.lizikj.trace.trace;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.github.kristofa.brave.AbstractSpanCollector;
import com.github.kristofa.brave.SpanCollectorMetricsHandler;
import com.lizikj.trace.context.TraceContext;
import com.lizikj.trace.trace.collector.HttpCollector;
import com.lizikj.trace.trace.collector.SimpleMetricsHandler;
import com.twitter.zipkin.gen.Span;

/**
 * 日志追踪代理器
 * @auth zone
 * @date 2017-10-14
 */
public class TraceAgent {
	private static TraceAgent traceAgent;
	
	public static void init(String serviceUrl) {
		traceAgent = new TraceAgent(serviceUrl);
	}
	
	public static TraceAgent getTraceAgent() {
		return traceAgent;
	}
	private final AbstractSpanCollector collector;

	private static int THREAD_POOL_COUNT = Runtime.getRuntime().availableProcessors();

	private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_COUNT, new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			Thread worker = new Thread(r);
			worker.setName("TRACE-AGENT-WORKER");
			worker.setDaemon(true);
			return worker;
		}
	});

	public TraceAgent(String server) {
		SpanCollectorMetricsHandler metrics = new SimpleMetricsHandler();
		collector = HttpCollector.create(server, TraceContext.getTraceConfig(), metrics);
	}

	public void send(final List<Span> spans) {
		if (spans != null && !spans.isEmpty()) {
			executor.submit(new CollectionWork(collector, spans));
		}
	}

	static final class CollectionWork implements Runnable {

		private List<Span> spans;
		private AbstractSpanCollector collector;

		CollectionWork(AbstractSpanCollector collector, List<Span> spans) {
			this.spans = spans;
			this.collector = collector;
		}

		@Override
		public void run() {
			for (Span span : spans) {
				collector.collect(span);
			}
			collector.flush();
		}
	}
}

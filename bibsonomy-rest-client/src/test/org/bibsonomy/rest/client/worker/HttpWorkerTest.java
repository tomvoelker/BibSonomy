package org.bibsonomy.rest.client.worker;

import org.bibsonomy.rest.client.util.ProgressCallback;
import org.bibsonomy.rest.client.worker.impl.GetWorker;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.io.Reader;
import java.io.StringWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static java.lang.System.out;

/**
 * @author agr
 */
public class HttpWorkerTest {
	private static final int PORT = 8089;
	private static final String URL = "http://localhost:" + PORT;
	private static final String EMPTY_RESPONSE =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
			"<bibsonomy stat=\"ok\">" +
			"    <posts start=\"0\" end=\"0\" />" +
			"</bibsonomy>";

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(PORT);

	@Test
	public void perform_ShouldSupportConcurrentRequest() throws InterruptedException, ExecutionException {
		// Create mock server
		stubFor(get(anyUrl())
				.willReturn(aResponse()
						.withBody(EMPTY_RESPONSE)
						.withStatus(200)));

		final int N_THREADS = 4;
		final ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
		final CountDownLatch endController = new CountDownLatch(N_THREADS);

		Future[] futures = new Future[N_THREADS];

		for (int i = 0; i < N_THREADS; i++) {
			final int finalI = i;
			out.println("Submitting Callable " + finalI);

			futures[i] = executorService.submit(new Callable() {
				@Override
				public Object call() throws Exception {
					StringWriter writer = new StringWriter();

					HttpWorker worker = new GetWorker("ignored", "ignored", null, new ProgressCallback() {
						@Override
						public void setPercent(int percent) {
						}
					});
					worker.setRenderingFormat(RenderingFormat.XML);
					try (Reader resultReader = worker.perform(URL, null)) {
						int val;
						while ((val = resultReader.read()) != -1) {
							writer.append((char) val);
						}
					} finally {
						endController.countDown();
					}

					out.println("Callable " + finalI + " finished");
					return "" + writer;
				}
			});
		}
		out.println("Waiting for all Callables to end");
		boolean allFinished = endController.await(500, TimeUnit.MILLISECONDS);
		out.println("Wait finished");
		Assert.assertTrue("Error: Not all Callables have finished.", allFinished);
		executorService.shutdown();
		for (Future f: futures) {
			Assert.assertEquals(EMPTY_RESPONSE, f.get());
		}
	}
}
package qnd;

import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.transport.TTransportException;

import test.utils.Benchmark;
import test.utils.BenchmarkResult;
import test.utils.Operation;

import com.github.btnguyen2k.bloom.jclient.IBloomClient;
import com.github.btnguyen2k.bloom.jclient.impl.RestBloomClientFactory;

public class QndBenchmarkRestClient extends BaseQndThriftClient {

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private static void runTest(final IBloomClient client, final int numRuns, final int numThreads)
            throws TTransportException {
        BenchmarkResult result = new Benchmark(new Operation() {
            @Override
            public void run(int runId) {
                String item1 = String.valueOf(RANDOM.nextInt(numRuns) * 2);
                client.mightContain(item1);
            }
        }, numRuns, numThreads).run();
        System.out.println(result.summarize());
    }

    /**
     * @param args
     * @throws TTransportException
     */
    public static void main(String[] args) throws TTransportException {
        String bloomServerUrl = System.getProperty("bloomServerUrl");
        if (StringUtils.isBlank(bloomServerUrl)) {
            bloomServerUrl = "http://localhost:9000";
        }

        int numRuns, numThreads;

        try {
            numRuns = Integer.parseInt(System.getProperty("numRuns"));
        } catch (Exception e) {
            numRuns = 10000;
        }
        try {
            numThreads = Integer.parseInt(System.getProperty("numThreads"));
        } catch (Exception e) {
            numThreads = 4;
        }

        IBloomClient client = RestBloomClientFactory.newBloomClient(bloomServerUrl);
        client.initBloom("s3cr3t", "default", numRuns, 1E-6, true, false, false);
        for (long l = 0; l < numRuns; l++) {
            client.put(String.valueOf(l));
        }

        for (int i = 0; i < 10; i++) {
            runTest(client, numRuns, numThreads);
        }

        RestBloomClientFactory.cleanup();
    }
}

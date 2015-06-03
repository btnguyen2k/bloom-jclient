package qnd;

import com.github.btnguyen2k.bloom.jclient.BloomResponse;
import com.github.btnguyen2k.bloom.jclient.IBloomClient;
import com.github.btnguyen2k.bloom.jclient.impl.ThriftHttpBloomClient;
import com.github.btnguyen2k.bloom.jclient.impl.ThriftHttpBloomClientFactory;

public class QndThriftHttpBloomClient {

    public static void main(String[] args) throws Exception {
        IBloomClient bloomClient = new ThriftHttpBloomClient().setBloomServerUrls(
                "http://localhost:9000/thrift").init();
        BloomResponse response = bloomClient.initBloom("s3cr3t", "default", 1000000, 1E-6, true,
                false, false);
        System.out.println("Init: " + response);
        System.out.println();
        System.out.println("Put: " + bloomClient.put("nbthanh"));
        System.out.println("Check: " + bloomClient.mightContain("nbthanh"));
        System.out.println("Check: " + bloomClient.mightContain("nbtan"));
        System.out.println();
        bloomClient = ThriftHttpBloomClientFactory.newBloomClient("http://localhost:9000/thrift");
        System.out.println("Put: " + bloomClient.put("nbthanh"));
        System.out.println("Check: " + bloomClient.mightContain("nbthanh"));
        System.out.println("Check: " + bloomClient.mightContain("nbtan"));
    }

}

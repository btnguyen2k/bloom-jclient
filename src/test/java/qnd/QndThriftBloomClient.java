package qnd;

import com.github.btnguyen2k.bloom.jclient.BloomResponse;
import com.github.btnguyen2k.bloom.jclient.IBloomClient;
import com.github.btnguyen2k.bloom.jclient.impl.ThriftBloomClient;
import com.github.btnguyen2k.bloom.jclient.impl.ThriftBloomClientFactory;

public class QndThriftBloomClient {

    public static void main(String[] args) {
        IBloomClient bloomClient = new ThriftBloomClient().setBloomServerHostsAndPorts(
                "localhost:9090").init();
        BloomResponse response = bloomClient.initBloom("s3cr3t", "default", 1000000, 1E-6, true,
                false, false);
        System.out.println("Init: " + response);
        System.out.println();
        System.out.println("Put: " + bloomClient.put("nbthanh"));
        System.out.println("Check: " + bloomClient.mightContain("nbthanh"));
        System.out.println("Check: " + bloomClient.mightContain("nbtan"));
        System.out.println();
        bloomClient = ThriftBloomClientFactory.newBloomClient("localhost:9090");
        System.out.println("Put: " + bloomClient.put("nbthanh"));
        System.out.println("Check: " + bloomClient.mightContain("nbthanh"));
        System.out.println("Check: " + bloomClient.mightContain("nbtan"));
    }

}

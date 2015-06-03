package qnd;

import com.github.btnguyen2k.bloom.jclient.BloomResponse;
import com.github.btnguyen2k.bloom.jclient.IBloomClient;
import com.github.btnguyen2k.bloom.jclient.impl.RestBloomClient;
import com.github.btnguyen2k.bloom.jclient.impl.RestBloomClientFactory;

public class QndRestBloomClient {

    public static void main(String[] args) {
        IBloomClient bloomClient = new RestBloomClient().setBloomServerUrl("http://localhost:9000")
                .init();
        BloomResponse response = bloomClient.initBloom("s3cr3t", "default", 1000000, 1E-6, true,
                false, false);
        System.out.println("Init: " + response);
        System.out.println();
        System.out.println("Put: " + bloomClient.put("nbthanh"));
        System.out.println("Check: " + bloomClient.mightContain("nbthanh"));
        System.out.println("Check: " + bloomClient.mightContain("nbtan"));
        System.out.println();
        bloomClient = RestBloomClientFactory.newBloomClient("http://localhost:9000");
        System.out.println("Put: " + bloomClient.put("nbthanh"));
        System.out.println("Check: " + bloomClient.mightContain("nbthanh"));
        System.out.println("Check: " + bloomClient.mightContain("nbtan"));
    }

}
